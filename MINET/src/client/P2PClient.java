package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import server.MultiThreadServer;

import net.sf.json.util.JSONStringer;

public class P2PClient {
    
	private Socket socket = null;
	private int port = 8888;
	private final String HAND_SHAKE = "MINET localhost\t\n";    //握手命令
	
	//输入参数为json格式的message
	public P2PClient(String message, String desIP) throws UnknownHostException, IOException{
		/***************构建JSON:******************/
		/*{
			sender_name: sender_name,
			des_ip: ip,
			msg: message,
			sendTime: time
		}*/
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String sendTime = df.format(new Date());
		System.out.println(sendTime);// new Date()为获取当前系统时间
		
		JSONStringer js = new JSONStringer();
		js.object();
		js.key("sender_name").value(LoginUI.user.getUserName());
		js.key("des_ip").value(desIP);
		js.key("msg").value(message);
		js.key("send_time").value(sendTime);
		js.endObject();
		String json = js.toString() + "\n";
		/********************************************/
		socket = new Socket(desIP, port);
		//获得socket的输出流
		OutputStream socketOut = socket.getOutputStream();
		//发送消息
		socketOut.write(json.getBytes());
		//接收服务器的响应
		BufferedReader br = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
		//输出返回信息
		String msg = null;
		msg = br.readLine();
		/******若成功发送,修改ouput框,否则提示错误******/
		if(msg.equals("successful")){
			String out = MultiThreadServer.msgUIMap.get(desIP).output.getText();
			out += LoginUI.user.getUserName() + "\t";
			out += sendTime + "\n  ";
			out += message + "\n";
			MultiThreadServer.msgUIMap.get(desIP).output.setText(out);
			//设置光标为最后一行
			MultiThreadServer.msgUIMap.get(desIP).output.setCaretPosition(
					MultiThreadServer.msgUIMap.get(desIP).output.getDocument().getLength());
		}
		else{
			JOptionPane.showMessageDialog(null, "发送失败,请重新发送.");
		}
		/**************************************/
	}

}