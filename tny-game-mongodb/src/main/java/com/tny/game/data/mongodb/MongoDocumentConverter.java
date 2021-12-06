package com.tny.game.data.mongodb;

import org.bson.Document;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/12/2 9:05 下午
 */
public interface MongoDocumentConverter extends MongoDocumentMapper {

	default <T> T convert(Object source, Class<T> targetClass) {
		if (source == null) {
			return null;
		}
		if (targetClass == Document.class) {
			return as(toDocument(source));
		} else {
			return fromDocument((Document)source, targetClass);
		}
	}

}
