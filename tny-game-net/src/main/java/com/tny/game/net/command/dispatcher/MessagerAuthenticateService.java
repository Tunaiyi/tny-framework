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

import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/12 14:29
 **/
public class MessagerAuthenticateService implements MessagerAuthenticator {

    private final EndpointKeeperManager endpointKeeperManager;

    public MessagerAuthenticateService(EndpointKeeperManager endpointKeeperManager) {
        this.endpointKeeperManager = endpointKeeperManager;
    }

    @Override
    public void authenticate(MessageDispatcherContext dispatcherContext, RpcProviderContext context,
            Class<? extends AuthenticationValidator<?, ?>> validatorClass)
            throws AuthFailedException {
        var tunnel = context.netTunnel();
        var message = context.netMessage();
        var networkContext = context.networkContext();
        if (!tunnel.isAuthenticated()) {
            CertificateFactory<Object> certificateFactory = networkContext.getCertificateFactory();
            var validator = getValidator(message, dispatcherContext, validatorClass);
            Certificate<Object> certificate = validator.validate(tunnel, message, certificateFactory);
            // 是否需要做登录校验,判断是否已经登录
            if (certificate != null && certificate.isAuthenticated()) {
                EndpointKeeper<Object, Endpoint<Object>> endpointKeeper = this.endpointKeeperManager
                        .loadEndpoint(certificate.getMessagerType(), tunnel.getAccessMode());
                endpointKeeper.online(certificate, tunnel);
            }
        }
    }

    private AuthenticationValidator<Object, CertificateFactory<Object>> getValidator(Message message, MessageDispatcherContext dispatcherContext,
            Class<? extends AuthenticationValidator<?, ?>> validatorClass) {
        AuthenticationValidator<Object, CertificateFactory<Object>> validator = as(dispatcherContext.getValidator(validatorClass));
        if (validator != null) {
            return validator;
        }
        return as(dispatcherContext.getValidator(message.getProtocolId()));
    }

}
