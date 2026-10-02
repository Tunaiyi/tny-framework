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

package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.scanner.*;

import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/22 4:15 下午
 */
@FunctionalInterface
public interface AutoRegisterModuleClassesHandler extends ClassSelectedHandler {

    /**
     * 创建一个自动注册的Module 的类扫描 handler
     *
     * @param handler 处理器
     * @return 返回
     */
    static ClassSelectedHandler createHandler(AutoRegisterModuleClassesHandler handler) {
        return handler;
    }

    @Override
    default void selected(Collection<Class<?>> classes) {
        SimpleModule module = new SimpleModule();
        doSelected(module, classes);
        ObjectMapperFactory.registerGlobalModule(module);
    }

    void doSelected(SimpleModule mapper, Collection<Class<?>> classes);

}
