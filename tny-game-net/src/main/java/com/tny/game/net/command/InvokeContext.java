package com.tny.game.net.command;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;

/**
 * Created by Kun Yang on 2017/5/26.
 */
public abstract class InvokeContext {

    protected MethodControllerHolder controller;

    private Object result;

    private boolean intercept = false;

    public InvokeContext(MethodControllerHolder controller) {
        this.controller = controller;
    }

    public MethodControllerHolder getController() {
        return controller;
    }

    public Object getResult() {
        return result;
    }

    /**
     * 设置CommandResult,并中断执行
     *
     * @param result 运行结果
     */
    public void doneAndIntercept(CommandResult result) {
        if (!this.intercept) {
            this.intercept = true;
            this.setResult(result);
        }
    }

    /**
     * 设置结果码,并中断执行
     *
     * @param code 结果码
     */

    public void doneAndIntercept(ResultCode code) {
        if (!this.intercept) {
            this.intercept = true;
            this.setResult(code);
        }
    }

    /**
     * 设置CommandResult,不中断执行
     *
     * @param result 运行结果
     */

    public void setResult(CommandResult result) {
        this.result = result;
    }

    /**
     * 设置结果码,不中断执行
     *
     * @param code 结果码
     */
    public void setResult(ResultCode code) {
        this.result = code;
    }

    /**
     * 设置结果码与消息体,不中断执行
     *
     * @param code 结果码
     * @param body 消息体
     */
    public void setResult(ResultCode code, Object body) {
        this.result = ResultFactory.create(code, body);
    }

    /**
     * 设置运行结果Object,不中断执行
     *
     * @param result 消息
     */
    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isIntercept() {
        return intercept;
    }

    public abstract String getName();

    public abstract String getAppType();

    public abstract String getScopeType();

}
