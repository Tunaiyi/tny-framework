package com.tny.game.net.command.dispatcher;

import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 检测器持有者
 * Created by Kun Yang on 2017/3/4.
 */
public class CommandPluginHolder {

    private static final String EXPR_PREFIX = "@";

    private static final String NULL_EXPR = "@null";

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private final CommandPlugin<Object, Object> plugin;

    private final ControllerHolder controller;

    private final Object attributes;

    public CommandPluginHolder(ControllerHolder controller, CommandPlugin<?, ?> plugin, BeforePlugin annotation,
            ExprHolderFactory exprHolderFactory) {
        this(controller, plugin, annotation.attribute(), exprHolderFactory);
    }

    public CommandPluginHolder(ControllerHolder controller, CommandPlugin<?, ?> plugin, AfterPlugin annotation,
            ExprHolderFactory exprHolderFactory) {
        this(controller, plugin, annotation.attribute(), exprHolderFactory);
    }

    private CommandPluginHolder(ControllerHolder controller, CommandPlugin<?, ?> plugin, String attributes, ExprHolderFactory exprHolderFactory) {
        this.plugin = as(plugin);
        this.controller = controller;
        if (exprHolderFactory == null) {
            exprHolderFactory = new GroovyExprHolderFactory();
        }
        if (attributes.equals(NULL_EXPR)) {
            this.attributes = null;
        } else if (StringUtils.startsWith(attributes, EXPR_PREFIX)) {
            this.attributes = exprHolderFactory.create(attributes.substring(1))
                    .createExpr()
                    .execute(plugin.getAttributesClass());
        } else {
            this.attributes = attributes;
        }
    }

    public void invokePlugin(Tunnel<?> tunnel, Message message, MessageCommandContext context) throws Exception {
        if (DISPATCHER_LOG.isDebugEnabled()) {
            DISPATCHER_LOG.debug("调用 {}.{} | 触发插件 {}", this.controller.getControllerClass(), this.controller.getName(), this.plugin.getClass());
        }
        this.plugin.execute(as(tunnel), message, context, this.attributes);
    }

}
