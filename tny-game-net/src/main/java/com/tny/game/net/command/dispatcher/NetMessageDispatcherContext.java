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

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.plugins.*;

import java.util.Collection;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-09-30 18:03
 */
@UnitInterface
public interface NetMessageDispatcherContext extends MessageDispatcherContext {

    void addControllerPlugin(CommandPlugin<?> plugin);

    void addControllerPlugin(Collection<? extends CommandPlugin<?>> plugins);

    void addAuthProvider(AuthenticationValidator provider);

    void addAuthProvider(Collection<? extends AuthenticationValidator> providers);

    //    void fireExecuteStart(RpcInvokeCommand command);

    //    void fireExecuteEnd(RpcInvokeCommand command, Throwable cause);

    void fireException(RpcInvokeCommand command, Throwable cause);

    void fireDone(RpcInvokeCommand command, Throwable cause);

}
