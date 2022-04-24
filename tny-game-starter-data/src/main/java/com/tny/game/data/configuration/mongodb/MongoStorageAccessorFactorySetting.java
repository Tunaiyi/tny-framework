package com.tny.game.data.configuration.mongodb;

import com.tny.game.boot.utils.*;
import com.tny.game.data.mongodb.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
public class MongoStorageAccessorFactorySetting {

    private String idConverterFactory = BeanNameUtils.lowerCamelName(MongoEntityIdConverterFactory.class);

    private String entityObjectConverter = BeanNameUtils.lowerCamelName(JsonMongoEntityConverter.class);

    private String dataSource = "";

    public String getIdConverterFactory() {
        return idConverterFactory;
    }

    public MongoStorageAccessorFactorySetting setIdConverterFactory(String idConverterFactory) {
        this.idConverterFactory = idConverterFactory;
        return this;
    }

    public String getEntityObjectConverter() {
        return entityObjectConverter;
    }

    public MongoStorageAccessorFactorySetting setEntityObjectConverter(String entityObjectConverter) {
        this.entityObjectConverter = entityObjectConverter;
        return this;
    }

    public String getDataSource() {
        return dataSource;
    }

    public MongoStorageAccessorFactorySetting setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

}
