package com.tny.game.data.mongodb;

import org.bson.Document;

/**
 * <p>
 */
public interface MongoDocumentMapper {

	<T> T fromDocument(Document source, Class<T> targetClass);

	Document toDocument(Object source);

}
