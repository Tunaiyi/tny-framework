package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author kgtny
 * @date 2024/1/5 01:56
 **/
public interface TunnelUnavailableWatch {


    /**
     * @param tunnel 失效
     */
    void onUnavailable(NetTunnel tunnel);

}
