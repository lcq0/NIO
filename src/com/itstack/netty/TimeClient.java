//package com.itstack.netty;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
// 
///**
// * netty的客户端
// */
// 
//public class TimeClient {
// 
//    public void connect(String host, int port){
// 
//        //NioEventLoopGroup是一个线程组，它包含了一组NIO线程
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            //客户端辅助启动类
//            Bootstrap b = new Bootstrap();
// 
//            //设置线程组
//            b.group(group)
//                    .channel(NioSocketChannel.class)//设置Channel
//                    .option(ChannelOption.TCP_NODELAY, true)//设置TCP的参数
//                    .handler(new ChannelInitializer<SocketChannel>() {//匿名内部类设置handler
//                        @Override
//                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            socketChannel.pipeline().addLast(new TimeClientHandler());
//                        }
//                    });
//            //异步连接客户端，同步阻塞直到连接成功
//        ChannelFuture f = b.connect(host, port).sync();
//            //阻塞，等待客户端链路关闭后main函数才退出
//        f.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }finally {
//            group.shutdownGracefully();
//        }
//    }
// 
//    public static void main(String[] args) {
//        String host = "127.0.0.1";
//        int port = 8080;
//        new TimeClient().connect(host, port);
//    }
//}
// 
