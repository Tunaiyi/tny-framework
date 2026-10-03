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

import java.util.*;
import java.util.function.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * reduce-code-duplication D4 钉桩（task 4.1）：转换取值 getter 链 × ObjectMap/WrapperObjectMap 两侧 ×
 * {直取命中、可转换值、不可转换失败、null 值/缺键缺省、默认值路径} 全矩阵。
 * <p>
 * 两侧门面已知参数差异（design D4）：`ObjectMap.getFloat(key, def)` 传 {@code float.class}（原缺陷参数，
 * **禁止顺手修**——按现状原样保留），`WrapperObjectMap` 传 {@code Float.class}（fix 轮已修）。
 * 因 ObjectMap asObject 链走 ObjectAide.convertTo（其基本类型→包装类分支在基线即存在），
 * 现状可观察面：可转换值两侧同值返回；不可转换值两侧同抛 CCE 但 message 尾段
 * （"float" vs "class java.lang.Float"）如实分叉——逐格钉死现状，重构（共享引擎）后期望值一字不改。
 * <p>
 * 另钉 ObjectMap.getObject(key, def) 覆写与接口默认实现等价的现状，以及 Wrapper toMap 活包装 vs
 * ObjectMap 构造期拷贝的语义差异（保留用例，不归一）。
 */
public class MapConvertAccessContractTest {

    /**
     * 一次访问调用（对 MapAccessor 面）。
     */
    private interface Call {

        Object apply(MapAccessor accessor);
    }

    /**
     * 两侧同数据下的结论捕获：返回值或抛出物原样装瓶。
     */
    private static Object capture(MapAccessor accessor, Call call) {
        try {
            return call.apply(accessor);
        } catch (Throwable t) {
            return t;
        }
    }

