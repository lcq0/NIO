package com.itstack.service;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.itstack.netty.Lsy8583Util;



@Service
public class DataAnalysis {


	/**
	 * 解析数据包类
	 * @param buffer
	 * @param socket
	 */
	public void Analysis(byte[] buffer, Socket socket) {
		// TODO Auto-generated method stub
		System.out.println("收到线程发送的消息");
		try {
			OutputStream outputStream = socket.getOutputStream();
			InputStream inputStream = socket.getInputStream();
			
			//byte[] out = Parser.wrap(req);
			String input=new String (buffer);
			System.out.println("消息:"+input);
			
			Map back=Lsy8583Util.analyze8583(buffer);
			System.out.println("完成解析8583报文=="+back.toString()+"==");
			TreeMap filedMap=new TreeMap();//报文域
			filedMap.put("FIELD001", "0000");//交易码
			filedMap.put("FIELD002", "交易正常");//注意这个域是变长域!

			
			byte[] out=Lsy8583Util.make8583(filedMap);		
			
			//byte[] out = "收到线程发送的消息".getBytes("utf-8");

			outputStream.write(out);
			outputStream.flush();
			socket.close();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
 
	
}
