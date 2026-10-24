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
