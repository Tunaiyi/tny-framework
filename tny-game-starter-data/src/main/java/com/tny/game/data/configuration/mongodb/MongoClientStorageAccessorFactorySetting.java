package com.tny.game.data.configuration.mongodb;

import com.tny.game.boot.utils.*;
import com.tny.game.data.*;
import com.tny.game.data.mongodb.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
public class MongoClientStorageAccessorFactorySetting {

	private String idConverter = BeanNameUtils.defaultName(EntityIdConverter.class);

	private String entityConverter = BeanNameUtils.lowerCamelName(JsonEntityDocumentConverter.class);

	private String dataSource = "";

	public String getIdConverter() {
		return idConverter;
	}

	public MongoClientStorageAccessorFactorySetting setIdConverter(String idConverter) {
		this.idConverter = idConverter;
		return this;
	}

	public String getEntityConverter() {
		return entityConverter;
	}

	public MongoClientStorageAccessorFactorySetting setEntityConverter(String entityConverter) {
		this.entityConverter = entityConverter;
		return this;
	}

	public String getDataSource() {
		return dataSource;
	}

	public MongoClientStorageAccessorFactorySetting setDataSource(String dataSource) {
		this.dataSource = dataSource;
		return this;
	}

}
