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

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.expr.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 抽象消息派发器
 *
 * @author KGTny
 * <p>
 * 抽象消息派发器
 * <p>
 * <p>
 * 实现对controllerMap的初始化,派发消息流程<br>
 */
public abstract class BaseMessageDispatcher implements MessageDispatcher {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageDispatcher.class);

    /**
     * Controller Map
     */
    private final Map<Object, Map<MessageMode, MethodControllerHolder>> methodHolder = new ConcurrentHashMap<>();

    private final ExprHolderFactory exprHolderFactory;

    private final ContactAuthenticator contactAuthenticator;

    protected final NetMessageDispatcherContext context;

    protected BaseMessageDispatcher(NetAppContext appContext, ContactAuthenticator contactAuthenticator,
            ExprHolderFactory exprHolderFactory) {
        this.context = new DefaultMessageDispatcherContext(appContext);
        this.exprHolderFactory = exprHolderFactory;
        this.contactAuthenticator = contactAuthenticator;
    }

    @Override
    public RpcCommand dispatch(RpcEnterContext rpcContext) throws RpcInvokeException {
        var forwardCommand = tryForward(rpcContext);
        if (forwardCommand != null) {
            return forwardCommand;
        }
        var message = rpcContext.getMessage();
        // 获取方法持有器
        MethodControllerHolder controller = this.getController(message.getProtocolId(), message.getMode());
        if (controller != null) {
            var handleContext = new RpcInvokeContext(controller, rpcContext, this.context.getAppContext());
            return new RpcInvokeCommand(this.context, handleContext, contactAuthenticator);
        }
        if (message.getMode() == MessageMode.REQUEST) {
            LOGGER.warn("{} controller [{}] not exist", message.getMode(), message.getProtocolId());
            return new RpcRespondCommand(rpcContext, NetResultCode.SERVER_NO_SUCH_PROTOCOL, null);
        } else {
            return new RpcNoopCommand(rpcContext);
        }
    }

    private RpcForwardCommand tryForward(RpcEnterContext rpcContext) {
        var netContext = rpcContext.networkContext();
        NetBootstrapSetting setting = netContext.getSetting();
        if (!setting.isForwardable()) {
            return null;
        }
        var message = rpcContext.getMessage();
        RpcForwardHeader forwardHeader = message.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
        if (forwardHeader == null) {
            return null;
        }
        ForwardPoint rpcServicer = forwardHeader.getTo();
        if (rpcServicer == null) {
            return null;
        }
        RpcServiceType currentType = setting.getRpcServiceType();
        NetAppContext appContext = context.getAppContext();
        if (rpcServicer.getServiceType() == currentType &&
            (!rpcServicer.isAppointed() || appContext != null && rpcServicer.getServerId() == appContext.getServerId())) {
            return null;
        }
        return new RpcForwardCommand(rpcContext);
    }

    @Override
    public String getNameOf(MessageSchema message) {
        MethodControllerHolder controller = this.getController(message.getProtocolId(), message.getMode());
        if (controller == null) {
            return null;
        }
        return controller.getName();
    }

    @Override
    public boolean isCanDispatch(MessageHead head) {
        return this.getController(head.getProtocolId(), head.getMode()) != null;
    }

    private MethodControllerHolder getController(Object protocol, MessageMode mode) {
        // 获取方法持有器
        MethodControllerHolder controller = null;
        final Map<MessageMode, MethodControllerHolder> controllerMap = this.methodHolder.get(protocol);
        if (controllerMap != null) {
            controller = controllerMap.get(mode);
        }
        return controller;
    }

    /**
     * 添加控制器对象列表
     *
     * @param objects 控制器对象列表
     */
    protected void addControllers(Collection<Object> objects) {
        objects.forEach(this::addController);
    }

    /**
     * 启动期参数校验注解覆盖校验（message-checking 契约）：
     * 已注册控制器使用的校验注解若无对应检查器，立即失败而非带病上线静默放行。
     */
    protected void checkParamFilterCoverage() {
        ParamFilterPlugin filterPlugin = as(this.context.getPlugin(ParamFilterPlugin.class));
        if (filterPlugin == null) {
            return;
        }
        Set<Class<?>> usedAnnotations = new HashSet<>();
        this.methodHolder.values().forEach(modeMap -> modeMap.values().forEach(holder -> usedAnnotations.addAll(holder.getParamAnnotationClass())));
        filterPlugin.checkCoverage(usedAnnotations);
    }

    /**
     * 添加控制器对象
     *
     * @param object 控制器对象
     */
    private void addController(Object object) {
        Map<Object, Map<MessageMode, MethodControllerHolder>> methodHolder = this.methodHolder;
        final ClassControllerHolder holder = new ClassControllerHolder(object, this.context, this.exprHolderFactory);
        for (MethodControllerHolder controller : holder.getMethodControllers()) {
            Map<MessageMode, MethodControllerHolder> holderMap = methodHolder.computeIfAbsent(controller.getProtocol(), k -> new CopyOnWriteMap<>());
            MethodControllerHolder old = holderMap.putIfAbsent(controller.getMessageMode(), controller);
            if (old != null) {
                throw new IllegalArgumentException(format("{} 与 {} 对MessageMode {} 处理发生冲突", old, controller, controller.getMessageMode()));
            }
        }
    }

    @Override
    public void addCommandListener(MessageCommandListener listener) {
        this.context.addCommandListener(listener);
    }

    @Override
    public void addCommandListener(Collection<MessageCommandListener> listeners) {
        this.context.addCommandListener(listeners);
    }

    @Override
    public void removeCommandListener(MessageCommandListener listener) {
        this.context.removeCommandListener(listener);
    }

    @Override
    public void clearCommandListeners() {
        this.context.clearCommandListeners();
    }

}
