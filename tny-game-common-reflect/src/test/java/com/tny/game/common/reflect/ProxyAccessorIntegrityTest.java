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
package com.tny.game.common.reflect;

import com.tny.game.common.reflect.aop.*;
import com.tny.game.common.reflect.cglib.*;
import com.tny.game.common.reflect.exception.*;
import com.tny.game.common.reflect.javassist.*;
import com.tny.game.common.reflect.proxy.WrapperProxyFactory;
import javassist.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * proxy-accessor-integrity 契约（fix-common-dormant-defects 组7）：
 * 换位重载代理不碰撞、方法查找名字参与、过滤器参与缓存身份、
 * Error 不被折叠、属性名无幽灵空键、泛型组件穿透间接层、代理生成失败显式化。
 */
class ProxyAccessorIntegrityTest {

    // ===== fixtures =====

    public static class Swapped {

        public String describe(String name, int count) {
            return "S:" + name + "/" + count;
        }

        public Integer describe(int count, String name) {
            return count * 2;
        }
    }

    public static class Named {

        public String same(String v) {
            return "same";
        }

        public String other(String v) {
            return "other";
        }
    }

    public static class PrefixOnly {

        private String value = "x";

        /** 剥前缀后属性名为空的纯前缀方法 */
        public String get() {
            return value;
        }

        public void set(String v) {
            this.value = v;
        }
    }

    public interface Marker<T> {
    }

    public abstract static class Middle implements Marker<String> {
    }

    public static class Leaf extends Middle {
    }

    public static class Boom {

        public static volatile Throwable lastError;

        public void thrower() {
            throw new AssertionError("must-not-be-wrapped-as-target-exception");
        }
    }

    /** 跨加载域遮蔽 fixture：仅 shadowProxyNameCollision 使用，保证 INVOKER_MAP 无其条目 */
    public static class ShadowTarget {

        public void shadowed() {
        }
    }

    /** 业务异常钉桩 fixture：静态唯一哨兵实例，断言两实现包装原因的同一对象穿透 */
    public static class BizThrower {

        static final RuntimeException RUNTIME_BIZ = new IllegalStateException("biz-runtime-sentinel");

        static final Exception CHECKED_BIZ = new Exception("biz-checked-sentinel");

        public void throwRuntime() {
            throw RUNTIME_BIZ;
        }

        public void throwChecked() throws Exception {
            throw CHECKED_BIZ;
        }
    }

    /** 构造失败钉桩 fixture：仅带参构造的具体类 */
    public static class NoDefaultCtorTarget {

        private final int v;

        public NoDefaultCtorTarget(int v) {
            this.v = v;
        }

        public int getV() {
            return v;
        }
    }

    /** 构造失败钉桩 fixture：抽象类型 */
    public abstract static class AbstractConstructTarget {

        public abstract void doSomething();
    }

    /** 构造失败钉桩 fixture：接口 */
    public interface ConstructIfaceTarget {

        void runIt();
    }

    /** invoker 层唯一性钉桩 fixture——仅 invokerRepeatAndConcurrentYieldSingleInstance 使用 */
    public static class InvokerRaceTarget {

        public volatile int concurrentHits;

        public void raceMe() {
        }

        public void raceMeConcurrent() {
            concurrentHits++;
        }
    }

    /** 顶层类 fixture（AOP 代理生成对嵌套类命名不支持，见 AopGuardedTarget） */

    static class RecordingAdvice implements BeforeAdvice {

        final List<String> invoked = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void doBefore(java.lang.reflect.Method method, Object[] args, Object target) throws Throwable {
            invoked.add(method.getName());
        }
    }

    // ===== 换位重载（规格：类名散列顺序敏感） =====

    @Test
    void swappedOverloadsGetWorkingInvokers() throws Exception {
        ClassAccessor accessor = JavassistAccessors.getGClass(Swapped.class);
        java.lang.reflect.Method m1 = Swapped.class.getMethod("describe", String.class, int.class);
        java.lang.reflect.Method m2 = Swapped.class.getMethod("describe", int.class, String.class);
        MethodAccessor a1 = accessor.getMethod(m1);
        MethodAccessor a2 = accessor.getMethod(m2);
        assertNotNull(a1, "换位重载其一未获得可用访问器（异或碰撞后查表为空）");
        assertNotNull(a2, "换位重载其一未获得可用访问器（异或碰撞后查表为空）");
        assertEquals("S:a/1", a1.invoke(new Swapped(), "a", 1));
        assertEquals(4, a2.invoke(new Swapped(), 2, "b"));
    }

