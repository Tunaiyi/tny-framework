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

package com.tny.game.data.configuration.storage;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * AsyncObjectStorageFactoriesProperties 的访问器样板父类（reduce-code-duplication D7：同包 public abstract 父，层级纯增量）。
 * <p>
 * 方法体逐字搬自原类克隆段，不做任何行为修正：
 * <ul>
 * <li>enable 默认 true；</li>
 * <li>storage 懒造默认值由子类构造器传入（`new QueueObjectStorageFactorySetting()` 留子类）；</li>
 * <li>storages 默认 new HashMap<>()——现状 getter 直返 live 可变引用；</li>
 * <li>setter 均为赋值+链式返回，null 直存（禁止顺手修）。</li>
 * </ul>
 * 本族键名为 storage/storages，与 Mongo/Redisson 的 accessor/accessors 名族不兼容
 * （共父会泄漏多余绑定键、改变绑定面），故与 AbstractStorageAccessorFactoryProperties 分立两父。
 * 子类保留链式 setter 协变覆写、协变 getter 覆写与类上全部 Spring 注解（@ConfigurationProperties 注册点不动）。
 * 绑定面回归钉桩见 FactoryPropertiesBinderRegressionTest。
 *
 * @param <S> 对象存储工厂 Setting 类型
 */
public abstract class AbstractObjectStorageFactoriesProperties<S> {

    private boolean enable = true;

    @NestedConfigurationProperty
    private S storage;

    private Map<String, S> storages = new HashMap<>();

    protected AbstractObjectStorageFactoriesProperties(S defaultStorage) {
        this.storage = defaultStorage;
    }

    public boolean isEnable() {
        return enable;
    }

    public AbstractObjectStorageFactoriesProperties<S> setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public S getStorage() {
        return storage;
    }

    public AbstractObjectStorageFactoriesProperties<S> setStorage(S storage) {
        this.storage = storage;
        return this;
    }

    public Map<String, S> getStorages() {
        return storages;
    }

    public AbstractObjectStorageFactoriesProperties<S> setStorages(Map<String, S> storages) {
        this.storages = storages;
        return this;
    }

}
