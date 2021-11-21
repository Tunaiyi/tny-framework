package com.tny.game.data.mongodb;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:21 下午
 */
public abstract class BaseMongoStorageAccessorFactory extends AbstractCachedFactory<Class<?>, StorageAccessor<?, ?>> implements StorageAccessorFactory {

	protected final String dataSource;

	protected EntityIdConverterFactory entityIdConverterFactory;

	protected EntityObjectConverter entityObjectConverter;

	protected MongoTemplate mongoTemplate;

	public BaseMongoStorageAccessorFactory(String dataSource) {
		this.dataSource = dataSource;
	}

	public BaseMongoStorageAccessorFactory(EntityIdConverterFactory entityIdConverterFactory, EntityObjectConverter entityObjectConverter,
			MongoTemplate mongoTemplate, String dataSource) {
		this.dataSource = dataSource;
		this.entityIdConverterFactory = entityIdConverterFactory;
		this.entityObjectConverter = entityObjectConverter;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public <A extends StorageAccessor<?, ?>> A createAccessor(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
		EntityIdConverter<?, ?, ?> idConverter = entityIdConverterFactory.createConverter(scheme, keyMaker);
		return loadOrCreate(scheme.getEntityClass(), clazz -> this.newMongoStorageAccessor(clazz, idConverter));
	}

	protected abstract StorageAccessor<?, ?> newMongoStorageAccessor(Class<?> entityClass, EntityIdConverter<?, ?, ?> idConverter);

	public BaseMongoStorageAccessorFactory setEntityIdConverterFactory(EntityIdConverterFactory entityIdConverterFactory) {
		this.entityIdConverterFactory = entityIdConverterFactory;
		return this;
	}

	public BaseMongoStorageAccessorFactory setEntityObjectConverter(EntityObjectConverter entityObjectConverter) {
		this.entityObjectConverter = entityObjectConverter;
		return this;
	}

	public BaseMongoStorageAccessorFactory setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		return this;
	}

}