    // ===== 方法查找名字参与 =====

    @Test
    void methodLookupHonorsName() {
        ClassAccessor accessor = JavassistAccessors.getGClass(Named.class);
        MethodAccessor same = accessor.getMethod("same", String.class);
        assertNotNull(same);
        assertEquals("same", same.getName(), "同参不同名的重载返回了错误方法（名字未参与匹配）");
        // 契约（组7 REAL_GAP-1）：未命中显式失败，不得返回 null 把失败转嫁给远端消费点
        assertThrows(MethodNotFoundException.class, () -> accessor.getMethod("absent", String.class),
                "javassist 面未命中必须抛方法不存在异常");
        ClassAccessor cglib = CGlibUtils.getGClass(Named.class);
        MethodAccessor cglibSame = cglib.getMethod("same", String.class);
        assertNotNull(cglibSame);
        assertEquals("same", cglibSame.getName(), "cglib 面同参不同名重载返回了错误方法（名字未参与匹配）");
        assertThrows(MethodNotFoundException.class, () -> cglib.getMethod("absent", String.class),
                "cglib 面未命中同样必须显式失败");
    }

    // ===== 过滤器参与缓存身份 =====

    @Test
    void methodFilterParticipatesInCacheIdentity() {
        ClassAccessor unfiltered = JavassistAccessors.getGClass(Named.class);
        assertFalse(unfiltered.getGMethodList().isEmpty(), "无过滤器版本应含方法");
        ClassAccessor filtered = JavassistAccessors.getGClass(Named.class, method -> false);
        assertTrue(filtered.getGMethodList().isEmpty(),
                "带全拒过滤器的版本方法集为空——原实现与无过滤版本共用缓存，先注册者定生死");
        // 反向顺序也要成立
        ClassAccessor filtered2 = JavassistAccessors.getGClass(Boom.class, method -> false);
        assertTrue(filtered2.getGMethodList().isEmpty());
        assertFalse(JavassistAccessors.getGClass(Boom.class).getGMethodList().isEmpty());
    }

    // ===== Error 不被折叠 =====

    @Test
    void errorPropagatesNotWrappedAsTargetException() {
        ClassAccessor accessor = JavassistAccessors.getGClass(Boom.class);
        MethodAccessor method = accessor.getMethod("thrower");
        Throwable thrown = assertThrows(Throwable.class, () -> method.invoke(new Boom()));
        // 契约：Error 原样透传（cglib 面 FastMethod 语义），不得折叠为反射目标异常
        assertInstanceOf(AssertionError.class, thrown,
                "Error 被包装成 InvocationTargetException（与另一实现透传语义分裂）");
    }

    /** 组7 REAL_GAP-3：cglib 面 FastMethod 把目标 Error 折叠为 InvocationTargetException——须在边界解包原样透传 */
    @Test
    void errorPropagatesThroughCglibAccessorUnwrapped() {
        ClassAccessor accessor = CGlibUtils.getGClass(Boom.class);
        MethodAccessor method = accessor.getMethod("thrower");
        assertNotNull(method, "cglib 面按名查找未命中 thrower");
        Throwable thrown = assertThrows(Throwable.class, () -> method.invoke(new Boom()));
        assertInstanceOf(AssertionError.class, thrown,
                "cglib 面 Error 被折叠为 InvocationTargetException（与 javassist 面透传语义分裂）");
    }

    // ===== 跨加载域显式失败 =====

