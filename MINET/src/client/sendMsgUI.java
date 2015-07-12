package client;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JLabel;

import server.MultiThreadServer;


public class sendMsgUI extends JFrame {

	private JPanel contentPane;
	public String desIP;
	public JTextArea output;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					sendMsgUI frame = new sendMsgUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public sendMsgUI(final String ip) {
		desIP = ip;
		setTitle("172.18.69.199 Tom");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 358, 375);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		output = new JTextArea();
		output.setWrapStyleWord(true);
		output.setLineWrap(true);
		//output.setBackground(Color.WHITE);
		//output.setForeground(Color.BLACK)
		//output.setBackground(Color.LIGHT_GRAY);
		output.setText("");
		//output.setBounds(10, 10, 322, 192);
		output.setEditable(false);
		//contentPane.add(output);
		JScrollPane outputScroll = new JScrollPane(output);
		outputScroll.setBounds(10, 10, 322, 192);
		contentPane.add(outputScroll);
		
		final JTextArea input = new JTextArea();
		input.setBounds(10, 225, 322, 69);
		contentPane.add(input);
		input.setLineWrap(true);
		
		JButton send = new JButton("发送");
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = input.getText();
				if(!msg.equals("")){
					try {
						P2PClient client = new P2PClient(msg, ip);
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					input.setText("");
				}
			}
		});
		//send.setEnabled(false);
		send.setBounds(142, 304, 93, 23);
		contentPane.add(send);
		
		JButton cancel = new JButton("取消");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MultiThreadServer.msgUIMap.remove(ip);
				dispose();
			}
		});
		cancel.setBounds(239, 304, 93, 23);
		contentPane.add(cancel);
		
		JLabel label = new JLabel("\u8F93\u5165\u5185\u5BB9:");
		label.setBounds(10, 210, 54, 15);
		contentPane.add(label);
	}

}
