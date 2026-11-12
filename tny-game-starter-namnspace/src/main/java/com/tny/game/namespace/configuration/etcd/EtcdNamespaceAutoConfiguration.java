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

package com.tny.game.namespace.configuration.etcd;

import com.tny.game.codec.*;
import com.tny.game.codec.configuration.*;
import com.tny.game.namespace.etcd.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        EtcdNamespaceProperties.class,
})
@AutoConfigureAfter(ObjectCodecAutoConfiguration.class)
public class EtcdNamespaceAutoConfiguration {

    @Bean
    @ConditionalOnClass(EtcdNamespaceExplorerFactory.class)
    EtcdNamespaceExplorerFactory etcdNamespaceExplorerFactory(EtcdNamespaceProperties properties, ObjectCodecAdapter objectCodecAdapter) {
        return new EtcdNamespaceExplorerFactory(properties, null, objectCodecAdapter);
    }

}
