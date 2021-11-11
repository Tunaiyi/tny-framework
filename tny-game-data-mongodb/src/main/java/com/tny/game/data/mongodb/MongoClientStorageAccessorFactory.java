package com.tny.game.data.mongodb;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:21 下午
 */
public class MongoClientStorageAccessorFactory extends AbstractCachedFactory<Class<?>, StorageAccessor<?, ?>> implements StorageAccessorFactory {

	public static final String ACCESSOR_NAME = "mongoClientStorageAccessorFactory";

	private EntityIdConverterFactory entityIdConverterFactory;

	private EntityObjectConverter entityObjectConverter;

	private MongoDatabaseFactory databaseFactory;

	public MongoClientStorageAccessorFactory() {
	}

	public MongoClientStorageAccessorFactory(
			EntityObjectConverter entityObjectConverter,
			EntityIdConverterFactory entityIdConverterFactory,
			MongoDatabaseFactory databaseFactory) {
		this.entityIdConverterFactory = entityIdConverterFactory;
		this.entityObjectConverter = entityObjectConverter;
		this.databaseFactory = databaseFactory;
	}

	@Override
	public <A extends StorageAccessor<?, ?>> A createAccessor(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
		EntityIdConverter<?, ?, ?> idConverter = entityIdConverterFactory.createConverter(scheme, keyMaker);
		return loadOrCreate(scheme.getEntityClass(), (clazz) -> new MongoClientStorageAccessor<>(
				clazz, as(idConverter), entityObjectConverter, databaseFactory.getMongoDatabase()));
	}

	public MongoClientStorageAccessorFactory setEntityIdConverterFactory(EntityIdConverterFactory entityIdConverterFactory) {
		this.entityIdConverterFactory = entityIdConverterFactory;
		return this;
	}

	public MongoClientStorageAccessorFactory setEntityObjectConverter(EntityObjectConverter entityObjectConverter) {
		this.entityObjectConverter = entityObjectConverter;
		return this;
	}

	public MongoClientStorageAccessorFactory setDatabaseFactory(MongoDatabaseFactory databaseFactory) {
		this.databaseFactory = databaseFactory;
		return this;
	}

}
