package com.tny.game.data.mongodb;

import com.mongodb.client.result.UpdateResult;
import com.tny.game.common.utils.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.mongodb.utils.*;
import org.bson.Document;
import org.slf4j.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static com.tny.game.data.mongodb.utils.QueryUtils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/11 3:07 下午
 */
public class MongodbStorageAccessor<K extends Comparable<?>, O> implements StorageAccessor<K, O> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongodbStorageAccessor.class);

	private final MongoTemplate mongoTemplate;

	private final Class<O> entityClass;

	private final EntityConverter entityConverter;

	private final EntityIdConverter<K, O, ?> idConvertor;

	public MongodbStorageAccessor(Class<O> entityClass, EntityIdConverter<K, O, ?> idConverter, EntityConverter entityConverter,
			MongoTemplate mongoTemplate) {
		this.entityClass = entityClass;
		this.idConvertor = idConverter;
		this.entityConverter = entityConverter;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public O get(K key) {
		Object id = keyToId(key);
		return mongoTemplate.findOne(queryIdIs(id), entityClass);
	}

	@Override
	public List<O> get(Collection<? extends K> keys) {
		if (keys.size() == 1) {
			for (K key : keys) {
				Object id = keyToId(key);
				return mongoTemplate.find(queryIdIs(id), entityClass);
			}
		}
		List<Object> idList = keysToIdList(keys);
		return mongoTemplate.find(QueryUtils.queryIdIn(idList), entityClass);
	}

	@Override
	public boolean insert(O object) {
		try {
			mongoTemplate.insert(object);
			return true;
		} catch (RuntimeException e) {
			if (MongoUtils.checkDuplicateKey(e)) {
				LOGGER.warn("insert {} Duplicate", entityClass, e);
				return false;
			}
			throw e;
		}
	}

	@Override
	public Collection<O> insert(Collection<O> objects) {
		try {
			Collection<O> inserted = mongoTemplate.insert(objects);
			if (inserted == objects) {
				return Collections.emptyList();
			}
			Collection<O> failed = new HashSet<>(objects);
			failed.removeAll(inserted);
			return failed;
		} catch (RuntimeException e) {
			if (MongoUtils.checkDuplicateKey(e)) {
				LOGGER.warn("insert {} Duplicate", entityClass, e);
				return Collections.emptyList();
			}
			throw e;
		}
	}

	private <T> Document toDocument(T entity) {
		return this.entityConverter.convert(entity, Document.class);
	}

	@Override
	public boolean update(O object) {
		try {
			Document entityDocument = toDocument(object);
			if (MongoUtils.isIDNullValue(entityDocument)) {
				entityDocument.remove("_id");
			}
			Document updateDocument = new Document();
			updateDocument.put("$set", entityDocument);
			Object id = entityToId(object);
			Query query = queryIdIs(id);
			Update update = Update.fromDocument(updateDocument);
			UpdateResult result = mongoTemplate.updateFirst(query, update, entityClass);
			return result.getModifiedCount() > 0;
		} catch (RuntimeException e) {
			if (MongoUtils.checkDuplicateKey(e)) {
				LOGGER.warn("insert {} Duplicate", entityClass, e);
				return false;
			}
			throw e;
		}
	}

	@Override
	public Collection<O> update(Collection<O> objects) {
		Collection<O> failed = new ArrayList<>();
		for (O entity : objects) {
			if (!update(entity)) {
				failed.add(entity);
			}
		}
		return failed;
	}

	@Override
	public boolean save(O object) {
		try {
			mongoTemplate.save(object);
			return true;
		} catch (RuntimeException e) {
			if (MongoUtils.checkDuplicateKey(e)) {
				LOGGER.warn("insert {} Duplicate", entityClass, e);
				return false;
			}
			throw e;
		}
	}

	@Override
	public Collection<O> save(Collection<O> objects) {
		Collection<O> failed = new ArrayList<>();
		for (O entity : objects) {
			if (!update(entity)) {
				failed.add(entity);
			}
		}
		return failed;
	}

	@Override
	public void delete(O object) {
		mongoTemplate.remove(object);
	}

	@Override
	public void delete(Collection<O> objects) {
		mongoTemplate.remove(queryIdIn(entitiesToIdList(objects)), entityClass);
	}

	private Object keyToId(K key) {
		return Asserts.checkNotNull(idConvertor.keyToId(key), "key {} id is null", entityClass, key);
	}

	private Object entityToId(O entity) {
		return Asserts.checkNotNull(idConvertor.entityToId(entity), "entity {} id is null", entityClass, entity);
	}

	private List<Object> keysToIdList(Collection<? extends K> keys) {
		List<Object> idList = new ArrayList<>(keys.size());
		for (K key : keys) {
			Object id = keyToId(key);
			idList.add(id);
		}
		return idList;
	}

	private List<Object> entitiesToIdList(Collection<? extends O> entities) {
		List<Object> idList = new ArrayList<>(entities.size());
		for (O entity : entities) {
			Object id = entityToId(entity);
			idList.add(id);
		}
		return idList;
	}

}