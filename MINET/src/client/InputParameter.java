package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.parser.Parser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import server.MultiThreadServer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class InputParameter extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField userID;

	/**
	 * Create the dialog.
	 */
	public InputParameter(final MainUI mainUI, final String type) {
		setBounds(100, 100, 227, 115);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblid = new JLabel("输入用户ID");
			contentPanel.add(lblid);
		}
		{
			userID = new JTextField();
			contentPanel.add(userID);
			userID.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						/*得到textField的内容*/
						//System.out.println(userID.getText());
						MainUI.userID = Integer.valueOf(userID.getText());
						//System.out.println(MainUI.userID);
						if(type.equals("launchTalk")){
							/*************发送请求聊天报文,得到响应**************/
							boolean isLaunch = LoginUI.user.launchTalk(MainUI.userID);
							if(isLaunch){
								String desIP = null;
								//获得请求聊天用户的IP地址
								for(int i = 0; i < MainUI.onlineList.size(); ++i){
									String id = MainUI.onlineList.get(i).get("user_id");
									if(id.equals(userID.getText())){
										desIP = MainUI.onlineList.get(i).get("ip_addr");
									}
								}
								if(desIP != null){
									sendMsgUI sendMsgFrame = new sendMsgUI(desIP);
									//将IP与对应sendMsgUI的映射传入Server中,方便管理
									MultiThreadServer.msgUIMap.put(desIP, sendMsgFrame);
									sendMsgFrame.setVisible(true);
									/*根据用户信息修改title*/
									Map<String, String> m = LoginUI.user.getFriendInfo(MainUI.userID);
									sendMsgFrame.setTitle("与" + m.get("username") + "聊天中");
									dispose();
								}
								else{
									JOptionPane.showMessageDialog(null, "用户不存在或不在线(或用户不是你的好友),请重新输入.");
								}
							}
							else
								JOptionPane.showMessageDialog(null, "请求失败,请重新输入.");
							/******************************************/
							//mainUI.dispose();
						}
						else if(type.equals("addFriend")){
							/*************发送添加好友报文,得到响应**************/
							boolean isAdd = LoginUI.user.addFriend(MainUI.userID);
							if(isAdd){
								dispose();
							}
							else{
								JOptionPane.showMessageDialog(null, "用户不存在,请重新输入.");
							}
							/******************************************/
						}
						else if(type.equals("userInfo")){
							/*************发送查看用户信息报文,得到响应**************/
							Map<String, String> m = LoginUI.user.getFriendInfo(MainUI.userID);
							if(m.isEmpty()){
								JOptionPane.showMessageDialog(null, "用户不存在,请重新输入.");
							}
							else{
								JOptionPane.showMessageDialog(null, m.get("username") + "  " + m.get("email"));
							}
							/******************************************/
						}
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
