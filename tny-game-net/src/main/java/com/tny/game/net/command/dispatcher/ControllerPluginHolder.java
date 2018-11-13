package com.tny.game.net.command.dispatcher;

import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.expr.groovy.GroovyExprHolderFactory;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.command.plugins.ControllerPlugin;
import com.tny.game.net.message.Message;
import com.tny.game.net.transport.Tunnel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

/**
 * 检测器持有者
 * Created by Kun Yang on 2017/3/4.
 */
public class ControllerPluginHolder {

    private static final String EXPR_PREFIX = "@";
    private static final String NULL_EXPR = "@null";

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private ControllerPlugin plugin;

    private ControllerHolder controller;

    private PluginTrigger trigger;

    private Object attributes;

    @SuppressWarnings("unchecked")
    public ControllerPluginHolder(ControllerHolder controller, ControllerPlugin<?, ?> plugin, BeforePlugin annotation, ExprHolderFactory exprHolderFactory) {
        this(controller, plugin, annotation.attribute(), exprHolderFactory);
    }

    public ControllerPluginHolder(ControllerHolder controller, ControllerPlugin<?, ?> plugin, AfterPlugin annotation, ExprHolderFactory exprHolderFactory) {
        this(controller, plugin, annotation.attribute(), exprHolderFactory);
    }

    private ControllerPluginHolder(ControllerHolder controller, ControllerPlugin<?, ?> plugin, String attributes, ExprHolderFactory exprHolderFactory) {
        this.plugin = plugin;
        this.controller = controller;
        if (exprHolderFactory == null)
            exprHolderFactory = new GroovyExprHolderFactory();
        if (attributes.equals(NULL_EXPR))
            this.attributes = null;
        else if (StringUtils.startsWith(attributes, EXPR_PREFIX))
            this.attributes = exprHolderFactory.create(attributes.substring(1))
                    .createExpr()
                    .execute(plugin.getAttributesClass());
        else
            this.attributes = attributes;
    }

    @SuppressWarnings("unchecked")
    public void invokePlugin(Tunnel tunnel, Message message, InvokeContext context) throws Exception {
        if (DISPATCHER_LOG.isDebugEnabled())
            DISPATCHER_LOG.debug("调用 {}.{} | 触发 [{}]插件 - {}", this.controller.getControllerClass(), this.controller.getName(), this.trigger, plugin.getClass());
        plugin.execute(tunnel, message, context, attributes);
    }

}
