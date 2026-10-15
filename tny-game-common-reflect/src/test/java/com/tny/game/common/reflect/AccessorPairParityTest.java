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
package com.tny.game.common.reflect;

import com.tny.game.common.reflect.cglib.*;
import com.tny.game.common.reflect.javassist.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组5（N5）行为钉桩（reduce-code-duplication，重构前必须绿——P13/D1）：
 * reflect 两对逐字克隆类的现状行为账。
 * 对一：{@link CGlibPropertyAccessor} ↔ {@link JSsistPropertyAccessor}——同一目标类双实现属性读写结果一致、
 * {@code new Object[]{...}} 与 varargs 实参形态等价（对 {@code invoke(Object, Object...)} 两写法落为同一数组）、
 * protected setName/setReader/setWriter/setType 注入面现状（null 态、UOE 消息文案、泛型类型取用优先级）。
 * 对二：{@link CGlibUtils} ↔ {@link JavassistAccessors}——工厂返回本面产物、缓存身份（同类复用、过滤器身份参与键）。
 * 禁止顺手修（零行为变更口径）；发现的差异/可疑现状一律如实钉桩。
 */
class AccessorPairParityTest {

    // ===== fixtures =====

    /** 双实现公共属性样例类：cglib 面收录 public 方法、javassist 面收录非 private——本样例两者交集即公共 bean 方法 */
    public static class SampleBean {

        private int count;

        private String title;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    /** protected 注入面探针：跨包子类只能经 this 调用继承的 protected setter，还原 ClassAccessor 构造期注入口径 */
    public static class CglibInjectProbe extends CGlibPropertyAccessor {

        public void inject(String name, Class<?> type, MethodAccessor reader, MethodAccessor writer) {
            setName(name);
            setType(type);
            setReader(reader);
            setWriter(writer);
        }
    }

    public static class JsstInjectProbe extends JSsistPropertyAccessor {

        public void inject(String name, Class<?> type, MethodAccessor reader, MethodAccessor writer) {
            setName(name);
            setType(type);
            setReader(reader);
            setWriter(writer);
        }
    }

    // ===== 对一：双实现属性读写结果一致 =====

    @Test
    void propertyReadParityAcrossFaces() throws Exception {
        SampleBean bean = new SampleBean();
        PropertyAccessor cglibCount = CGlibUtils.getGClass(SampleBean.class).getProperty("count");
        PropertyAccessor jsstCount = JavassistAccessors.getGClass(SampleBean.class).getProperty("count");
        assertNotNull(cglibCount, "cglib 面未登记 count 属性");
        assertNotNull(jsstCount, "javassist 面未登记 count 属性");

        assertEquals(0, cglibCount.getPropertyValue(bean), "cglib 面读初值");
        assertEquals(0, jsstCount.getPropertyValue(bean), "javassist 面读初值");
        assertEquals(cglibCount.getPropertyValue(bean), jsstCount.getPropertyValue(bean), "双实现读结果不一致");

        assertEquals(int.class, cglibCount.getPropertyType());
        assertEquals(int.class, jsstCount.getPropertyType());
        assertTrue(cglibCount.isReadable() && jsstCount.isReadable());
        assertTrue(cglibCount.isWritable() && jsstCount.isWritable());
        assertEquals(cglibCount.getGenericType(), jsstCount.getGenericType(), "双实现泛型类型不一致");

        PropertyAccessor cglibTitle = CGlibUtils.getGClass(SampleBean.class).getProperty("title");
        PropertyAccessor jsstTitle = JavassistAccessors.getGClass(SampleBean.class).getProperty("title");
        assertNull(cglibTitle.getPropertyValue(bean), "cglib 面读 null 值 String 属性");
        assertNull(jsstTitle.getPropertyValue(bean), "javassist 面读 null 值 String 属性");
        assertEquals(String.class, cglibTitle.getPropertyType());
        assertEquals(String.class, jsstTitle.getPropertyType());
    }

