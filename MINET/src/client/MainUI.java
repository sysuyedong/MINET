package client;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JTextPane;

import server.MultiThreadServer;


public class MainUI extends JFrame {

	private JPanel contentPane;
	private JButton addFriend;
	private JButton userInfo;
	private JButton Logout;
	public static int userID;
	private ArrayList<Map<String, String>> chatList;
	public static MultiThreadServer p2pServer;
	public static ArrayList<Map<String, String>> onlineList;

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public MainUI() throws IOException {
		//启动P2P服务器
		p2pServer = new MultiThreadServer();
		Runnable runServer = new Runnable() {
			
			@Override
			public void run() {
				p2pServer.service();
			}
		};
		Thread serverThread = new Thread(runServer);
		serverThread.start();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 289, 447);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("\u7528\u6237\u5217\u8868");
		label.setBounds(5, 5, 146, 15);
		contentPane.add(label);
		
		JButton launchTalk = new JButton("发起聊天");
		launchTalk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InputParameter inputDialog = new InputParameter(MainUI.this, "launchTalk");
				inputDialog.setTitle("发起聊天");
				inputDialog.setVisible(true);
			}
		});
		launchTalk.setBounds(28, 316, 93, 23);
		contentPane.add(launchTalk);
		
		addFriend = new JButton("添加好友");
		addFriend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputParameter inputDialog = new InputParameter(MainUI.this, "addFriend");
				inputDialog.setTitle("添加好友");
				inputDialog.setVisible(true);
			}
		});
		addFriend.setBounds(142, 347, 93, 23);
		contentPane.add(addFriend);
		
		userInfo = new JButton("用户信息");
		userInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputParameter inputDialog = new InputParameter(MainUI.this, "userInfo");
				inputDialog.setTitle("用户信息");
				inputDialog.setVisible(true);
			}
		});
		userInfo.setBounds(28, 376, 93, 23);
		contentPane.add(userInfo);
		
		Logout = new JButton("下线");
		Logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean isLogout = LoginUI.user.logout();
				if(isLogout){
					/*****下线的同时,关闭心跳和check_new_talk*****/
					/*******************************/
					dispose();
					LoginUI loginFrame = new LoginUI();
					loginFrame.setVisible(true);
				}
				else{
					JOptionPane.showMessageDialog(null, "下线失败,请再次尝试.");
				}
			}
		});
		Logout.setBounds(142, 376, 93, 23);
		contentPane.add(Logout);
		
		JButton button = new JButton("群消息");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RoomDialog roomDialog = new RoomDialog();
				roomDialog.setVisible(true);
			}
		});
		button.setBounds(28, 347, 93, 23);
		contentPane.add(button);
		
		final JButton receiveMsg = new JButton("接收信息");
		receiveMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < chatList.size(); ++i){
					Map<String, String> map = chatList.get(i);
					String id = map.get("user_id");
					String name = map.get("username");
					String ip = map.get("ip_addr");
					sendMsgUI sendMsgFrame = new sendMsgUI(ip);
					MultiThreadServer.msgUIMap.put(ip, sendMsgFrame);
					sendMsgFrame.setVisible(true);
					/*根据用户信息修改title*/
					sendMsgFrame.setTitle("与" + name + "聊天中");
				}
			}
		});
		receiveMsg.setEnabled(false);
		receiveMsg.setBounds(142, 316, 93, 23);
		contentPane.add(receiveMsg);
		
		final JTextPane userList = new JTextPane();
		userList.setText("");
		//userList.setBounds(5, 52, 258, 254);
		userList.setEditable(false);
		//contentPane.add(userList);
		//设置滚动条
		JScrollPane scrollList = new JScrollPane(userList);
		scrollList.setBounds(5, 52, 258, 254);
		contentPane.add(scrollList);
		scrollList.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollList.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		
		JLabel lblidIp = new JLabel("用户ID            用户名            IP地址");
		lblidIp.setBounds(5, 27, 274, 15);
		contentPane.add(lblidIp);
		
		/***********发送请求报文,获得用户列表,并显示在UI界面上**********/
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						ArrayList<Map<String, String>> friendList = LoginUI.user.getFriendsList();
						onlineList = LoginUI.user.getOnlineFriendsList();
						String list = "";
						for(int i = 0; i < onlineList.size(); ++i){
							list += onlineList.get(i).get("user_id") + '\t';
							list += onlineList.get(i).get("username") + '\t';
							list += onlineList.get(i).get("ip_addr") + '\n';
						}
						userList.setText(list);
					}
				}, 0, 5000);
			}
		};
		Thread t = new Thread(r);
		t.start();
		
		/***********************************************************/
		
		/***********挂起查看是否有用户发送聊天请求*****maybe fail!!****/
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				Timer checkTalkTimer = new Timer();
				checkTalkTimer.scheduleAtFixedRate(new TimerTask() {
					
					@Override
					public void run() {
						chatList = LoginUI.user.checkNewTalk();
						if(chatList.size() != 0){
							receiveMsg.setEnabled(true);
						}
					}
				}, 0, 1000);
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
		/***********************************************************/
		
		/************设置心跳,每隔一段时间向服务器发送报文******************/
		LoginUI.user.breath();
		Runnable runBreath = new Runnable() {
			
			@Override
			public void run() {
				Timer checkTalkTimer = new Timer();
				checkTalkTimer.scheduleAtFixedRate(new TimerTask() {
					
					@Override
					public void run() {
						chatList = LoginUI.user.checkNewTalk();
						if(chatList.size() != 0){
							receiveMsg.setEnabled(true);
						}
					}
				}, 0, 5000);
			}
		};
		Thread beathThead = new Thread(runBreath);
		beathThead.start();
		/********************************************************/
	}
}
