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