    @Test
    void propertyWriteParityAcrossFaces() throws Exception {
        SampleBean bean = new SampleBean();
        // 经 cglib 面写、javassist 面读——结果必须一致
        CGlibUtils.getGClass(SampleBean.class).getProperty("count").setPropertyValue(bean, 7);
        JavassistAccessors.getGClass(SampleBean.class).getProperty("count").setPropertyValue(bean, 42);
        assertEquals(42, CGlibUtils.getGClass(SampleBean.class).getProperty("count").getPropertyValue(bean));
        assertEquals(42, JavassistAccessors.getGClass(SampleBean.class).getProperty("count").getPropertyValue(bean));

        JavassistAccessors.getGClass(SampleBean.class).getProperty("title").setPropertyValue(bean, "t");
        assertEquals("t", CGlibUtils.getGClass(SampleBean.class).getProperty("title").getPropertyValue(bean));

        // 类型化查询面（getProperty(name, class)）双实现同样命中
        assertNotNull(CGlibUtils.getGClass(SampleBean.class).getProperty("count", int.class));
        assertNotNull(JavassistAccessors.getGClass(SampleBean.class).getProperty("count", int.class));
        assertNull(CGlibUtils.getGClass(SampleBean.class).getProperty("count", String.class));
        assertNull(JavassistAccessors.getGClass(SampleBean.class).getProperty("count", String.class));
    }

    @Test
    void nullInstanceShortCircuitsBothFaces() throws Exception {
        // 现状：instance==null 读取返回 null、写入静默返回——均在 reader/writer 判空之前短路
        PropertyAccessor cglibCount = CGlibUtils.getGClass(SampleBean.class).getProperty("count");
        PropertyAccessor jsstCount = JavassistAccessors.getGClass(SampleBean.class).getProperty("count");
        assertNull(cglibCount.getPropertyValue(null));
        assertNull(jsstCount.getPropertyValue(null));
        cglibCount.setPropertyValue(null, 1);
        jsstCount.setPropertyValue(null, 1);
        // null 实例 + 未注入 reader：仍短路返回 null，不触 Setter/Getter 缺失异常
        assertNull(new CGlibPropertyAccessor().getPropertyValue(null));
        assertNull(new JSsistPropertyAccessor().getPropertyValue(null));
    }

    // ===== 对一：varargs 与 new Object[]{...} 实参形态等价 =====

    @Test
    void varargsAndArrayFormAreEquivalentOnBothFaces() throws Exception {
        SampleBean bean = new SampleBean();
        for (ClassAccessor face : new ClassAccessor[]{CGlibUtils.getGClass(SampleBean.class),
            JavassistAccessors.getGClass(SampleBean.class)}) {
            MethodAccessor intReader = face.getMethod("getCount");
            MethodAccessor titleReader = face.getMethod("getTitle");
            // 零参 getter：两种写法落为同一数组；显式传入的多余实参被 invoker 层忽略（现状——
            // PropertyAccessor.getPropertyValue 正是以 this.type 作实参调用零参 getter 而可工作）
            assertEquals(0, intReader.invoke(bean, new Object[]{int.class}), face.getClass() + " 数组形态读");
            assertEquals(0, intReader.invoke(bean, int.class), face.getClass() + " varargs 形态读");
            assertEquals(intReader.invoke(bean), intReader.invoke(bean, new Object[]{}), face.getClass() + " 空形态等价");
            // 一参 setter：两写法同为 {value} 单元素数组，结果一致
            MethodAccessor writer = face.getMethod("setTitle", String.class);
            writer.invoke(bean, new Object[]{"same"});
            assertEquals("same", titleReader.invoke(bean), face.getClass() + " 数组形态写后可读");
            writer.invoke(bean, "v2");
            assertEquals("v2", titleReader.invoke(bean), face.getClass() + " varargs 形态写后可读");
            // null 值单引用 varargs 同样静态包装为 {null}（与显式 new Object[]{null} 等价）
            writer.invoke(bean, (Object) null);
            assertNull(titleReader.invoke(bean), face.getClass() + " null 实参写后可读");
            writer.invoke(bean, new Object[]{null});
            assertNull(titleReader.invoke(bean), face.getClass() + " null 数组形态写后可读");
        }
    }

    // ===== 对一：protected 注入面现状 =====

