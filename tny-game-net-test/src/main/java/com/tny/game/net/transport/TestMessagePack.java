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
package com.tny.game.net.transport;

import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-09-27 14:42
 */
public class TestMessagePack {

    private RequestContent context;

    private NetMessage message;

    public TestMessagePack(MessageContent context, NetMessage message) {
        this.context = (RequestContent) context;
        this.message = message;
    }

    public NetMessage getMessage() {
        return this.message;
    }

    public MessageContent getContext() {
        return this.context;
    }

    public RequestContent getRequestContext() {
        return this.context;
    }

    public MessageMode getMode() {
        if (this.context != null) {
            return this.context.getMode();
        }
        if (this.message != null) {
            return this.message.getMode();
        }
        return null;
    }

}
