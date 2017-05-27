package com.tny.game.net.command;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.common.dispatcher.MethodControllerHolder;

/**
 * Created by Kun Yang on 2017/5/26.
 */
public class InvokeContext {

    protected MethodControllerHolder controller;

    protected Object result;

    protected boolean interrupted = false;

    public InvokeContext(MethodControllerHolder controller) {
        this.controller = controller;
    }

    /**
     * 设置CommandResult,并中断执行
     *
     * @param result 运行结果
     */
    public boolean done(CommandResult result) {
        if (!this.interrupted) {
            this.interrupted = true;
            this.setResult(result);
            return true;
        }
        return false;
    }

    /**
     * 设置结果码,并中断执行
     *
     * @param code 结果码
     */

    public boolean done(ResultCode code) {
        if (!this.interrupted) {
            this.interrupted = true;
            this.setResult(code);
            return true;
        }
        return false;
    }

    /**
     * 设置结果码与消息体,并中断执行
     *
     * @param code 结果码
     * @param body 消息体
     */

    public boolean done(ResultCode code, Object body) {
        if (!this.interrupted) {
            this.interrupted = true;
            this.setResult(ResultFactory.create(code, body));
            return true;
        }
        return false;
    }

    /**
     * 设置运行结果Object,并中断执行
     *
     * @param result 消息
     */
    public boolean done(Object result) {
        if (!this.interrupted) {
            this.interrupted = true;
            this.result = result;
            return true;
        }
        return false;
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

    public MethodControllerHolder getController() {
        return controller;
    }

    public Object getResult() {
        return result;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

}
