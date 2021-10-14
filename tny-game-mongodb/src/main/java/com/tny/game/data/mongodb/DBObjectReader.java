package com.tny.game.data.mongodb;

import com.mongodb.BasicDBObject;

/**
 * <p>
 */
public interface DBObjectReader {

	<T> T read(BasicDBObject source, Class<T> targetClass);

}
