package com.tny.game.data.configuration.mongodb;

import com.tny.game.boot.utils.*;
import com.tny.game.data.mongodb.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
public class MongoTemplateStorageAccessorFactorySetting {

	private String dataSource = "";

	private String entityObjectConverter = BeanNameUtils.lowerCamelName(JsonEntityObjectConverter.class);

	private String idConverterFactory = BeanNameUtils.lowerCamelName(MongoEntityIdConverterFactory.class);

	public String getIdConverterFactory() {
		return idConverterFactory;
	}

	public MongoTemplateStorageAccessorFactorySetting setIdConverterFactory(String idConverterFactory) {
		this.idConverterFactory = idConverterFactory;
		return this;
	}

	public String getEntityObjectConverter() {
		return entityObjectConverter;
	}

	public MongoTemplateStorageAccessorFactorySetting setEntityObjectConverter(String entityObjectConverter) {
		this.entityObjectConverter = entityObjectConverter;
		return this;
	}

	public String getDataSource() {
		return dataSource;
	}

	public MongoTemplateStorageAccessorFactorySetting setDataSource(String dataSource) {
		this.dataSource = dataSource;
		return this;
	}

}
