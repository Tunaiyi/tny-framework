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
package com.tny.game.net.monitor;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.listener.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/11/7 04:53
 **/
public class MessageCommandMonitor implements MessageCommandListener {

    @Override
    public void onExecuteStart(RpcInvokeCommand command) {
    }

    @Override
    public void onExecuteEnd(RpcInvokeCommand command, Throwable cause) {
    }

    @Override
    public void onDone(RpcInvokeCommand command, Throwable cause) {
    }

}
