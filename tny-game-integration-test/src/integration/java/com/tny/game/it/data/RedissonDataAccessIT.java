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
package com.tny.game.it.data;

import com.tny.game.redisson.configuration.RedissonAutoConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.redisson.api.*;
import org.springframework.boot.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Redis 数据访问真实服务实例验证（specs R5"干净环境可复现执行"，add-integration-testing 任务 5.2）。
 * <p>
 * 装配策略（design D5）：以 {@code SpringApplication.run} 走框架自有 starter 装配链
 * （{@code RedissonAutoConfiguration} → {@code ImportRedisDataSourceBeanDefinitionRegistrar} 读
 * {@code tny.datasource.redisson.setting.*}），连接信息注入容器随机映射端口；断言 RedissonClient
 * 的 map/lock 真实读写。范围披露：{@code RedissonStorageAccessor} 的对象-关系层依赖业务
 * EntityScheme 注解元数据，属实体装配域（另见 verification.md），本 IT 钉至框架数据源装配 +
 * 真实服务交互层——即 accessor 内部实际使用的 map 原语。
 */
@Tag("integration")
@Isolated
@Tag("docker")
@Testcontainers(disabledWithoutDocker = true)
class RedissonDataAccessIT {

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7.2-alpine").withExposedPorts(6379);

    @Configuration(proxyBeanMethods = false)
    @Import(RedissonAutoConfiguration.class)
    static class RedissonITApp {
    }

    private ConfigurableApplicationContext context;

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    @Test
    void frameworkDatasourceWiringTalksToRealContainer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("tny.datasource.redisson.setting.host", REDIS.getHost());
        properties.put("tny.datasource.redisson.setting.port", String.valueOf(REDIS.getFirstMappedPort()));

        SpringApplication application = new SpringApplication(RedissonITApp.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setDefaultProperties(properties);
        context = application.run();

        RedissonClient client = context.getBean(RedissonClient.class);
        assertThat(client).as("框架数据源装配产出 RedissonClient").isNotNull();

        RMap<String, String> map = client.getMap("it-table");
        assertThat(map.fastPutIfAbsent("k1", "v1")).isTrue();
        assertThat(map.get("k1")).isEqualTo("v1");

        RLock lock = client.getLock("it-lock");
        assertThat(lock.tryLock()).as("真实 Redis 锁获取").isTrue();
        assertThat(lock.isHeldByCurrentThread()).isTrue();
        lock.unlock();
        assertThat(lock.isLocked()).isFalse();
    }

}
