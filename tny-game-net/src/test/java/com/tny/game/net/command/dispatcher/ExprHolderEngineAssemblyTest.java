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
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 引擎装配合同测试（remove-engine-transitive-assembly 任务 2.1；规格 expression-engine-assembly 需求一/二）：
 * 命令插件属性求值在未显式传入工厂时按 ExprHolderFactory 契约的注册情况裁决——零个报"未装配表达式引擎"并附
 * 配置位置、恰一个用之、多个报冲突并列出注册清单；显式传入始终优先。单元注册表为进程级全局静态且无摘除接口，
 * 四个用例以 @Order 按注册数递进（空、一、二），契约键 ExprHolderFactory 在 net 测试 JVM 内仅本类注册。
 */
@TestMethodOrder(OrderAnnotation.class)
class ExprHolderEngineAssemblyTest {

    /** 仅作 AfterPlugin 注解 value 元素的占位插件类型，Holder 构造只读取 attribute()。 */
    private static final class ProbePlugin implements CommandPlugin<Object> {

        @Override
        public Class<Object> getAttributesClass() {
            return Object.class;
        }

        @Override
        public void execute(Tunnel tunnel, Message message, RpcInvokeContext context, Object attribute) {
        }
    }

    @AfterPlugin(value = ProbePlugin.class, attribute = "@42")
    private static final class AnnotatedProbe {
    }

    @UnitInterface
    @Unit(unitInterfaces = ExprHolderFactory.class)
    private static final class StubEngineA implements ExprHolderFactory {

        @Override
        public ExprContext getContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ExprHolder create(String formula) throws ExprException {
            return engineHolder(1);
        }
    }

    @UnitInterface
    @Unit(unitInterfaces = ExprHolderFactory.class)
    private static final class StubEngineB implements ExprHolderFactory {

        @Override
        public ExprContext getContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ExprHolder create(String formula) throws ExprException {
            return engineHolder(2);
        }
    }

    private static CommandPluginHolder holderWith(ExprHolderFactory factory) {
        ControllerHolder controller = mock(ControllerHolder.class);
        @SuppressWarnings("unchecked")
        CommandPlugin<Object> plugin = mock(CommandPlugin.class);
        when(plugin.getAttributesClass()).thenReturn(Object.class);
        AfterPlugin annotation = AnnotatedProbe.class.getAnnotation(AfterPlugin.class);
        return new CommandPluginHolder(controller, plugin, annotation, factory);
    }

    /** Expr 与 ExprHolderFactory 均为多方法接口，构造 stub 走 Mockito。 */
    private static ExprHolder engineHolder(Object value) {
        Expr expr = mock(Expr.class);
        when(expr.execute(Object.class)).thenReturn(value);
        ExprHolder holder = mock(ExprHolder.class);
        when(holder.createExpr()).thenReturn(expr);
        return holder;
    }

    @Test
    @Order(1)
    @DisplayName("未显式传入且注册表为空时报未装配表达式引擎并附配置位置")
    void missingEngineFailsExplicitly() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> holderWith(null));
        assertTrue(error.getMessage().contains("未装配表达式引擎"), "错误须给出未装配定性：" + error.getMessage());
        assertTrue(error.getMessage().contains("@42"), "错误须附出错的配置位置（属性表达式原文）：" + error.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("恰一个注册引擎时未显式传入也解析成功")
    void singleRegisteredEngineResolves() {
        UnitLoader.register(new StubEngineA());
        assertDoesNotThrow(() -> holderWith(null));
    }

    @Test
    @Order(3)
    @DisplayName("多引擎共存时报冲突且错误信息列出注册清单")
    void multipleEnginesFailWithRegistryListing() {
        UnitLoader.register(new StubEngineB());
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> holderWith(null));
        assertTrue(error.getMessage().contains("StubEngineA"), "冲突错误须列出已注册引擎清单：" + error.getMessage());
        assertTrue(error.getMessage().contains("StubEngineB"), "冲突错误须列出已注册引擎清单：" + error.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("显式传入工厂优先于注册表解析（两引擎共存下仍成功）")
    void explicitFactoryAlwaysWins() {
        ExprHolderFactory explicit = new ExprHolderFactory() {

            @Override
            public ExprContext getContext() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ExprHolder create(String formula) throws ExprException {
                return engineHolder(42);
            }
        };
        assertDoesNotThrow(() -> holderWith(explicit));
    }

}
