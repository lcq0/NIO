package com.itstack.Multiplex;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.itstack.netty.Lsy8583Util;

/**
 *
 */
public class NIOCLient1 {
	
	/*标识数字*/
    private static int flag = 0;
    private static final String host = "localhost";
    private static final int port = 9588;
    private Selector selector;

    public static void main(String[] args){
//    	NIOCLient1 client = new NIOCLient1();
//    	client.initHeartbeat();
        for (int i=0;i<3;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NIOCLient1 client = new NIOCLient1();
                    client.connect(host, port);
                    client.listen();
                }
            }).start();
        }
    }

    public void connect(String host, int port) {
        try {
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            this.selector = Selector.open();
            sc.register(selector, SelectionKey.OP_CONNECT);
            sc.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        while (true) {
            try {
                int events = selector.select();
                if (events > 0) {
                    Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                    while (selectionKeys.hasNext()) {
                        SelectionKey selectionKey = selectionKeys.next();
                        selectionKeys.remove();
                        //连接事件
                        if (selectionKey.isConnectable()) {
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            if (socketChannel.isConnectionPending()) {
                                socketChannel.finishConnect();
                            }

                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            socketChannel.write(ByteBuffer.wrap(("Hello this is from Client " + Thread.currentThread().getName()).getBytes()));
                        } else if (selectionKey.isReadable()) {
                            SocketChannel sc = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int count = sc.read(buffer);
                            if (count>0) {
								String recText = new String( buffer.array(),0,count);
								buffer.flip();
								System.out.println("收到服务端的数据("+Thread.currentThread().getName()+")："+recText);
							}
                            
                            if ("Thread-1".equalsIgnoreCase(Thread.currentThread().getName())) {
                            	System.out.println("关闭线程1");
								selectionKey.cancel();
								sc.close();
							}else {
								if (flag>3&&"Thread-2".equalsIgnoreCase(Thread.currentThread().getName())) {
									System.out.println("关闭线程2");
									selectionKey.cancel();
									sc.close();
								}else if (flag>5&&"Thread-0".equalsIgnoreCase(Thread.currentThread().getName())) {
									System.out.println("关闭线程0");
									selectionKey.cancel();
									sc.close();
								}else {
									sc.register(selector, SelectionKey.OP_WRITE);
								}
							}
                            
                        }else if (selectionKey.isWritable()) {
                            SocketChannel sc = (SocketChannel) selectionKey.channel();
                            
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            buffer.put(ByteBuffer.wrap(("hei this is from Client "+ (flag++) + " " + Thread.currentThread().getName()).getBytes()));
                            //将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位
                            buffer.flip();
                            sc.write(buffer);
                            sc.register(selector, SelectionKey.OP_READ);
                        }
                    }
                    try {
        				Thread.sleep(3000);
        			} catch (InterruptedException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void initHeartbeat(){
		Timer hearttimer = new Timer();
		TimerTask hearttask = new TimerTask() {
			@Override
			public void run() {
				//心跳检测
				System.out.println("发送心跳报文");
				NIOCLient1 client = new NIOCLient1();
                client.connect(host, port);
                client.listen();
			}
		};

		hearttimer.schedule(hearttask, 3000, 1000);
	}
}

