/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
