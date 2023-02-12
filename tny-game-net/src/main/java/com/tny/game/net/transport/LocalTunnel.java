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

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.rpc.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/21 17:39
 **/
public class LocalTunnel<UID> extends BaseNetTunnel<UID, NetSession<UID>, LocalMessageTransporter> {

    public LocalTunnel(long id, NetSession<UID> session, LocalMessageTransporter transporter, NetAccessMode accessMode, NetworkContext context) {
        super(id, transporter, accessMode, context);
        this.bind(session);
    }

    @Override
    protected boolean replaceEndpoint(NetEndpoint<UID> endpoint) {
        return false;
    }

    @Override
    protected boolean onOpen() {
        return true;
    }

}