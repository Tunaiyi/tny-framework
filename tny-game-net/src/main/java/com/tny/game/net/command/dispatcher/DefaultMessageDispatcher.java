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

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.command.plugins.*;

@Unit
public class DefaultMessageDispatcher extends BaseMessageDispatcher implements AppPrepareStart {

    public DefaultMessageDispatcher(NetAppContext appContext, ContactAuthenticator contactAuthenticator) {
        super(appContext, contactAuthenticator, new GroovyExprHolderFactory());
    }

    public DefaultMessageDispatcher(NetAppContext appContext, ContactAuthenticator contactAuthenticator,
            ExprHolderFactory exprHolderFactory) {
        super(appContext, contactAuthenticator, exprHolderFactory);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_9);
    }

    @Override
    public void prepareStart() {
        this.context.addAuthProvider(UnitLoader.getLoader(AuthenticationValidator.class).getAllUnits());
        this.context.addControllerPlugin(UnitLoader.getLoader(CommandPlugin.class).getAllUnits());
        this.context.addCommandListener(UnitLoader.getLoader(MessageCommandListener.class).getAllUnits());
    }

}
