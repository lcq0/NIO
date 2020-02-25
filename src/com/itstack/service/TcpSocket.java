package com.itstack.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component
public class TcpSocket implements Runnable {
	private Integer Port;
	private ServerSocket Server;
	private ExecutorService ThreadPool;
	public Vector<Socket> AllClients;

	public TcpSocket() {
		try {
			AllClients = new Vector<>();
			AllClients.clear();
			Port = 9588;
			ThreadPool = Executors.newCachedThreadPool();
			Server = new ServerSocket(Port);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = Server.accept();
				if (socket != null) {
					SocketReceive socketReceive = new SocketReceive(socket, AllClients);
					ThreadPool.submit(socketReceive);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}