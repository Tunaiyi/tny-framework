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
package com.tny.game.basics.transaction;

import com.tny.game.boot.transaction.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.listener.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/24 3:03 下午
 */
@Unit
public class MessageCommandTransactionHandler implements MessageCommandListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageCommandTransactionHandler.class);

    @Override
    public void onExecuteStart(RpcInvokeCommand command) {
        TransactionManager.open();
    }

    @Override
    public void onExecuteEnd(RpcInvokeCommand command, Throwable cause) {
        try {
            TransactionManager.close();
        } catch (Throwable e) {
            try {
                TransactionManager.rollback(e);
            } catch (Throwable ex) {
                LOGGER.warn("协议[{}] => 异常", command.getName(), ex);
            }
        }
    }

}
