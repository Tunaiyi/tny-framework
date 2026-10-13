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

package com.tny.game.data.configuration.mongodb;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.codec.jackson.mapper.*;
import com.tny.game.data.*;
import com.tny.game.data.configuration.*;
import com.tny.game.data.mongodb.*;
import com.tny.game.data.mongodb.configuration.*;
import com.tny.game.data.mongodb.loader.*;
import com.tny.game.data.mongodb.mapper.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MongoClientStorageAccessorFactory.class)
@AutoConfigureAfter({MongodbAutoConfiguration.class})
@AutoConfigureBefore(DataAutoConfiguration.class)
@Import({
        ImportMongoStorageAccessorFactoryDefinitionRegistrar.class,
})
@EnableConfigurationProperties({
        MongoStorageAccessorFactoryProperties.class,
})
@ConditionalOnProperty(value = "tny.data.enable", matchIfMissing = true)
public class MongoStorageAutoConfiguration {

    @Bean
    @ConditionalOnClass(EntityIdConverter.class)
    public MongoEntityIdConverterFactory mongoEntityIdConverterFactory() {
        return new MongoEntityIdConverterFactory();
    }

    @Bean
    @ConditionalOnMissingBean(JsonMongoEntityConverter.class)
    JsonMongoEntityConverter jsonMongoEntityConverter(
            ObjectProvider<MongoDocumentEnhance<?>> enhances,
            ObjectProvider<ObjectMapperCustomizer> mapperCustomizers) {
        ObjectMapper mapper = ObjectMapperFactory.createMapper();
        mapper.registerModule(MongoObjectMapperMixLoader.getModule())
                .setAnnotationIntrospector(new MongoIdIntrospector())
                .configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
        mapperCustomizers.forEach((action) -> action.customize(mapper));
        return new JsonMongoEntityConverter(mapper, enhances.stream().collect(Collectors.toList()));
    }

}
