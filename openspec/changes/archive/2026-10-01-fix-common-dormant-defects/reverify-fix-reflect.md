# 复验阻断收口记录 — reflect（proxy-accessor-integrity，2026-10-01）

对应 reverify-report.md 阻断 #6–#10（复验 wf_8c6dce69-8c9）。每条先对抗自查（读 spec 当前文本 + 实现全文 + 临时探针实测两实现行为，探针收口后已移出源码树至 /tmp/reflect-probe-quarantine/），再钉桩/修实现。

## 逐条记录

### 1. [HIGH] 『目标业务异常两实现一致包装』零断言 —— FIXED（钉桩，两实现行为实测已一致）
- 自查：spec Requirement『两种方法访问器实现的异常契约一致』承诺受检/非受检异常统一包装并保留原因，此前仅有 Error 路径钉桩（errorPropagates* 两例），业务异常零断言，指控属实。
- 探针实测：javassist 面 `JSsistMethodAccessor.invoke` 对 Exception 包装为 `InvocationTargetException(cause=原对象)`；cglib 面 `CGlibMethodAccessor.invoke` 重抛 FastMethod 的 `InvocationTargetException(target=原对象)`——非受检与受检业务异常两实现口径均一致，无需改实现。
- 落点：`ProxyAccessorIntegrityTest.businessExceptionWrappedConsistentlyAcrossImplementations`（fixture `BizThrower` 静态唯一哨兵，assertSame 原因穿透 + 两实现包装种类一致 assertSame），首跑即绿（钉桩补齐类，非"行为未实现"）。

### 2. [HIGH] 『构造失败两实现均显式可定位』+ tasks 7.1『构造异常两实现一致』零钉桩 —— FIXED（钉桩，规格已按前轮收窄）
- 自查：src/test 全量 grep `newInstance` 零命中属实；spec 前轮已收窄为"抛异常即合格、具体异常类型允许不同"（spec.md L58/L70），不改实现、只兑现钉桩。
- 探针实测：无默认构造器具体类——js `InstantiationException`（消息含类名）/ cglib `IllegalArgumentException`（栈帧指向目标专属 `…$$FastClassByCGLIB`）；抽象类型与接口——两实现异常均携目标类名。
- 落点：`ProxyAccessorIntegrityTest.constructionFailureExplicitAndLocatableInBothImplementations`（fixture `NoDefaultCtorTarget`/`AbstractConstructTarget`/`ConstructIfaceTarget`；断言=显式抛出、不静默返回、消息/成因链/栈帧可定位到目标类二进制名），首跑即绿。

### 3. [HIGH] 『多可见性声明按集合精确生效』无多可见性 fixture —— FIXED（钉桩，实现实测已精确）
- 自查：既有 `classLevelAopValueRestrictsInterception` 仅钉单可见性声明（PUBLIC），双可见性+包私有组合零覆盖，指控属实。
- 探针实测：`AoperBuilder.isAop` 遍历 `globalAOP.value()` 集合，生成循环本就只覆盖 public/protected——声明 {PUBLIC, PROTECTED} 时被拦截集合恰为 {pub, prot}、包私有直通基实现。
- 落点：新建顶层 fixture `AopMultiVisibleTarget`（`@AOP({PUBLIC, PROTECTED})`，pub/prot/pkg + 计数）+ `ProxyAccessorIntegrityTest.multiVisibilityDeclarationInterceptsExactlyDeclaredSet`（拦截序列 assertEquals([pub, prot]) 精确等值 + 三方法计数均 1，包私有"不被拦截≠被吞掉"），首跑即绿。

### 4. [HIGH] 『无包名类请求显式失败』仅覆盖匿名类分支 —— FIXED（复验驳回部分前提 + 修实现对齐，红基线留痕）
- 自查：报告称"实现存在但无默认包 fixture"——前半句不实：`WrapperProxyFactory` 守卫条件为 `targetPackage == null`，而 JDK9+ 默认包类 `getPackage()` 返回**非 null 空名** Package（实测 JDK21/25 皆然），"无包名"分支永不触发；失败以 `ClassFormatError: Illegal class name "/WrapperProxy$$…"` 从代理名拼装处泄漏（Error 被 `catch (RuntimeException | Error)` 原样上抛），不满足 Scenario『同样抛出可定位成因的显式异常』——规格承诺行为未实现，属"补实现"而非仅钉桩。
- 红基线留痕（修复前，`--tests ProxyAccessorIntegrityTest` 首跑）：`defaultPackageTargetFailsExplicitlyWithReason() FAILED … expected: <java.lang.IllegalStateException> but was: <java.lang.ClassFormatError>`（`17 tests completed, 1 failed`，余 16 含本域其余 4 新钉全绿，证明红点精确落在此项）。
- 修复落点：`WrapperProxyFactory.createWrapperProxyClass` 守卫扩为 `targetPackage == null || targetPackage.getName().isEmpty() || isAnonymous || isLocal`，与匿名类分支同口径 `IllegalStateException("目标类不可代理（匿名/本地/无包名）: " + 类名)`。
- 测试落点：新建默认包顶层 fixture `DefaultPackageTarget`（无包名声明，经 `Class.forName` 触达）+ `ProxyAccessorIntegrityTest.defaultPackageTargetFailsExplicitlyWithReason`（assertThrows IllegalStateException + 消息含目标类名），修复后转绿。

### 5. [HIGH] 『同签名重复与并发请求唯一』invoker 层零断言 —— FIXED（钉桩，行为实测已唯一）
- 自查：既有并发钉桩（`concurrentWrapperCreationYieldsSingleClass`）确属 WrapperProxyFactory 层，`InvokerFactory` 的 INVOKER_MAP 判等命中与首次定义竞态零覆盖，指控属实。
- 探针实测：反射返回的不同 `Method` 实例判等相等 → `newInvoker` 返回同一实例；8 线程 barrier 并发首次请求 → distinct=1。
- 落点：`ProxyAccessorIntegrityTest.invokerRepeatAndConcurrentYieldSingleInstance`（fixture `InvokerRaceTarget.raceMe/raceMeConcurrent`，重复断言用判等不同实例、并发断言用未预热方法保证真"首次"，并断言唯一实例调用正确路由自身方法），首跑即绿。

## 本域模块测试收口

- 命令：`./gradlew :tny-game-common-reflect:test`
- 修复前红基线运行：`17 tests completed, 1 failed`（仅 #4 新钉桩对旧行为红）
- 收口终跑：`BUILD SUCCESSFUL`；结果行 `tests 33 failures 0 skipped 0`（基线 28 + 新增 5 钉桩，全绿）
- 文件面：`ProxyAccessorIntegrityTest.java`（5 例钉桩 + 4 嵌套 fixture）、新建 `AopMultiVisibleTarget.java`、新建默认包 `DefaultPackageTarget.java`、`WrapperProxyFactory.java`（守卫修复）；新建 Java 文件均带 Mulan PSL v2 头；未触碰本域外文件。探针与旧临时 fixture 已移出源码树（/tmp/reflect-probe-quarantine/）。
