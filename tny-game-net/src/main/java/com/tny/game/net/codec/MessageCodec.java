package com.tny.game.net.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

@UnitInterface
public interface MessageCodec {

    Message decode(byte[] bytes, MessageFactory factory) throws Exception;

    byte[] encode(Message message) throws Exception;

}
