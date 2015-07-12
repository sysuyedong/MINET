package client;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JList;
import javax.swing.JTextPane;


public class chatUI extends JFrame {

	private JPanel contentPane;
	private int roomID;
	private Thread thread;
	Timer timer;

	/**
	 * Create the frame.
	 */
	public chatUI(int id) {
		roomID = id;
		setTitle("chatroom");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 473, 375);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JTextArea output = new JTextArea();
		//output.setBounds(0, 0, 334, 209);
		//contentPane.add(output);
		output.setEditable(false);
		output.setLineWrap(true);
		JScrollPane outputScroll = new JScrollPane(output);
		outputScroll.setBounds(0, 0, 334, 209);
		contentPane.add(outputScroll);
		
		JLabel label = new JLabel("\u8F93\u5165\u5185\u5BB9");
		label.setBounds(0, 212, 54, 15);
		contentPane.add(label);
		
		final JTextArea input = new JTextArea();
		input.setBounds(0, 231, 334, 74);
		input.setLineWrap(true);
		contentPane.add(input);
		
		JButton send = new JButton("发送");
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = input.getText();
				if(msg != ""){
					//消息不支持\n..
					System.out.println(msg);
					boolean isSend = LoginUI.user.sendMessage(roomID, msg);
					if(!isSend){
						JOptionPane.showMessageDialog(null, "发送失败,请重新发送.");
					}
					else{
						input.setText("");
					}
				}
			}
		});
		send.setBounds(135, 304, 93, 23);
		contentPane.add(send);
		
		JButton cancel = new JButton("取消");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//退出聊天室,同时要取消更新群用户和实时更新消息
				boolean isExit = LoginUI.user.exitRoom(roomID);
				if(isExit){
					timer.cancel();
					dispose();
				}
				else{
					JOptionPane.showMessageDialog(null, "退出失败,请再次尝试.");
				}
			}
		});
		cancel.setBounds(238, 304, 93, 23);
		contentPane.add(cancel);
		
		JLabel label_1 = new JLabel("\u7528\u6237\u5217\u8868");
		label_1.setBounds(344, 4, 54, 15);
		contentPane.add(label_1);
		
		final JTextPane userList = new JTextPane();
		userList.setBounds(354, 29, 93, 276);
		contentPane.add(userList);
		userList.setEditable(false);
		
		/***********更新群用户列表***************/
		Timer updateUserTimer = new Timer();
		updateUserTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				ArrayList<Map<String, String>> list = LoginUI.user.getRoomUsers(roomID);
				String users = "";
				for(int i = 0; i < list.size(); ++i){
					users += list.get(i).get("userID") + '\t';
					users += list.get(i).get("userName") + '\n';
				}
				userList.setText(users);
			}
		}, 0, 5000);
		/************************************************************/
		
		/************挂起实时更新群消息的请求**************/
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				timer = new Timer();
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						ArrayList<Map<String, String>> msgList = LoginUI.user.updateMessage(roomID);
						String msg = output.getText();
						/*for(int i = msgList.size() - 1; i >= 0; --i){
							msg += msgList.get(i).get("senderName") + "(";
							msg += msgList.get(i).get("senderID") + ")\t";
							msg += msgList.get(i).get("sendTime") + "\n";
							msg += msgList.get(i).get("msg") + "\n";
						}
						output.setText(msg);
						output.setCaretPosition(output.getDocument().getLength());*/
						if(msgList.size() != 0){
							msg += msgList.get(0).get("senderName") + "(";
							msg += msgList.get(0).get("senderID") + ")\t";
							msg += msgList.get(0).get("sendTime") + "\n  ";
							msg += msgList.get(0).get("msg") + "\n";
							output.setText(msg);
							output.setCaretPosition(output.getDocument().getLength());
						}
						System.out.println("Receiving message..");
					}
				}, 0, 100);
				
			}
		};
		thread = new Thread(runnable);
		thread.start();
		/************************************************************/
	}
}
