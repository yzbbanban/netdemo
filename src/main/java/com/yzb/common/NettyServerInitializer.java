package com.yzb.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author wangban
 * @date 14:22 2019/2/27
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //服务端心跳检测
        pipeline.addLast(new IdleStateHandler(
                20,
                20,
                20,
                TimeUnit.SECONDS));
        //粘包拆包处理
        ByteBuf delimiter = Unpooled.copiedBuffer("&&&".getBytes());
//解码的帧的最大长度为：2048；解码时是否去掉分隔符：false；解码分隔符：&&&
        pipeline.addLast(new DelimiterBasedFrameDecoder(2048,false,delimiter));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        // Handler 处理
        pipeline.addLast(new DiscardServerHandler());
    }
}
