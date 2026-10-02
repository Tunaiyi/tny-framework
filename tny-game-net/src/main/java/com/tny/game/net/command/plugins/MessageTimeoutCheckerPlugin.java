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
