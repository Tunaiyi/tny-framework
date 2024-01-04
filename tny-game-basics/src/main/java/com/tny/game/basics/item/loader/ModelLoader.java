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

package com.tny.game.basics.item.loader;

import java.util.Collection;

/**
 * 模型加载器
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 01:38
 **/
public interface ModelLoader<M> {

    ModelLoader<M> addPath(String path);

    ModelLoader<M> addPaths(Collection<String> paths);

    ModelLoader<M> addPaths(String... paths);

    ModelLoader<M> addEnumClass(Class<? extends Enum<?>> clazz);

    ModelLoader<M> addEnumClass(Collection<Class<? extends Enum<?>>> classes);

    <C> ModelLoader<M> setContextHandler(ModelLoaderContextHandler<C> contextHandler);

    void load();

}
