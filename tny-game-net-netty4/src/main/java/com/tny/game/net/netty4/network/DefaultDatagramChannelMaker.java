package com.tny.game.net.netty4.network;

import com.tny.game.net.netty4.network.codec.*;
import io.netty.channel.Channel;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 1:48 上午
 */
public class DefaultDatagramChannelMaker<C extends Channel> extends DatagramChannelMaker<C> {

    public DefaultDatagramChannelMaker() {
    }

    public DefaultDatagramChannelMaker(DatagramPackEncoder encoder, DatagramPackDecoder decoder) {
        super(encoder, decoder);
    }

}
