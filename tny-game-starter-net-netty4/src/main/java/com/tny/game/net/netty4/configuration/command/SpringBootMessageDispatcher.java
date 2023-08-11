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
package com.tny.game.net.netty4.configuration.command;

import com.tny.game.common.lifecycle.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.dispatcher.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public final class SpringBootMessageDispatcher extends DefaultMessageDispatcher implements AppPrepareStart {

    @Autowired
    private ApplicationContext applicationContext;

    public SpringBootMessageDispatcher(NetAppContext appContext, ContactAuthenticator contactAuthenticator, ExprHolderFactory exprHolderFactory) {
        super(appContext, contactAuthenticator, exprHolderFactory);
    }

    @Override
    public void prepareStart() {
        super.prepareStart();
        final Map<String, Object> handlerMap = this.applicationContext.getBeansWithAnnotation(RpcController.class);
        this.addControllers(handlerMap.values());
    }

}
