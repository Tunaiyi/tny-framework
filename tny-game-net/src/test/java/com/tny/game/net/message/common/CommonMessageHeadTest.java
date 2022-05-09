package com.tny.game.net.message.common;

import com.tny.game.net.message.*;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class CommonMessageHeadTest extends MessageHeadTest {

    @Override
    public MessageHead create(long id, MessageMode mode, int protocol, int code, long time, long toMessage) {
        return new CommonMessageHead(id, mode, protocol, 0, code, toMessage, time, null);
    }

}
