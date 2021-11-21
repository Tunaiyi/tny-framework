package com.tny.game.data.mongodb;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:21 下午
 */
public class MongoClientStorageAccessorFactory extends BaseMongoStorageAccessorFactory {

	public static final String ACCESSOR_NAME = "mongoClientStorageAccessorFactory";

	public MongoClientStorageAccessorFactory(String dataSource) {
		super(dataSource);
	}

	public MongoClientStorageAccessorFactory(EntityIdConverterFactory entityIdConverterFactory,
			EntityObjectConverter entityObjectConverter, MongoTemplate mongoTemplate, String dataSource) {
		super(entityIdConverterFactory, entityObjectConverter, mongoTemplate, dataSource);
	}

	@Override
	protected StorageAccessor<?, ?> newMongoStorageAccessor(Class<?> entityClass, EntityIdConverter<?, ?, ?> idConverter) {
		return new MongoClientStorageAccessor<>(entityClass, as(idConverter), entityObjectConverter, mongoTemplate, dataSource);
	}

}
