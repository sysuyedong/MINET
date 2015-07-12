package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class RoomDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField roomname;

	/**
	 * Create the dialog.
	 */
	public RoomDialog() {
		setBounds(100, 100, 238, 120);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel label = new JLabel("输入群名:");
			contentPanel.add(label);
		}
		{
			roomname = new JTextField();
			contentPanel.add(roomname);
			roomname.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("创建群");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String roomName = roomname.getText();
						/***************发送创建群报文************/
						int roomID = LoginUI.user.createRoom(roomName);
						//创建失败
						if(roomID == -1){
							JOptionPane.showMessageDialog(null, "群名已存在,请重新输入.");
						}
						else{
							chatUI chatRoom = new chatUI(roomID);
							chatRoom.setVisible(true);
							chatRoom.setTitle("ChatRoom:" + roomName);
							dispose();
						}
						/************************************/						
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("加入群");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						int roomID = Integer.valueOf(roomname.getText());
						/***************发送加入群报文************/
						boolean isJoin = LoginUI.user.joinRoom(roomID);
						if(isJoin){
							chatUI chatRoom = new chatUI(roomID);
							chatRoom.setVisible(true);
							dispose();
						}
						else{
							JOptionPane.showMessageDialog(null, "群ID不存在,请重新输入.");
						}
						/************************************/
						
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
