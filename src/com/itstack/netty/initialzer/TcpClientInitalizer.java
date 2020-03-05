package com.itstack.netty.initialzer;

import java.util.concurrent.TimeUnit;

import com.itstack.netty.handler.TcpClientHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpClientInitalizer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //按照\r\n进行解码
    	ByteBuf delimiter = Unpooled.copiedBuffer("ETX".getBytes());
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(2048,delimiter));
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new StringEncoder());
//        ch.pipeline().addLast(new IdleStateHandler(15, 15, 15, TimeUnit.SECONDS));
        ch.pipeline().addLast(new TcpClientHandler());
    }
}
