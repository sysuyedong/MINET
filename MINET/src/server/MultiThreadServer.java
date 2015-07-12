package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import net.sf.json.JSONObject;

import client.LoginUI;
import client.sendMsgUI;

 public class MultiThreadServer {
    private int port=8888;
    private ServerSocket serverSocket;
    private ExecutorService executorService;    //线程池
    private final int POOL_SIZE = 10;    //单个CPU线程池大小
    public static Map<String, sendMsgUI> msgUIMap = new HashMap<String,sendMsgUI>();    //跟据IP地址区分不同的P2P消息页面
    
    public MultiThreadServer() throws IOException{
        serverSocket=new ServerSocket(port);
        //Runtime的availableProcessor()方法返回当前的CPU数目.
        executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*POOL_SIZE);
        System.out.println("Server:服务器启动");
    }
    
    public ServerSocket getServer(){
    	return serverSocket;
    }   
    
    public void service(){
        while(true){
            Socket socket=null;
            try {
                  //接受客户连接,只要客户进行连接,就会触发accept(),从而建立连接.
                socket=serverSocket.accept();
                executorService.execute(new Handler(socket));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /*public static void main(String[] args) throws IOException {
        new MultiThreadServer().service();
    }*/

}

class Handler implements Runnable{
    private Socket socket;
    public Handler(Socket socket){
        this.socket=socket;
    }
    //得到socket的输出流
    private PrintWriter getWriter(Socket socket) throws IOException{
        OutputStream socketOut=socket.getOutputStream();
        return new PrintWriter(socketOut,true);
    }
    //得到socket的输入流
    private BufferedReader getReader(Socket socket) throws IOException{
        InputStream socketIn=socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn));
    }
    public String echo(String msg){
        return "echo:"+msg;
    }
    //建立连接,接收客户消息
    public void run(){
        try {
        	System.out.println("Server:New connection accepted "+socket.getInetAddress()+":"+socket.getPort());
        	BufferedReader br=getReader(socket);
        	PrintWriter pw=getWriter(socket);
            /***************处理JSON,并修改ouput框*****************/
        	String json = br.readLine();
        	System.out.println(json);
        	JSONObject jo = new JSONObject();
        	jo = JSONObject.fromObject(json);
        	String senderName = jo.getString("sender_name");
        	String sendTime = jo.getString("send_time");
        	String message = jo.getString("msg");
            /*********************************************************/
            /*
             * 根据不同的Source IP,修改相对应的sendMsgUI
             * 
             *
             */
        	InetAddress addr = socket.getInetAddress();
        	String sourceIP = addr.getHostAddress();
        	System.out.println(sourceIP);
        	String out = MultiThreadServer.msgUIMap.get(sourceIP).output.getText();
        	out += senderName + "\t";
			out += sendTime + "\n  ";
			out += message + "\n";
        	MultiThreadServer.msgUIMap.get(sourceIP).output.setText(out);
        	//设置光标为最后一行
			MultiThreadServer.msgUIMap.get(sourceIP).output.setCaretPosition(
					MultiThreadServer.msgUIMap.get(sourceIP).output.getDocument().getLength());
        	
        	//返回成功消息
        	pw.println("successful");
        	
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
        	try {
        		if(socket!=null)
        			socket.close();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
    }
}