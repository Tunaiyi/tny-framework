package com.tny.game.data.mongodb;

import org.bson.Document;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/12/2 4:05 下午
 */
public interface MongoDocumentEnhance<T> {

	Class<T> type();

	void onRead(Document source, T target);

	void onWrite(T source, Document target);

}
