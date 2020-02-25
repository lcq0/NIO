package com.itstack.service;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestInitializingBean222 {
	@Autowired
	private TcpSocket tcpsocket;

	@PostConstruct // 无效的启动标签
	public void initService() {

		// 为了颜色一致，我们用管理Err输出
		System.out.println("---------- 启动Socket线程池       ---------- ");

		// TCPServer.moreserver(1234);

		Thread serverthread = new Thread(tcpsocket);
		serverthread.start();
		// Lsy8583Util.exeMain();
	}

	// @Override
	// public void afterPropertiesSet() throws Exception {
	//
	// //为了颜色一致，我们用管理Err输出
	// System.err.println("---------- Spring自动加载 ---------- ");
	// System.err.println("---------- 启动Netty线程池 ---------- ");
	// System.out.println("---------- 启动Netty线程池 ---------- ");
	//
	// // TCPServer.moreserver(1234);
	// }

}
