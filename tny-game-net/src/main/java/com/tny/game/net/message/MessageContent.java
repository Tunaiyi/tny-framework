package com.tny.game.net.message;

import com.tny.game.common.type.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageContent extends MessageSchema {

	/**
	 * @return 获取结果码
	 */
	int getCode();

	/**
	 * @return 是否存在消息
	 */
	boolean existBody();

	/**
	 * @return 获取消息体
	 */
	Object getBody();

	/**
	 * @return 获取消息体
	 */
	<T> T bodyAs(Class<T> clazz);

	/**
	 * @return 获取消息体
	 */
	<T> T bodyAs(ReferenceType<T> clazz);

}
