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

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.tny.game.data.mongodb.configuration.MongodbAutoConfiguration;
import org.bson.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;
import org.springframework.boot.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Mongo 数据访问真实服务实例验证（specs R5"干净环境可复现执行"，add-integration-testing 任务 5.3）。
 * <p>
 * 装配策略同 {@link RedissonDataAccessIT}：SpringApplication 走框架自有
 * {@code MongodbAutoConfiguration}（读 {@code tny.datasource.mongodb.setting.uri}），
 * 容器 uri 注入，断言 MongoClient 真实读写。镜像取 mongo:4.4（本地已有、与 legacy
 * mongo-java-driver 3.12 协议兼容；设计钉 6.0 是为驱动兼容上界，4.4 同样在下界内——
 * CI 环境若需 6.0 仅改常量，见 verification 范围披露）。
 */
@Tag("integration")
@Isolated
@Tag("docker")
@Testcontainers(disabledWithoutDocker = true)
class MongodbDataAccessIT {

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:4.4");

    @Configuration(proxyBeanMethods = false)
    @Import(MongodbAutoConfiguration.class)
    static class MongodbITApp {
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
    void frameworkDatasourceWiringTalksToRealMongo() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("tny.datasource.mongodb.setting.uri", MONGO.getConnectionString());
        properties.put("tny.datasource.mongodb.setting.database", "it-db");

        SpringApplication application = new SpringApplication(MongodbITApp.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setDefaultProperties(properties);
        context = application.run();

        MongoClient client = context.getBean(MongoClient.class);
        assertThat(client).as("框架数据源装配产出 MongoClient").isNotNull();

        MongoDatabase database = client.getDatabase("it-db");
        MongoCollection<Document> collection = database.getCollection("it-coll");
        collection.deleteMany(new Document());

        collection.insertOne(new Document("name", "tny-it").append("score", 42));
        Document found = collection.find(Filters.eq("name", "tny-it")).first();
        assertThat(found).as("真实 Mongo 写入可读回").isNotNull();
        assertThat(found.get("score")).isEqualTo(42);

        collection.deleteMany(new Document("name", "tny-it"));
        assertThat(collection.countDocuments()).as("删除生效").isZero();
    }

}
