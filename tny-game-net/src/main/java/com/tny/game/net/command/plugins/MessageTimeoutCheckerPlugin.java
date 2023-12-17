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

import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

/**
 * 检查消息超时Plugin
 * 参数 为超时时间ms:
 *
 * 3000 3秒
 * <p>
 * Created by Kun Yang on 2017/3/4.
 */
public class MessageTimeoutCheckerPlugin implements CommandPlugin<Long> {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    @Override
    public Class<Long> getAttributesClass() {
        return Long.class;
    }

    @Override
    public void execute(Tunnel tunnel, Message message, RpcInvokeContext context, Long attribute) throws Exception {
        if (attribute <= 0) {
            return;
        }
        // 是否需要做超时判断
        MessageHead head = message.getHead();
        if (System.currentTimeMillis() + attribute > head.getTime()) {
            DISPATCHER_LOG.warn("调用 {} 业务方法失败, 消息超时!", context.getName());
            context.doneAndIntercept(NetResultCode.REQUEST_TIMEOUT);
        }
    }

}
