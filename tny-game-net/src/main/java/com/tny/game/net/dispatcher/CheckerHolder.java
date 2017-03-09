package com.tny.game.net.dispatcher;

import com.tny.game.annotation.Check;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import com.tny.game.common.result.ResultCode;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.Message;
import com.tny.game.net.checker.ControllerChecker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检测器持有者
 * Created by Kun Yang on 2017/3/4.
 */
public class CheckerHolder {

    private static final Logger DISPATCHER_LOG = LoggerFactory.getLogger(CoreLogger.DISPATCHER);

    private ControllerChecker<Object> checker;

    private ControllerHolder controller;

    private Object attributes;

    @SuppressWarnings("unchecked")
    public CheckerHolder(ControllerHolder controller, ControllerChecker checker, Check check) {
        this.checker = checker;
        this.controller = controller;
        if (StringUtils.isNoneBlank(check.attributesFx()))
            this.attributes = MvelFormulaFactory.create(check.attributesFx().substring(1), FormulaType.EXPRESSION)
                    .createFormula()
                    .execute(checker.getAttributesClass());
        else
            this.attributes = check.attributes();
    }

    public ResultCode check(Message<?> message, AppContext context) {
        if (DISPATCHER_LOG.isDebugEnabled())
            DISPATCHER_LOG.debug("调用 {}.{} 检测Request - {}", this.controller.getControllerClass(), this.controller.getName(), checker.getClass());
        ResultCode code = checker.check(message, this.controller, context, attributes);
        return code != null ? code : ResultCode.SUCCESS;
    }

}
