package com.tny.game.net.transport;


/**
 * Created by Kun Yang on 2017/3/18.
 */
public interface MessageEvent<UID> {

    /**
     * @return 所属通道ID
     */
    NetTunnel<UID> getTunnel();

}
