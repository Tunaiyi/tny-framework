package com.tny.game.net.message;

/**
 * Created by Kun Yang on 2018/7/17.
 */
public interface MessageHead extends MessageSchema {

    /**
     * @return 返回请求Id
     */
    long getId();

    /**
     * @return 消息响应码
     */
    int getCode();

    /**
     * 获取请求时间
     *
     * @return 返回请求时间
     */
    long getTime();

}
