/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.basics.configuration;

import com.tny.game.basics.item.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.stream.Collectors;

import static com.tny.game.basics.configuration.BasicsPropertyConstants.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(BasicsWarehouseModuleProperties.class)
@ConditionalOnProperty(name = BASICS_WAREHOUSE_MODULE_ENABLE, havingValue = "true")
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
