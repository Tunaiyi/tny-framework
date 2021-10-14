package com.tny.game.data.mongodb;

import com.mongodb.BasicDBObject;

/**
 * <p>
 */
public interface DBObjectWriter {

	<S> BasicDBObject write(S source, Class<S> sourceClass);

}
