package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 17:45
 */
public interface Transporter<UID> extends Connection, Transport<UID> {

    /**
     * 通道通道
     *
     * @param tunnel 通道通道
     */
    void bind(NetTunnel<UID> tunnel);

}
