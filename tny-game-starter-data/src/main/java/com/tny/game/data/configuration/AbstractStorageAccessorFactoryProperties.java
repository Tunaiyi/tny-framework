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

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * Mongo/RedissonStorageAccessorFactoryProperties 共享的访问器样板父类（reduce-code-duplication D7：public abstract 父，层级纯增量）。
 * <p>
 * 方法体逐字搬自原两类克隆段，不做任何行为修正：
 * <ul>
 * <li>enable 默认 true；</li>
 * <li>accessor 懒造默认值由子类构造器传入（`new XxxSetting()` 留子类）；</li>
 * <li>accessors 默认 new HashMap<>()——现状 getter 直返 live 可变引用（与 netty4 侧只读包装不同，禁止抹平）；</li>
 * <li>setter 均为赋值+链式返回，null 直存（setAccessor(null)/setAccessors(null) 现状置 null，禁止顺手修）。</li>
 * </ul>
 * 落位说明：Mongo/Redisson 分居 configuration.mongodb / configuration.redisson 两子包，
 * 访问器键名同为 accessor/accessors，故共享本父类置于公共包 configuration（跨包继承，键集零增减）；
 * Async 侧键名为 storage/storages，与 accessor 名族不兼容（并父会泄漏多余绑定键），另立本包同级父类。
 * 子类保留链式 setter 协变覆写、协变 getter 覆写与类上全部 Spring 注解（@ConfigurationProperties 注册点不动）。
 * 绑定面回归钉桩见 FactoryPropertiesBinderRegressionTest。
 *
 * @param <S> 访问器工厂 Setting 类型
 */
public abstract class AbstractStorageAccessorFactoryProperties<S> {

    private boolean enable = true;

    @NestedConfigurationProperty
    private S accessor;

    private Map<String, S> accessors = new HashMap<>();

    protected AbstractStorageAccessorFactoryProperties(S defaultAccessor) {
        this.accessor = defaultAccessor;
    }

    public boolean isEnable() {
        return enable;
    }

    public AbstractStorageAccessorFactoryProperties<S> setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public S getAccessor() {
        return accessor;
    }

    public AbstractStorageAccessorFactoryProperties<S> setAccessor(S accessor) {
        this.accessor = accessor;
        return this;
    }

    public Map<String, S> getAccessors() {
        return accessors;
    }

    public AbstractStorageAccessorFactoryProperties<S> setAccessors(Map<String, S> accessors) {
        this.accessors = accessors;
        return this;
    }

}
