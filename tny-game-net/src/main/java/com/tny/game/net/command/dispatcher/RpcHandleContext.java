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

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.Executor;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * @author KGTny
 * @ClassName: ControllerInfo
 * @date 2011-10-26 下午4:22:47
 * <p>
 * 控制器信息
 * <p>
 * <br>
 */
public class RpcHandleContext {

    private static final ThreadLocal<RpcHandleContext> LOCAL_CONTEXT = new ThreadLocal<>();

    private MessageCommand command;

    private RpcHandleContext() {
    }

    /**
     * 获取当前线程正在执行的控制信息 <br>
     *
     * @return 获取当前线程正在执行的控制信息
     */
    public static RpcHandleContext current() {
        RpcHandleContext info = LOCAL_CONTEXT.get();
        if (info == null) {
            info = new RpcHandleContext();
            LOCAL_CONTEXT.set(info);
        }
        return info;
    }

    /**
     * @return 获取当前线程正在执行的终端
     */
    public static <U> Endpoint<U> currentEndpoint() {
        return current().getEndpoint();
    }

    /**
     * @return 获取当前线程正在执行的通道
     */
    public static <U> Tunnel<U> currentTunnel() {
        return current().getTunnel();
    }

    /**
     * @return 获取当前线程正在处理的消息
     */
    public static Message currentMessage() {
        return current().getMessage();
    }

    static void setCurrent(MessageCommand command) {
        RpcHandleContext info = LOCAL_CONTEXT.get();
        if (info == null) {
            info = new RpcHandleContext();
            LOCAL_CONTEXT.set(info);
        }
        info.command = command;
    }

    static void clean() {
        RpcHandleContext info = LOCAL_CONTEXT.get();
        if (info != null) {
            info.command = null;
        }
    }

    /**
     * @return 获取消息
     */
    public Message getMessage() {
        return this.command.getMessage();
    }

    /**
     * @return 获取终端
     */
    public <U> Endpoint<U> getEndpoint() {
        return as(this.command.getEndpoint());
    }

    /**
     * @return 获取通道
     */
    public <U> Tunnel<U> getTunnel() {
        return as(this.command.getTunnel());
    }

    /**
     * @return 当前用户执行器
     */
    public Executor getExecutor() {
        return this.command.getEndpoint();
    }

    @Override
    public String toString() {
        return "ControllerInfo [getProtocol()=" + this.command + "]";
    }

}
