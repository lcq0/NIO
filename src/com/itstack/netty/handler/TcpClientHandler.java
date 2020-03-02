package com.itstack.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.itstack.netty.bean.DefaultFuture;
import com.itstack.netty.bean.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;


public class TcpClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	//判断服务端和客户端是在能够正常通信的情况下
        if(msg.toString().equals("ping")){ 
            ctx.channel().writeAndFlush("ping");
            ctx.channel().writeAndFlush("EXT");
            return ;
        }
        System.out.println("客户端获取到服务端响应数据:"+msg.toString());

        String str = getJSONObject(msg.toString()).toString();
      //读取服务端的响应结果
        Response res = JSONObject.parseObject(str, Response.class);
        //存储响应结果
        DefaultFuture.recive(res);
        
    }

    private JSONObject getJSONObject(String str){
        JSONObject json = JSONObject.parseObject(str);
        json.remove("content");
        json.put("msg","保存用户信息成功");
        return json;
    }
}