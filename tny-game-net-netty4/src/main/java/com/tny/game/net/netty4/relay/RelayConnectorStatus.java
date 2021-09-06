package com.tny.game.net.netty4.relay;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:10 下午
 */
enum RelayConnectorStatus {

	/**
	 * 初始化
	 */
	INIT(true),

	/**
	 * 连接中
	 */
	CONNECTING(false),

	/**
	 * 打开
	 */
	OPEN(false),

	/**
	 * 失败
	 */
	DISCONNECT(true),

	/**
	 * 关闭
	 */
	CLOSE(false),

	//
	;

	private final boolean canConnect;

	RelayConnectorStatus(boolean canConnect) {
		this.canConnect = canConnect;
	}

	boolean isCanConnect() {
		return canConnect;
	}
}
