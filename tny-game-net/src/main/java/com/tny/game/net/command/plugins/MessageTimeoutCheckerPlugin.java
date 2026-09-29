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

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

/**
 * 检查消息超时Plugin
 * 参数 为超时时间ms:
 * <p>
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
        // 注解 attribute 缺省为 null（"@null"），拆箱比较会 NPE → fail-closed 误拦整个协议；未声明即不检查
        if (attribute == null || attribute <= 0) {
            return;
        }
        // 耗时 = 当前时刻 - 消息请求时间（客户端时钟），超过阈值判定超时。
        // 注意：依赖客户端时钟，超前可能误拦、滞后可能放松——业务按容忍度配置阈值（fix-message-checker-plugins design R2）
        MessageHead head = message.getHead();
        if (System.currentTimeMillis() - head.getTime() > attribute) {
            DISPATCHER_LOG.warn("调用 {} 业务方法失败, 消息超时!", context.getName());
            context.doneAndIntercept(NetResultCode.REQUEST_TIMEOUT);
        }
    }

}