    @Test
    void freshAccessorNullStateParity() throws Exception {
        CGlibPropertyAccessor cglib = new CGlibPropertyAccessor();
        JSsistPropertyAccessor jsst = new JSsistPropertyAccessor();
        assertNull(cglib.getName());
        assertNull(jsst.getName());
        assertFalse(cglib.isReadable() || jsst.isReadable());
        assertFalse(cglib.isWritable() || jsst.isWritable());
        assertNull(cglib.getPropertyType());
        assertNull(jsst.getPropertyType());
        assertNull(cglib.getGenericType(), "无 reader/writer 时泛型类型为 null");
        assertNull(jsst.getGenericType());

        SampleBean bean = new SampleBean();
        // 缺失 reader/writer 的显式失败文案现状：name 未注入渲染为字面量 null
        UnsupportedOperationException cgGet = assertThrows(UnsupportedOperationException.class,
                () -> cglib.getPropertyValue(bean));
        UnsupportedOperationException jsGet = assertThrows(UnsupportedOperationException.class,
                () -> jsst.getPropertyValue(bean));
        assertEquals(bean.getClass() + "不支持 [null] 属性 Getter 方法", cgGet.getMessage());
        assertEquals(jsGet.getMessage(), cgGet.getMessage(), "双实现 Getter 缺失消息文案不一致");
        UnsupportedOperationException cgSet = assertThrows(UnsupportedOperationException.class,
                () -> cglib.setPropertyValue(bean, "v"));
        UnsupportedOperationException jsSet = assertThrows(UnsupportedOperationException.class,
                () -> jsst.setPropertyValue(bean, "v"));
        assertEquals(bean.getClass() + "不支持 [null] 属性 Setter 方法", cgSet.getMessage());
        assertEquals(jsSet.getMessage(), cgSet.getMessage(), "双实现 Setter 缺失消息文案不一致");
    }

    @Test
    void injectedStateParityAcrossFaces() throws Exception {
        SampleBean bean = new SampleBean();
        ClassAccessor cglibFace = CGlibUtils.getGClass(SampleBean.class);
        ClassAccessor jsstFace = JavassistAccessors.getGClass(SampleBean.class);

        // 仅注入 reader + name + type：读可用、写抛 Setter 缺失（消息含注入名）
        CglibInjectProbe cgOnlyReader = new CglibInjectProbe();
        cgOnlyReader.inject("count", int.class, cglibFace.getMethod("getCount"), null);
        assertEquals(0, cgOnlyReader.getPropertyValue(bean));
        assertEquals(int.class, cgOnlyReader.getPropertyType());
        assertEquals("count", cgOnlyReader.getName());
        assertTrue(cgOnlyReader.isReadable() && !cgOnlyReader.isWritable());
        UnsupportedOperationException cgNoWriter = assertThrows(UnsupportedOperationException.class,
                () -> cgOnlyReader.setPropertyValue(bean, 5));
        assertEquals(bean.getClass() + "不支持 [count] 属性 Setter 方法", cgNoWriter.getMessage());

        JsstInjectProbe jsOnlyReader = new JsstInjectProbe();
        jsOnlyReader.inject("count", int.class, jsstFace.getMethod("getCount"), null);
        assertEquals(cgOnlyReader.getPropertyValue(bean), jsOnlyReader.getPropertyValue(bean));
        UnsupportedOperationException jsNoWriter = assertThrows(UnsupportedOperationException.class,
                () -> jsOnlyReader.setPropertyValue(bean, 5));
        assertEquals(cgNoWriter.getMessage(), jsNoWriter.getMessage());

        // 仅注入 writer：读抛 Getter 缺失，写可用
        CglibInjectProbe cgOnlyWriter = new CglibInjectProbe();
        cgOnlyWriter.inject("title", String.class, null, cglibFace.getMethod("setTitle", String.class));
        assertThrows(UnsupportedOperationException.class, () -> cgOnlyWriter.getPropertyValue(bean));
        cgOnlyWriter.setPropertyValue(bean, "injected");
        assertEquals("injected", bean.getTitle());

        JsstInjectProbe jsOnlyWriter = new JsstInjectProbe();
        jsOnlyWriter.inject("title", String.class, null, jsstFace.getMethod("setTitle", String.class));
        assertThrows(UnsupportedOperationException.class, () -> jsOnlyWriter.getPropertyValue(bean));
        bean.setTitle(null);
        jsOnlyWriter.setPropertyValue(bean, "injected2");
        assertEquals("injected2", bean.getTitle());

        // getGenericType 现状优先级：reader 命中优先于 writer；仅 writer 时取参数表[0]
        CglibInjectProbe cgBoth = new CglibInjectProbe();
        cgBoth.inject("count", int.class, cglibFace.getMethod("getCount"), cglibFace.getMethod("setCount", int.class));
        assertEquals(int.class, cgBoth.getGenericType(), "reader+writer 齐备时泛型类型应取 reader 返回类型");
        CglibInjectProbe cgWriterOnly = new CglibInjectProbe();
        cgWriterOnly.inject("count", int.class, null, cglibFace.getMethod("setCount", int.class));
        assertEquals(int.class, cgWriterOnly.getGenericType(), "仅 writer 时泛型类型应取参数表首元素");

        JsstInjectProbe jsBoth = new JsstInjectProbe();
        jsBoth.inject("count", int.class, jsstFace.getMethod("getCount"), jsstFace.getMethod("setCount", int.class));
        assertEquals(cgBoth.getGenericType(), jsBoth.getGenericType());
        JsstInjectProbe jsWriterOnly = new JsstInjectProbe();
        jsWriterOnly.inject("count", int.class, null, jsstFace.getMethod("setCount", int.class));
        assertEquals(cgWriterOnly.getGenericType(), jsWriterOnly.getGenericType());

        // 注：注入型态下 getPropertyValue 以 this.type 作实参调用零参 getter——双实现 invoker 层均忽略多余实参，
        // 读写结果与经 ClassAccessor 装配的属性一致（上方断言已覆盖）。
    }

