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
package com.tny.game.common.lifecycle;

import com.tny.game.common.lifecycle.annotation.AsLifecycle;
import com.tny.game.common.lifecycle.annotation.StaticInit;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * lifecycle-scanning-robustness 契约（fix-common-dormant-defects 组9）：
 * 坏类不终止整轮扫描（按类隔离+汇总留痕）、全局态可复位、追加类入口可带优先级。
 */
class LifecycleScanRobustnessTest {

    @AsLifecycle(order = 31)
    public static class GoodA {

        public static final List<String> FIRED = Collections.synchronizedList(new ArrayList<>());

        @StaticInit
        public static void initA() {
            FIRED.add("A");
        }
    }

    @AsLifecycle(order = 32)
    public static class GoodB {

        @StaticInit
        public static void initB() {
            GoodA.FIRED.add("B");
        }
    }

    /** 缺 @StaticInit 的坏类（注解在但无初始化方法） */
    @AsLifecycle(order = 33)
    public static class NoInitMethod {
    }

    /** 无 @AsLifecycle 的坏类 */
    public static class NoAnnotation {
    }

    public static class LateHandler implements AppPrepareStart {
        @Override
        public void prepareStart() throws Exception {
        }
    }

    @Test
    void badClassesDoNotAbortWholeScan() {
        LifecycleLoader.reset();
        GoodA.FIRED.clear();
        // 好-坏-好 混排：两个好类必须完成注册，坏类被跳过而非炸整轮
        assertDoesNotThrow(() -> LifecycleLoader.registerAll(
                List.of(GoodA.class, NoAnnotation.class, NoInitMethod.class, GoodB.class)));
        Set<StaticInitiator> initiators = LifecycleLoader.getStaticInitiators();
        assertEquals(2, initiators.size(),
                "按类隔离后应恰好注册两个好类: " + initiators);
        // 直接单类 API 仍显式失败（调用方需感知）
        assertThrows(RuntimeException.class, () -> LifecycleLoader.register(NoAnnotation.class));
        assertThrows(IllegalArgumentException.class, () -> LifecycleLoader.register(NoInitMethod.class));
        LifecycleLoader.reset();
        assertTrue(LifecycleLoader.getStaticInitiators().isEmpty(), "复位入口必须清空扫描登记");
    }

    /** 同类重复扫描注册的幂等由跳过集判等保证（至多一条记录） */
    @Test
    void repeatRegisterSameClassIsIdempotentInSet() {
        LifecycleLoader.reset();
        LifecycleLoader.register(GoodA.class);
        LifecycleLoader.register(GoodA.class);
        assertEquals(1, LifecycleLoader.getStaticInitiators().size(),
                "同处理类在扫描登记中至多一条记录");
        LifecycleLoader.reset();
    }

    /** 追加类入口可携带优先级（原恒固定档） */
    @Test
    void appendClassWithPriority() {
        Lifecycle.resetRegistry();
        PrepareStarter anchor = PrepareStarter.value(AnchorHandler.class, LifecyclePriorities.of(50));
        PrepareStarter appended = anchor.append(LateHandler.class, LifecyclePriorities.of(40));
        assertEquals(40, appended.getOrder(), "带优先级追加必须真实采用声明档位（原固定默认档）");
        // 既有无优先级入口保持固定默认档语义（独立锚，避开 next 占用）
        PrepareStarter anchor2 = PrepareStarter.value(DefaultedAnchor.class, LifecyclePriorities.of(2000));
        PrepareStarter defaulted = anchor2.append(DefaultedHandler.class);
        assertEquals(LifecycleLevel.CUSTOM_LEVEL_5.getOrder(), defaulted.getOrder());
        Lifecycle.resetRegistry();
    }

    public static class AnchorHandler implements AppPrepareStart {
        @Override
        public void prepareStart() throws Exception {
        }
    }

    public static class DefaultedAnchor implements AppPrepareStart {
        @Override
        public void prepareStart() throws Exception {
        }
    }

    public static class DefaultedHandler implements AppPrepareStart {
        @Override
        public void prepareStart() throws Exception {
        }
    }

}