    private static Map<String, Object> data() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("str", "text");
        map.put("int", 42);
        map.put("long", 9_000_000_000L);
        map.put("floatVal", 1.25f);
        map.put("doubleVal", 2.5d);
        map.put("byteVal", (byte) 7);
        map.put("shortVal", (short) 300);
        map.put("boolVal", Boolean.TRUE);
        map.put("numStr", "7");
        map.put("portStr", "8080");
        map.put("ratioStr", "1.25");
        map.put("badStr", "abc");
        map.put("trueStr", "true");
        map.put("nullVal", null);
        return map;
    }

    /**
     * 全矩阵哨兵：同一份数据分别装进 ObjectMap 与 WrapperObjectMap，逐格比对返回值/异常类型/异常消息。
     */
    private static void assertSameBoth(String label, Call call) {
        assertSameBoth(label, call, true);
    }

    /**
     * @param messageMustMatch false 用于已知参数差异格（float.class vs Float.class 的 CCE 消息尾段分叉，现状如实钉）。
     */
    private static void assertSameBoth(String label, Call call, boolean messageMustMatch) {
        Map<String, Object> map = data();
        Object direct = capture(new ObjectMap(map), call);
        Object wrapper = capture(new WrapperObjectMap(map), call);
        boolean directThrew = direct instanceof Throwable;
        boolean wrapperThrew = wrapper instanceof Throwable;
        assertEquals(directThrew, wrapperThrew, label + " 两侧结论分裂: " + direct + " vs " + wrapper);
        if (directThrew) {
            Throwable a = (Throwable) direct;
            Throwable b = (Throwable) wrapper;
            assertEquals(a.getClass(), b.getClass(), label + " 异常类型分裂");
            if (messageMustMatch) {
                assertEquals(a.getMessage(), b.getMessage(), label + " 异常消息分裂");
            }
        } else {
            assertEquals(direct, wrapper, label + " 返回值分裂");
        }
    }

    // ------------------------------------------------------------------
    // 直取命中 + 可转换 + 不可转换 + null/缺省 全矩阵
    // ------------------------------------------------------------------

    @Test
    void stringChannelMatrix() {
        assertSameBoth("getString(str)", a -> a.getString("str"));
        assertSameBoth("getString(int) toString", a -> a.getString("int"));
        assertSameBoth("getString(nullVal)", a -> a.getString("nullVal"));
        assertSameBoth("getString(absent)", a -> a.getString("absent"));
        assertSameBoth("getString(int,def)", a -> a.getString("int", "def"));
        assertSameBoth("getString(absent,def)", a -> a.getString("absent", "def"));
        assertSameBoth("getString(nullVal,def)", a -> a.getString("nullVal", "def"));
    }

    @Test
    void integerChannelMatrix() {
        assertSameBoth("getInt(int)", a -> a.getInt("int"));
        assertSameBoth("getInt(long) 窄化", a -> a.getInt("long"));
        assertSameBoth("getInt(portStr)", a -> a.getInt("portStr"));
        assertSameBoth("getInt(badStr) 显式失败", a -> a.getInt("badStr"));
        assertSameBoth("getInt(str) 显式失败", a -> a.getInt("str"));
        assertSameBoth("getInt(absent) 含键名 NPE", a -> a.getInt("absent"));
        assertSameBoth("getInt(nullVal) 含键名 NPE", a -> a.getInt("nullVal"));
        assertSameBoth("getInt(absent,def)", a -> a.getInt("absent", 7));
        assertSameBoth("getInt(int,def)", a -> a.getInt("int", 7));
    }

    @Test
    void byteShortLongDoubleChannels() {
        assertSameBoth("getByte(byteVal)", a -> a.getByte("byteVal"));
        assertSameBoth("getByte(numStr)", a -> a.getByte("numStr"));
        assertSameBoth("getByte(ratioStr) 显式失败", a -> a.getByte("ratioStr"));
        assertSameBoth("getByte(absent,def)", a -> a.getByte("absent", (byte) 1));
        assertSameBoth("getShort(shortVal)", a -> a.getShort("shortVal"));
        assertSameBoth("getShort(int) 窄化", a -> a.getShort("int"));
        assertSameBoth("getShort(str) 显式失败", a -> a.getShort("str"));
        assertSameBoth("getShort(int,def)", a -> a.getShort("int", (short) 1));
        assertSameBoth("getLong(long)", a -> a.getLong("long"));
        assertSameBoth("getLong(int) 拓宽", a -> a.getLong("int"));
        assertSameBoth("getLong(portStr)", a -> a.getLong("portStr"));
        assertSameBoth("getLong(absent)", a -> a.getLong("absent"));
        assertSameBoth("getLong(absent,def)", a -> a.getLong("absent", 1L));
        assertSameBoth("getDouble(doubleVal)", a -> a.getDouble("doubleVal"));
        assertSameBoth("getDouble(int)", a -> a.getDouble("int"));
        assertSameBoth("getDouble(ratioStr)", a -> a.getDouble("ratioStr"));
        assertSameBoth("getDouble(absent,def)", a -> a.getDouble("absent", 1d));
    }

    /**
     * Float 通道逐格现状钉桩。
     */
    @Test
    void floatChannelMatrix() {
        // 可转换格：两侧同值（ObjectMap 传 float.class 的现状在这些格返回正常值，钉"现状非 CCE"，见类注释——禁止顺手修）
        assertSameBoth("getFloat(floatVal)", a -> a.getFloat("floatVal"));
        assertSameBoth("getFloat(floatVal,def) 现状=值命中不抛（缺陷参数经 convertTo 基本类型分支消化）",
                      a -> a.getFloat("floatVal", -1f));
        assertSameBoth("getFloat(int,def) 数值转换", a -> a.getFloat("int", -1f));
        assertSameBoth("getFloat(ratioStr,def)", a -> a.getFloat("ratioStr", -1f));
        assertSameBoth("getFloat(absent,def) 走缺省", a -> a.getFloat("absent", -1f));
        assertSameBoth("getFloat(nullVal,def) 走缺省", a -> a.getFloat("nullVal", -1f));
        assertSameBoth("getFloat(absent) notnull NPE", a -> a.getFloat("absent"));
        // 字符串值格：两侧同入数值解析分支（targetClass 同为 Float.class）→ 同抛 NumberFormatException、消息一致
        assertSameBoth("getFloat(str,def) 同抛 NFE", a -> a.getFloat("str", -1f));
        assertSameBoth("getFloat(trueStr,def) 同抛 NFE", a -> a.getFloat("trueStr", -1f));
        assertSameBoth("getFloat(badStr,def) 同抛 NFE", a -> a.getFloat("badStr", -1f));
        // 非数值非字符串格：两侧同抛 CCE，消息尾段分叉（float.class vs Float.class）——已知差异，消息不比对
        assertSameBoth("getFloat(boolVal,def) 同抛 CCE 消息分叉", a -> a.getFloat("boolVal", -1f), false);
        assertSameBoth("getFloat(str) notnull 消息一致", a -> a.getFloat("str"));
    }

    /**
     * fix-registered-defects D8 翻转（task 5.1）：原钉桩"ObjectMap 侧 float.class 尾段 'float' vs Wrapper 侧
     * Float.class 尾段 'class java.lang.Float' 逐字分叉"翻到 object-access-conversion 差量承诺方向——
     * 共享引擎侧原始类参数统一装箱归一 + ObjectMap 门面 float.class→Float.class 后，
     * 两实现对同输入产出逐字相同的装箱形态消息尾段（BREAKING：措辞向，ObjectMap 侧尾段由 "float" 变 "class java.lang.Float"）。
     */
    @Test
    void floatDefChannelMessageDriftPinned() {
        Map<String, Object> map = data();
        ObjectMap direct = new ObjectMap(map);
        WrapperObjectMap wrapper = new WrapperObjectMap(map);

        ClassCastException directEx = assertThrows(ClassCastException.class, () -> direct.getFloat("boolVal", -1f));
        assertEquals("class java.lang.Boolean can not convert to class java.lang.Float", directEx.getMessage(),
                     "ObjectMap 侧尾段已归一为装箱形态（D8 翻转：此前 '…to float'）");
        ClassCastException wrapperEx = assertThrows(ClassCastException.class, () -> wrapper.getFloat("boolVal", -1f));
        assertEquals("class java.lang.Boolean can not convert to class java.lang.Float", wrapperEx.getMessage());
        assertEquals(directEx.getMessage(), wrapperEx.getMessage(), "两侧同输入失败消息尾段逐字相等（装箱形态）");
    }

    @Test
    void booleanAndObjectChannels() {
        assertSameBoth("getBoolean(boolVal)", a -> a.getBoolean("boolVal"));
        assertSameBoth("getBoolean(trueStr) 对齐基准显式失败", a -> a.getBoolean("trueStr"));
        assertSameBoth("getBoolean(absent)", a -> a.getBoolean("absent"));
        assertSameBoth("getBoolean(absent,def)", a -> a.getBoolean("absent", true));
        assertSameBoth("getBoolean(boolVal,def)", a -> a.getBoolean("boolVal", false));
        assertSameBoth("getObject(int)", a -> a.getObject("int"));
        assertSameBoth("getObject(absent)", a -> a.getObject("absent"));
        assertSameBoth("getObject(absent,def)", a -> a.getObject("absent", "def"));
        assertSameBoth("getObject(int,def)", a -> a.getObject("int", "def"));
    }

    // ------------------------------------------------------------------
    // 非转换面但同类语义：缺省键 NPE 消息含键名、getMapAccessor、getNotNullToFunction
    // ------------------------------------------------------------------

    @Test
    void notNullFailureCarriesKeyNameBothSides() {
        Map<String, Object> map = data();
        NullPointerException direct = assertThrows(NullPointerException.class,
                                                   () -> new ObjectMap(map).getLong("absent"));
        assertEquals("[absent] value is null", direct.getMessage());
        NullPointerException wrapper = assertThrows(NullPointerException.class,
                                                    () -> new WrapperObjectMap(map).getLong("absent"));
        assertEquals("[absent] value is null", wrapper.getMessage());
    }

    @Test
    void mapAccessorChannelAgrees() {
        Map<String, Object> inner = new LinkedHashMap<>();
        inner.put("k", 1);
        Map<String, Object> map = data();
        map.put("child", inner);
        assertSameBoth("getMapAccessor(child)", a -> {
            MapAccessor child = a.getMapAccessor("child", null);
            return child == null ? null : child.size() + "/" + child.getInt("k");
        });
        assertSameBoth("getMapAccessor(absent,def)", a -> {
            MapAccessor def = new ObjectMap();
            MapAccessor got = a.getMapAccessor("absent", def);
            return got == def ? "def" : "other";
        });
    }

    @Test
    void getNotNullToFunctionAgrees() {
        Map<String, Object> map = data();
        StringBuilder sink = new StringBuilder();
        ObjectMap direct = new ObjectMap(map);
        direct.getNotNullToFunction("int", (Function<Integer, ?>) v -> {
            sink.append(v);
            return null;
        }, Integer.class);
        assertEquals("42", sink.toString());
        sink.setLength(0);
        direct.getNotNullToFunction("absent", (Function<Integer, ?>) v -> {
            sink.append(v);
            return null;
        }, Integer.class);
        assertEquals("", sink.toString(), "缺键不得调用 function（现状）");

        WrapperObjectMap wrapper = new WrapperObjectMap(map);
        wrapper.getNotNullToFunction("int", (Function<Integer, ?>) v -> {
            sink.append(v);
            return null;
        }, Integer.class);
        assertEquals("42", sink.toString());
        wrapper.getNotNullToFunction("ratioStr", (Function<Float, ?>) v -> {
            sink.append(v);
            return null;
        }, Float.class);
        assertEquals("421.25", sink.toString(), "可转换值经宽松转换后调用（两侧共享引擎现状）");
    }

    // ------------------------------------------------------------------
    // 语义差异保留用例：toMap 不可变 + Wrapper 活包装 vs ObjectMap 拷贝
    // ------------------------------------------------------------------

    @Test
    void toMapImmutabilityAndCopySemantics() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("k", 1);

        ObjectMap direct = new ObjectMap(map);
        Map<String, Object> directView = direct.toMap();
        assertThrows(UnsupportedOperationException.class, () -> directView.put("x", 1));
        map.put("added", 2);
        assertFalse(direct.containsKey("added"), "ObjectMap 构造期拷贝：源 map 后续变化不进视图（现状）");

        WrapperObjectMap wrapper = new WrapperObjectMap(map);
        Map<String, Object> wrapperView = wrapper.toMap();
        assertThrows(UnsupportedOperationException.class, () -> wrapperView.put("x", 1));
        assertEquals(2, wrapper.size(), "WrapperObjectMap 活包装：源 map 变化即时可见（现状，不归一）");
    }

}
