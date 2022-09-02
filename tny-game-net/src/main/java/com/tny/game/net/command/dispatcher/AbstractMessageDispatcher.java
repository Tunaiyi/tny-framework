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

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.expr.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
public abstract class AbstractMessageDispatcher implements MessageDispatcher {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageDispatcher.class);

    /**
     * Controller Map
     */
    private final Map<Object, Map<MessageMode, MethodControllerHolder>> methodHolder = new ConcurrentHashMap<>();

    private final ExprHolderFactory exprHolderFactory;

    protected final NetMessageDispatcherContext context;

    private final EndpointKeeperManager endpointKeeperManager;

    protected AbstractMessageDispatcher(NetAppContext appContext, EndpointKeeperManager endpointKeeperManager, ExprHolderFactory exprHolderFactory) {
        this.context = new DefaultMessageDispatcherContext(appContext);
        this.endpointKeeperManager = endpointKeeperManager;
        this.exprHolderFactory = exprHolderFactory;
    }

    @Override
    public Command dispatch(NetTunnel<?> tunnel, Message message) throws CommandException {
        // 获取方法持有器
        MethodControllerHolder controller = this.getController(message.getProtocolId(), message.getMode());
        if (controller != null) {
            return new ControllerMessageCommand(tunnel, controller, message, this.context, this.endpointKeeperManager);
        }
        if (message.getMode() == MessageMode.REQUEST) {
            LOGGER.warn("{} controller [{}] not exist", message.getMode(), message.getProtocolId());
            return new RunnableCommand(() -> MessageSendAide.response(tunnel, message, NetResultCode.SERVER_NO_SUCH_PROTOCOL, null));
        }
        return null;
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
     * 添加控制器对象
     *
     * @param object 控制器对象
     */
    protected void addController(Object object) {
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
