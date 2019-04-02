package com.tny.game.net.codec;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.message.*;

@UnitInterface
public interface MessageCodec<UID> {

    Message<UID> decode(byte[] bytes, MessageFactory<UID> factory) throws Exception;

    byte[] encode(Message<UID> message) throws Exception;

}
