package com.tny.game.net.common;

import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.annotation.Check;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.command.checker.ControllerChecker;
import com.tny.game.net.command.dispatcher.ControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检测器持有者
 * Created by Kun Yang on 2017/3/4.
 */
public class ControllerCheckerHolder {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(NetLogger.DISPATCHER);

    private ControllerChecker checker;

    private ControllerHolder controller;

    private Object attributes;

    @SuppressWarnings("unchecked")
    public ControllerCheckerHolder(ControllerHolder controller, ControllerChecker checker, Check check) {
        this.checker = checker;
        this.controller = controller;
        if (StringUtils.isNoneBlank(check.attributesFx()))
            this.attributes = MvelFormulaFactory.create(check.attributesFx().substring(1), FormulaType.EXPRESSION)
                    .createFormula()
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
