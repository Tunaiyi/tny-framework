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
        return current().getSession();
    }

    /**
     * @return 获取当前线程正在执行的会话
     */
    public static Executor currentExecutor() {
        return current().getSession();
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
