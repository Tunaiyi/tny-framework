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
package com.tny.game.net.command.listener;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.command.dispatcher.*;

/**
 * @author KGTny
 * @ClassName: DispatcherRequestListener
 * @Description: 请求派发监听器
 * @date 2011-11-18 上午10:59:39
 * <p>
 * <p>
 * <br>
 */
@UnitInterface
public interface MessageCommandListener {

    /**
     * 每次 Command 执行开始<br>
     *
     * @param command 分发上下文
     */
    default void onExecuteStart(RpcInvokeCommand command) {
    }

    /**
     * 每次 Command 执行异常
     *
     * @param command 分发上下文
     * @param cause   失败异常
     */
    default void onException(RpcInvokeCommand command, Throwable cause) {
    }

    /**
     * 每次 Command 执行结束<br>
     *
     * @param command 分发上下文
     * @param cause   失败异常, 成功为 null
     */
    default void onExecuteEnd(RpcInvokeCommand command, Throwable cause) {
    }

    /**
     * 执行Command任务完成  <br>
     *
     * @param command 分发上下文
     * @param cause   失败异常, 成功为 null
     */
    default void onDone(RpcInvokeCommand command, Throwable cause) {
    }

}
