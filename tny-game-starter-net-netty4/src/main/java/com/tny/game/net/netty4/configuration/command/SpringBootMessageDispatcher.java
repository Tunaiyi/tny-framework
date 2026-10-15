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
package com.tny.game.net.netty4.configuration.command;

import com.tny.game.common.lifecycle.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.application.*;
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
        // 控制器注册完成后校验注解-检查器覆盖关系（message-checking 契约，启动即失败）
        this.checkParamFilterCoverage();
    }

}
