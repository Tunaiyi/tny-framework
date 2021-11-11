package com.tny.game.data.mongodb;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:21 下午
 */
public class MongoTemplateStorageAccessorFactory extends AbstractCachedFactory<Class<?>, StorageAccessor<?, ?>> implements StorageAccessorFactory {

	public static final String ACCESSOR_NAME = "mongoTemplateStorageAccessorFactory";

	private EntityIdConverterFactory entityIdConverterFactory;

	private EntityObjectConverter entityObjectConverter;

	private MongoTemplate mongoTemplate;

	public MongoTemplateStorageAccessorFactory() {
	}

	public MongoTemplateStorageAccessorFactory(EntityIdConverterFactory entityIdConverterFactory, EntityObjectConverter entityObjectConverter,
			MongoTemplate mongoTemplate) {
		this.entityIdConverterFactory = entityIdConverterFactory;
		this.entityObjectConverter = entityObjectConverter;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public <A extends StorageAccessor<?, ?>> A createAccessor(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
		EntityIdConverter<?, ?, ?> idConverter = entityIdConverterFactory.createConverter(scheme, keyMaker);
		return loadOrCreate(scheme.getEntityClass(), (clazz) -> new MongodbStorageAccessor<>(
				clazz, as(idConverter), entityObjectConverter, mongoTemplate));
	}

	public MongoTemplateStorageAccessorFactory setEntityIdConverterFactory(EntityIdConverterFactory entityIdConverterFactory) {
		this.entityIdConverterFactory = entityIdConverterFactory;
		return this;
	}

	public MongoTemplateStorageAccessorFactory setEntityObjectConverter(EntityObjectConverter entityObjectConverter) {
		this.entityObjectConverter = entityObjectConverter;
		return this;
	}

	public MongoTemplateStorageAccessorFactory setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		return this;
	}

}
