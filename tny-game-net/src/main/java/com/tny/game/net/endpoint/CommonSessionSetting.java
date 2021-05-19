package com.tny.game.net.endpoint;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/17 4:45 下午
 */
public class CommonSessionSetting implements SessionSetting {

    private int sendMessageCachedSize = 0;

    @Override
    public int getSendMessageCachedSize() {
        return this.sendMessageCachedSize;
    }

    public CommonSessionSetting setSendMessageCachedSize(int sendMessageCachedSize) {
        this.sendMessageCachedSize = sendMessageCachedSize;
        return this;
    }

}
