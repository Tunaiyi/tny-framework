package com.tny.game.basics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.tny.game.basics.develop.ItemModelPaths.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/24 1:09 下午
 */
@ConfigurationProperties("tny.basics.item.default-item-model")
public class DefaultItemModelProperties {

	private String[] paths = {DEFAULT_ITEM_MODEL_CONFIG_PATH};

	public String[] getPaths() {
		return paths;
	}

	public DefaultItemModelProperties setPaths(String[] paths) {
		this.paths = paths;
		return this;
	}

}
