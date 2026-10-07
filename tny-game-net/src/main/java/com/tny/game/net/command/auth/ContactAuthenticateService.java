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

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/12 14:29
 **/
public class ContactAuthenticateService implements ContactAuthenticator {

    private final SessionKeeperManager sessionKeeperManager;

    public ContactAuthenticateService(SessionKeeperManager sessionKeeperManager) {
        this.sessionKeeperManager = sessionKeeperManager;
    }

    @Override
    public void authenticate(MessageDispatcherContext dispatcherContext, RpcEnterContext context,
            Class<? extends AuthenticationValidator> validatorClass)
            throws AuthFailedException {
        var validator = getValidator(dispatcherContext, validatorClass);
        if (validator == null) {
            throw new AuthFailedException(NetResultCode.SERVER_ERROR, "{} is null", validatorClass);
        }
        authenticate(dispatcherContext, context, validator);
    }

    @Override
    public void authenticate(MessageDispatcherContext dispatcherContext, RpcEnterContext context,
            AuthenticationValidator validator)
            throws AuthFailedException {
        var tunnel = context.netTunnel();
        if (validator == null || tunnel.isAuthenticated()) {
            // 三级解析皆无校验器：安全跳过，由调用链后续未登录判定兜底
            return;
        }
        Certificate certificate = validator.validate(tunnel, context.getMessage());
        // 是否需要做登录校验,判断是否已经登录
        if (certificate != null && certificate.isAuthenticated()) {
            SessionKeeper sessionKeeper = this.sessionKeeperManager
                    .loadKeeper(certificate.getContactType(), tunnel.getAccessMode());
            sessionKeeper.online(certificate, tunnel);
        }
    }

    private AuthenticationValidator getValidator(MessageDispatcherContext dispatcherContext,
            Class<? extends AuthenticationValidator> validatorClass) {
        return as(dispatcherContext.getValidator(validatorClass));
    }

}
