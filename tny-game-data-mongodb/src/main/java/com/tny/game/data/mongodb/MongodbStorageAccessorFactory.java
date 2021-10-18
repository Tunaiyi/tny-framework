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
public class MongodbStorageAccessorFactory extends AbstractCachedFactory<Class<?>, StorageAccessor<?, ?>> implements StorageAccessorFactory {

	public static final String ACCESSOR_NAME = "mongodbStorageAccessorFactory";

	private EntityIdConverter<?, ?, ?> idConverter;

	private EntityConverter entityConverter;

	private MongoTemplate mongoTemplate;

	public MongodbStorageAccessorFactory() {
	}

	public MongodbStorageAccessorFactory(EntityIdConverter<?, ?, ?> idConvertor, EntityConverter entityConverter, MongoTemplate mongoTemplate) {
		this.idConverter = idConvertor;
		this.entityConverter = entityConverter;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public <A extends StorageAccessor<?, ?>> A createAccessor(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
		return loadOrCreate(scheme.getEntityClass(), (clazz) -> new MongodbStorageAccessor<>(
				clazz, as(idConverter), entityConverter, mongoTemplate));
	}

	public MongodbStorageAccessorFactory setIdConverter(EntityIdConverter<?, ?, ?> idConverter) {
		this.idConverter = idConverter;
		return this;
	}

	public MongodbStorageAccessorFactory setEntityConverter(EntityConverter entityConverter) {
		this.entityConverter = entityConverter;
		return this;
	}

	public MongodbStorageAccessorFactory setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		return this;
	}

}
