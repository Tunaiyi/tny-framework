package com.tny.game.net.rpc.annotation;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 3:46 下午
 */
public enum RpcInvocation {

	/**
	 * 默认, 根据返回值
	 */
	DEFAULT,

	/**
	 * 同步
	 */
	SYNC,

	/**
	 * 异步
	 */
	ASYNC,

}
