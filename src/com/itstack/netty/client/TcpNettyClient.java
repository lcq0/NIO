package com.itstack.netty.client;

import com.alibaba.fastjson.JSONObject;
import com.itstack.netty.bean.ClientRequest;
import com.itstack.netty.bean.DefaultFuture;
import com.itstack.netty.bean.Response;
import com.itstack.netty.bean.User;
import com.itstack.netty.initialzer.TcpClientInitalizer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TcpNettyClient  {
	private static int reconnectTime = 0;
	private static String ip1[] = "localhost:9588".split(":");
	private static String ip2[] = "192.168.3.26:9588".split(":");
	
    static EventLoopGroup group =null;
    static Bootstrap client =null;
    public static ChannelFuture future=null;
    private static ChannelFutureListener channelFutureListener = null;
    public static boolean doConnect(final String ip, final int port) {
    	group = new NioEventLoopGroup();
        client = new Bootstrap();
        client.group(group);
        client.channel(NioSocketChannel.class);
        client.option(ChannelOption.SO_KEEPALIVE,true);
        client.handler(new TcpClientInitalizer());
        client.remoteAddress(ip, port);
        
        channelFutureListener = new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				reconnectTime++;
				if (f.isSuccess()) {
					System.out.println("服务端链接成功...:"+future.channel().remoteAddress().toString());
				}else {
					 f.channel().eventLoop().schedule(new Runnable() {  
	                        @Override  
	                        public void run() {  
	                        	if (reconnectTime!=1&&(reconnectTime-1)%3==0) {
	                        		if (!ip.equalsIgnoreCase(ip1[0])||port!=Integer.parseInt(ip1[1])) {
	                        			System.err.println("客户端断线重连失败,每隔2s重试一次(每重试三次则更换另一个IP地址),当前ip端口："+ip1[0]+":"+ip1[1]+",已经重试次数："+reconnectTime);
	                        			doConnect(ip1[0], Integer.parseInt(ip1[1])); 
	                        		}else {
	                        			System.err.println("客户端断线重连失败,每隔2s重试一次(每重试三次则更换另一个IP地址),当前ip端口："+ip2[0]+":"+ip2[1]+",已经重试次数："+reconnectTime);
	                        			doConnect(ip2[0], Integer.parseInt(ip2[1])); 
									}
	                        	}else {
	                        		System.err.println("客户端断线重连失败,每隔2s重试一次(每重试三次则更换另一个IP地址),当前ip端口："+ip+":"+port+",已经重试次数："+reconnectTime);
	                        		doConnect(ip, port);
								}
	                        }  
	                    }, 2, TimeUnit.SECONDS);  
				}
			}
		};
        
        try {
            future = client.connect(ip, port);
            future.addListener(channelFutureListener);
            future.awaitUninterruptibly();
//            future.sync();
            if (!future.isSuccess()) {
				return false;
			}
        }/* catch (InterruptedException e) {
          System.err.println("客户端启动异常1："+e.getMessage());
        }*/ catch (Exception e) {
        	System.err.println("客户端启动异常2："+e.getMessage());
		}
    	
    	return true;
	}
    //发送数据的方法
    public static Object send(ClientRequest request){
        try{
        	if (future.isSuccess()) {
        		System.out.println("客户端向服务端发送请求数据:"+JSONObject.toJSONString(request));
                
                //客户端直接发送请求数据到服务端
                  future.channel().writeAndFlush(JSONObject.toJSONString(request));
      			//根据\r\n进行换行
                  future.channel().writeAndFlush("ETX");
      			//通过请求实例化请求和响应之间的关系
                  DefaultFuture defaultFuture = new DefaultFuture(request);
      			//通过请求ID，获取对应的响应处理结果
                  Response response = defaultFuture.get(10);
                  return response;
			}else {
//				future.awaitUninterruptibly(5*1000);
				System.err.println("发送数据过程中，客户端与服务端断开链接："+JSONObject.toJSONString(request));
			}
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 模拟用户并发请求
     */
    public static void main(String[] args) {
    	if (doConnect(ip1[0],Integer.parseInt(ip1[1]))) {
    		
    		for(int i=0;i<30;i++){
                new Thread(new UserRequestThread(i)).start();//模拟多线程并发请求
            }
		}
    }
    
    static  class UserRequestThread implements Runnable{
        private int requestId;
        public UserRequestThread(int requestId){
            this.requestId = requestId;
        }

        public void run() {
           synchronized (UserRequestThread.class){
               ClientRequest request = new ClientRequest();
               request.setCommand("saveUser");
               User user = new User();
               user.setAge(new Random().nextInt(4)*requestId);
               user.setId(requestId);
               user.setName("jiahp"+requestId);
               request.setContent(user);
               //拿到请求的结果
               Object result = TcpNettyClient.send(request);
               System.out.println("客户端长连接测试返回结果:"+JSONObject.toJSONString(result));
               System.out.println("      ");
           }
        }
    }

}
