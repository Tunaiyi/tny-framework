/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.mongodb;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.tny.game.common.utils.*;
import com.tny.game.data.*;
import com.tny.game.data.mongodb.utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.slf4j.*;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;
import java.util.Map.Entry;

import static com.tny.game.data.mongodb.utils.QueryUtils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/11 3:07 下午
 */
public class MongoClientStorageAccessor<K extends Comparable<?>, O> extends MongoStorageAccessor<K, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoClientStorageAccessor.class);

    private final MongoDatabase database;

    private final MongoTemplate mongoTemplate;

    private final MongoEntityConverter entityObjectConverter;

    private final EntityIdConverter<K, O, ?> idConvertor;

    private final ThreadLocal<List<WriteModel<? extends Document>>> bulkOperations;

    private String collectionName;

    public MongoClientStorageAccessor(Class<O> entityClass, EntityIdConverter<K, O, ?> idConverter, MongoEntityConverter entityObjectConverter,
            MongoTemplate mongoTemplate, String dataSource) {
        super(entityClass, dataSource);
        this.idConvertor = idConverter;
        this.entityObjectConverter = entityObjectConverter;
        this.mongoTemplate = mongoTemplate;
        MongoDatabaseFactory factory = mongoTemplate.getMongoDatabaseFactory();
        this.database = factory.getMongoDatabase();
        this.bulkOperations = ThreadLocal.withInitial(LinkedList::new);
    }

    private String collectionName() {
        if (collectionName != null) {
            return collectionName;
        }
        return collectionName = mongoTemplate.getCollectionName(entityClass);
    }

    protected MongoCollection<Document> collection() {
        return database.getCollection(entityClass.getSimpleName());

    }

    @Override
    public <T> List<T> find(Map<String, Object> findValue, Class<T> returnClass) {
        MongoCollection<Document> collection = collection();
        Criteria criteria = null;
        for (Entry<String, Object> entry : findValue.entrySet()) {
            if (criteria == null) {
                criteria = Criteria.where(entry.getKey()).is(entry.getValue());
            } else {
                criteria.and(entry.getKey()).is(entry.getValue());
            }
        }
        if (criteria == null) {
            return findAll(returnClass);
        }
        List<T> objects = new ArrayList<>();
        FindIterable<Document> documents = collection.find(Query.query(criteria).getQueryObject());
        for (Document document : documents) {
            objects.add(entityObjectConverter.convertToRead(document, returnClass));
        }
        return objects;
    }

    @Override
    public <T> List<T> findAll(Class<T> returnClass) {
        MongoCollection<Document> collection = collection();
        FindIterable<Document> documents = collection.find();
        List<T> objects = new ArrayList<>();
        for (Document document : documents) {
            objects.add(entityObjectConverter.convertToRead(document, returnClass));
        }
        return objects;
    }

    @Override
    public List<O> find(Map<String, Object> findValue) {
        return find(findValue, this.entityClass);
    }

    @Override
    public List<O> findAll() {
        return findAll(this.entityClass);
    }

    @Override
    public O get(K key) {
        Object id = keyToId(key);
        MongoCollection<Document> collection = collection();
        FindIterable<Document> documents = collection.find(queryIdIs(id).getQueryObject());
        Document document = documents.first();
        if (document == null) {
            return null;
        }
        return entityObjectConverter.convertToRead(document, entityClass);
    }

    @Override
    public List<O> get(Collection<? extends K> keys) {
        List<O> objects = new ArrayList<>();
        if (keys.size() == 1) {
            for (K key : keys) {
                O value = get(key);
                if (value == null) {
                    return objects;
                }
                objects.add(value);
                return objects;
            }
        }
        List<Object> idList = keysToIdList(keys);
        MongoCollection<Document> collection = collection();
        FindIterable<Document> documents = collection.find(queryIdIn(idList).getQueryObject());
        for (Document document : documents) {
            //			Object id = entityToId(key);
            objects.add(entityObjectConverter.convertToRead(document, this.entityClass));
        }
        return objects;
    }

    private void addBulkOperation(WriteModel<? extends Document> model) {
        this.bulkOperations.get().add(model);
    }

    @Override
    public boolean insert(O object) {
        try {
            Document document = toDocument(object);
            addBulkOperation(new InsertOneModel<>(document));
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
            for (O object : objects) {
                Document document = toDocument(object);
                addBulkOperation(new InsertOneModel<>(document));
            }
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
            addBulkOperation(new UpdateOneModel<>(query.getQueryObject(), updateDocument));
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
        for (O object : objects) {
            update(object);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean save(O object) {
        try {
            Document entityDocument = toDocument(object);
            Document updateDocument = new Document();
            updateDocument.put("$set", entityDocument);
            Object id = entityToId(object);
            Query query = queryIdIs(id);
            addBulkOperation(new UpdateOneModel<>(query.getQueryObject(), updateDocument, new UpdateOptions().upsert(true)));
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
        for (O object : objects) {
            save(object);
        }
        return Collections.emptyList();
    }

    @Override
    public void delete(O object) {
        Object id = entityToId(object);
        Query query = queryIdIs(id);
        addBulkOperation(new DeleteOneModel<>(query.getQueryObject(), new DeleteOptions()));
    }

    @Override
    public void delete(Collection<O> objects) {
        for (O object : objects) {
            delete(object);
        }
    }

    @Override
    public void execute() {
        List<WriteModel<? extends Document>> operations = bulkOperations.get();
        if (CollectionUtils.isNotEmpty(operations)) {
            try {
                BulkWriteResult result = collection().bulkWrite(operations);
                LOGGER.error("同步统计: 插入数量 {}; 修改数量 {}; 删除数量 {}; ",
                        result.getInsertedCount(), result.getModifiedCount(), result.getDeletedCount());
            } finally {
                operations.clear();
            }
        }
    }

    private Document toDocument(O entity) {
        Object id = entityToId(entity);
        return this.entityObjectConverter.convertToWrite(id, entity);
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