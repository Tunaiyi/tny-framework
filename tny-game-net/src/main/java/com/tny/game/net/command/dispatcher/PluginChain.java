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
package com.tny.game.net.command.dispatcher;

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
            LOGGER.error("invoke plugin {} exception", this.plugin.getClass(), e);
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
