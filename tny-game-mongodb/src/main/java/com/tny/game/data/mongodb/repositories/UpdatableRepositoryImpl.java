package com.tny.game.data.mongodb.repositories;

import com.tny.game.data.mongodb.*;
import com.tny.game.data.mongodb.utils.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2020/3/1.
 */
public class UpdatableRepositoryImpl<T, ID> extends SimpleMongoRepository<T, ID> implements UpdatableRepository<T, ID> {

	private MongoOperations mongoOperations;

	private MongoEntityInformation<T, ID> metadata;

	private MongoDocumentMapper entityConverter;

	protected Class<T> clazz;

	/**
	 * Creates a new {@link SimpleMongoRepository} for the given {@link MongoEntityInformation} and {@link MongoTemplate}.
	 *
	 * @param metadata        must not be {@literal null}.
	 * @param mongoOperations must not be {@literal null}.
	 */
	public UpdatableRepositoryImpl(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations, MongoDocumentMapper entityConverter) {
		super(metadata, mongoOperations);
		this.metadata = metadata;
		this.mongoOperations = mongoOperations;
		this.entityConverter = entityConverter;
		clazz = metadata.getJavaType();
	}

	public MongoOperations mongoOperations() {
		return this.mongoOperations;
	}

	private MongoDocumentMapper converter() {
		return entityConverter;
	}

	private <T> Document toDocument(T entity) {
		return this.converter().toDocument(entity);
	}

	@Override
	public T loadOrCreate(Query query, T entity) {
		Document entityDocument = toDocument(entity);
		if (MongoUtils.isIDNullValue(entityDocument)) {
			entityDocument.remove("_id");
		}
		Document updateDocument = new Document();
		updateDocument.put("$setOnInsert", entityDocument);
		Update update = Update.fromDocument(updateDocument);
		return updateOrInsert(query, entity, updateDocument);
	}

	@Override
	public T findAndSave(Query query, T entity) {
		Document entityDocument = toDocument(entity);
		if (MongoUtils.isIDNullValue(entityDocument)) {
			entityDocument.remove("_id");
		}
		Document updateDocument = new Document();
		updateDocument.put("$set", entityDocument);
		return updateOrInsert(query, entity, updateDocument);
	}

	@Override
	public T findAndUpdate(Query query, T entity) {
		Document entityDocument = this.converter().toDocument(entity);
		if (MongoUtils.isIDNullValue(entityDocument)) {
			entityDocument.remove("_id");
		}
		Document updateDocument = new Document();
		updateDocument.put("$set", entityDocument);
		return update(query, entity, updateDocument);
	}

	private T update(Query query, T entity, Document updateDocument) {
		Update update = Update.fromDocument(updateDocument);
		Class<T> entityClass = as(entity.getClass());
		return mongoOperations.findAndModify(query, update,
				FindAndModifyOptions.options().returnNew(true), entityClass);
	}

	private T updateOrInsert(Query query, T entity, Document updateDocument) {
		Update update = Update.fromDocument(updateDocument);
		Class<T> entityClass = as(entity.getClass());
		return mongoOperations.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true).upsert(true), entityClass);
	}

	// @Override
	// public T updateExclude(Query query, T document, String... excludeFields) {
	//     Document updateDocument = this.converter().convert(document, Document.class);
	//     Update update = Update.fromDocument(updateDocument, excludeFields);
	//     return mongoOperations.findAndModify(query, update, clazz);
	// }
	//
	// @Override
	// public T updateInclude(Query query, T document, String... includeFields) {
	//     Document updateDocument = this.converter().convert(document, Document.class);
	//     Update update = new Update();
	//     for (String field : includeFields) {
	//         update.pull(field, updateDocument.get(field));
	//     }
	//     return mongoOperations.findAndModify(query, update, clazz);
	// }

	@Autowired(required = false)
	private void setEntityConverter(MongoDocumentMapper entityConverter) {
		this.entityConverter = entityConverter;
	}

}
