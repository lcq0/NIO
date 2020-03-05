package com.itstack.netty.handler;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.itstack.netty.bean.Constants;
import com.itstack.netty.bean.DefaultFuture;
import com.itstack.netty.bean.Response;
import com.itstack.netty.client.TcpNettyClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;


public class TcpClientHandler extends ChannelInboundHandlerAdapter {

	private int heartTimes = 0;
	private String heartBeatMsg ="ping";//心跳包
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	//判断服务端和客户端是在能够正常通信的情况下
    	System.out.println("客户端获取到服务端响应数据:"+msg.toString());
        if(msg.toString().equalsIgnoreCase(heartBeatMsg)){ 
//            ctx.channel().writeAndFlush(heartBeatMsg);
//            ctx.channel().writeAndFlush("ETX");
        	System.out.println("客服端收单服务端心跳响应\r\n");
            return ;
        }
        
        String str = getJSONObject(msg.toString()).toString();
      //读取服务端的响应结果
        Response res = JSONObject.parseObject(str, Response.class);
        //存储响应结果
        DefaultFuture.recive(res);
        
    }
    
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    		throws Exception {
    	super.userEventTriggered(ctx, evt);
    	if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case ALL_IDLE:
                    //读写空闲时间15s，发送心跳
                    ctx.writeAndFlush(heartBeatMsg);
                    ctx.writeAndFlush("ETX");
                    System.out.println("客户端发送心跳(读写空闲时间15s)----------"+(++heartTimes));
                    if (heartTimes==Integer.MAX_VALUE-1) {
                    	heartTimes=0;
					}
                    break;
                case WRITER_IDLE:
                	//写空闲时间15s，发送心跳
                    ctx.writeAndFlush(heartBeatMsg);
                    ctx.writeAndFlush("ETX");
                    System.out.println("客户端发送心跳(写空闲时间15s)----------"+(++heartTimes));
                    if (heartTimes==Integer.MAX_VALUE-1) {
                    	heartTimes=0;
					}
                    break;
                default:
                    break;
            }
        }
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	super.channelActive(ctx);
    	System.out.println("初始化链接成功："+ctx.channel().remoteAddress().toString());
    	
    	ctx.writeAndFlush(heartBeatMsg);
        ctx.writeAndFlush("ETX");
        System.out.println("客户端发送登入操作！");
    	
        initHeartbeat(ctx);
    }
    
    /**
	 * 心跳
	 */
	private void initHeartbeat(final ChannelHandlerContext ctx){
		Timer hearttimer = new Timer();
		TimerTask hearttask = new TimerTask() {
			@Override
			public void run() {
				ctx.writeAndFlush(heartBeatMsg);
		        ctx.writeAndFlush("ETX");
		        System.out.println("客户端发送心跳(读写空闲时间15s)----------"+(++heartTimes));
			}
		};

		hearttimer.schedule(hearttask, 1*1000, 15*1000);
	}
    
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	TcpNettyClient.future=null;
    	System.err.println(ctx.channel().remoteAddress().toString());
    	//重新连接服务器  
        ctx.channel().eventLoop().schedule(new Runnable() {  
            @Override  
            public void run() {  
            	System.out.println("使用过程中服务端断开链接，开始重连操作...");
            	TcpNettyClient.doConnect(Constants.ip1, Constants.port);  
            }  
        }, 2, TimeUnit.SECONDS);  
//        ctx.close();
        super.channelInactive(ctx);
    }

    private JSONObject getJSONObject(String str){
        JSONObject json = JSONObject.parseObject(str);
        json.remove("content");
        json.put("msg","保存用户信息成功");
        return json;
    }
}