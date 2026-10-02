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

import com.tny.game.common.context.*;
import com.tny.game.net.session.*;

import java.util.concurrent.Executor;

/**
 * @author KGTny
 * @ClassName: ControllerInfo
 * @date 2011-10-26 下午4:22:47
 * <p>
 * 控制器信息
 * <p>
 * <br>
 */
public class RpcContexts {

    private static final ThreadLocal<RpcEnterContext> LOCAL_CONTEXT = new ThreadLocal<>();

    private static final RpcEnterContext EMPTY = new RpcEnterInvocationContext(null, null, false, ContextAttributes.empty());

    private static RpcEnterContext empty() {
        return EMPTY;
    }

    /**
     * 获取当前线程正在执行的控制信息 <br>
     *
     * @return 获取当前线程正在执行的控制信息
     */
    public static RpcHandleContext current() {
        var info = LOCAL_CONTEXT.get();
        if (info == null) {
            return RpcContexts.empty();
        }
        return info;
    }

    /**
     * @return 获取当前线程正在执行的会话
     */
    public static Session currentSession() {
        requireBound();
        return current().getSession();
    }

    /**
     * @return 获取当前线程正在执行的会话
     */
    public static Executor currentExecutor() {
        requireBound();
        return current().getSession();
    }

    /**
     * 空上下文（EMPTY）访问会话必然 NPE——改为带信息异常（command-execution 配套修复）。
     */
    private static void requireBound() {
        var info = LOCAL_CONTEXT.get();
        if (info == null || !info.isValid()) {
            throw new IllegalStateException("当前线程没有绑定的命令上下文，无法访问会话/执行器");
        }
    }

    static void setCurrent(RpcEnterContext context) {
        var info = LOCAL_CONTEXT.get();
        if (context.isValid() && (info == null || !info.isValid())) {
            context.resume();
            LOCAL_CONTEXT.set(context);
        }
    }

    static void clear() {
        var info = LOCAL_CONTEXT.get();
        if (info != null && info.isValid()) {
            info.suspend();
            LOCAL_CONTEXT.set(RpcContexts.empty());
        }
    }

}
