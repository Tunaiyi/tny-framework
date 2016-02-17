package com.tny.game.net.base;

public interface Message extends Protocol {

    /**
     * 请求Id
     *
     * @return 返回请求Id
     */
    int getID();

    /**
     * 传输类型 请求|响应
     *
     * @return
     */
    MessageType getMessage();

}
