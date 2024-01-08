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

package com.tny.game.net.netty4.network;

import com.tny.game.net.application.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/19 4:30 下午
 */
public class TestGeneralClientTunnel extends GeneralClientTunnel {

    public TestGeneralClientTunnel(long id, MessageTransport transport, NetSession session, NetworkContext context) {
        super(id, transport, session, context, null);
    }


    @Override
    protected MessageTransport getTransport() {
        return super.getTransport();
    }

}
