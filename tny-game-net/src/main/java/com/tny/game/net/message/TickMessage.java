/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.message;

import com.tny.game.common.context.*;

/**
 * Created by Kun Yang on 2017/3/30.
 */
public final class TickMessage extends AbstractNetMessage implements NetMessage, Message {

    private static final TickMessage PING = new TickMessage(TickMessageHead.ping());

    private static final TickMessage PONG = new TickMessage(TickMessageHead.pong());

    public static NetMessage ping() {
        return PING;
    }

    public static NetMessage pong() {
        return PONG;
    }

    private TickMessage(NetMessageHead head) {
        super(head);
    }

    @Override
    public Attributes attributes() {
        throw new UnsupportedOperationException("DetectMessage unsupported attributes");
    }

    @Override
    public long getToMessage() {
        return 0;
    }

}
