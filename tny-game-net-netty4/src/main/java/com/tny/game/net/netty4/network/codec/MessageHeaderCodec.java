package com.tny.game.net.netty4.network.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

@UnitInterface
public interface MessageHeaderCodec extends NetContentCodec<MessageHeader<?>> {

}