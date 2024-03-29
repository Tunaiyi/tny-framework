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
