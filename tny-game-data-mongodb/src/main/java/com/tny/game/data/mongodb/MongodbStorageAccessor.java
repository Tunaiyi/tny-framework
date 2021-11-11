package com.tny.game.data.mongodb;

import com.mongodb.bulk.BulkWriteResult;
import com.tny.game.common.utils.*;
import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.mongodb.utils.*;
import org.bson.Document;
import org.slf4j.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static com.tny.game.data.mongodb.utils.QueryUtils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/11 3:07 下午
 */
public class MongodbStorageAccessor<K extends Comparable<?>, O> implements BatchStorageAccessor<K, O> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongodbStorageAccessor.class);

	private final MongoTemplate mongoTemplate;

	private final Class<O> entityClass;

	private final EntityObjectConverter entityObjectConverter;

	private final EntityIdConverter<K, O, ?> idConvertor;

	private final ThreadLocal<BulkOperations> bulkOperations;

	public MongodbStorageAccessor(Class<O> entityClass, EntityIdConverter<K, O, ?> idConverter, EntityObjectConverter entityObjectConverter,
			MongoTemplate mongoTemplate) {
		this.entityClass = entityClass;
		this.idConvertor = idConverter;
		this.entityObjectConverter = entityObjectConverter;
		this.mongoTemplate = mongoTemplate;
		this.bulkOperations = new ThreadLocal<>();
		//ThreadLocal.withInitial(() -> mongoTemplate.bulkOps(BulkMode.ORDERED, entityClass));
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
			this.operations().insert(object);
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
			this.operations().insert(objects);
			return Collections.emptyList();
		} catch (RuntimeException e) {
			if (MongoUtils.checkDuplicateKey(e)) {
				LOGGER.warn("insert {} Duplicate", entityClass, e);
				return Collections.emptyList();
			}
			throw e;
		}
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
			this.operations().updateOne(query, update);
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
			Document entityDocument = toDocument(object);
			if (MongoUtils.isIDNullValue(entityDocument)) {
				entityDocument.remove("_id");
			}
			Document updateDocument = new Document();
			updateDocument.put("$set", entityDocument);
			Object id = entityToId(object);
			Query query = queryIdIs(id);
			Update update = Update.fromDocument(updateDocument);
			this.operations().upsert(query, update);
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
		Object id = entityToId(object);
		Query query = queryIdIs(id);
		this.operations().remove(query);
	}

	@Override
	public void delete(Collection<O> objects) {
		List<Query> queries = new ArrayList<>();
		for (O object : objects) {
			Object id = entityToId(object);
			queries.add(queryIdIs(id));
		}
		this.operations().remove(queries);
	}

	@Override
	public void execute() {
		BulkOperations operations = bulkOperations.get();
		if (operations != null) {
			try {
				BulkWriteResult result = operations.execute();
				LOGGER.error("同步统计: 插入数量 {}; 修改数量 {}; 删除数量 {}; ",
						result.getInsertedCount(), result.getModifiedCount(), result.getDeletedCount());
			} finally {
				bulkOperations.remove();
			}
		}
	}

	private BulkOperations operations() {
		BulkOperations operations = this.bulkOperations.get();
		if (operations == null) {
			operations = mongoTemplate.bulkOps(BulkMode.ORDERED, entityClass);
			this.bulkOperations.set(operations);
		}
		return operations;
	}

	private Document toDocument(O entity) {
		Object id = entityToId(entity);
		return this.entityObjectConverter.convertToWrite(id, entity, Document.class);
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