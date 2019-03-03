package com.yzb.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端处理通道.这里只是打印一下请求的内容，并不对请求进行任何的响应 DiscardServerHandler 继承自
 * ChannelHandlerAdapter， 这个类实现了ChannelHandler接口， ChannelHandler提供了许多事件处理的接口方法，
 * 然后你可以覆盖这些方法。 现在仅仅只需要继承ChannelHandlerAdapter类而不是你自己去实现接口方法。
 */
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static Map<String, ChannelHandlerContext> chan = new ConcurrentHashMap<>();
    public static Map<String, String> user = new ConcurrentHashMap<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel incoming = ctx.channel();

        // Broadcast a message to multiple Channels
        System.out.println("[SERVER] - " + incoming.id() + " 加入\n");
        System.out.println("[SERVER] - " + incoming.remoteAddress() + " 加入\n");

//        channels.add(ctx.channel());
        chan.put(incoming.id() + "", ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
        Channel incoming = ctx.channel();

        // Broadcast a message to multiple Channels
        System.out.println("[SERVER] - " + incoming.remoteAddress() + " 离开\n");

        // A closed Channel is automatically removed from ChannelGroup,
        // so there is no need to do "channels.remove(ctx.channel());"
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        //为ByteBuf分配四个字节
        ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt(1);
        ctx.writeAndFlush(time);
        Thread.sleep(2000);
    }


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //打印客户端输入，传输过来的的字符
        System.out.println(msg);

        ByteBuf in = (ByteBuf) msg;

        int i = 0;
        byte[] result = new byte[8];
        try {
            while (in.isReadable()) {
                byte b = in.readByte();
                System.out.println(toHexString1(b));
                result[i] = b;
                i++;
            }
        } catch (Exception e) {
            ctx.writeAndFlush("error");
        }


        ByteBuf buf = ctx.alloc().buffer(4);
        byte[] closeMsg = new byte[]{
                //A区头部
                (byte) 0xFF, (byte) 0x00,
                (byte) 0xAA,
                (byte) 0x00
        };


        buf.writeBytes(result);
        ctx.writeAndFlush(buf);
//            // 绑定用户
//            if (msg.toString().contains("#")) {
//                String userId = msg.toString().replace("#", "");
//                user.put(userId, "" + ctx.channel().id());
//                ctx.writeAndFlush("ok");
//            } else {
//                String[] tar = msg.toString().split(":");
//                try {
//                    String ch = user.get(tar[0]);
//                    ChannelHandlerContext target = chan.get(ch);
//                    target.writeAndFlush(tar[1]);
//                } catch (Exception e) {
//                    ctx.writeAndFlush("not on line");
//                }
//            }

    }

    /**
     * 客户端 失去连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    private int loss_connect_time = 0;

    /**
     * 心跳机制  用户事件触发
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            if (state == IdleState.READER_IDLE) {
                ctx.writeAndFlush("no client");
                loss_connect_time++;
                log.info(String.valueOf(10 * loss_connect_time) + "秒没有接收到客户端的信息了");
                if (loss_connect_time >= 100) {
                    log.info("------------服务器主动关闭客户端链路");
                    ctx.channel().close();
                }
            } else if (state == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush("no server link");
            }


        } else {
            super.userEventTriggered(ctx, evt);
        }

    }

    /***
     * 这个方法会在发生异常时触发
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * exceptionCaught() 事件处理方法是当出现 Throwable 对象才会被调用，即当 Netty 由于 IO
         * 错误或者处理器在处理事件时抛出的异常时。在大部分情况下，捕获的异常应该被记录下来 并且把关联的 channel
         * 给关闭掉。然而这个方法的处理方式会在遇到不同异常的情况下有不 同的实现，比如你可能想在关闭连接之前发送一个错误码的响应消息。
         */
        // 出现异常就关闭
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * 数组转成十六进制字符串
     *
     * @param b
     * @return
     */
    public static String toHexString(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }


    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

}
