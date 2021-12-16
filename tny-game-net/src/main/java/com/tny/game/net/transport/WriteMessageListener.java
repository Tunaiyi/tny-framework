package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-19 19:21
 */
@FunctionalInterface
public interface WriteMessageListener {

	/**
	 * 发送结速
	 */
	void onWrite(MessageWriteAwaiter future);

}
