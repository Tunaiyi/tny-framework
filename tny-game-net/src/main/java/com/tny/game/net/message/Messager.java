package com.tny.game.net.message;

import com.tny.game.net.base.*;

/**
 * 通讯者(消息发送接受者)
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/21 04:17
 **/
public interface Messager {

    long getMessagerId();

    MessagerType getMessagerType();

}