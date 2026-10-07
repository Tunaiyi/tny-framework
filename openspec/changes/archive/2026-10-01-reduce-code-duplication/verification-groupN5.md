# Group N5 verification — reflect 平行对收敛（tasks 5.1-5.3）

日期：2026-10-01。分支 5.7.x。组号 N5（harness lane 命名，对应 tasks.md 第 5 节）。本组不 commit（提交由主线收口统一处理）。
基线 = 开工时工作树（含上轮复验修复），非 HEAD。

## 测试命令与结果

| 命令 | 结果 |
|---|---|
| `./gradlew :tny-game-common-reflect:test --tests "com.tny.game.common.reflect.AccessorPairParityTest"`（**收敛前**钉桩） | BUILD SUCCESSFUL；AccessorPairParityTest tests=7 failures=0 errors=0（7/7 绿，期望值此后一字未改） |
| `./gradlew :tny-game-common-reflect:test`（收敛后全模块） | BUILD SUCCESSFUL；7 类 40 例 0 失败 0 错误：AOPTest 2、AccessorPairParityTest 7、ProxyAccessorIntegrityTest **17/17**、ReflectAideGetterSetterFixTest 3、ReflectAideTest 6、ReflectasmTest 4、JavassistAccessorsTest 1 |
| `./gradlew :tny-game-common-reflect:test --rerun-tasks`（防 UP-TO-DATE 伪证） | BUILD SUCCESSFUL；结果 XML 实测 AccessorPairParityTest `tests="7" failures="0" errors="0"`、ProxyAccessorIntegrityTest `tests="17" failures="0" errors="0"` |
| `./gradlew :tny-game-protoex:test :tny-game-net:test :tny-game-data:test` | BUILD SUCCESSFUL；结果 XML mtime 均为本次运行：protoex tests=30 f=0 e=0、net tests=167 f=0 e=0、data tests=7 f=0 e=0（data 的 `AnnotationCacheKeyMaker` 为 javassist 面 `getPropertyValue` 唯一跨模块消费者） |
| 复扫探针 `python3 baseline/dupscan.py && python3 dupmerge.py`（本组自查，正式口径以 9.1 收口复扫为准） | `CGlibUtils/JavassistAccessors` 在 M1/M2/M3 岛清单中**全部消失**（对二彻底并表）；`CGlibPropertyAccessor/JSsistPropertyAccessor` 残余为委托壳岛（M1 22-73、M3 12-77——冻结成员集所致，见遗留登记 1）；reflect M1 模块冗余 109→77、M3 306→245（v2 基线口径，含不在本组范围的 ClassAccessor 对 25×2 残余） |
| 期望值零改动证明 | AccessorPairParityTest 在收敛前后两次运行之间无任何编辑（唯一编辑发生在首跑之前）；`git status` 显示 ProxyAccessorIntegrityTest/JavassistAccessorsTest 等既有测试文件零改动 |

## 变更文件清单

新增 main（`com.tny.game.common.reflect` 本包，public **final 具体件**——非抽象、非接口、不涉任何既有类层级）：
- `PropertyAccessorSupport.java`（96 行）：对一逐字克隆的单一事实源。状态四元组（name/reader/writer/type）+ 全部语义：null 实例短路、Getter/Setter 缺失的 `UnsupportedOperationException` 文案（逐字符保留 `"…不支持 [x] 属性 Getter/Setter 方法"`）、`getGenericType` reader 优先取用规则、`reader.invoke(instance, new Object[]{type})` 显式数组单实现。invoke 写法差异收敛依据：向 `invoke(Object, Object...)` 的 varargs 形位传单个引用实参由 javac 静态包装为 `new Object[]{x}`——`javap -c` 核验收敛前两门面 `getPropertyValue` 指令序列逐字节相同（`iconst_1; anewarray; aastore; invokeinterface`），对任何值（含 null）两写法落为同一数组，零行为变更。
- `ClassAccessorCacheSupport.java`（75 行）：对二共享骨架。`synchronized (clazz)` 锁对象、双检、`putIfAbsent` 竞态返回先注册者、null clazz 路径顺序逐字保留；实例化目标差异（CGlibClassAccessor vs JSsistClassAccessor）以 `BiFunction<Class<?>, MethodFilter, ClassAccessor>` 工厂形参承载（D4"单一事实源+两侧现状双保"先例形态）；缓存实例**每面独立持有**（原各自 static 双表语义保留，两面互不复用——已由钉桩 `factoriesYieldOwnFaceProductsAndStableCacheIdentity` 锁定）；嵌套 `CacheKey` 降为 support 包私有（原即两文件各自的包私有嵌套类，非 API）。

