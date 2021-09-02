package com.tny.game.net.relay.cluster;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:49 下午
 */
public enum ServeClusterImportance {

	/**
	 * 可选, 申请不到继续创建 tunnel
	 */
	UNNECESSARY,

	/**
	 * 可选, 申请不到继续创建 tunnel
	 */
	OPTIONAL,

	/**
	 * 必要, 申请不到关闭 tunnel
	 */
	REQUIRED,

}
