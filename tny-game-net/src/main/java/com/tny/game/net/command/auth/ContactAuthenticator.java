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
package com.tny.game.net.command.auth;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/12 14:29
 **/
public interface ContactAuthenticator {

    void authenticate(MessageDispatcherContext dispatcherContext, RpcEnterContext rpcContext,
            Class<? extends AuthenticationValidator> validatorClass) throws AuthFailedException;

    /**
     * 以已解析的校验器实例执行鉴权（协议级/全局兜底注册的校验器按协议号登记，不得再按类名二次查找）；
     * validator 为 null 时安全跳过（由调用链后续未登录判定兜底）。
     * 默认实现回退到类签名，保证仅实现类签名的下游实现兼容。
     */
    default void authenticate(MessageDispatcherContext dispatcherContext, RpcEnterContext rpcContext,
            AuthenticationValidator validator) throws AuthFailedException {
        if (validator != null) {
            authenticate(dispatcherContext, rpcContext, validator.getClass());
        }
    }

}
