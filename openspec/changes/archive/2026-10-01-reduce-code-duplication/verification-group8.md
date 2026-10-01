# Verification 组 N8 — starter properties + data EntityManager（tasks 8.1–8.5，design D7 + D8-EntityManager）

日期：2026-10-01　分支：5.7.x　范围：`tny-game-starter-net-netty4`（guide 两 Properties）、`tny-game-starter-data`（mongodb/redisson/storage 三 Properties）、`tny-game-data`（EntityManager 批量 default）。

## 模块测试命令 + 结果行

重构前钉桩（rule 3 前置，期望值以"重构前实跑"为准；三次期望值修订均如实向现状收敛、非向实现修正）：

- `./gradlew :tny-game-data:test` → BUILD SUCCESSFUL，`EntityManagerBatchTest` tests=7 failures=0 errors=0 skipped=0（重构前绿实录）
- `./gradlew :tny-game-starter-data:test` → 首轮 BUILD SUCCESSFUL 后暴露 Mongo 侧 3 例 NoClassDefFoundError（`tny-game-data-mongodb` 仅 compileOnly 不进 test 运行时，属测试 classpath 补位非行为问题）→ `build.gradle` 增 `testImplementation project(":tny-game-data-mongodb")` 后 → tests=12 failures=0（重构前绿实录）
- `./gradlew :tny-game-starter-net-netty4:test --tests "...BootstrapPropertiesBinderRegressionTest"` → 首轮 10 例中 3 例按观测现状修订期望（键集排序、setName fill-if-blank、Rpc 复数条目绑定名——见遗留登记 2/3），另暴露 `tny-game-net-netty4-codec-jprotobuf` 仅 compileOnly 的同类 classpath 缺口 → `build.gradle` 增对应 `testImplementation` → tests=10 failures=0（重构前绿实录）

重构后验证（task 8.5）：

- `./gradlew :tny-game-data:test --rerun` → BUILD SUCCESSFUL，TOTAL=7 FAIL=0 ERR=0 SKIP=0（期望值零改动）
- `./gradlew :tny-game-starter-data:test --rerun` → BUILD SUCCESSFUL，TOTAL=12 FAIL=0 ERR=0 SKIP=0（期望值零改动）
- `./gradlew :tny-game-starter-net-netty4:test --rerun` → BUILD SUCCESSFUL，全模块 7 个套件 TOTAL=25 FAIL=0 ERR=0 SKIP=0（含本组 10 例、既有 BootAssemblySemanticsTest/Codec* 套件——后者属并行他案（add-crc32-verify-tier）在途文件，同绿）
- `./gradlew :tny-game-mongodb:compileJava --rerun` → 实跑 BUILD SUCCESSFUL；`./gradlew :tny-game-redisson:compileJava --rerun` → 实跑 BUILD SUCCESSFUL（哨兵由 UP-TO-DATE 强制转实跑取证）
- 加测消费面：`./gradlew :tny-game-integration-test:compileIntegrationTestJava` → BUILD SUCCESSFUL
- 确定性说明：改动区无时钟/随机/容器依赖；Binder 钉桩用 StandardEnvironment+PropertiesPropertySource 内存源，不触外部资源。

## 变更文件清单

生产代码——新增父类（3 文件，全部带 Mulan PSL v2 头，import 简单名，体内零 FQN）：

- `tny-game-starter-net-netty4/src/main/java/com/tny/game/net/netty4/network/guide/AbstractSpringBootBootstrapProperties.java`（109 行）
  - `public abstract <S extends SpringNettyNetServerBootstrapSetting, C extends SpringNettyNetClientBootstrapSetting>`，implements `SpringBootNetBootstrapSettings`；Net/Rpc 双子的 87×2 行样板 bodies 单一事实源。同包父类，D7"提同包"字面达成。差异仅以构造参数 `clientDefaultName`（"default"/"rpc"）承载。父类不标注 `@ConfigurationProperties`/`@ConditionalOnMissingBean`，注册点留子类。
- `tny-game-starter-data/src/main/java/com/tny/game/data/configuration/AbstractStorageAccessorFactoryProperties.java`（76 行）
  - `public abstract <S>`，accessor/accessors/enable 键族；Mongo+Redisson 共父（51×2 行 bodies 合一）。落公共包 `configuration`（见遗留登记 1，跨子包共享之偏离申明）。懒造默认 Setting 由子类构造器传入（"字段初始化器与懒造默认值留子类"）。
