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

package com.tny.game.data.configuration;

import com.tny.game.data.configuration.mongodb.MongoStorageAccessorFactoryProperties;
import com.tny.game.data.configuration.mongodb.MongoStorageAccessorFactorySetting;
import com.tny.game.data.configuration.redisson.RedissonStorageAccessorFactoryProperties;
import com.tny.game.data.configuration.redisson.RedissonStorageAccessorFactorySetting;
import com.tny.game.data.configuration.storage.AsyncObjectStorageFactoriesProperties;
import com.tny.game.data.configuration.storage.QueueObjectStorageFactorySetting;
import org.junit.jupiter.api.*;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * reduce-code-duplication D7 绑定面回归钉桩（重构前绿）：starter-data 三 `*Properties`
 * 的键集 / 默认值 / 嵌套 Setting 绑定现状账。属性文件→对象经生产同款
 * `Binder.get(environment).bind(prefix, propertiesClass)` 路径（ImportConfigurationBeanDefinitionRegistrar#loadProperties）。
 * 提父收敛后期望值一字不改仍须全绿——键集钉桩尤其防止父类多余 public 访问器泄漏进绑定面。
 */
class FactoryPropertiesBinderRegressionTest {

    private static final String MONGO_PREFIX = "tny.data.storage-accessor.mongo-accessor";
    private static final String REDISSON_PREFIX = "tny.data.storage-accessor.redisson-accessor";
    private static final String ASYNC_PREFIX = "tny.data.object-storage.async-storage";

