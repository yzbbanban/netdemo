package com.yzb.domain;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangban
 * @date 17:51 2019/2/27
 */
public class OnlineUser {
    /**
     * 用户表
     */
    private static Map<Integer, ChannelHandlerContext> onlineUser = new ConcurrentHashMap<Integer, ChannelHandlerContext>();

    public static void put(Integer uid, ChannelHandlerContext uchc) {
        onlineUser.put(uid, uchc);
    }

    public static void remove(Integer uid) {
        onlineUser.remove(uid);
    }

    public static ChannelHandlerContext get(Integer uid) {
        return onlineUser.get(uid);
    }
}
