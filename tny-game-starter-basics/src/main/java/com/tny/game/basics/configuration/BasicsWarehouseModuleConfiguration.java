/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
