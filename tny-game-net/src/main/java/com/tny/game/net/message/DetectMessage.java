package com.tny.game.net.message;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.tunnel.Tunnel;

/**
 * Created by Kun Yang on 2017/3/30.
 */
public class DetectMessage<UID> extends AbstractMessageHeader implements NetMessage<UID> {

    private Tunnel<UID> tunnel;
    private int protocol;
    private long time;
    private MessageMode mode;
    private Object head;

    public static <UID> NetMessage<UID> ping() {
        return new DetectMessage<>(PING_PROTOCOL_NUM, null, MessageMode.PING);
    }

    public static <UID> NetMessage<UID> ping(Object head) {
        return new DetectMessage<>(PING_PROTOCOL_NUM, head, MessageMode.PING);
    }

    public static <UID> NetMessage<UID> pong() {
        return new DetectMessage<>(PONG_PROTOCOL_NUM, null, MessageMode.PONG);
    }

    public static <UID> NetMessage<UID> pong(Object head) {
        return new DetectMessage<>(PONG_PROTOCOL_NUM, head, MessageMode.PONG);
    }

    private DetectMessage(int protocol, Object head, MessageMode mode) {
        this.time = System.currentTimeMillis();
        this.protocol = protocol;
        this.mode = mode;
        this.head = head;
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
    protected Object getHead() {
        return null;
    }

    @Override
    public boolean isHasHead() {
        return false;
    }

    @Override
    public <T> T getHead(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T getHead(ReferenceType<T> clazz) {
        return null;
    }

    @Override
    public <T> T getBody(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T getBody(ReferenceType<T> clazz) {
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
        return mode;
    }

    @Override
    public void sendBy(Tunnel<UID> tunnel) {
        this.tunnel = tunnel;
    }
}
