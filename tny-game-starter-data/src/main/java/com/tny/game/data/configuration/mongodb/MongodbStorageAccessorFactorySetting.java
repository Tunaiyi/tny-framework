package com.tny.game.data.configuration.mongodb;

import com.tny.game.boot.utils.*;
import com.tny.game.data.mongodb.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
public class MongodbStorageAccessorFactorySetting {

	private String idConverter = BeanNameUtils.defaultName(EntityIdConverter.class);

	private String entityConverter = BeanNameUtils.lowerCamelName(JsonEntityDocumentConverter.class);

	private String mongoTemplate = "";

	public String getIdConverter() {
		return idConverter;
	}

	public MongodbStorageAccessorFactorySetting setIdConverter(String idConverter) {
		this.idConverter = idConverter;
		return this;
	}

	public String getEntityConverter() {
		return entityConverter;
	}

	public MongodbStorageAccessorFactorySetting setEntityConverter(String entityConverter) {
		this.entityConverter = entityConverter;
		return this;
	}

	public String getMongoTemplate() {
		return mongoTemplate;
	}

	public MongodbStorageAccessorFactorySetting setMongoTemplate(String mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		return this;
	}

}
