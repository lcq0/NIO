//package com.itstack.netty;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelHandlerAdapter;
//import io.netty.channel.ChannelHandlerContext;
// 
//public class TimeClientHandler extends ChannelHandlerAdapter {
// 
//    private ByteBuf firstMessage;
// 
//    /**
//     * 初始化ByteBuf，初始化发送给服务端的消息
//     */
//    public TimeClientHandler(){
//        byte [] bytes = "query time order".getBytes();
//        firstMessage = Unpooled.buffer(bytes.length);
//        firstMessage.writeBytes(bytes);
//    }
// 
//    /**
//     * 连接服务端成功后开始发送消息
//     * @param ctx
//     * @throws Exception
//     */
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        //super.channelActive(ctx);
//        ctx.writeAndFlush(firstMessage);
//    }
// 
//    /**
//     * 读取客户端的返回消息
//     * @param ctx
//     * @param msg
//     * @throws Exception
//     */
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//       // super.channelRead(ctx, msg);
//        ByteBuf  buf = (ByteBuf) msg;
//        byte [] bytes = new byte[buf.readableBytes()];
//        buf.readBytes(bytes);
//        String time = new String(bytes, "UTF-8");
//        System.out.println("receive time from server, time = " + time);
//    }
// 
//    /**
//     * 发生异常时关闭ctx
//     * @param ctx
//     * @param cause
//     * @throws Exception
//     */
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        //super.exceptionCaught(ctx, cause);
//        ctx.close();
//    }
//}
