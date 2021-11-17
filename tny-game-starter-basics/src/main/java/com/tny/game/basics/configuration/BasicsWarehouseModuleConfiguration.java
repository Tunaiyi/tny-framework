package com.tny.game.basics.configuration;

import com.tny.game.basics.item.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.stream.Collectors;

import static com.tny.game.basics.configuration.BasicsPropertiesConstants.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableConfigurationProperties(BasicsWarehouseModuleProperties.class)
@ConditionalOnProperty(name = BASICS_WAREHOUSE_MODULE_ENABLE, havingValue = "true")
@AutoConfigureAfter(BasicsAutoConfiguration.class)
public class BasicsWarehouseModuleConfiguration {

	@Bean
	@Primary
	@ConditionalOnBean(StuffOwnerExplorer.class)
	public GameStuffOwnerService<?, ?, ?> gameStuffOwnerService(StuffOwnerExplorer stuffOwnerExplorer) {
		return new GameStuffOwnerService<>(stuffOwnerExplorer);
	}

	@Bean
	@ConditionalOnBean(WarehouseManager.class)
	public TradeService tradeService(
			WarehouseManager warehouseManager,
			ObjectProvider<PrimaryStuffService<?>> primaryObjectProvider,
			ObjectProvider<StuffService<?>> serviceObjectProvider) {
		return new GameTradeService(
				warehouseManager,
				primaryObjectProvider.getIfUnique(),
				serviceObjectProvider.stream().collect(Collectors.toList()));
	}

}
