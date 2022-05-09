package com.tny.game.net.netty4.relay;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.netty4.relay.codec.*;
import io.netty.channel.Channel;

@UnitInterface
public class DefaultRelayChannelMaker<C extends Channel> extends RelayPackChannelMaker<C> {

    public DefaultRelayChannelMaker() {
    }

    public DefaultRelayChannelMaker(RelayPacketEncoder encoder, RelayPacketDecoder decoder) {
        super(encoder, decoder);
    }

    @Override
    protected void postInitChannel(C channel) {

    }

}