- `tny-game-starter-data/src/main/java/com/tny/game/data/configuration/storage/AbstractObjectStorageFactoriesProperties.java`（75 行）
  - `public abstract <S>`，storage/storages/enable 键族；Async 单亲同包父类（与 accessor 名族异名不可并父，并之则 Async 绑定面泄漏多余键——键集钉桩禁绝，见遗留登记 1）。

生产代码——改造（5 文件；numstat：−154/+101，净 −53 行；三父类 +260 行，生产原始净增 +207 行、度量以岛线计）：

- `network/guide/SpringBootNetBootstrapProperties.java`（−34/+19）：类注解（`@ConfigurationProperties(prefix="tny.net.bootstrap.network")`+`@ConditionalOnMissingBean` 注册点）与 `implements SpringBootNetBootstrapSettings` 逐字保留；8 个 public 访问器全部协变重声明薄委托（保原声明结构与原 JVM 描述符，JavaBeanBinder 发现面逐字同构）；新增 public 无参构造器 `super("default")`。
- `network/guide/SpringBootRpcBootstrapProperties.java`（−34/+19）：同上，`super("rpc")`。
- `configuration/mongodb/MongoStorageAccessorFactoryProperties.java`（−20/+14）：注解逐字保留；无参构造 `super(new MongoStorageAccessorFactorySetting())`；链式 setter×3 + `getAccessor` 协变重声明（`isEnable`/`getAccessors` 走继承——erasure 描述符与原声明一致，键集用例实测零变）。
- `configuration/redisson/RedissonStorageAccessorFactoryProperties.java`（−20/+14）：同上模式。
- `configuration/storage/AsyncObjectStorageFactoriesProperties.java`（−20/+13）：同上模式（storage 族）。
- `tny-game-data/src/main/java/com/tny/game/data/EntityManager.java`（−26/+22）：`insert/update/save/deleteEntities` 四个批量 default **签名逐字不动**、方法体收敛为 `applyEach(entities, entity -> this.xxxEntity(entity) ? 1 : 0)`；新增 `default int applyEach(Collection<E>, ToIntFunction<E>)`（D8 授权面，纯增量）；抽象方法零触碰；新增 import `java.util.function.ToIntFunction`。

构建脚本（测试运行时 classpath 补位，生产依赖图零变，见遗留登记 5）：

- `tny-game-starter-data/build.gradle` +2：`testImplementation project(":tny-game-data-mongodb")`
- `tny-game-starter-net-netty4/build.gradle` +2：`testImplementation project(":tny-game-net-netty4-codec-jprotobuf")`

测试代码——新增钉桩 3 文件（重构前绿→重构后期望值零改动仍绿）：

- `tny-game-data/src/test/java/com/tny/game/data/EntityManagerBatchTest.java`（170 行，7 例）：桩计次+成败脚本队列+迭代序流水；四批量方法计数、空集合边界、null 集合 NPE 现状钉、LinkedHashSet 遍历序。
- `tny-game-starter-data/src/test/java/com/tny/game/data/configuration/FactoryPropertiesBinderRegressionTest.java`（246 行，12 例）：三 Properties 各 {键集(Introspector)/默认值/链式+null 直存/Binder 属性文件→对象（enable+嵌套 accessor/storage+复数 map relaxed kebab 键）} 四例；绑定走生产同路径 `Binder.get(env).bind(prefix, Class)`。
- `tny-game-starter-net-netty4/src/test/java/com/tny/game/net/netty4/network/guide/BootstrapPropertiesBinderRegressionTest.java`（258 行，10 例）：键集/默认值只读包装/setter fill-if-blank 改名与 null 容忍/Binder 改名观测值钉（Net=default/beta，Rpc=rpc/rpc）/协变 getter 与链式编译面。

## 公共签名冻结自查结论

- 5 个 Properties public 类：类名、`implements` 关系、类注解逐字未动；新增 `extends Abstract...` 层级为 D7 唯一豁免（纯增量，instanceof 只增不减）。消费者可见方法：netty4 子类 8 访问器声明全量原样保留（协变覆写委托）；data 子类链式 setter 与协变 getter 声明原样保留，`isEnable`/`getAccessors`（`getStorages`）改为继承——源可见签名逐字相同且 JVM 描述符（`()Z`/`()Ljava/util/Map;`）与原声明一致；构造器：原隐式无参 → 显式 public 无参（`getDeclaredConstructor().newInstance()` 生产路径实测绿）。
- `EntityManager`：10 个抽象方法、4 个批量 default、其余 default 重载全部签名逐字不动；`applyEach` 为 D8 明文授权新增 default（实现类覆写面不变，仓内引用面 grep 实测 EntityManager 仅 tny-game-data 自有文件）。
- 绑定键集零增减由 Introspector 用例双向钉死（重构前后同期望同绿）：Net/Rpc {client, clients, server, servers}、Mongo/Redisson {accessor, accessors, enable}、Async {enable, storage, storages}——storage 名族未泄漏 accessor 键、accessor 名族未泄漏 storage 键，两父分立的必要性即此。
- `git status` 复核：本组触碰仅限上列 14 文件（3 父类 + 6 生产改造 + 2 build.gradle + 3 钉桩测试；另 +2 openspec 台账：tasks.md 勾记、本 verification）；netty4 `NetAutoConfiguration.java`(+6)/`CodecGenerationAssemblyTest.java`/`CodecSpiBeanCoverageTest.java` 属并行 add-crc32-verify-tier 案在途文件，非本组产出、未改动。

