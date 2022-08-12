/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
    default void onExecuteStart(MessageCommand command) {
    }

    /**
     * 每次 Command 执行结束<br>
     *
     * @param command 分发上下文
     * @param cause   失败异常, 成功为 null
     */
    default void onExecuteEnd(MessageCommand command, Throwable cause) {
    }

    /**
     * 执行Command任务完成  <br>
     *
     * @param command 分发上下文
     * @param cause   失败异常, 成功为 null
     */
    default void onDone(MessageCommand command, Throwable cause) {
    }

}
