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
package com.tny.game.net.command.plugins;

import com.tny.game.common.context.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

public class MessageSequenceCheckerPlugin implements VoidCommandPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    private static final AttrKey<Integer> CHECK_MESSAGE_ID = AttrKeys.key(MessageSequenceCheckerPlugin.class, "CHECK_MESSAGE_ID");

    @Override
    public void doExecute(Tunnel tunnel, Message message, RpcInvokeContext context) {
        if (!tunnel.isAuthenticated()) {
            return;
        }
        Endpoint endpoint = null;
        if (tunnel instanceof Endpoint) {
            endpoint = (Endpoint) tunnel;
        } else if (tunnel instanceof Tunnel) {
            endpoint = ((Tunnel) tunnel).getEndpoint();
        }
        Integer lastHandledId = endpoint.attributes().getAttribute(CHECK_MESSAGE_ID, 0);
        MessageHead head = message.getHead();
        if (head.getId() > lastHandledId) {
            endpoint.attributes().setAttribute(CHECK_MESSAGE_ID, lastHandledId);
        } else {
            LOGGER.warn("message [{}] is handled, the id of the last message handled is {}", message, lastHandledId);
            context.doneAndIntercept(NetResultCode.MESSAGE_HANDLED);
        }
    }

}
