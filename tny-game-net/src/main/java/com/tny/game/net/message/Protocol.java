package com.tny.game.net.message;

public interface Protocol {

    int PING_PONG_PROTOCOL_NUM = 0;

    /**
     * @return 协议号
     */
    int getProtocolNumber();

    /**
     * 指定消息是否是属于此协议
     *
     * @param head 消息头
     * @return
     */
    default boolean isOwn(MessageHead head) {
        return this.getProtocolNumber() == head.getProtocolNumber();
    }

    /**
     * 指定消息是否是属于此协议
     *
     * @param subject 消息
     * @return
     */
    default boolean isOwn(MessageSubject subject) {
        return this.getProtocolNumber() == subject.getProtocolNumber();
    }

}
