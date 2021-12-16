package com.tny.game.net.transport;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.message.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageReceipt {

	/**
	 * @return 获取响应 Future, 如果没有返回 null
	 */
	StageFuture<Message> respondFuture();

	/**
	 * @return 是否有响应 Future
	 */
	boolean isRespondAwaitable();

	/**
	 * @return 获取发送 Future, 如果没有返回 null
	 */
	StageFuture<Void> writeFuture();

	/**
	 * @return 是否有发送 Future
	 */
	boolean isWriteAwaitable();

}
