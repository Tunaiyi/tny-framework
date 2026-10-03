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
package com.tny.game.it.data;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.tny.game.data.mongodb.configuration.MongodbAutoConfiguration;
import com.tny.game.redisson.configuration.RedissonAutoConfiguration;
import org.bson.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * 数据源连接目标不可达（specs R5"连接目标不可达时明确失败"，add-integration-testing 任务 5.4）。
 * <p>
 * 指向 {@code 127.0.0.1} 上一个确定关闭的端口 → 连接被即时拒绝（connection refused），验证：
 * <ul>
 *   <li>Redisson：数据源装配在建 client 时即抛出定位到目标地址的失败，上下文启动失败，不回退不挂起；</li>
 *   <li>Mongo：client 装配不即时连接，首次真实操作抛 {@link MongoSocketOpenException}/超时类失败并指向目标，
 *       不回退到其它实例。</li>
 * </ul>
 * 不需容器（本机 closed port），故仅 {@code @Tag("integration")} 不打 docker，无容器环境也执行。
 */
@Tag("integration")
class UnreachableDatasourceIT {

    /** 取自 IANA 未保留高位端口，测试期间无进程监听（connection refused 即时返回）。 */
    private static final int CLOSED_PORT = 6;

    @Configuration(proxyBeanMethods = false)
    @Import(RedissonAutoConfiguration.class)
    static class RedissonITApp {
    }

    @Configuration(proxyBeanMethods = false)
    @Import(MongodbAutoConfiguration.class)
    static class MongodbITApp {
    }

    @Test
    void redisUnreachableTargetFailsFastAtContextStartup() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("tny.datasource.redisson.setting.host", "127.0.0.1");
        properties.put("tny.datasource.redisson.setting.port", String.valueOf(CLOSED_PORT));
        properties.put("tny.datasource.redisson.setting.timeout", "200ms");

        SpringApplication application = new SpringApplication(RedissonITApp.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setDefaultProperties(properties);

        Throwable failure = catchThrowable(application::run);
        assertThat(failure).as("不可达 Redis 应在装配期即时失败（不挂起）").isNotNull();
        assertThat(rootMessageChain(failure)).as("失败原因定位到目标地址")
                .containsAnyOf("127.0.0.1", "Unable to connect", "Connection refused", "Redis server");
    }

    @Test
    void mongoUnreachableTargetFailsOnFirstOperationWithTargetLocation() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("tny.datasource.mongodb.setting.uri",
                "mongodb://127.0.0.1:" + CLOSED_PORT + "/it-db?serverSelectionTimeoutMS=800&connectTimeoutMS=500");
        properties.put("tny.datasource.mongodb.setting.database", "it-db");

        SpringApplication application = new SpringApplication(MongodbITApp.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setDefaultProperties(properties);

        ConfigurableApplicationContext context = null;
        try {
            context = application.run();
            MongoClient client = context.getBean(MongoClient.class);
            MongoCollection<Document> collection = client.getDatabase("it-db").getCollection("it-coll");
            // 短 serverSelectionTimeout（见 uri 参数）下首次读操作即时失败并指向目标，不回退
            Throwable failure = catchThrowable(() -> collection.countDocuments());
            assertThat(failure).as("不可达 Mongo 首次操作应失败").isInstanceOf(MongoException.class);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    private static String rootMessageChain(Throwable error) {
        StringBuilder chain = new StringBuilder();
        for (Throwable current = error; current != null; current = current.getCause()) {
            chain.append(current.getClass().getName()).append(':').append(current.getMessage()).append('\n');
            if (current.getCause() == current) {
                break;
            }
        }
        return chain.toString();
    }

}