    private static Binder binderOf(String... lines) throws Exception {
        Properties properties = new Properties();
        properties.load(new StringReader(String.join("\n", lines)));
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new PropertiesPropertySource("pin", properties));
        return Binder.get(environment);
    }

    private static List<String> beanPropertyNames(Class<?> type) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
        return Arrays.stream(beanInfo.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    // ---------- MongoStorageAccessorFactoryProperties ----------

    @Test
    @DisplayName("Mongo：JavaBean 键集现状 = {accessor, accessors, enable}")
    void mongoBeanKeySet() throws Exception {
        assertEquals(List.of("accessor", "accessors", "enable"),
                beanPropertyNames(MongoStorageAccessorFactoryProperties.class));
    }

    @Test
    @DisplayName("Mongo：默认值现状——enable=true、accessor 懒造默认 Setting、accessors 空可变 HashMap（live 引用）")
    void mongoDefaults() {
        MongoStorageAccessorFactoryProperties properties = new MongoStorageAccessorFactoryProperties();
        assertTrue(properties.isEnable());
        MongoStorageAccessorFactorySetting defaultSetting = new MongoStorageAccessorFactorySetting();
        assertNotNull(properties.getAccessor());
        assertEquals(defaultSetting.getDataSource(), properties.getAccessor().getDataSource());
        assertEquals(defaultSetting.getIdConverterFactory(), properties.getAccessor().getIdConverterFactory());
        assertEquals(defaultSetting.getEntityObjectConverter(), properties.getAccessor().getEntityObjectConverter());
        assertTrue(properties.getAccessors().isEmpty());
        // 现状：getter 直返 live map（可变、非包装）
        assertSame(properties.getAccessors(), properties.getAccessors());
        Map<String, MongoStorageAccessorFactorySetting> probe = new HashMap<>();
        probe.put("probe", new MongoStorageAccessorFactorySetting());
        assertDoesNotThrow(() -> properties.getAccessors().putAll(probe));
    }

    @Test
    @DisplayName("Mongo：链式 setter 返回子类自身不断链；setAccessors(null)/setAccessor(null) 现状直存 null")
    void mongoSetterChainAndNull() {
        MongoStorageAccessorFactoryProperties properties = new MongoStorageAccessorFactoryProperties();
        MongoStorageAccessorFactorySetting setting = new MongoStorageAccessorFactorySetting();
        Map<String, MongoStorageAccessorFactorySetting> map = new HashMap<>();
        MongoStorageAccessorFactoryProperties chained = properties
                .setEnable(false).setAccessor(setting).setAccessors(map);
        assertSame(properties, chained);
        assertFalse(properties.isEnable());
        assertSame(setting, properties.getAccessor());
        assertSame(map, properties.getAccessors());
        properties.setAccessor(null);
        assertNull(properties.getAccessor(), "现状：setAccessor(null) 直存 null，无懒造兜底");
        properties.setAccessors(null);
        assertNull(properties.getAccessors(), "现状：setAccessors(null) 直存 null");
    }

    @Test
    @DisplayName("Mongo：属性文件→对象 键集/默认值/嵌套 Setting 现状")
    void mongoBinder() throws Exception {
        MongoStorageAccessorFactoryProperties properties = binderOf(
                MONGO_PREFIX + ".enable=false",
                MONGO_PREFIX + ".accessor.data-source=primary-ds",
                MONGO_PREFIX + ".accessors.alpha.data-source=alpha-ds",
                MONGO_PREFIX + ".accessors.beta.data-source=beta-ds"
        ).bind(MONGO_PREFIX, MongoStorageAccessorFactoryProperties.class).orElseThrow(() -> new IllegalStateException("bind failed"));
        MongoStorageAccessorFactorySetting defaultSetting = new MongoStorageAccessorFactorySetting();
        assertFalse(properties.isEnable());
        assertNotNull(properties.getAccessor());
        assertEquals("primary-ds", properties.getAccessor().getDataSource());
        assertEquals(defaultSetting.getIdConverterFactory(), properties.getAccessor().getIdConverterFactory(),
                "部分嵌套绑定不得冲掉其余默认值");
        assertEquals(defaultSetting.getEntityObjectConverter(), properties.getAccessor().getEntityObjectConverter());
        assertEquals(Set.of("alpha", "beta"), properties.getAccessors().keySet());
        assertEquals("alpha-ds", properties.getAccessors().get("alpha").getDataSource());
        assertEquals("beta-ds", properties.getAccessors().get("beta").getDataSource());
        assertEquals(defaultSetting.getIdConverterFactory(), properties.getAccessors().get("alpha").getIdConverterFactory());
    }

    // ---------- RedissonStorageAccessorFactoryProperties ----------

    @Test
    @DisplayName("Redisson：JavaBean 键集现状 = {accessor, accessors, enable}")
    void redissonBeanKeySet() throws Exception {
        assertEquals(List.of("accessor", "accessors", "enable"),
                beanPropertyNames(RedissonStorageAccessorFactoryProperties.class));
    }

    @Test
    @DisplayName("Redisson：默认值现状——accessor 懒造默认 Setting（dataSource/tableHead 默认 null）、accessors 空可变")
    void redissonDefaults() {
        RedissonStorageAccessorFactoryProperties properties = new RedissonStorageAccessorFactoryProperties();
        assertTrue(properties.isEnable());
        RedissonStorageAccessorFactorySetting defaultSetting = new RedissonStorageAccessorFactorySetting();
        assertNotNull(properties.getAccessor());
        assertEquals(defaultSetting.getDataSource(), properties.getAccessor().getDataSource());
        assertEquals(defaultSetting.getTableHead(), properties.getAccessor().getTableHead());
        assertEquals(defaultSetting.getIdConverterFactory(), properties.getAccessor().getIdConverterFactory());
        assertNull(properties.getAccessor().getDataSource(), "现状钉死：Redisson dataSource 默认 null（与 Mongo 空串不同）");
        assertTrue(properties.getAccessors().isEmpty());
        assertSame(properties.getAccessors(), properties.getAccessors());
    }

    @Test
    @DisplayName("Redisson：链式 setter 不断链 + null 直存现状")
    void redissonSetterChainAndNull() {
        RedissonStorageAccessorFactoryProperties properties = new RedissonStorageAccessorFactoryProperties();
        RedissonStorageAccessorFactorySetting setting = new RedissonStorageAccessorFactorySetting();
        Map<String, RedissonStorageAccessorFactorySetting> map = new HashMap<>();
        assertSame(properties, properties.setEnable(true).setAccessor(setting).setAccessors(map));
        assertSame(setting, properties.getAccessor());
        assertSame(map, properties.getAccessors());
        properties.setAccessors(null);
        assertNull(properties.getAccessors());
        properties.setAccessor(null);
        assertNull(properties.getAccessor());
    }

    @Test
    @DisplayName("Redisson：属性文件→对象 键集/默认值/嵌套 Setting 现状")
    void redissonBinder() throws Exception {
        RedissonStorageAccessorFactoryProperties properties = binderOf(
                REDISSON_PREFIX + ".enable=false",
                REDISSON_PREFIX + ".accessor.table-head=t_head",
                REDISSON_PREFIX + ".accessors.alpha.data-source=alpha-ds",
                REDISSON_PREFIX + ".accessors.beta.data-source=beta-ds"
        ).bind(REDISSON_PREFIX, RedissonStorageAccessorFactoryProperties.class).orElseThrow(() -> new IllegalStateException("bind failed"));
        RedissonStorageAccessorFactorySetting defaultSetting = new RedissonStorageAccessorFactorySetting();
        assertFalse(properties.isEnable());
        assertEquals("t_head", properties.getAccessor().getTableHead());
        assertEquals(defaultSetting.getIdConverterFactory(), properties.getAccessor().getIdConverterFactory());
        assertNull(properties.getAccessor().getDataSource());
        assertEquals(Set.of("alpha", "beta"), properties.getAccessors().keySet());
        assertEquals("alpha-ds", properties.getAccessors().get("alpha").getDataSource());
        assertEquals(defaultSetting.getTableHead(), properties.getAccessors().get("alpha").getTableHead());
    }

    // ---------- AsyncObjectStorageFactoriesProperties ----------

    @Test
    @DisplayName("Async：JavaBean 键集现状 = {enable, storage, storages}（与 Mongo/Redisson 异名，禁并入 accessor 父）")
    void asyncBeanKeySet() throws Exception {
        assertEquals(List.of("enable", "storage", "storages"),
                beanPropertyNames(AsyncObjectStorageFactoriesProperties.class));
    }

    @Test
    @DisplayName("Async：默认值现状——enable=true、storage 懒造默认 Setting、storages 空可变 HashMap")
    void asyncDefaults() {
        AsyncObjectStorageFactoriesProperties properties = new AsyncObjectStorageFactoriesProperties();
        assertTrue(properties.isEnable());
        QueueObjectStorageFactorySetting defaultSetting = new QueueObjectStorageFactorySetting();
        assertNotNull(properties.getStorage());
        assertEquals(defaultSetting.getStoreExecutor(), properties.getStorage().getStoreExecutor());
        assertEquals(defaultSetting.getAccessorFactory(), properties.getStorage().getAccessorFactory());
        assertTrue(properties.getStorages().isEmpty());
        assertSame(properties.getStorages(), properties.getStorages());
        assertDoesNotThrow(() -> properties.getStorages().put("probe", new QueueObjectStorageFactorySetting()));
    }

    @Test
    @DisplayName("Async：链式 setter 返回子类自身不断链；null 直存现状")
    void asyncSetterChainAndNull() {
        AsyncObjectStorageFactoriesProperties properties = new AsyncObjectStorageFactoriesProperties();
        QueueObjectStorageFactorySetting setting = new QueueObjectStorageFactorySetting();
        Map<String, QueueObjectStorageFactorySetting> map = new HashMap<>();
        AsyncObjectStorageFactoriesProperties chained = properties
                .setEnable(false).setStorage(setting).setStorages(map);
        assertSame(properties, chained);
        assertFalse(properties.isEnable());
        assertSame(setting, properties.getStorage());
        assertSame(map, properties.getStorages());
        properties.setStorage(null);
        assertNull(properties.getStorage(), "现状：setStorage(null) 直存 null，无懒造兜底");
        properties.setStorages(null);
        assertNull(properties.getStorages());
    }

    @Test
    @DisplayName("Async：属性文件→对象 键集/默认值/嵌套 Setting 现状（kebab 键 relaxed 绑定）")
    void asyncBinder() throws Exception {
        AsyncObjectStorageFactoriesProperties properties = binderOf(
                ASYNC_PREFIX + ".enable=false",
                ASYNC_PREFIX + ".storage.store-executor=custom-executor",
                ASYNC_PREFIX + ".storages.alpha.accessor-factory=factory-a",
                ASYNC_PREFIX + ".storages.beta.accessor-factory=factory-b"
        ).bind(ASYNC_PREFIX, AsyncObjectStorageFactoriesProperties.class).orElseThrow(() -> new IllegalStateException("bind failed"));
        QueueObjectStorageFactorySetting defaultSetting = new QueueObjectStorageFactorySetting();
        assertFalse(properties.isEnable());
        assertEquals("custom-executor", properties.getStorage().getStoreExecutor());
        assertEquals(defaultSetting.getAccessorFactory(), properties.getStorage().getAccessorFactory());
        assertEquals(Set.of("alpha", "beta"), properties.getStorages().keySet());
        assertEquals("factory-a", properties.getStorages().get("alpha").getAccessorFactory());
        assertEquals(defaultSetting.getStoreExecutor(), properties.getStorages().get("beta").getStoreExecutor());
    }

}
