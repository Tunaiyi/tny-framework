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

package com.tny.game.redisson.configuration;

import com.tny.game.boot.registrar.*;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.boot.utils.BeanNameUtils.*;
import static java.util.Objects.*;

/**
 * <p>
 */
public class ImportRedisDataSourceBeanDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";

    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata annotationMetadata, @Nonnull BeanDefinitionRegistry registry) {
        RedissonDataSourceProperties properties = loadProperties(RedissonDataSourceProperties.class);
        if (!properties.isEnable()) {
            return;
        }

        ObjectProvider<RedissonAutoConfigurationCustomizer> customizerObjectProvider = this.beanFactory.getBeanProvider(
                RedissonAutoConfigurationCustomizer.class);
        List<RedissonAutoConfigurationCustomizer> customizers = customizerObjectProvider
                .orderedStream()
                .collect(Collectors.toList());

        RedisDataSourceSetting sourceSetting = properties.getSetting();
        if (sourceSetting != null) {
            registerRedissonClient(registry, "default", sourceSetting, customizers);
        }
        properties.getSettings()
                .forEach((name, setting) -> registerRedissonClient(registry, name, setting, customizers));
    }

    public void registerRedissonClient(BeanDefinitionRegistry registry, String beanName, RedisDataSourceSetting redisProperties,
            List<RedissonAutoConfigurationCustomizer> redissonAutoConfigurationCustomizers) {
        Config config;
        Duration timeoutValue = redisProperties.getTimeout();
        int timeout;
        if (null == timeoutValue) {
            timeout = 10000;
        } else {
            timeout = (int) timeoutValue.toMillis();
        }

        Cluster cluster;
        Sentinel sentinel = redisProperties.getSentinel();
        if (sentinel != null) {
            List<String> nodesValue = sentinel.getNodes();
            String[] nodes = nodesValue.toArray(new String[0]);
            config = new Config();
            config.useSentinelServers()
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .addSentinelAddress(nodes)
                    .setDatabase(redisProperties.getDatabase())
                    .setConnectTimeout(timeout)
                    .setPassword(redisProperties.getPassword());
        } else if ((cluster = redisProperties.getCluster()) != null) {
            List<String> nodesObject = cluster.getNodes();
            String[] nodes = convert(requireNonNull(nodesObject));

            config = new Config();
            config.useClusterServers()
                    .addNodeAddress(nodes)
                    .setConnectTimeout(timeout)
                    .setPassword(redisProperties.getPassword());
        } else {
            config = new Config();
            String prefix = REDIS_PROTOCOL_PREFIX;
            var ssl = redisProperties.getSsl();
            if (ssl.isEnabled()) {
                prefix = REDISS_PROTOCOL_PREFIX;
            }

            config.useSingleServer()
                    .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setConnectTimeout(timeout)
                    .setDatabase(redisProperties.getDatabase())
                    .setPassword(redisProperties.getPassword());
        }
        if (redissonAutoConfigurationCustomizers != null) {
            for (RedissonAutoConfigurationCustomizer customizer : redissonAutoConfigurationCustomizers) {
                customizer.customize(config);
            }
        }

        RedissonClient redissonClient = Redisson.create(config);
        String clientBeanName = nameOf(beanName, RedissonClient.class);
        registry.registerBeanDefinition(clientBeanName, BeanDefinitionBuilder
                .genericBeanDefinition(RedissonClient.class, () -> redissonClient)
                .setDestroyMethodName("shutdown")
                .getBeanDefinition());

        RedisConnectionFactory redissonConnectionFactory = new RedissonConnectionFactory(redissonClient);
        String connectionFactoryBeanName = nameOf(beanName, RedisConnectionFactory.class);
        registry.registerBeanDefinition(connectionFactoryBeanName, BeanDefinitionBuilder
                .genericBeanDefinition(RedisConnectionFactory.class, () -> redissonConnectionFactory)
                .getBeanDefinition());

        if (redisProperties.isTemplateEnable()) {
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(redissonConnectionFactory);
            String templateBeanName = nameOf(beanName, RedisTemplate.class);
            registry.registerBeanDefinition(templateBeanName, BeanDefinitionBuilder
                    .genericBeanDefinition(RedisTemplate.class, () -> template)
                    .getBeanDefinition());
        }

        if (redisProperties.isStringTemplateEnable()) {
            StringRedisTemplate stringTemplate = new StringRedisTemplate();
            stringTemplate.setConnectionFactory(redissonConnectionFactory);
            String stringTemplateBeanName = nameOf(beanName, StringRedisTemplate.class);
            registry.registerBeanDefinition(stringTemplateBeanName, BeanDefinitionBuilder
                    .genericBeanDefinition(RedisTemplate.class, () -> stringTemplate)
                    .getBeanDefinition());
        }

    }

    private String[] convert(List<String> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith(REDIS_PROTOCOL_PREFIX) && !node.startsWith(REDISS_PROTOCOL_PREFIX)) {
                nodes.add(REDIS_PROTOCOL_PREFIX + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[0]);
    }

}
