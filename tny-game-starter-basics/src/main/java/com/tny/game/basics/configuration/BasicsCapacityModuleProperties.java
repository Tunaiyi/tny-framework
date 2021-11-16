package com.tny.game.basics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/15 10:23 下午
 */
@ConfigurationProperties(BasicsPropertiesConstants.BASICS_CAPACITY_MODULE)
public class BasicsCapacityModuleProperties {

	private boolean enable = false;

	public boolean isEnable() {
		return enable;
	}

	public BasicsCapacityModuleProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

}
