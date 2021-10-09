package com.tny.game.data.configuration.redisson;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
public class RedissonStorageAccessorFactorySetting {

	private String name;

	private String tableHead;

	public String getName() {
		return name;
	}

	public RedissonStorageAccessorFactorySetting setName(String name) {
		this.name = name;
		return this;
	}

	public String getTableHead() {
		return tableHead;
	}

	public RedissonStorageAccessorFactorySetting setTableHead(String tableHead) {
		this.tableHead = tableHead;
		return this;
	}

}
