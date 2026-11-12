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
package com.tny.game.net.command.plugins;

import com.tny.game.common.context.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
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
        Session session = tunnel.getSession();
        if (session == null) { // 未绑定会话按豁免处理（与未认证同路径）
            return;
        }
        Integer lastHandledId = session.attributes().getAttribute(CHECK_MESSAGE_ID, 0);
        MessageHead head = message.getHead();
        if (head.getId() > lastHandledId) {
            // 水位推进为本次放行消息的编号（原实现误写旧值导致防重放永不生效）
            session.attributes().setAttribute(CHECK_MESSAGE_ID, head.getId() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) head.getId());
        } else {
            LOGGER.warn("message [{}] is handled, the id of the last message handled is {}", message, lastHandledId);
            context.doneAndIntercept(NetResultCode.MESSAGE_HANDLED);
        }
    }

}
