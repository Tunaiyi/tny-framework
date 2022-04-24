package com.tny.game.net.message;

import com.tny.game.common.result.*;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class TickMessageHead extends AbstractNetMessageHead {

    private final int protocol;

    private final long time;

    public static TickMessageHead ping() {
        return new TickMessageHead(PING_PONG_PROTOCOL_NUM, MessageMode.PING);
    }

    public static TickMessageHead pong() {
        return new TickMessageHead(PING_PONG_PROTOCOL_NUM, MessageMode.PONG);
    }

    private TickMessageHead(int protocol, MessageMode mode) {
        super(mode);
        this.time = System.currentTimeMillis();
        this.protocol = protocol;
    }

    @Override
    public void allotMessageId(long id) {
    }

    @Override
    public int getProtocolId() {
        return this.protocol;
    }

    @Override
    public int getLine() {
        return 0;
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
        return this.time;
    }

}