修改 main（行数 前→后；主源码合计 358→391）：
- `cglib/CGlibPropertyAccessor.java` 96→77：成员集合与顺序逐字保留（7 public 含默认构造器 + 4 protected setter + implements PropertyAccessor），全部方法体改一行委托 `PropertyAccessorSupport`。
- `javassist/JSsistPropertyAccessor.java` 96→77：同上。
- `cglib/CGlibUtils.java` 83→33：两个 public static 工厂签名逐字冻结，体收敛为 `CACHE.get(clazz, filter, CGlibClassAccessor::new)`（protected 构造器同包方法引用）；删除包私有嵌套 CacheKey 与 static 双表（入 support）。
- `javassist/JavassistAccessors.java` 83→33：同上，工厂实参 `JSsistClassAccessor::new`；跨模块消费形态（protoex/net/data 静态调用点）零改动。

新增 test（钉桩账，先于实现收敛编写并实测重构前绿）：
- `AccessorPairParityTest.java`（7 例）：双实现属性读写结果一致（含跨面写读回、类型化查询 `getProperty(name, class)` 面）；null 实例短路先于 reader/writer 判空的现状；varargs/显式数组/空数组/null 实参形态等价（双门面各跑）；protected setName/setReader/setWriter/setType 注入面现状（新鲜访问器 null 态、缺失 Getter/Setter 的显式失败文案含字面量 `[null]`、注入后读写生效、`getGenericType` reader 优先/仅 writer 取参数表[0]）；对二工厂产物本面类型（`CGlibClassAccessor`/`JSsistClassAccessor`/`CGlibPropertyAccessor`/`JSsistPropertyAccessor` 各自 assertInstanceOf）与缓存身份（同类复用 assertSame、过滤器引用身份参与键、两面互不复用）。

## design Open Question 裁决（本组两条）

- 共享骨架落包：**`com.tny.game.common.reflect` 本包**。理由：两对类分居 `reflect.cglib` 与 `reflect.javassist` 姊妹包——Java 包私有不跨包（含父包→子包方向），"包内私有件"物理不可达；就近原则下父包正是所涉契约（PropertyAccessor/MethodAccessor/ClassAccessor）与共享工具先例（ReflectAide）所在地。"新建包内子包"仅改目录名不改可见性，收益为零，否决。
- 由此新增 2 个 public final 具体类进发布面：纯增量、无既有类层级/签名变化、final 不可扩展、无消费者应依赖（发布件内部桥）——与 D7 豁免不冲突（不新增任何既有类的父类）。D2 否决的"public 抽象层"（接口+实现族、可被消费者编程依赖）不适用：本件是具体实现桥，非抽象。

## 公共签名冻结自查（javap -p 编译产物逐条比对开工态）

- `CGlibPropertyAccessor`/`JSsistPropertyAccessor`：`public class … implements PropertyAccessor`、public 默认构造器、7 个 public 方法（含 `throws InvocationTargetException` 声明）、4 个 protected setter——逐字在位；类层级（extends Object / implements）零变化；变化仅发生在 private 字段（4 个私有标量→1 个私有 support——私有成员不在消费者可见合同面）。
- `CGlibUtils`/`JavassistAccessors`：public 类名、public 默认构造器、两个 `public static ClassAccessor getGClass` 重载逐字在位；删除成员仅 `private static` 字段与包私有嵌套类（非 API）；新增 private static 字段（非 API）。
- 既有测试零放宽：`ProxyAccessorIntegrityTest` 17 例、`JavassistAccessorsTest`、`ReflectasmTest`、`AOPTest` 等文件未触碰，17/17 全绿为实测。
- 新增声明集合：`PropertyAccessorSupport`（final 类 + public 构造器 + 8 public 读方法 + 4 public 写方法——写方法 public 是跨包委托的物理必需，命名与 protected setter 一一对应）、`ClassAccessorCacheSupport`（final 类 + public 构造器 + 1 public 方法；嵌套 CacheKey 包私有）。除这两个新增顶层件外，公共面零变化、零删除。

