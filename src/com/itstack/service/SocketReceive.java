package com.itstack.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class SocketReceive implements Runnable {
	private Socket socket;
	private Vector<Socket> allclients;

	public SocketReceive() {
	}

	public SocketReceive(Socket socket, Vector<Socket> clients) {
		this.socket = socket;
		allclients = clients;
	}

	@Override
	public void run() {
		while (true) {
			if (null == socket) {
				if (allclients.contains(socket)) {
					allclients.remove(socket);
				}
				return;
			}
			boolean isClosed = socket.isClosed();
			if (isClosed) {
				if (allclients.contains(socket)) {
					allclients.remove(socket);
				}
				return;
			}
			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());
				byte[] buffer = new byte[1024 * 4];
				if (in.read(buffer) == -1) {
					if (allclients.contains(socket)) {
						allclients.remove(socket);
					}
					return;
				}
				//DataAnalysis dataanalysis = SpringUtil.getBean(DataAnalysis.class); 
				DataAnalysis dataanalysis = new DataAnalysis();
				dataanalysis.Analysis(buffer, socket);

			} catch (IOException e) {
				System.out.println(e);
				if (allclients.contains(socket)) {
					allclients.remove(socket);
				}
				try {
					socket.close();
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}