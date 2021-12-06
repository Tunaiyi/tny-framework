package com.tny.game.data.mongodb;

import org.bson.Document;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/21 9:07 下午
 */
public interface MongoEntityConverter {

	<T> T convertToRead(Document source, Class<T> targetClass);

	Document convertToWrite(Object id, Object source);

}
