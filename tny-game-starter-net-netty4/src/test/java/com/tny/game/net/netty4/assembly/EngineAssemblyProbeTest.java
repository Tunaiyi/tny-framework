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

package com.tny.game.net.netty4.assembly;

import com.tny.game.boot.registrar.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.expr.*;
import com.tny.game.expr.groovy.*;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 装配探针（remove-engine-transitive-assembly 任务 1.1）：实测"引擎工厂以 @Bean 进入 Spring 上下文后，
 * 经真实 UnitLoadInitiator 装配能否按 ExprHolderFactory 契约键注册进 UnitLoader"——设计 D1/D3 的成立前提。
 * 匿名子类实例避免与同 JVM 其他测试的具名注册冲突（单元名取 simpleName，匿名类名为其编号形态）。
 */
class EngineAssemblyProbeTest {

    @Configuration(proxyBeanMethods = false)
    static class ProbeConfig {

        @Bean
        ExprHolderFactory probeEngine() {
            return new GroovyExprHolderFactory() {
            };
        }
    }

    @Test
    void unitLoadInitiatorRegistersAnnotatedEngineBeanUnderContractKey() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProbeConfig.class)) {
            UnitLoadInitiator initiator = new UnitLoadInitiator();
            Field field = UnitLoadInitiator.class.getDeclaredField("context");
            field.setAccessible(true);
            field.set(initiator, context);
            initiator.prepareStart();

            Collection<ExprHolderFactory> units = UnitLoader.getLoader(ExprHolderFactory.class).getAllUnits();
            assertEquals(1, units.size(), "探针引擎应按契约键注册为恰一个单元");
            ExprHolderFactory unit = units.iterator().next();
            assertInstanceOf(GroovyExprHolderFactory.class, unit);

            ExprHolder holder = unit.create("1 + 2");
            Number value = holder.createExpr().execute(Number.class);
            assertNotNull(value);
            assertEquals(3, value.intValue(), "经注册单元求值的结果应与直接构造引擎求值逐值一致");
        }
    }

}