    // ===== 对二：工厂与缓存身份现状 =====

    @Test
    void factoriesYieldOwnFaceProductsAndStableCacheIdentity() {
        ClassAccessor cglib1 = CGlibUtils.getGClass(SampleBean.class);
        ClassAccessor cglib2 = CGlibUtils.getGClass(SampleBean.class);
        assertSame(cglib1, cglib2, "cglib 面同类二次请求未命中缓存");
        assertInstanceOf(CGlibClassAccessor.class, cglib1);

        ClassAccessor jsst1 = JavassistAccessors.getGClass(SampleBean.class);
        ClassAccessor jsst2 = JavassistAccessors.getGClass(SampleBean.class);
        assertSame(jsst1, jsst2, "javassist 面同类二次请求未命中缓存");
        assertInstanceOf(JSsistClassAccessor.class, jsst1);

        assertInstanceOf(CGlibPropertyAccessor.class, cglib1.getProperty("count"),
                "cglib 面产物属性必须是 CGlibPropertyAccessor");
        assertInstanceOf(JSsistPropertyAccessor.class, jsst1.getProperty("count"),
                "javassist 面产物属性必须是 JSsistPropertyAccessor");

        // 过滤器引用身份参与缓存键（双面对称钉桩；javassist 面已由 ProxyAccessorIntegrityTest 覆盖，此处补 cglib 面与互吞检查）
        ClassAccessor cgFiltered = CGlibUtils.getGClass(SampleBean.class, method -> false);
        assertNotSame(cglib1, cgFiltered, "cglib 面带/不带过滤器共用缓存条目");
        assertTrue(cgFiltered.getGMethodList().isEmpty(), "cglib 面全拒过滤器产物方法集须为空");
        assertFalse(cglib1.getGMethodList().isEmpty());
        ClassAccessor jsFiltered = JavassistAccessors.getGClass(SampleBean.class, method -> false);
        assertNotSame(jsst1, jsFiltered, "javassist 面带/不带过滤器共用缓存条目");
        assertTrue(jsFiltered.getGMethodList().isEmpty());

        // 两面缓存相互独立：不得跨面复用产物
        assertNotSame((Object) cglib1, (Object) jsst1);
    }

}
