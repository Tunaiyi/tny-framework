package com.tny.game.data.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/12 4:23 下午
 */
@ConfigurationProperties(prefix = "tny.data")
public class DataConfigurationProperties {

	private boolean enable = true;

	public boolean isEnable() {
		return enable;
	}

	public DataConfigurationProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

}
