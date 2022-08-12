/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.expr;

import java.util.*;

/**
 * Created by Kun Yang on 2018/5/24.
 */
public interface ExprContext {

    ExprContext importClasses(Class<?>... classes);

    ExprContext importClasses(Collection<Class<?>> classes);

    ExprContext importStaticClasses(Class<?>... classes);

    ExprContext importStaticClasses(Collection<Class<?>> classes);

    ExprContext importClassAs(String alias, Class<?> clazz);

    ExprContext importClassesAs(Map<String, Class<?>> aliasClassMap);

    ExprContext bind(String key, Object object);

    ExprContext bindAll(Map<String, Object> attributes);

}
