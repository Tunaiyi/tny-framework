package com.tny.game.net.message;

import com.tny.game.common.result.*;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class DetectMessageHead extends AbstractNetMessageHead {

    private int protocol;
    private long time;

    public static DetectMessageHead ping() {
        return new DetectMessageHead(PING_PONG_PROTOCOL_NUM, MessageMode.PING);
    }

    public static DetectMessageHead pong() {
        return new DetectMessageHead(PING_PONG_PROTOCOL_NUM, MessageMode.PONG);
    }

    private DetectMessageHead(int protocol, MessageMode mode) {
        super(mode);
        this.time = System.currentTimeMillis();
        this.protocol = protocol;
    }

    @Override
    public int getProtocolNumber() {
        return protocol;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public int getCode() {
        return ResultCode.SUCCESS_CODE;
    }

    @Override
    public long getToMessage() {
        return MessageAide.EMPTY_MESSAGE_ID;
    }

    @Override
    public long getTime() {
        return time;
    }

}
