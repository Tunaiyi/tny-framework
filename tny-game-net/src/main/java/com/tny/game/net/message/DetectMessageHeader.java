package com.tny.game.net.message;

import com.tny.game.common.result.ResultCode;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class DetectMessageHeader extends AbstractNetMessageHeader {

    private int protocol;
    private long time;

    public static DetectMessageHeader ping() {
        return new DetectMessageHeader(PING_PONG_PROTOCOL_NUM, MessageMode.PING);
    }

    public static DetectMessageHeader pong() {
        return new DetectMessageHeader(PING_PONG_PROTOCOL_NUM, MessageMode.PONG);
    }

    private DetectMessageHeader(int protocol, MessageMode mode) {
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
        return MessageAide.REQUEST_TO_MESSAGE_ID;
    }

    @Override
    protected Object getAttachment() {
        return null;
    }

    @Override
    public boolean isHasAttachment() {
        return false;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public NetMessageHeader setId(long id) {
        return this;
    }
}
