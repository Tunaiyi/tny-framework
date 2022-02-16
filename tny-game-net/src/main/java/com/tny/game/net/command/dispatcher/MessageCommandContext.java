package com.tny.game.net.command.dispatcher;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;

/**
 * <p>
 */
public class MessageCommandContext {

	private final MessageCommandPromise promise;

	protected MethodControllerHolder controller;

	private boolean intercept = false;

	public MessageCommandContext(MethodControllerHolder controller) {
		this.controller = controller;
		this.promise = new MessageCommandPromise(getName());
	}

	/**
	 * @return 获取名字
	 */
	public String getName() {
		return this.controller.getName();
	}

	/**
	 * @return 获取结果
	 */
	public Object getResult() {
		return this.promise.getResult();
	}

	public MethodControllerHolder getController() {
		return this.controller;
	}

	protected MessageCommandPromise getPromise() {
		return this.promise;
	}

	/**
	 * 设置CommandResult,并中断执行
	 *
	 * @param result 运行结果
	 */
	public void doneAndIntercept(RpcResult<?> result) {
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

	public void setResult(RpcResult<?> result) {
		this.promise.setResult(result);
	}

	/**
	 * 设置结果码,不中断执行
	 *
	 * @param code 结果码
	 */
	public void setResult(ResultCode code) {
		this.promise.setResult(code);
	}

	/**
	 * 设置结果码与消息体,不中断执行
	 *
	 * @param code 结果码
	 * @param body 消息体
	 */
	public void setResult(ResultCode code, Object body) {
		this.promise.setResult(RpcResults.result(code, body));
	}

	/**
	 * 设置运行结果Object,不中断执行
	 *
	 * @param result 消息
	 */
	public void setResult(Object result) {
		this.promise.setResult(result);
	}

	/**
	 * 设置运行结果Object,不中断执行
	 */
	public void setVoidResult() {
		this.promise.setVoidResult();
	}

	public boolean isIntercept() {
		return this.intercept;
	}

	public boolean isDone() {
		return this.isIntercept() || this.getPromise().isDone();
	}

}
