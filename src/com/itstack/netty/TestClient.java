package com.itstack.netty;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.TreeMap;
 

public class TestClient {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		exeMain() ;
	}
	
	public static void exeMain() {
		try {
			//***********************组装8583报文测试--start***********************//
			TreeMap filedMap=new TreeMap();//报文域
			filedMap.put("FIELD003", "1799");//交易码
			filedMap.put("FIELD013", "2013-11-06");//交易日期
			filedMap.put("FIELD008", "12345678901");//账号
			filedMap.put("FIELD033", "aa索隆bb");//注意这个域是变长域!
			filedMap.put("FIELD036", "123456");//注意这个域是变长域!
			
			byte[] send=Lsy8583Util.make8583(filedMap);
			System.out.println("完成组装8583报文=="+new String(send,"UTF-8")+"==");
			//***********************组装8583报文测试--end***********************//
			
			Socket socket = new Socket("127.0.0.1", 9588);
			OutputStream outputStream = socket.getOutputStream();
			InputStream inputStream = socket.getInputStream();

	

			outputStream.write(send);
			outputStream.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
