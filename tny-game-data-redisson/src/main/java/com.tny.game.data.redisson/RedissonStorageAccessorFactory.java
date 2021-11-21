package com.tny.game.data.redisson;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
import com.tny.game.redisson.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:21 下午
 */
public class RedissonStorageAccessorFactory implements StorageAccessorFactory {

	public static final String ACCESSOR_NAME = "redissonStorageAccessorFactory";

	private String tableHead;

	private String dataSource;

	private EntityIdConverterFactory entityIdConverterFactory;

	public RedissonStorageAccessorFactory(String dataSource, String tableHead) {
		this.tableHead = ifNull(tableHead, "");
		this.dataSource = dataSource;
	}

	@Override
	public <A extends StorageAccessor<?, ?>> A createAccessor(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
		Class<?> entityClass = scheme.getEntityClass();
		TypedRedisson<?> redisson = RedissonFactory.createTypedRedisson(entityClass);
		String tableKey = StringUtils.isEmpty(tableHead) ? entityClass.getSimpleName() : tableHead + ":" + entityClass.getSimpleName();
		EntityIdConverter<?, ?, ?> idConverter = entityIdConverterFactory.createConverter(scheme, keyMaker);
		return as(new RedissonStorageAccessor<>(dataSource, tableKey, idConverter, as(redisson)));
	}

	public RedissonStorageAccessorFactory setTableHead(String tableHead) {
		this.tableHead = tableHead;
		return this;
	}

	public RedissonStorageAccessorFactory setDataSource(String dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	public RedissonStorageAccessorFactory setEntityIdConverterFactory(EntityIdConverterFactory entityIdConverterFactory) {
		this.entityIdConverterFactory = entityIdConverterFactory;
		return this;
	}

}
