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
        // 控制器注册完成后校验注解-检查器覆盖关系（message-checking 契约）
        this.checkParamFilterCoverage();
    }

}
