package com.tny.game.data.mongodb;

/**
 * <p>
 */
public interface EntityConverter {

	<T> T convert(Object source, Class<T> targetClass);

}
