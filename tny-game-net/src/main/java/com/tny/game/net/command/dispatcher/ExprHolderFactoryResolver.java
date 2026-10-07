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

package com.tny.game.net.command.dispatcher;

import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.expr.*;

import java.util.*;
import java.util.stream.*;

/**
 * 表达式引擎工厂的装配解析（remove-engine-transitive-assembly D1；规格 expression-engine-assembly 需求一/二）：
 * 显式传入的工厂始终优先；未传入时按 ExprHolderFactory 契约在单元装配注册表中解析——恰一个注册即用之，
 * 零个报"未装配表达式引擎"并附出错配置位置，多个报冲突并列出注册清单，不存在静默回退到某一具体引擎的形态。
 */
final class ExprHolderFactoryResolver {

    private ExprHolderFactoryResolver() {
    }

    static ExprHolderFactory resolve(ExprHolderFactory explicit, String location) {
        if (explicit != null) {
            return explicit;
        }
        Collection<ExprHolderFactory> units = UnitLoader.getLoader(ExprHolderFactory.class).getAllUnits();
        if (units.isEmpty()) {
            throw new IllegalArgumentException("未装配表达式引擎：ExprHolderFactory 契约无任何注册实现，无法求值配置属性 \""
                    + location + "\"——请由装配方引入表达式引擎（如 tny-game-expr-groovy）并经框架装配注册，或在构造处显式传入工厂");
        }
        if (units.size() > 1) {
            String names = units.stream().map(unit -> unit.getClass().getSimpleName()).collect(Collectors.joining(", "));
            throw new IllegalArgumentException("装配了多个表达式引擎：[" + names + "]，无法为配置属性 \"" + location
                    + "\" 唯一解析求值引擎——请仅装配其一，或在构造处显式传入工厂");
        }
        return units.iterator().next();
    }

}
