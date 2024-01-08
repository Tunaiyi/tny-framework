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
        var tunnel = context.netTunnel();
        var message = context.getMessage();
        var networkContext = context.networkContext();
        if (!tunnel.isAuthenticated()) {
            var validator = getValidator(dispatcherContext, validatorClass);
            if (validator == null) {
                throw new AuthFailedException(NetResultCode.SERVER_ERROR, "{} is null", validatorClass);
            }
            Certificate certificate = validator.validate(tunnel, message);
            // 是否需要做登录校验,判断是否已经登录
            if (certificate != null && certificate.isAuthenticated()) {
                SessionKeeper sessionKeeper = this.sessionKeeperManager
                        .loadKeeper(certificate.getContactType(), tunnel.getAccessMode());
                sessionKeeper.online(certificate, tunnel);
            }
        }
    }

    private AuthenticationValidator getValidator(MessageDispatcherContext dispatcherContext,
            Class<? extends AuthenticationValidator> validatorClass) {
        return as(dispatcherContext.getValidator(validatorClass));
    }

}
