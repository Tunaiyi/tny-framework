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
