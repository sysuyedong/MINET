package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RegisterUI extends JFrame {

	private JPanel contentPane;
	private JTextField username;
	private JTextField email;
	private JPasswordField password;
	private JPasswordField passwordAgain;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegisterUI frame = new RegisterUI();
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
	public RegisterUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 362, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel username_label = new JLabel("用户名:");
		username_label.setBounds(36, 73, 54, 15);
		contentPane.add(username_label);
		
		JLabel password_label = new JLabel("密码:");
		password_label.setBounds(48, 110, 54, 15);
		contentPane.add(password_label);
		
		JLabel email_label = new JLabel("邮箱:");
		email_label.setBounds(48, 179, 54, 15);
		contentPane.add(email_label);
		
		username = new JTextField();
		username.setBounds(117, 70, 159, 21);
		contentPane.add(username);
		username.setColumns(10);
		
		email = new JTextField();
		email.setBounds(117, 176, 159, 21);
		contentPane.add(email);
		email.setColumns(10);
		
		JLabel label_3 = new JLabel("注册新用户");
		label_3.setFont(new Font("宋体", Font.BOLD, 16));
		label_3.setBounds(36, 25, 93, 15);
		contentPane.add(label_3);
		
		JButton yes = new JButton("确定");
		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*********检测格式是否正确,发送注册报文信息,得到响应*******/
				String name = username.getText();
				String psd = password.getText();
				if(!psd.equals(passwordAgain.getText())){
					System.out.println("Password doesn't match!!");
				}
				String em = email.getText();
				System.out.println("Info:" + name + " " + psd + " " + em);
				User user = new User();
				boolean isRegister =user.register(name, psd, em);
				/******************************************/
				if(isRegister){
					dispose();
					LoginUI loginFrame = new LoginUI();
					loginFrame.setVisible(true);
				}
				else{
					System.out.println("Register failed!!");
					JOptionPane.showMessageDialog(null, "用户名已存在.");
				}
			}
		});
		yes.setBounds(48, 229, 93, 23);
		contentPane.add(yes);
		
		JButton cancel = new JButton("取消");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				LoginUI loginFrame = new LoginUI();
				loginFrame.setVisible(true);
			}
		});
		cancel.setBounds(207, 229, 93, 23);
		contentPane.add(cancel);
		
		password = new JPasswordField();
		password.setBounds(117, 107, 159, 21);
		contentPane.add(password);
		
		JLabel label = new JLabel("确认密码:");
		label.setBounds(23, 145, 67, 15);
		contentPane.add(label);
		
		passwordAgain = new JPasswordField();
		passwordAgain.setBounds(117, 142, 159, 21);
		contentPane.add(passwordAgain);
	}

}
