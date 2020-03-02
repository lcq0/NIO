package com.itstack.netty.initialzer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.itstack.netty.handler.TcpServerHandler;

public class TcpServerInitalizer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ByteBuf delimiter = Unpooled.copiedBuffer("EXT".getBytes());
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(2048,delimiter));
        //添加客户端和服务端之间的心跳检查状态
        ch.pipeline().addLast(new IdleStateHandler(6, 2, 1, TimeUnit.SECONDS));
        ch.pipeline().addLast(new TcpServerHandler());
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new StringEncoder());
	}
}
