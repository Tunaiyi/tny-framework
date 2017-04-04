package com.tny.game.net.tunnel;

import com.tny.game.net.session.NetSession;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface NetTunnel<UID> extends Tunnel<UID> {

    boolean bind(NetSession<UID> session);

    void ping();

    void pong();

    /**
     * 写出内容太
     *
     * @param content 写出事件
     */
    void write(TunnelContent<UID> content);

}