## 遗留登记（现状差异/可疑语义，一律未修、已钉桩或如实保留）

1. **对一委托壳残余克隆岛**（本组范围）：两壳的 12 个一行委托方法文本相同，M1 岛 22-73（52 行）/M3 岛 12-77（66 行）——消费者可见成员集（8 public+4 protected）× 两包被逐字冻结，委托壳是可达单一事实源后的文本下限；语义已全部在 Support 单副本，壳内无任何可漂移逻辑。与组2 遗留 #7"public 重载模板样板未并表"同类，止步（并表需删 public 类名或改层级=越出冻结面）。
2. **主源码原始行数净 +33**（358→391）：冻结签名强制两壳声明开销所致；**冗余行**（克隆岛口径）本家族净降（对二岛全灭 M1 −144 行级、对一语义全并表）。如实报数，不为行数指标删成员。
3. **`getPropertyValue` 以 `this.type` 作实参调用 getter**（零参 getter 收到一个 Class 实参——invoker 层现状忽略多余实参：cglib 3.3.0 FastMethod 实测不校验实参数、javassist 生成代理仅对 ≥1 参方法校验长度且零参方法不读 args）——可疑语义原样保留并钉桩（`varargsAndArrayFormAreEquivalentOnBothFaces`），修复另立变更。
4. **`CacheKey` 对 filter 取引用身份**（`System.identityHashCode`）：等值语义为"同一 lambda 实例才同键"，每次新建 lambda 的调用方会绕缓存增长——现状保留（该键设计即上轮组7 修复的产物），原 `java.util.Objects.hash` 体内全限定改为 import 简名调用（新文件规则4；语义逐字相同）。
5. **两面缓存独立性无静态保证**：CGlib/Javassist 面各自 static 实例，未来若有人合并将静默改产物类型——已以 `assertNotSame` + `assertInstanceOf` 钉桩兜底。
6. **ClassAccessor 对（CGlibClassAccessor↔JSsistClassAccessor）未在本组收敛**：侦察事实明确本组范围为两对（PropertyAccessor 对 + Utils 对）；ClassAccessor 对现存 25×2 行 M1 岛（getJavaClass/getGMethodList/getAccessorMap/getProperty×2 等尾段）但其方法筛选口径（public-only vs 非private+bridge、Object 声明类剔除）与 newInstance/getName 语义差异大，属"能过即收"待裁决面——留主线 9.1 复扫时决定是否另案。
7. **`getPropertyDescriptor/setPropertyDescriptor` 符号不存在于仓内**（任务 5.1 措辞）：注入面按实际存在的 protected setName/setReader/setWriter/setType 钉桩，已在 tasks.md 5.1 行注明。

## 工作序列（提交由主线收口）

1. 侦察四文件逐字比对 + javap 核验 invoke 两写法字节级相同 + 探针实测 cglib FastMethod 不校验实参数量。
2. 新增 `AccessorPairParityTest`（7 例）→ 收敛前实跑 7/7 绿。
3. 新增 `PropertyAccessorSupport`/`ClassAccessorCacheSupport`；四门面改薄委托。
4. 实跑：reflect 全模块 40 例绿（--rerun-tasks 复核 7/7+17/17）→ protoex/net/data 30+167+7 绿 → 复扫探针确认对二岛全灭。

## 环境备注

- gradle 全程串行执行、未用 `--stop`；一次 `grep -c "test\|@Test"` 命令为 zsh 语法噪音（`===`），与构建无关。
- `/tmp/dupislands.md` 等复扫产物为并行代理共用路径的本次重跑结果，正式收口复扫以 9.1 为准。
- 本域外文件零触碰：仅上列 4 改 + 2 新增 main + 1 新增 test + tasks.md/本文件。