## 遗留登记（零行为变更铁律下未触碰项）

1. **D7"各提同包"与包现实/键名现实不可同时字面达成**：starter-data 三胞胎分居 `configuration.{mongodb,redisson,storage}` 三个子包且键名异族（accessor/accessors vs storage/storages）。单父方案会向 Async 泄漏 `accessor`/`accessors` 绑定键（或反向），违反键集现状钉桩，铁律否决；字面"各提同包"则三父互为克隆、岛零减。落地为两族两父：accessor 父置公共包 `configuration`（Mongo/Redisson 跨子包共享，偏离"同包"，父子均 public、层级纯增量性质不变），storage 父置 `configuration.storage`（与 Async 同包✓）。netty4 父与两子同包，字面达成✓。复扫口径下此布局为键集冻结的必然形态。
2. **Binder 现状怪癖（Rpc 复数条目名=单数缺省值）**：绑定 `tny.net.bootstrap.rpc.clients.beta.*` 后条目终名"rpc"而非键名"beta"；Net 同形态得"beta"。探针实测为 JavaBeanBinder `BeanProperty#setValue` 路径差异所致（条目对象进入复数 setter 时已被填名单数缺省名，fill-if-blank 后续键名改写跳过），机制未完全穿透、禁止顺手修；已按观测值钉桩（rpcBinder），提父后零漂移（期望零改动仍绿，证明父类化未扰绑定路径）。
3. **setName 为 fill-if-blank 语义**：`CommonNetBootstrapSetting.setName` 仅当现名空白写入——Net/Rpc 四个 setter 内"改名"实为"补空白名"，已有名不覆盖（netSetterRenameSemantics 钉死；`server: ~` 空值不 NPE 条款原样入父类注释）。
4. **data 三胞胎 setter null 直存 + getter 直返 live 可变引用**：`setAccessor(null)`/`setAccessors(null)`（storage 同）置 null 无兜底；`getAccessors()` 返可变 HashMap 非包装（与 netty4 侧 `Collections.unmodifiableMap` 形态异构）——两侧异构现状禁止抹平，已各钉。
5. **compileOnly 硬引用潜伏面（前置即存，非本组引入）**：`MongoStorageAccessorFactorySetting` 字段初始化硬引用 `tny-game-data-mongodb` 的 `MongoEntityIdConverterFactory`/`JsonMongoEntityConverter`，`SpringNettyChannelSetting` 构造器硬引用 `tny-game-net-netty4-codec-jprotobuf` 的 `TypeProtobufMessageBodyCodec`——两依赖主源集均 compileOnly，装配期缺 jar 即 NCDFE。本组仅在 test 源集补运行时位（生产依赖图零变）；缺 jar 兜底/文档化另案。
6. **starter-data 既有 `SpringCacheTest`（@SpringBootTest）不在 JUnit 发现集**：`:test` 重构前后均只产出本组 12 例结果文件，该 1 例类未运行（历史状态，未调查根因——非本组职责，登记）。
7. **两父类结构同形残余**：`AbstractStorageAccessorFactoryProperties` 与 `AbstractObjectStorageFactoriesProperties` 除方法名外骨架一致（enable+单例+map+6 访问器），标识符归一的 M3 可能仍聚为 2 文件岛（约 51 行级）——键名冻结下的不可消解残余，收口复扫若命中按"异名族不可并父"如实报口径（遗留登记 1 的推论）。
8. **协变重声明的取舍记录**：netty4 子类将 getter 亦全量重声明（非 D7 文本明列的仅链式 setter）——理由：保原声明面与原 JVM 描述符，使 JavaBeanBinder 发现面与改前逐字同构（遗留登记 2 的绑定路径对方法声明位点敏感）；data 侧同型位点仅重声明描述符会变者（getAccessor/getStorage），`(enable/getAccessors)` 继承无变。若后续消费者以反射 getDeclaredMethod 依赖父类侧声明位，本条为查证索引。