    /**
     * 组7 REAL_GAP-2：派生代理名被他域/他签名产物占用（Class.forName 命中）而本域 INVOKER_MAP 无该 method
     * 条目时，newInvoker 不得静默返回 null（幽灵句柄延后为远离成因的 NPE），必须显式失败。
     */
    @Test
    void shadowedProxyNameFailsExplicitly() throws Exception {
        java.lang.reflect.Method method = ShadowTarget.class.getMethod("shadowed");
        // 复刻 InvokerFactory 的顺序敏感散列命名（本 fixture 不在 java.util 前缀下，零参）
        String declaringName = method.getDeclaringClass().getName();
        StringBuilder signature = new StringBuilder(declaringName);
        signature.append('#').append(method.getName());
        for (Class<?> paramClass : method.getParameterTypes()) {
            signature.append('#').append(paramClass.getName());
        }
        String proxyClassName = declaringName + "$" + method.getName() + "$" + Math.abs(signature.toString().hashCode());
        // 预置遮蔽：同加载域中先定义同名代理类产物，但调用器映射从未登记该方法
        ClassPool pool = ClassPool.getDefault();
        pool.makeClass(proxyClassName).toClass(ShadowTarget.class);
        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> InvokerFactory.newInvoker(method),
                "他域产物遮蔽下 newInvoker 静默返回了空句柄");
        assertTrue(failure.getMessage().contains(proxyClassName),
                "异常须含代理类名以便定位: " + failure.getMessage());
        assertTrue(failure.getMessage().contains(declaringName),
                "异常须含 declaringClass 以便定位: " + failure.getMessage());
    }

    // ===== 幽灵空键 =====

    @Test
    void purePrefixMethodsDoNotRegisterNullKey() {
        ClassAccessor accessor = JavassistAccessors.getGClass(PrefixOnly.class);
        assertFalse(accessor.getAccessorMap().containsKey(null),
                "纯前缀方法 get()/set() 注册了 null 幽灵属性键");
        assertNull(accessor.getProperty(null));
    }

    // ===== 泛型组件穿透间接层 =====

    @Test
    void componentTypeTraversesIndirectHierarchy() {
        List<Class<?>> direct = ReflectAide.getComponentType(Leaf.class, Marker.class);
        assertEquals(List.of(String.class), direct,
                "接口经抽象类间接实现时组件解析返回空（注册逻辑静默跳过的源头）");
    }

    // ===== 代理生成失败显式化 =====

    @Test
    void anonymousProxyFailsExplicitly() {
        Runnable anon = () -> {
        };
        // 原实现：异常被吞、返回 null，NPE 在远离成因的 createWrapper 处才爆
        assertThrows(RuntimeException.class,
                () -> WrapperProxyFactory.getWrapperProxyClass(anon.getClass()),
                "无法生成代理时必须显式失败，不得返回 null");
    }

    /** 并发首建同一目标类：单产物、无 LinkageError */
    @Test
    void concurrentWrapperCreationYieldsSingleClass() throws Exception {
        int threads = 8;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<Class<?>>> futures = new ArrayList<>();
            for (int t = 0; t < threads; t++) {
                futures.add(pool.submit(() -> {
                    barrier.await(5, TimeUnit.SECONDS);
                    return WrapperProxyFactory.getWrapperProxyClass(PrefixOnly.class);
                }));
            }
            Set<Class<?>> distinct = new HashSet<>();
            for (Future<Class<?>> f : futures) {
                distinct.add(f.get(30, TimeUnit.SECONDS));
            }
            assertEquals(1, distinct.size(), "并发首建产生多个代理类（双定义竞态）");
        } finally {
            pool.shutdownNow();
        }
    }

    // ===== 切面注解值参数生效 =====

    @Test
    void classLevelAopValueRestrictsInterception() throws Exception {
        RecordingAdvice advice = new RecordingAdvice();
        AoperBuilder<AopGuardedTarget> builder = AoperBuilder.newBuilder(AopGuardedTarget.class);
        builder.setBeforeAdvice(advice);
        AopGuardedTarget proxy = builder.build();
        assertNotNull(proxy, "构建失败必须显式异常而非返回 null");
        proxy.pub();
        AopGuardedTargetAccessors.callProtected(proxy);
        assertTrue(advice.invoked.contains("pub"), "public 方法应被 @AOP(PUBLIC) 拦截");
        assertFalse(advice.invoked.contains("prot"),
                "类级 @AOP 的值参数被忽略——protected 等效纳入代理（注解语义失效）");
    }

    /**
     * 组7 SPEC 收窄：不同注解集合互不吞——钉缓存身份语义（后注册者不得免费获得先注册者产物；
     * 独立产物或携目标类与成因的显式失败二选一，产物类名端到端唯一不在承诺面）。
     */
    @Test
    void differentAnnotationSetBuildsDoNotShareCacheEntry() {
        Object first = AoperBuilder.newBuilder(AopDoubleTarget.class).addAnnotation(Deprecated.class).build();
        assertNotNull(first, "常规目标的构建应成功");
        Class<?> firstClass = first.getClass();
        Object sameSet = AoperBuilder.newBuilder(AopDoubleTarget.class).addAnnotation(Deprecated.class).build();
        assertSame(firstClass, sameSet.getClass(), "同一注解集合重复请求未命中同一缓存条目");
        Throwable failure = null;
        Object second = null;
        try {
            second = AoperBuilder.newBuilder(AopDoubleTarget.class).addAnnotation(Override.class).build();
        } catch (Throwable t) {
            failure = t;
        }
        if (failure != null) {
            assertTrue(failure instanceof IllegalStateException,
                    "生成失败应显式抛 IllegalStateException，实为: " + failure.getClass().getName());
            assertTrue(failure.getMessage().contains(AopDoubleTarget.class.getSimpleName()),
                    "异常须携带目标类以便定位: " + failure.getMessage());
            assertNotNull(failure.getCause(), "生成失败异常须附带成因（原吞异常后裸抛无 cause）");
        } else {
            assertNotSame(firstClass, second.getClass(),
                    "不同注解集合的请求静默复用了先注册者产物（缓存互吞）");
        }
    }

    // ===== 目标业务异常两实现一致包装（复验阻断#6，钉『目标业务异常两实现一致包装』Scenario） =====

    @Test
    void businessExceptionWrappedConsistentlyAcrossImplementations() {
        ClassAccessor js = JavassistAccessors.getGClass(BizThrower.class);
        ClassAccessor cglib = CGlibUtils.getGClass(BizThrower.class);
        // 非受检业务异常：两实现调用方均收到"调用目标异常"（InvocationTargetException），原因均为原抛出对象
        java.lang.reflect.InvocationTargetException jsRuntime = assertThrows(java.lang.reflect.InvocationTargetException.class,
                () -> js.getMethod("throwRuntime").invoke(new BizThrower()));
        assertSame(BizThrower.RUNTIME_BIZ, jsRuntime.getCause(), "javassist 面包装原因应为原业务异常对象本身");
        java.lang.reflect.InvocationTargetException cglibRuntime = assertThrows(java.lang.reflect.InvocationTargetException.class,
                () -> cglib.getMethod("throwRuntime").invoke(new BizThrower()));
        assertSame(BizThrower.RUNTIME_BIZ, cglibRuntime.getCause(), "cglib 面包装原因应为原业务异常对象本身");
        // 受检业务异常同样统一包装（Requirement MUST：受检/非受检统一包装并保留原因）
        java.lang.reflect.InvocationTargetException jsChecked = assertThrows(java.lang.reflect.InvocationTargetException.class,
                () -> js.getMethod("throwChecked").invoke(new BizThrower()));
        assertSame(BizThrower.CHECKED_BIZ, jsChecked.getCause(), "javassist 面受检异常原因应为原抛出对象");
        java.lang.reflect.InvocationTargetException cglibChecked = assertThrows(java.lang.reflect.InvocationTargetException.class,
                () -> cglib.getMethod("throwChecked").invoke(new BizThrower()));
        assertSame(BizThrower.CHECKED_BIZ, cglibChecked.getCause(), "cglib 面受检异常原因应为原抛出对象");
        assertSame(jsRuntime.getClass(), cglibRuntime.getClass(), "两实现业务异常包装种类不一致（口径分裂）");
    }

    // ===== 构造失败两实现均显式可定位（复验阻断#7，钉『构造失败两实现均显式可定位』Scenario） =====

    @Test
    void constructionFailureExplicitAndLocatableInBothImplementations() {
        // 无默认构造器的具体类、抽象类型与接口：两实现请求创建实例均须显式抛出、可定位到目标、不静默返回空对象；
        // 具体异常类型允许不同（spec 已收窄为"抛异常即合格"，此处只钉显式性与可定位性）
        for (Class<?> target : List.of(NoDefaultCtorTarget.class, AbstractConstructTarget.class, ConstructIfaceTarget.class)) {
            assertConstructionFailureLocatable(JavassistAccessors::getGClass, target, "javassist");
            assertConstructionFailureLocatable(CGlibUtils::getGClass, target, "cglib");
        }
    }

    private static void assertConstructionFailureLocatable(java.util.function.Function<Class<?>, ClassAccessor> face,
                                                           Class<?> target, String faceName) {
        Object created = null;
        Throwable thrown = null;
        try {
            created = face.apply(target).newInstance();
        } catch (Throwable t) {
            thrown = t;
        }
        if (thrown == null) {
            fail(faceName + " 实现对不可构造目标应显式抛出异常，实为静默返回: " + created);
        }
        // 可定位性：异常消息、成因链或栈帧须指向目标类产物（cglib 无默认构造器路径消息不含类名，
        // 但异常源自目标专属 FastClass——栈帧含目标二进制名；javassist 面消息即含目标类名）
        StringBuilder text = new StringBuilder(String.valueOf(thrown.getMessage()));
        for (StackTraceElement element : thrown.getStackTrace()) {
            text.append('\n').append(element.getClassName());
        }
        Throwable cause = thrown.getCause();
        while (cause != null) {
            text.append('\n').append(cause.getClass().getName()).append(' ').append(cause.getMessage());
            for (StackTraceElement element : cause.getStackTrace()) {
                text.append('\n').append(element.getClassName());
            }
            cause = cause.getCause();
        }
        assertTrue(text.toString().contains(target.getName()),
                faceName + " 实现构造失败异常须可定位到目标类 " + target.getName() + "，实为: " + thrown);
    }

    // ===== 多可见性声明按集合精确生效（复验阻断#8，钉『多可见性声明按集合精确生效』Scenario） =====

    @Test
    void multiVisibilityDeclarationInterceptsExactlyDeclaredSet() {
        RecordingAdvice advice = new RecordingAdvice();
        AopMultiVisibleTarget proxy = AoperBuilder.newBuilder(AopMultiVisibleTarget.class).setBeforeAdvice(advice).build();
        assertNotNull(proxy, "切面构建失败必须显式异常而非返回 null");
        proxy.pub();
        proxy.prot();
        proxy.pkg();
        assertEquals(List.of("pub", "prot"), new ArrayList<>(advice.invoked),
                "被拦截集合与声明集合 {PUBLIC, PROTECTED} 不完全一致——包私有被误拦截或声明方法被漏拦截");
        assertEquals(1, proxy.publicCalls, "公开方法应被拦截且仍执行原实现");
        assertEquals(1, proxy.protectedCalls, "受保护方法应被拦截且仍执行原实现");
        assertEquals(1, proxy.packageCalls, "未声明可见性的方法须直接调用执行（不被拦截不等于被吞掉）");
    }

    // ===== 无包名类请求显式失败（复验阻断#9，钉『无包名类请求显式失败（边界路径）』Scenario） =====

    @Test
    void defaultPackageTargetFailsExplicitlyWithReason() throws Exception {
        // JDK9+ 默认包类的 getPackage() 返回非 null 空名 Package——原守卫仅查 null，"无包名"分支永不触发，
        // 失败以 ClassFormatError 从代理名拼装处泄漏（不说明成因）。修复后应与匿名类分支同口径显式失败。
        Class<?> defaultPackageClass = Class.forName("DefaultPackageTarget");
        assertNotNull(defaultPackageClass, "默认包 fixture 应可加载");
        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> WrapperProxyFactory.getWrapperProxyClass(defaultPackageClass),
                "无包名类请求包装代理应抛出说明原因的显式异常，而非以非法类名崩溃泄漏");
        assertTrue(failure.getMessage().contains("DefaultPackageTarget"),
                "异常须携带目标类说明以便定位: " + failure.getMessage());
    }

    // ===== invoker 层同签名重复与并发请求唯一（复验阻断#10，钉『同签名重复与并发请求唯一』Scenario） =====

    @Test
    void invokerRepeatAndConcurrentYieldSingleInstance() throws Exception {
        java.lang.reflect.Method firstMethod = InvokerRaceTarget.class.getMethod("raceMe");
        java.lang.reflect.Method sameSignatureCopy = InvokerRaceTarget.class.getMethod("raceMe");
        assertNotSame(firstMethod, sameSignatureCopy, "前提：反射每次返回不同的 Method 实例（判等参与缓存键才有意义）");
        MethodInvoker base = InvokerFactory.newInvoker(firstMethod);
        assertNotNull(base);
        assertSame(base, InvokerFactory.newInvoker(sameSignatureCopy), "同签名重复请求返回了不同 invoker 实例（Method 判等未参与缓存键）");
        assertSame(base, InvokerFactory.newInvoker(firstMethod), "同一 Method 重复请求返回了不同 invoker 实例");
        // 并发首次请求：raceMeConcurrent 未被上文预热，线程真正竞争首次定义
        int threads = 8;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<MethodInvoker>> futures = new ArrayList<>();
            for (int t = 0; t < threads; t++) {
                futures.add(pool.submit(() -> {
                    barrier.await(5, TimeUnit.SECONDS);
                    return InvokerFactory.newInvoker(InvokerRaceTarget.class.getMethod("raceMeConcurrent"));
                }));
            }
            Set<MethodInvoker> distinct = new HashSet<>();
            for (Future<MethodInvoker> future : futures) {
                distinct.add(future.get(30, TimeUnit.SECONDS));
            }
            assertEquals(1, distinct.size(), "并发首次请求产生多个 invoker 实例（重复定义竞态或缓存身份漂移）");
            InvokerRaceTarget host = new InvokerRaceTarget();
            distinct.iterator().next().invoke(host);
            assertEquals(1, host.concurrentHits, "并发所得唯一 invoker 应正确路由到自身签名方法");
            base.invoke(host);
        } finally {
            pool.shutdownNow();
        }
    }

}
