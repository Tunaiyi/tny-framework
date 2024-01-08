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

import com.tny.game.net.session.*;

import java.util.concurrent.Executor;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 16:50
 **/
public interface RpcHandleContext extends RpcContext {

    /**
     * @return 获取会话
     */
    Session getSession();

    /**
     * @return 当前执行器
     */
    default Executor getExecutor() {
        return getSession();
    }

}
