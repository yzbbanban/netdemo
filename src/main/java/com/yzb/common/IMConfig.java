package com.yzb.common;

/**
 * @author wangban
 * @date 17:30 2019/2/27
 */
public interface IMConfig {
    /**
     * 客户端配置 版本号
     */
    int CLIENT_VERSION = 1;
    /**
     * 服务端配置 服务器IP
     */
    String SERVER_HOST = "127.0.0.1";
    /**
     * 服务器端口
     */
    int SERVER_PORT = 9090;
    /**
     * 消息相关
     * 表示服务器消息
     */
    int SERVER_ID = 0;
    /**
     * 即时通信应用ID为1
     */
    byte APP_IM = 1;
    /**
     * 连接后第一次消息确认建立连接和发送认证信息
     */
    byte TYPE_CONNECT = 0;
    /**
     * 文本消息
     */
    byte TYPE_MSG_TEXT = 1;
    /**
     * 空消息
     */
    String MSG_EMPTY = "";

}
