package com.tny.game.data.mongodb;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/21 9:07 下午
 */
public interface EntityObjectConverter {

	<T> T convertToRead(Object source, Class<T> targetClass);

	<T> T convertToWrite(Object id, Object source, Class<T> targetClass);

}
