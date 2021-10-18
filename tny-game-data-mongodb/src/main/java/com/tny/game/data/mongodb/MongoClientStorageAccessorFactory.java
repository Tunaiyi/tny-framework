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

	private EntityIdConverter<?, ?, ?> idConverter;

	private EntityConverter entityConverter;

	private MongoDatabaseFactory databaseFactory;

	public MongoClientStorageAccessorFactory() {
	}

	public MongoClientStorageAccessorFactory(
			EntityIdConverter<?, ?, ?> idConvertor,
			EntityConverter entityConverter,
			MongoDatabaseFactory databaseFactory) {
		this.idConverter = idConvertor;
		this.entityConverter = entityConverter;
		this.databaseFactory = databaseFactory;
	}

	@Override
	public <A extends StorageAccessor<?, ?>> A createAccessor(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
		return loadOrCreate(scheme.getEntityClass(), (clazz) -> new MongoClientStorageAccessor<>(
				clazz, as(idConverter), entityConverter, databaseFactory.getMongoDatabase()));
	}

	public MongoClientStorageAccessorFactory setIdConverter(EntityIdConverter<?, ?, ?> idConverter) {
		this.idConverter = idConverter;
		return this;
	}

	public MongoClientStorageAccessorFactory setEntityConverter(EntityConverter entityConverter) {
		this.entityConverter = entityConverter;
		return this;
	}

	public MongoClientStorageAccessorFactory setDatabaseFactory(MongoDatabaseFactory databaseFactory) {
		this.databaseFactory = databaseFactory;
		return this;
	}

}
