package com.tny.game.basics.configuration;

import com.tny.game.basics.item.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import static com.tny.game.basics.configuration.BasicsPropertiesConstants.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableConfigurationProperties(BasicsWarehouseModuleProperties.class)
@ConditionalOnProperty(name = BASICS_WAREHOUSE_MODULE_ENABLE, havingValue = "true")
public class BasicsWarehouseModuleConfiguration {

	@Bean
	@ConditionalOnBean({BasicsWarehouseModuleProperties.class})
	public GameWarehouseService gameWarehouseService() {
		return new GameWarehouseService();
	}

}
