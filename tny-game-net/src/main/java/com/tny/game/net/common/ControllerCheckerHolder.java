package com.tny.game.net.common;

import com.tny.game.common.result.ResultCode;
import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.expr.groovy.GroovyExprHolderFactory;
import com.tny.game.net.annotation.Check;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.command.checker.ControllerChecker;
import com.tny.game.net.command.dispatcher.ControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

/**
 * 检测器持有者
 * Created by Kun Yang on 2017/3/4.
 */
public class ControllerCheckerHolder {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private ControllerChecker checker;

    private ControllerHolder controller;

    private Object attributes;

    private ExprHolderFactory exprHolderFactory;

    @SuppressWarnings("unchecked")
    public ControllerCheckerHolder(ControllerHolder controller, ControllerChecker checker, Check check, ExprHolderFactory exprHolderFactory) {
        this.checker = checker;
        this.controller = controller;
        this.exprHolderFactory = exprHolderFactory;
        if (this.exprHolderFactory == null)
            this.exprHolderFactory = new GroovyExprHolderFactory();
        if (StringUtils.isNoneBlank(check.attributesFx()))
            this.attributes = exprHolderFactory.create(check.attributesFx().substring(1))
                    .createExpr()
                    .execute(checker.getAttributesClass());
        else
            this.attributes = check.attributes();
    }

    @SuppressWarnings("unchecked")
    public ResultCode check(Tunnel tunnel, Message message) {
        if (DISPATCHER_LOG.isDebugEnabled())
            DISPATCHER_LOG.debug("调用 {}.{} 检测Request - {}", this.controller.getControllerClass(), this.controller.getName(), checker.getClass());
        ResultCode code = checker.check(tunnel, message, this.controller, attributes);
        return code != null ? code : ResultCode.SUCCESS;
    }

}
