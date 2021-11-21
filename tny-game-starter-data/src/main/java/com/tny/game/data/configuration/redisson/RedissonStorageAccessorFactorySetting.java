package com.tny.game.data.configuration.redisson;

import com.tny.game.boot.utils.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
public class RedissonStorageAccessorFactorySetting {

	private String dataSource;

	private String tableHead;

	private String idConverterFactory = BeanNameUtils.lowerCamelName(EntityKeyMakerIdConverterFactory.class);

	public String getDataSource() {
		return dataSource;
	}

	public RedissonStorageAccessorFactorySetting setDataSource(String dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	public String getTableHead() {
		return tableHead;
	}

	public RedissonStorageAccessorFactorySetting setTableHead(String tableHead) {
		this.tableHead = tableHead;
		return this;
	}

	public String getIdConverterFactory() {
		return idConverterFactory;
	}

	public RedissonStorageAccessorFactorySetting setIdConverterFactory(String idConverterFactory) {
		this.idConverterFactory = idConverterFactory;
		return this;
	}

}
