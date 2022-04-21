package com.tny.game.net.message;

/**
 * 通讯者(消息发送接受者)
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/21 04:17
 **/
public interface Messager {

    long getIdentity();

    MessagerType getType();

}