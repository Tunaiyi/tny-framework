package com.tny.game.net.base;

import com.tny.game.common.result.*;

public enum NetResultCode implements ResultCode {

	EMPTY(0, "无处理", ResultCodeType.GENERAL),

	//	/** 请求处理成功 100*/
	//	SUCCESS(ResultCode.SUCCESS_CODE, "请求处理成功", ResultCodeType.GENERAL),

	/**
	 * 服务端返回响应编码响应异常(不断开连接)
	 */
	DECODE_FAILED(191, "编码响应异常", ResultCodeType.WARN),

	/**
	 * 服务端返回响应编码响应异常
	 */
	ENCODE_FAILED(192, "编码响应异常", ResultCodeType.WARN),

	/**
	 * 服务端接受请求解码格式错误(断开连接)
	 */
	DECODE_ERROR(193, "解码格式错误", ResultCodeType.ERROR),

	/**
	 * 服务端返回响应编码响应异常
	 */
	ENCODE_ERROR(194, "编码响应错误", ResultCodeType.ERROR),
	/**
	 * 收到的网络包超时失效
	 */
	PACKET_TIMEOUT(195, "网络包超时失效", ResultCodeType.WARN),
	/**
	 * 网络包校验失败
	 */
	PACKET_VERIFY_FAILED(196, "网络包校验失败", ResultCodeType.WARN),

	/**
	 * 服务端接受请求异常
	 */
	SERVER_ERROR(200, "服务端异常", ResultCodeType.WARN),
	/**
	 * 服务端接受请求异常
	 */
	SERVER_RECEIVE_EXCEPTION(201, "服务端接受请求异常", ResultCodeType.WARN),
	/**
	 * 服务端执行业务异常
	 */
	SERVER_EXECUTE_EXCEPTION(202, "服务端执行业务异常", ResultCodeType.GENERAL),
	/**
	 * 请求模块不存在
	 */
	@Deprecated
	SERVER_NO_SUCH_MODULE(204, "请求模块不存在", ResultCodeType.WARN),
	/**
	 * 请求操作不存在
	 */
	SERVER_NO_SUCH_PROTOCOL(205, "请求操作不存在", ResultCodeType.WARN),
	/**
	 * 分发消息异常
	 */
	SERVER_DISPATCH_EXCEPTION(206, "分发消息异常", ResultCodeType.WARN),
	/**
	 * 非法请求参数
	 */
	SERVER_ILLEGAL_PARAMETERS(207, "非法请求参数", ResultCodeType.WARN),

	/**
	 * 无法接收该类型消息
	 */
	SERVER_NO_RECEIVE_MODE(208, "无法接收该类型消息", ResultCodeType.WARN),

	/**
	 * 无法发送该类型消息
	 */
	SERVER_NO_SEND_MODE(209, "无法发送该类型消息", ResultCodeType.WARN),

	/**
	 * 验证失败
	 */
	VALIDATOR_FAIL_ERROR(210, "验证失败", ResultCodeType.ERROR),
	/**
	 * 用户未登录
	 */
	NO_LOGIN(211, "用户未登录", ResultCodeType.WARN),
	/**
	 * 没有权限调用
	 */
	NO_PERMISSIONS(212, "用户没有权限调用", ResultCodeType.WARN),
	/**
	 * 请求消息被篡改
	 */
	MESSAGE_FALSIFY(213, "请求消息被篡改", ResultCodeType.WARN),
	/**
	 * 请求过期
	 */
	REQUEST_TIMEOUT(214, "请求过期", ResultCodeType.WARN),
	/**
	 * 响应过期
	 */
	RESPONSE_TIMEOUT(215, "响应过期", ResultCodeType.WARN),
	/**
	 * 会话丢失
	 */
	SESSION_LOSS_ERROR(216, "会话丢失", ResultCodeType.ERROR),
	/**
	 * 会话超时
	 */
	SESSION_TIMEOUT_ERROR(217, "会话超时", ResultCodeType.ERROR),
	/**
	 * 会话丢失
	 */
	SESSION_CREATE_FAILED(218, "会话创建失败", ResultCodeType.GENERAL),

	/**
	 * 消息已处理过
	 */
	MESSAGE_HANDLED(219, "消息已处理过", ResultCodeType.WARN),
	/**
	 * 证书无效
	 */
	INVALID_CERTIFICATE_ERROR(220, "验证无效", ResultCodeType.ERROR),
	/**
	 * 验证失败
	 */
	SERVER_OFFLINE_ERROR(221, "服务器未上线", ResultCodeType.ERROR),

	/**
	 * 服务端执行业务超时
	 */
	EXECUTE_TIMEOUT(222, "服务端执行业超时", ResultCodeType.GENERAL),

	/**
	 * 用户已登录
	 */
	LOGGED_IN(223, "用户已登录", ResultCodeType.GENERAL),
	/**
	 * 集群网络繁忙
	 */
	CLUSTER_NETWORK_BUSY(224, "集群网络未接通", ResultCodeType.ERROR),
	/**
	 * 集群网络未接通
	 */
	CLUSTER_NETWORK_UNCONNECTED_ERROR(225, "集群网络未接通错误", ResultCodeType.ERROR),
	/**
	 * 集群不存在
	 */
	CLUSTER_NOT_EXIST_ERROR(226, "集群不存在", ResultCodeType.ERROR),

	/**
	 * 客户端IO异常
	 */
	IO_EXCEPTION(300, "客户端IO异常", ResultCodeType.WARN),
	/**
	 * 客户端请求中断
	 */
	INTERRUPTED(301, "客户端请求中断", ResultCodeType.WARN),
	/**
	 * 客户端请求超时
	 */
	WAIT_TIMEOUT(302, "客户端请求超时", ResultCodeType.WARN),
	/**
	 * 远程调用异常
	 */
	REMOTE_EXCEPTION(303, "远程调用异常", ResultCodeType.WARN),
	/**
	 * 客户端连接中断
	 */
	CONNECT_INTERRUPTED(304, "客户端请求中断", ResultCodeType.WARN),
	/**
	 * 客户端请求服务端无响应
	 */
	REMOTE_NO_RESPONSE(305, "服务端无响应", ResultCodeType.WARN),
	/**
	 * 客户端请求失败
	 */
	REQUEST_FAILED(306, "客户端请求失败", ResultCodeType.WARN),

	//
	;

	private int code;

	private String message;

	private ResultCodeType type;

	NetResultCode(int code, String message, ResultCodeType type) {
		this.code = code;
		this.message = message;
		this.type = type;
		this.registerSelf();
	}

	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public ResultCodeType getType() {
		return type;
	}

}
