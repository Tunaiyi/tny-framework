package com.tny.game.net.message;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.tunnel.Tunnel;

/**
 * Created by Kun Yang on 2017/3/30.
 */
public class DetectMessage<UID> implements NetMessage<UID> {

    private Tunnel<UID> tunnel;
    private int protocol;
    private long time;
    private MessageMode messageMode;

    public static <UID> NetMessage<UID> ping() {
        return new DetectMessage<>(PING_PROTOCOL_NUM, MessageMode.PING);
    }

    public static <UID> NetMessage<UID> pong() {
        return new DetectMessage<>(PONG_PROTOCOL_NUM, MessageMode.PONG);
    }

    private DetectMessage(int protocol, MessageMode messageMode) {
        this.time = System.currentTimeMillis();
        this.protocol = protocol;
        this.messageMode = messageMode;
    }

    private DetectMessage() {
    }

    @Override
    public int getProtocol() {
        return protocol;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public long getSessionID() {
        return 0L;
    }

    @Override
    public UID getUserID() {
        return tunnel.getUID();
    }

    @Override
    public boolean isLogin() {
        return tunnel.isLogin();
    }

    @Override
    public String getUserGroup() {
        return tunnel.getUserGroup();
    }

    @Override
    public int getCode() {
        return ResultCode.SUCCESS_CODE;
    }

    @Override
    public int getToMessage() {
        return 0;
    }

    @Override
    public <T> T getBody(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T getBody(BodyClass<T> clazz) {
        return null;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getSign() {
        return null;
    }

    @Override
    public Attributes attributes() {
        return null;
    }

    @Override
    public MessageMode getMode() {
        return messageMode;
    }

    @Override
    public void sendBy(Tunnel<UID> tunnel) {
        this.tunnel = tunnel;
    }
}
