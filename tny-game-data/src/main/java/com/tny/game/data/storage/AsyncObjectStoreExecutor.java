package com.tny.game.data.storage;

/**
 * 同步线程执行器
 *
 * @author KGTny
 */
public interface AsyncObjectStoreExecutor {

	/**
	 * 注册持久化器
	 *
	 * @param storage 持久化器
	 */
	void register(AsyncObjectStorage<?, ?> storage);

	/**
	 * 关闭
	 */
	boolean shutdown() throws InterruptedException;

}
