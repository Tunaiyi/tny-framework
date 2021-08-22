package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/18 5:39 下午
 */
public interface WritableConnection extends Connection {

	/**
	 * 创建写出应答对象
	 *
	 * @return 返回写出应答对象
	 */
	WriteMessagePromise createWritePromise();

}
