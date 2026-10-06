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
package com.tny.game.net.command.dispatcher;

import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

public class PluginChain {

    public static final Logger LOGGER = LoggerFactory.getLogger(PluginChain.class);

    private PluginChain next;

    private final CommandPluginHolder plugin;

    public PluginChain(CommandPluginHolder plugin) {
        this.plugin = plugin;
        this.next = null;
    }

    public void execute(Tunnel tunnel, Message message, RpcInvokeContext context) {
        if (this.plugin == null || context.isIntercept()) {
            return;
        }
        try {
            this.plugin.invokePlugin(tunnel, message, context);
        } catch (Throwable e) {
            // fail-closed：校验插件抛异常视同校验失败，拦截消息不回退放行（message-checking 规格）
            LOGGER.error("invoke plugin {} exception, message intercepted", this.plugin.getClass(), e);
            context.doneAndIntercept(NetResultCode.SERVER_ERROR);
        }
        if (this.next == null || context.isIntercept()) {
            return;
        }
        this.next.execute(tunnel, message, context);
    }

    public void append(PluginChain chain) {
        if (this.next == null || this.next.plugin == null) {
            this.next = chain;
        } else {
            this.next.append(chain);
        }
    }

}
