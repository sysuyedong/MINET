package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;


import server.MultiThreadServer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import javax.swing.JPasswordField;

public class LoginUI extends JFrame {

	private JPanel contentPane;
	private JTextField usernameOrEmail;
	private JPasswordField password;
	public static User user;

	/**
	 * Create the frame.
	 */
	public LoginUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 281, 236);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblMinet = new JLabel("MINET");
		lblMinet.setFont(new Font("宋体", Font.PLAIN, 24));
		lblMinet.setBounds(99, 10, 73, 29);
		contentPane.add(lblMinet);
		
		JLabel label = new JLabel("用户名/邮箱: ");
		label.setBounds(20, 59, 84, 15);
		contentPane.add(label);
		
		usernameOrEmail = new JTextField();
		usernameOrEmail.setBounds(114, 56, 127, 21);
		contentPane.add(usernameOrEmail);
		usernameOrEmail.setColumns(10);
		
		JButton login = new JButton("登陆");
		//设置登录按钮监听器
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*************发送登陆报文,得到响应******************/
				String username = usernameOrEmail.getText();
				String psd = password.getText();
				user = new User();
				boolean isLogin = user.login(username, psd);
				/********************************************/
				if(isLogin){
					dispose();
					MainUI frame;
					try {
						frame = new MainUI();
						frame.setVisible(true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//登录失败
				else{
					System.out.println("Login failed!!");
				}
			}
		});
		login.setBounds(148, 165, 93, 23);
		contentPane.add(login);
		
		JButton register = new JButton("注册");
		register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				RegisterUI registerFrame = new RegisterUI();
				registerFrame.setVisible(true);
			}
		});
		register.setBounds(32, 165, 93, 23);
		contentPane.add(register);
		
		JLabel label_1 = new JLabel("密码:");
		label_1.setBounds(63, 100, 36, 15);
		contentPane.add(label_1);
		
		password = new JPasswordField();
		password.setBounds(114, 97, 127, 21);
		contentPane.add(password);
	}

}
