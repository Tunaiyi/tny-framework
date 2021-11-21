package com.tny.game.data.mongodb;

import com.tny.game.data.accessor.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/11 3:07 下午
 */
public abstract class MongoStorageAccessor<K extends Comparable<?>, O> implements BatchStorageAccessor<K, O> {

	private final String dataSource;

	protected final Class<O> entityClass;

	public MongoStorageAccessor(Class<O> entityClass, String dataSource) {
		this.entityClass = entityClass;
		this.dataSource = dataSource;
	}

	public Class<O> getEntityClass() {
		return entityClass;
	}

	@Override
	public String getDataSource() {
		return dataSource;
	}

}