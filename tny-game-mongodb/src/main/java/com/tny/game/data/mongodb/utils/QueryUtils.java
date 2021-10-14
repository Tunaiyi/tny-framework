package com.tny.game.data.mongodb.utils;

import org.springframework.data.mongodb.core.query.*;

import java.util.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-03 04:17
 */
public class QueryUtils {

	public static Criteria idIs(Object id) {
		return Criteria.where(MongoUtils.ID).is(id);
	}

	public static <T> Criteria idIn(Collection<T> ids) {
		if (ids.size() == 1) {
			for (T id : ids) {
				return idIs(id);
			}
		}
		return Criteria.where(MongoUtils.ID).in(ids);
	}

	public static Query queryIdIs(Object id) {
		return Query.query(idIs(id));
	}

	public static <T> Query queryIdIn(Collection<T> ids) {
		return Query.query(idIn(ids));
	}

	public static Query queryIdIn(Object... ids) {
		return Query.query(idIn(Arrays.asList(ids)));
	}

	public static void updateIfNotNull(Update update, String field, Object value) {
		if (value != null) {
			update.set(field, value);
		}
	}

}
