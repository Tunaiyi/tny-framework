package com.tny.game.net.message;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.tunnel.Tunnel;

/**
 * Created by Kun Yang on 2017/3/30.
 */
public class DetectMessage<UID> extends AbstractNetMessage<UID> implements NetMessage<UID> {

    private Tunnel<UID> tunnel;

    public static <UID> NetMessage<UID> ping() {
        return new DetectMessage<>(DetectMessageHeader.ping());
    }

    public static <UID> NetMessage<UID> pong() {
        return new DetectMessage<>(DetectMessageHeader.pong());
    }

    private DetectMessage(MessageHeader header) {
        this.setHeader(header);
    }

    @Override
    public long getSessionID() {
        return 0L;
    }

    @Override
    public UID getUserID() {
        return tunnel.getUid();
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
    public Attributes attributes() {
        throw new UnsupportedOperationException("DetectMessage unsupported attributes");
    }

    @Override
    public void sendBy(Tunnel<UID> tunnel) {
        this.tunnel = tunnel;
    }

    @Override
    protected Object getBody() {
        return null;
    }

    @Override
    protected AbstractNetMessage<UID> setBody(Object body) {
        return null;
    }
}
