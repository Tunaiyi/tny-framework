/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.configuration;

import com.tny.game.basics.mongodb.mapper.*;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.data.mongodb.*;
import com.tny.game.data.mongodb.configuration.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.stream.Collectors;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MongodbAutoConfiguration.class)
@EnableConfigurationProperties({
        DefaultItemModelProperties.class,
})
public class BasicsMongoAutoConfiguration {

    @Bean
    MongoBasicsObjectMapperCustomizer mongoBasicsObjectMapperCustomizer() {
        return new MongoBasicsObjectMapperCustomizer();
    }

    @Bean
    GameJsonMongoEntityConverter gameJsonEntityObjectConverter(
            ObjectProvider<MongoDocumentEnhance<?>> enhances,
            ObjectProvider<ObjectMapperCustomizer> mapperCustomizers) {
        ObjectMapperFactory factory = new ObjectMapperFactory();
        factory.addCustomizers(mapperCustomizers.stream().collect(Collectors.toList()));
        return new GameJsonMongoEntityConverter(factory.create(), enhances.stream().collect(Collectors.toList()));
    }

}
