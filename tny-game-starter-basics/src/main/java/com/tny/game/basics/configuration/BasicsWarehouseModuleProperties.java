package com.tny.game.basics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.tny.game.basics.configuration.BasicsPropertiesConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/15 10:23 下午
 */
@ConfigurationProperties(BASICS_WAREHOUSE_MODULE)
public class BasicsWarehouseModuleProperties {

	private boolean enable = false;

	public boolean isEnable() {
		return enable;
	}

	public BasicsWarehouseModuleProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

}
