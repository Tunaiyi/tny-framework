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
package com.tny.game.common.collection.map.access;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Map 包装访问器转换统一契约（fix-common-audit-findings D1）：
 * 类型命中直取不变；错型值走宽松转换（数字字符串→数值、数值跨类型窄化、任意值→字符串），
 * 与同接口的非包装实现语义一致（同一份数据、两种访问器，结论必须相同）；
 * 无法转换的组合仍显式失败（String→布尔保持抛——对齐基准行为而非顺手扩张）。
 */
class WrapperObjectMapConversionTest {

    private static MapAccessor wrap(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put((String) kv[i], kv[i + 1]);
        }
        return MapAccessors.wrap(map);
    }

    private static MapAccessor direct(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put((String) kv[i], kv[i + 1]);
        }
        return new ObjectMap(map);
    }

    /** 快路径：值即目标类型时直取，行为与成本不变 */
    @Test
    void exactTypeStillDirect() {
        MapAccessor accessor = wrap("i", 42, "s", "text", "f", 1.25f, "b", Boolean.TRUE);
        assertEquals(42, accessor.getInt("i"));
        assertEquals("text", accessor.getString("s"));
        assertEquals(1.25f, accessor.getFloat("f"), 0f);
        assertTrue(accessor.getBoolean("b"));
    }

    /** 数字字符串 → 各数值形态 */
    @Test
    void numericStringConverts() {
        MapAccessor accessor = wrap("port", "8080", "ratio", "1.5", "tiny", "7", "mid", "300", "fstr", "1.25");
        assertEquals(8080, accessor.getInt("port"));
        assertEquals(8080L, accessor.getLong("port"));
        assertEquals(1.5d, accessor.getDouble("ratio"), 0d);
        assertEquals((byte) 7, accessor.getByte("tiny"));
        assertEquals((short) 300, accessor.getShort("mid"));
        assertEquals(1.25f, accessor.getFloat("fstr"), 0f);
    }

    /** 数值跨类型窄化/拓宽 */
    @Test
    void numberNarrowingConverts() {
        MapAccessor accessor = wrap("d", 2.7d, "i", 5, "l", 9_000_000_000L);
        assertEquals(2, accessor.getInt("d"), "2.7 窄化为 2（与基准实现一致）");
        assertEquals(5L, accessor.getLong("i"));
        assertEquals(5.0d, accessor.getDouble("i"), 0d);
        // long→int 截断值以基准实现为准（守约哨兵断言覆盖，见 wrapperAndDirectAgreeOnMatrix）
        assertEquals(direct("l", 9_000_000_000L).getInt("l"), wrap("l", 9_000_000_000L).getInt("l"));
    }

    /** 任意值 → 字符串走 toString */
    @Test
    void anyValueToString() {
        assertEquals("123", wrap("k", 123).getString("k"));
        assertEquals("1.5", wrap("k", 1.5d).getString("k"));
    }

    /** 无法转换的组合显式失败：非数值字符串→数字抛数字格式异常；字符串→布尔抛类型异常（基准行为） */
    @Test
    void unconvertibleFailsExplicitly() {
        assertThrows(NumberFormatException.class, () -> wrap("k", "abc").getInt("k"));
        assertThrows(ClassCastException.class, () -> wrap("k", "true").getBoolean("k"),
                "对齐基准：字符串到布尔不做解析转换");
    }

    /** 缺键语义：有缺省返回缺省；无缺省入口抛含键名空指针 */
    @Test
    void missingKeySemantics() {
        MapAccessor accessor = wrap("k", 1);
        assertEquals(7, accessor.getInt("absent", 7));
        assertNull(accessor.getString("absent"));
        NullPointerException e = assertThrows(NullPointerException.class, () -> accessor.getInt("absent"));
        assertTrue(e.getMessage().contains("absent"), "异常须含键名: " + e.getMessage());
    }

    /** 守约哨兵：同一份数据，包装与非包装访问器逐路径结论一致（规格禁止实现分裂） */
    @Test
    void wrapperAndDirectAgreeOnMatrix() {
        Object[][] data = {
                {"port", "8080"},
                {"ratio", "1.5"},
                {"dbl", 2.7d},
                {"i", 42},
                {"s", "text"},
                {"b", Boolean.TRUE},
        };
        Map<String, Object> map = new LinkedHashMap<>();
        for (Object[] kv : data) {
            map.put((String) kv[0], kv[1]);
        }
        MapAccessor wrapper = MapAccessors.wrap(map);
        MapAccessor baseline = new ObjectMap(map);
        for (String key : map.keySet()) {
            assertEquals(baseline.getString(key), wrapper.getString(key), key + " 字符串通道分裂");
            assertEquals(baseline.size(), wrapper.size(), key + " 尺寸通道分裂");
        }
        assertEquals(baseline.getInt("port"), wrapper.getInt("port"));
        assertEquals(baseline.getDouble("ratio"), wrapper.getDouble("ratio"), 0d);
        assertEquals(baseline.getInt("dbl"), wrapper.getInt("dbl"));
        assertEquals(baseline.getLong("i"), wrapper.getLong("i"));
        assertTrue(wrapper.getBoolean("b"));
        assertThrows(ClassCastException.class, () -> wrapper.getBoolean("s"),
                "非数字字符串→布尔：两实现都须显式失败");
        assertThrows(ClassCastException.class, () -> baseline.getBoolean("s"));
    }

}
