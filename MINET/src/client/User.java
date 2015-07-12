package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class User {
	private String username;
	private String password;
	private String email;
	private boolean loginState;
	private ArrayList<Integer> friendsList;
	private HttpClient httpClient;
	
	private final String URL = "http://appdev.sysu.edu.cn:5925/";
	private final String URL_REGISTER = "user/register/";
	private final String URL_LOGIN = "user/login/";
	private final String URL_LOGOUT = "user/logout/";
	private final String URL_BREATH = "user/heartbeat/";
	private final String URL_GET_ONLINE_FRIEND = "user/get_online_friends/";
	private final String URL_GET_FRIEND = "user/get_friends/";
	private final String URL_ADD_FRIEND = "user/add_friend/";
	private final String URL_GET_USER_INFO = "user/get_user_info/";
	private final String URL_LAUNCH_TALK = "talk/launch_talk/";
	private final String URL_CHECK_NEW_TALK = "talk/check_new_talk/";
	private final String URL_CREATE_ROOM = "chatroom/create_room/";
	private final String URL_JOIN_ROOM = "chatroom/join_room/";
	private final String URL_SEND_MESSAGE = "chatroom/send_message/";
	private final String URL_EXIT_ROOM = "chatroom/exit_room/";
	private final String URL_UPDATE_MESSAGE = "chatroom/update_message/";
	private final String URL_GET_ROOM_USER_NUM = "chatroom/get_room_user_num/";
	private final String URL_GET_ROOM_USERS = "chatroom/get_room_users/";
	private final String URL_GET_ROOM_INFO = "chatroom/get_room_info/";
	
	public User(){
		username = "";
		password = "";
		email = "";
		loginState = false;
		friendsList = new ArrayList<Integer>();
		httpClient = new HttpClient();
		
		//设置http的BASIC认证
		List<String> authPrefs = new ArrayList<String>(1);
		authPrefs.add(AuthPolicy.BASIC);
		httpClient.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
		//抢先认证
		//httpClient.getParams().setAuthenticationPreemptive(true);
		httpClient.getParams().setHttpElementCharset("UTF-8");
		httpClient.getParams().setContentCharset("UTF-8");
	}
	
	public boolean register(String name, String psd, String e){
		String url = URL + URL_REGISTER;
		PostMethod postMethod = new PostMethod(url);
		//设置post的request报文
		postMethod.setRequestBody("{\"username\": \"" + name + "\", \"password\": \"" + psd + "\", \"email\": \"" + e + "\"}");
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
			    new DefaultHttpMethodRetryHandler());
		postMethod.setDoAuthentication(false);
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			//用户名已存在
			if(statusCode == 406){
				System.out.println("Username already exist!\n");
				return false;
			}
			else if(statusCode != HttpStatus.SC_OK){
				System.err.println("Method failed: "
					      + postMethod.getStatusLine());
			}
			else{
				username = name;
				password = psd;
				email = e;
				//返回user_id
				byte[] responseBody = postMethod.getResponseBody();

				System.out.println(new String(responseBody));
			}
		} catch (HttpException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		return true;
	}
	
	public boolean login(String usernameOrMail, String psd){
		String url = URL + URL_LOGIN;
		PostMethod postMethod = new PostMethod(url);
		//设置post的request报文
		postMethod.setRequestBody("{\"username_or_mail\": \"" + usernameOrMail + "\", \"password\": \"" + psd + "\"}");
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
			    new DefaultHttpMethodRetryHandler());
		postMethod.setDoAuthentication(false);
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			//用户名不存在或密码错误
			if(statusCode == 400){
				System.out.println("Username doesn't exist, or wrong password.\n");
				return false;
			}
			else if(statusCode != HttpStatus.SC_OK){
				System.err.println("Method failed: "
					      + postMethod.getStatusLine());
			}
			else{
				username = usernameOrMail;
				password = psd;
				//设置BASIC认证的credentials
				httpClient.getState().setCredentials(new AuthScope("appdev.sysu.edu.cn", 5925), 
						new UsernamePasswordCredentials(usernameOrMail, psd));
				httpClient.getParams().setAuthenticationPreemptive(true);
				//返回user_id
				byte[] responseBody = postMethod.getResponseBody();

				System.out.println(new String(responseBody));
			}
		} catch (HttpException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			//postMethod.releaseConnection();
		}
		loginState = true;
		return true;
	}
	
	//登出
	public boolean logout(){
		if(loginState = true){
			String url = URL + URL_LOGOUT;
			PostMethod postMethod = new PostMethod(url);
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			postMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(postMethod);
				//认证失败
				if(statusCode == 401){
					System.out.println("Auth failed.\n");
					return false;
				}
				//用户名不存在
				if(statusCode == 400){
					System.out.println("Username doesn't exist.\n");
					return false;
				}
				//用户当前不在线
				if(statusCode == 406){
					System.out.println("User is not online.\n");
					return false;
				}
				else if(statusCode != HttpStatus.SC_OK){
					System.err.println("Method failed: "
						      + postMethod.getStatusLine());
				}
			} catch (HttpException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				postMethod.releaseConnection();
			}
		}
		loginState = false;
		return true;
	}
	
	//心跳，此接口的目的在于判断客户端是否正常和客户端链接正常，因此在使用的时候，客户端应该每隔一段时间发送此请求，以检查网络是否正常链接中。
	public void breath(){
		if(loginState == true){
			//重新定义一个新的HttpClient,多线程中不能执行两次connection
			HttpClient breathHttpClient = new HttpClient();
			//设置BASIC认证的credentials
			breathHttpClient.getState().setCredentials(new AuthScope("appdev.sysu.edu.cn", 5925), 
					new UsernamePasswordCredentials(username, password));
			breathHttpClient.getParams().setAuthenticationPreemptive(true);
			
			String url = URL + URL_BREATH;
			GetMethod getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			getMethod.setDoAuthentication(true);
			try {
				int statusCode = breathHttpClient.executeMethod(getMethod);
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + getMethod.getStatusLine());
				}
				else{
					byte[] responseBody = getMethod.getResponseBody();

					System.out.println(new String(responseBody));
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				getMethod.releaseConnection();
			}
		}
	}
	
	//获取当前在线的好友的列表
	public ArrayList<Map<String, String>> getOnlineFriendsList(){
		ArrayList<Map<String, String>> friendList = new ArrayList<Map<String,String>>();
		if(loginState == true){
			String url = URL + URL_GET_ONLINE_FRIEND;
			GetMethod getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			getMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + getMethod.getStatusLine());
				}
				else{
					byte[] responseBody = getMethod.getResponseBody();
					/*************处理json格式的响应并加入list中*********/
					String res = new String(responseBody);
					JSONObject json = new JSONObject();
					json = JSONObject.fromObject(res);
					JSONArray jsonArray = (JSONArray)json.get("online_friends");
					for(int i = 0; i < jsonArray.size(); ++i){
						JSONObject jsonFriend = (JSONObject)jsonArray.get(i);
						String userID = jsonFriend.get("user_id").toString();
						String username = jsonFriend.get("username").toString();
						String ip = jsonFriend.get("ip_addr").toString();
						Map<String, String> m = new HashMap<String, String>();
						m.put("user_id", userID);
						m.put("username", username);
						m.put("ip_addr", ip);
						friendList.add(m);
					}

					/*********************************************/
					System.out.println(new String(responseBody));
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				getMethod.releaseConnection();
			}
		}
		return friendList;
	}
	
	//获取所有的好友列表
	public ArrayList<Map<String, String>> getFriendsList(){
		ArrayList<Map<String, String>> friendList = new ArrayList<Map<String,String>>();
		if(loginState == true){
			String url = URL + URL_GET_FRIEND;
			GetMethod getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			getMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + getMethod.getStatusLine());
				}
				else{
				byte[] responseBody = getMethod.getResponseBody();
				/*************处理json格式的响应并加入list中*********/
				String res = new String(responseBody);
				JSONObject json = new JSONObject();
				json = JSONObject.fromObject(res);
				JSONArray jsonArray = (JSONArray)json.get("friends");
				for(int i = 0; i < jsonArray.size(); ++i){
					JSONObject jsonFriend = (JSONObject)jsonArray.get(i);
					String userID = jsonFriend.get("user_id").toString();
					String username = jsonFriend.get("username").toString();
					String ip = jsonFriend.get("ip_addr").toString();
					Map<String, String> m = new HashMap<String, String>();
					m.put("user_id", userID);
					m.put("username", username);
					m.put("ip_addr", ip);
					friendList.add(m);
				}
				/*********************************************/
				System.out.println(new String(responseBody));
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				getMethod.releaseConnection();
			}
		}
		return friendList;
	}
	
	//添加好友
	public boolean addFriend(int userID){
		if(loginState == true){
			String url = URL + URL_ADD_FRIEND + String.valueOf(userID) + "/";
			PostMethod postMethod = new PostMethod(url);
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			postMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(postMethod);
				//用户不存在
				if(statusCode == 400){
					System.out.println("User doesn't exist.\n");
					return false;
				}
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + postMethod.getStatusLine());
				}
				else{
				byte[] responseBody = postMethod.getResponseBody();

				System.out.println(new String(responseBody));
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				postMethod.releaseConnection();
			}
		}
		friendsList.add(userID);
		return true;
	}
	
	//获取用户信息
	public Map<String, String> getFriendInfo(int userID){
		Map<String, String> userInfo = new HashMap<String, String>();
		String url = URL + URL_GET_USER_INFO + String.valueOf(userID) + "/";
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
			    new DefaultHttpMethodRetryHandler());
		getMethod.setDoAuthentication(false);
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			//用户不存在
			if(statusCode == 400){
				System.out.println("User doesn't exist.\n");
				return userInfo;
			}
			//没有连接上
			if(statusCode != HttpStatus.SC_OK){
				loginState = false;
				System.err.println("Method failed: "
					      + getMethod.getStatusLine());
			}
			else{
			byte[] responseBody = getMethod.getResponseBody();

			/*****把返回的json响应信息传入map中******************/
			String res = new String(responseBody);
			JSONObject json = new JSONObject();
			json = JSONObject.fromObject(res);
			String username = json.getString("username");
			String email = json.getString("email");
			userInfo.put("username", username);
			userInfo.put("email", email);
			/**************************************************/
			System.out.println(new String(responseBody));
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return userInfo;
	}
	
	//向某个用户发起聊天，在发起聊天之后，用户会通知你想要聊天对象你想要和他聊天，详情请看下面的接口。
	public boolean launchTalk(int userID){
		if(loginState == true){
			String url = URL + URL_LAUNCH_TALK + String.valueOf(userID) + "/";
			PostMethod postMethod = new PostMethod(url);
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			postMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(postMethod);
				//用户不存在
				if(statusCode == 400){
					System.out.println("User doesn't exist.\n");
					return false;
				}
				//用户不在线
				if(statusCode == 406){
					System.out.println("User is not online.\n");
					return false;
				}
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + postMethod.getStatusLine());
				}
				else{
				byte[] responseBody = postMethod.getResponseBody();

				System.out.println(new String(responseBody));
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				postMethod.releaseConnection();
			}
			return true;
		}
		return false;
	}
	
	//检查是否有人想和你聊天，客户端应该时刻链接这个接口。
	public ArrayList<Map<String, String>> checkNewTalk(){
		ArrayList<Map<String, String>> chatList = new ArrayList<Map<String,String>>();
		if(loginState == true){
			//重新定义一个新的HttpClient,多线程中不能执行两次connection
			HttpClient updateHttpClient = new HttpClient();
			//设置BASIC认证的credentials
			updateHttpClient.getState().setCredentials(new AuthScope("appdev.sysu.edu.cn", 5925), 
					new UsernamePasswordCredentials(username, password));
			updateHttpClient.getParams().setAuthenticationPreemptive(true);
			String url = URL + URL_CHECK_NEW_TALK;
			GetMethod getMethod = new GetMethod(url);
			getMethod.setRequestHeader("Connection", "Keep-Alive");
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			getMethod.setDoAuthentication(true);
			try {
				System.out.println("check\n");
				int statusCode = updateHttpClient.executeMethod(getMethod);
				//用户不存在
				if(statusCode == 406){
					System.out.println("User is not online or connection is not Keep-Alive.\n");
				}
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + getMethod.getStatusLine());
				}
				else{
				byte[] responseBody = getMethod.getResponseBody();

				/*****处理json响应，得到聊天用户列表******************/
				String res = new String(responseBody);
				if(res == "")
					return chatList;
				JSONObject json = new JSONObject();
				json = JSONObject.fromObject(res);
				JSONArray jsonArray = (JSONArray)json.get("senders");
				for(int i = 0; i < jsonArray.size(); ++i){
					JSONObject jsonFriend = (JSONObject)jsonArray.get(i);
					String userID = jsonFriend.get("user_id").toString();
					String username = jsonFriend.get("username").toString();
					String ip = jsonFriend.get("user_ip").toString();
					Map<String, String> m = new HashMap<String, String>();
					m.put("user_id", userID);
					m.put("username", username);
					m.put("ip_addr", ip);
					chatList.add(m);
				}
				/**************************************************/
				System.out.println(new String(responseBody));
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return chatList;
	}
	
	//创建聊天室
	public int createRoom(String roomName){
		int roomID = -1;
		if(loginState == true){
			String url = URL + URL_CREATE_ROOM;
			PostMethod postMethod = new PostMethod(url);
			//设置post的request报文
			postMethod.setRequestBody("{\"room_name\": \"" + roomName + "\"}");
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			postMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(postMethod);
				//聊天室已存在
				if(statusCode == 406){
					System.out.println("RoomName already exist!\n");
					return -1;
				}
				else if(statusCode != HttpStatus.SC_OK){
					System.err.println("Method failed: "
						      + postMethod.getStatusLine());
				}
				//返回json
				else{
				byte[] responseBody = postMethod.getResponseBody();
				/************处理返回的json格式*************/
				String res = new String(responseBody);
				JSONObject json = new JSONObject();
				json = JSONObject.fromObject(res);
				roomID = json.getInt("room_id");
				/******************************************/
				System.out.println(new String(responseBody));
				}
			} catch (HttpException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				postMethod.releaseConnection();
			};
		}
		return roomID;
	}
	
	//加入聊天室.当用户加入聊天室之后，服务器会向聊天室中所有用户广播一条消息，例如ragnarok join room!
	public boolean joinRoom(int roomID){
		if(loginState == true){
			String url = URL + URL_JOIN_ROOM + String.valueOf(roomID) + "/";
			PostMethod postMethod = new PostMethod(url);
			//设置post的request报文
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			postMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(postMethod);
				//聊天室不存在
				if(statusCode == 400){
					System.out.println("Room doesn't exist!\n");
					return false;
				}
				//用户不在线
				if(statusCode == 406){
					System.out.println("You are not online!\n");
					return false;
				}
				else if(statusCode != HttpStatus.SC_OK){
					System.err.println("Method failed: "
						      + postMethod.getStatusLine());
				}
				else{
				byte[] responseBody = postMethod.getResponseBody();
				
				System.out.println(new String(responseBody));
				}
			} catch (HttpException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				postMethod.releaseConnection();
			}
			return true;
		}
		return false;
	}
	
	//向聊天室发送消息
	public boolean sendMessage(int roomID, String msg){
		if(loginState == true){
			String url = URL + URL_SEND_MESSAGE + String.valueOf(roomID) + "/";
			PostMethod postMethod = new PostMethod(url);
			//设置post的request报文
			postMethod.setRequestBody("{\"message\": \"" + msg + "\"}");
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			postMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(postMethod);
				//聊天室不存在
				if(statusCode == 400){
					System.out.println("RoomName doesn't exist!\n");
					return false;
				}
				//用户不在线
				if(statusCode == 406){
					System.out.println("You are not online!\n");
					return false;
				}
				else if(statusCode != HttpStatus.SC_OK){
					System.err.println("Method failed: "
						      + postMethod.getStatusLine());
				}
				else{
				//返回json
				byte[] responseBody = postMethod.getResponseBody();
				System.out.println(new String(responseBody));
				}
			} catch (HttpException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				postMethod.releaseConnection();
			}
			return true;
		}
		return false;
	}
	
	//退出聊天室
	public boolean exitRoom(int roomID){
		if(loginState == true){
			String url = URL + URL_EXIT_ROOM + String.valueOf(roomID) + "/";
			PostMethod postMethod = new PostMethod(url);
			//设置post的request报文
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			postMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(postMethod);
				//聊天室不存在
				if(statusCode == 400){
					System.out.println("Room doesn't exist!\n");
					return false;
				}
				//用户不在线或用户不在该聊天室
				if(statusCode == 406){
					System.out.println("You are not online!\n");
					return false;
				}
				else if(statusCode != HttpStatus.SC_OK){
					System.err.println("Method failed: "
						      + postMethod.getStatusLine());
				}
				else{
				byte[] responseBody = postMethod.getResponseBody();
				
				System.out.println(new String(responseBody));
				}
			} catch (HttpException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				postMethod.releaseConnection();
			}
			return true;
		}
		return false;
	}
	
	//实时更新聊天室中的信息
	public ArrayList<Map<String, String>> updateMessage(int roomID){
		ArrayList<Map<String, String>> msgList = new ArrayList<Map<String,String>>();
		if(loginState == true){
			//重新定义一个新的HttpClient,多线程中不能执行两次connection
			HttpClient updateHttpClient = new HttpClient();
			//设置BASIC认证的credentials
			updateHttpClient.getState().setCredentials(new AuthScope("appdev.sysu.edu.cn", 5925), 
					new UsernamePasswordCredentials(username, password));
			updateHttpClient.getParams().setAuthenticationPreemptive(true);
			
			String url = URL + URL_UPDATE_MESSAGE + String.valueOf(roomID) + "/";
			GetMethod getMethod = new GetMethod(url);
			getMethod.setRequestHeader("Connection", "Keep-Alive");
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			getMethod.setDoAuthentication(true);
			try {
				System.out.println("check\n");
				int statusCode = updateHttpClient.executeMethod(getMethod);
				//用户不存在
				if(statusCode == 406){
					System.out.println("User is not in this room or connection is not Keep-Alive.\n");
				}
				//
				if(statusCode == 400){
					System.out.println("room name is not exist.\n");
				}
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + getMethod.getStatusLine());
				}
				else{
					byte[] responseBody = getMethod.getResponseBody();

					/*****处理json响应，得到消息的具体信息******************/
					String res = new String(responseBody);
					System.out.println("res=" + res);
					JSONObject json = new JSONObject();
					json = JSONObject.fromObject(res);
					JSONArray msgArray = json.getJSONArray("messages");
					for(int i = 0; i < msgArray.size(); ++i){
						JSONObject jo = msgArray.getJSONObject(i);
						String msgID = jo.getString("msg_id");
						String senderID = jo.getString("sender_id");
						String senderName = jo.getString("sender_name");
						String msg = jo.getString("msg");
						String sendTime = jo.getString("send_time");
						Map<String, String> m = new HashMap<String, String>();
						m.put("msgID", msgID);
						m.put("senderID", senderID);
						m.put("senderName", senderName);
						m.put("msg", msg);
						m.put("sendTime", sendTime);
						msgList.add(m);
					}
				/**************************************************/
					System.out.println(new String(responseBody));
					return msgList;
				}
				}
			  catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				//getMethod.releaseConnection();
			}
		}
		return msgList;
	}
	
	//获取聊天室当前在线的人数
	public int getRoomUserNum(int roomID){
		int num = 0;
		if(loginState == true){
			String url = URL + URL_GET_ROOM_USER_NUM + String.valueOf(roomID) + "/";
			GetMethod getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			getMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				//用户不存在
				if(statusCode == 400){
					System.out.println("room name is not exist.\n");
				}
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + getMethod.getStatusLine());
				}
				else{
				byte[] responseBody = getMethod.getResponseBody();

				/***********处理json*********/
				String res = new String(responseBody);
				JSONObject json = new JSONObject();
				json = JSONObject.fromObject(res);
				num = json.getInt("user_num");
				/***********************************/
				System.out.println(new String(responseBody));
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				getMethod.releaseConnection();
			}
		}
		return num;
	}
	
	
	//获取聊天室所有用户的信息
	public ArrayList<Map<String, String>> getRoomUsers(int roomID){
		ArrayList<Map<String, String>> userList = new ArrayList<Map<String,String>>();
		if(loginState == true){
			String url = URL + URL_GET_ROOM_USERS + String.valueOf(roomID) + "/";
			GetMethod getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			getMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				//聊天室不存在
				if(statusCode == 400){
					System.out.println("room name is not exist.\n");
				}
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + getMethod.getStatusLine());
				}
				else{
				byte[] responseBody = getMethod.getResponseBody();

				/***********处理json*********/
				String res = new String(responseBody);
				JSONObject json = new JSONObject();
				json = JSONObject.fromObject(res);
				JSONArray userArray = json.getJSONArray("users");
				for(int i = 0; i < userArray.size(); ++i){
					JSONObject jo = userArray.getJSONObject(i);
					String userID = jo.getString("user_id");
					String userName = jo.getString("username");
					Map<String, String> m = new HashMap<String, String>();
					m.put("userID", userID);
					m.put("userName", userName);
					userList.add(m);
				}
				
				/***********************************/
				System.out.println(new String(responseBody));
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				getMethod.releaseConnection();
			}
		}
		return userList;
	}
	
	//获取聊天室信息
	public Map<String, String> getRoomInfo(int roomID){
		Map<String, String> m = new HashMap<String, String>();
		if(loginState == true){
			String url = URL + URL_GET_ROOM_USERS + String.valueOf(roomID) + "/";
			GetMethod getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				    new DefaultHttpMethodRetryHandler());
			getMethod.setDoAuthentication(true);
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				//聊天室不存在
				if(statusCode == 400){
					System.out.println("room name is not exist.\n");
				}
				//没有连接上
				if(statusCode != HttpStatus.SC_OK){
					loginState = false;
					System.err.println("Method failed: "
						      + getMethod.getStatusLine());
				}
				else{
				byte[] responseBody = getMethod.getResponseBody();

				/***********处理json*********/
				String res = new String(responseBody);
				JSONObject json = new JSONObject();
				json = JSONObject.fromObject(res);
				String roomName = json.getString("room_name");
				String LauncherID = json.getString("room_launcher_id");
				String LauncherName = json.getString("room_launcher_name");
				m.put("roomID", String.valueOf(roomID));
				m.put("roomName", roomName);
				m.put("LauncherID", LauncherID);
				m.put("LauncherName", LauncherName);
				/***********************************/
				System.out.println(new String(responseBody));
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				getMethod.releaseConnection();
			}
		}
		return m;
	}
	
	public String getUserName(){
		return username;
	}
}
