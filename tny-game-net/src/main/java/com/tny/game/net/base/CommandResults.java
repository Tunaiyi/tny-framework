package com.tny.game.net.base;

import com.tny.game.common.result.*;
import com.tny.game.net.command.*;
import org.apache.commons.lang3.StringUtils;

/**
 * @author KGTny
 */
public class CommandResults {

	/**
	 * 成功返回数据
	 */
	public final static CommandResult SUCCESS = new CommandResultImpl(NetResultCode.SUCCESS, null);

	/**
	 * 创建成功响应结果
	 * <p>
	 * <p>
	 * 创建成功响应结果<br>
	 *
	 * @param message 信息体
	 * @return 返回响应结果
	 */
	public static CommandResult success(Object message) {
		return new CommandResultImpl(NetResultCode.SUCCESS, message);
	}

	/**
	 * 请求结果
	 *
	 * @param code 消息码
	 * @return 返回
	 */
	public static CommandResult result(ResultCode code) {
		if (code.isSuccess()) {
			return success();
		} else {
			return fail(code);
		}
	}

	/**
	 * 创建成功响应结果
	 * <p>
	 * <p>
	 * 创建成功响应结果<br>
	 *
	 * @return 返回响应结果
	 */
	public static CommandResult success() {
		return SUCCESS;
	}

	public static CommandResult create(ResultCode code, Object body) {
		return new CommandResultImpl(code, body);
	}

	/**
	 * 创建失败响应结果
	 * <p>
	 * <p>
	 * 创建失败响应结果<br>
	 *
	 * @param code 结果码
	 * @param body 消息体
	 * @return 返回响应结果
	 */
	public static CommandResult fail(ResultCode code, Object body) {
		return new CommandResultImpl(code, body);
	}

	/**
	 * 创建失败响应结果
	 * <p>
	 * <p>
	 * 创建失败响应结果<br>
	 *
	 * @param code 结果码
	 * @return 返回响应结果
	 */
	public static CommandResult fail(ResultCode code) {
		return new CommandResultImpl(code, null);
	}

	/**
	 * 根据done创建响应结果
	 *
	 * @param done 错误结果
	 * @return 返回响应结果
	 */
	public static CommandResult fail(DoneResult<?> done) {
		return fail(done.getCode(), null);
	}

	//TODO Protocol 设置
	private static class CommandResultImpl implements CommandResult {

		private final ResultCode resultCode;

		private final Object body;

		private String descriptions;

		private CommandResultImpl(ResultCode code) {
			super();
			this.resultCode = code;
			this.body = null;
		}

		private CommandResultImpl(ResultCode code, Object body) {
			super();
			this.resultCode = code;
			this.body = body;
		}

		@Override
		public ResultCode getResultCode() {
			return this.resultCode;
		}

		@Override
		public String getDescriptions() {
			if (StringUtils.isNoneBlank(this.descriptions)) {
				return this.descriptions;
			}
			return this.resultCode.getMessage();
		}

		@Override
		public Object getBody() {
			return this.body;
		}

	}

}
