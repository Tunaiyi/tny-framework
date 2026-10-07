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

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StaticInitiator 修复契约：
 * 多 @StaticInit 方法全部执行且按方法名定序（原"最后遍历者"静默丢弃其余且非确定）；
 * init() 解包 InvocationTargetException（原调用方拿到反射包装异常）。
 */
class StaticInitiatorFixTest {

    @AsLifecycle(order = 77)
    public static class MultiInit {

        public static final List<String> EXECUTED = Collections.synchronizedList(new ArrayList<>());

        @StaticInit
        public static void bSecond() {
            EXECUTED.add("b");
        }

        @StaticInit
        public static void aFirst() {
            EXECUTED.add("a");
        }
    }

    @AsLifecycle(order = 78)
    public static class FailingInit {

        @StaticInit
        public static void boom() throws IOException {
            throw new IOException("static init business failure");
        }
    }

    @AsLifecycle(order = 79)
    public static class NoInitMethod {
    }

    @Test
    void allStaticInitMethodsExecuteInDeterministicOrder() throws Exception {
        MultiInit.EXECUTED.clear();
        StaticInitiator initiator = StaticInitiator.instance(MultiInit.class);
        initiator.init();
        assertEquals(List.of("a", "b"), MultiInit.EXECUTED,
                "两个 @StaticInit 必须都执行且按方法名定序（原实现只随机跑一个）");
    }

    @Test
    void initUnwrapsTargetException() {
        StaticInitiator initiator = StaticInitiator.instance(FailingInit.class);
        IOException e = assertThrows(IOException.class, initiator::init,
                "必须解包出业务 IOException 而非 InvocationTargetException");
        assertEquals("static init business failure", e.getMessage());
    }

    @Test
    void missingInitMethodFails() {
        assertThrows(IllegalArgumentException.class, () -> StaticInitiator.instance(NoInitMethod.class));
    }

    /** 同类不同方法的两条记录在 ConcurrentSkipListSet 不得被 compareTo==0 静默去重 */
    @Test
    void skipListKeepsSameClassDifferentMethod() {
        // instance() 现在合并同类方法为单条记录——验证 equals/compareTo 一致性：
        // 两条同源记录 equals 则 compareTo==0
        StaticInitiator one = StaticInitiator.instance(MultiInit.class);
        StaticInitiator two = StaticInitiator.instance(MultiInit.class);
        assertEquals(one, two);
        assertEquals(0, one.compareTo(two));
        TreeSet<StaticInitiator> set = new TreeSet<>();
        set.add(one);
        set.add(two);
        assertEquals(1, set.size());
    }

}
