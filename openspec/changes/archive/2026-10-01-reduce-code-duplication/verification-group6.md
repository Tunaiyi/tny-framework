# Group 6 (N6) verification — 注册表族泛型中间层收敛（tasks 6.1-6.3，design D5）

日期：2026-10-01。分支 5.7.x。开工基线 HEAD：`971298da`。零行为变更、公共签名逐字冻结。

## 测试命令与结果

| 命令 | 结果 |
|---|---|
| `./gradlew :tny-game-basics:test --tests "com.tny.game.basics.EnumRegistryFamilyContractTest"`（重构前首跑） | 9/10 绿 + 1 失败＝我方 `ofItemId` 期望值算错（失败消息记录了重构前实然行为 `expected TRUNK but was ZERO`），修正期望为现状后复跑 |
| 重构前基线复证（`git stash push` 8 门面 → 原实现跑 10/10 → `git stash pop`） | BUILD SUCCESSFUL，10 tests 0 failures —— 钉桩先行且期望值与重构后完全一致 |
| `./gradlew :tny-game-common-scheduler:test --tests "...TaskDeliveryContractTest"`（TaskReceiverTypes 源零改动，即重构前状态） | BUILD SUCCESSFUL；tests=12 failures=0 errors=0（含新并入 `registryForwardingSixPieceAndStaticBlockLoadingPinned`） |
| `./gradlew :tny-game-basics:test :tny-game-common-scheduler:test --rerun-tasks`（收敛后终验） | BUILD SUCCESSFUL；basics **19 tests / 0 failures / 0 errors**；scheduler **50 tests / 0 failures / 0 errors**（上轮收口的宽松查找/别名缺席钉桩 `lenientLookup.../strictLookup.../removedAliasLookup...` 全部仍绿，无回归） |
| `./gradlew :tny-game-net:compileJava --rerun-tasks`（ClassImporter 其他继承点哨兵） | BUILD SUCCESSFUL |

## 变更文件清单

新增 main（1）：
- `tny-game-basics/src/main/java/com/tny/game/basics/utlis/EnumRegistrySupport.java` —— 注册表族泛型中间层：`EnumeratorHolder` 转发六件套（`of`/`check(String)`/`check(int)`/`option`/`all`/`enumerator`）、`register` 骨架、私有构造，及两式严格失败消息模板（`"获取 {} " + label + " 不存在"`、`"获取 ID为 {} 的 " + label + " 不存在"`）的**单一事实源**。落 basics 模块既有共享件桶 `com.tny.game.basics.utlis`（先例 `TryResults`；8 门面跨 item/mould 两包，包私有不可达，故为 public 但纯增量新类型——零既有签名变更）。

修改 main（8，均仅方法体行变化；`git diff` 中 ± 行零声明行，见下自查）：
- `item/Behaviors.java`、`item/Actions.java`、`item/Abilities.java`、`item/DemandParams.java`、`item/DemandTypes.java`、`item/ItemTypes.java`、`mould/Features.java`、`mould/Moulds.java` —— 转发六件套与 register 体改为 `EnumRegistrySupport.*(holder, ...)` 一行薄委托；各类 `holder` 字段（protected/private、final/非 final、ItemTypes 匿名 postRegister 子类）、`extends ClassImporter` 层级、私有构造、注释块、`ItemTypes.ALIAS_HEAD_SYMBOL` 常量与 `ofAlias/ofModelId/ofItemId` 自有扩展逐字保留。
- 合计 diff：86 insertions / 72 deletions（8 门面）+ 中间层新增 114 行。

新增/追加 test：
- 新增 `tny-game-basics/src/test/java/com/tny/game/basics/EnumRegistryFamilyContractTest.java`（10 用例：8 类 × 六件套命中/宽松未命中/严格未命中**消息逐字**矩阵；`DemandTypes.check` 委托宽松通道现状差异；`ItemTypes` 别名三件套含 `ofAlias("")` 越界现状与 `ofItemId` 截断链恒落 0 段现状；`Actions.getAll()` 异名 + 无 `all()`；`Moulds/Features.register` public 与 item 6 类包私有 register 可见性矩阵）。8 门面 register 为包私有——测试按 tasks 6.1 指定路径落 basics 根包，经 `getDeclaredMethod+setAccessible` 按现状可见性注册（反射同时充当可见性哨兵）。
- 追加 `tny-game-common-scheduler/src/test/java/com/tny/game/common/scheduler/TaskDeliveryContractTest.java`：1 用例钉 `TaskReceiverTypes` 转发六件套行为账 + static 注册块装载现状（SYSTEM/PLAYER 双通道可达、严格消息模板、不可修改 all() 视图、enumerator 同实例、register 包私有）。

零触碰：`ClassImporter`、`EnumeratorHolder`（教训与硬约束在册）、`GameEnumClassLoader/FeatureEnumClassLoader/SchedulerEnumClassLoader` 的 `createSelector(Xxx::register)` 消费形态（basics/scheduler/net 编译+测试实证）。

## 公共签名冻结自查

- `git diff tny-game-basics/src/main/java/com/tny/game/basics/ | grep -E "^[+-]"` 经声明关键字过滤后**零命中**（唯一命中为含 "enumerator" 子串误报的体行）——全部 ± 行为方法体、import 行、注释行；8 类的类名/修饰符/`extends ClassImporter`/全部 public static 声明（含泛型界 `<T extends Xxx>` 与 `check(int)`/`check(String)` 重载形态、`Actions.getAll()` 异名、`Moulds/Features.register` public、其余 register 包私有、私有构造、holder 字段声明）逐字在位。
- `ClassImporter` 的仓内 13 活跃继承点（net/basics 侧）未动父类本身；`:tny-game-net:compileJava --rerun-tasks` 绿为哨兵证据。
- 新增 public 类型仅 `EnumRegistrySupport`（纯增量、无既有类型改名/改签/改层级），对应 Compatibility Impact 表「注册表 9 类 + ClassImporter/EnumeratorHolder：public 成员冻结、无感」行。

## 实现形态偏离说明（D5 字面 vs 落点）

D5 设想 `Abstract<Facade>Importer<T>` 照 `AbstractExpr` 模板方法**继承**承载六件套。落地时发现静态语境不可行：门面六件套均为 `public static` 且各带类特定泛型界，若改为父类静态承载，成员声明点/擦除返回类型（如 `(String)Behavior → (String)Enumerable`）随之下移，**破坏下游已编译 jar 的方法描述符解析**（NoSuchMethodError 级），违反 Compatibility 表「public 成员冻结」与铁律 2（D7 为唯一层级豁免且仅涉 starter 面）。故按 P8「组合复用行为」落组合形态：泛型中间层 `EnumRegistrySupport` 承载全部转发**逻辑**（消息模板、注册骨架、宽松/严格通道），门面保留逐字冻结的声明作一行薄委托。语义账 = 单一事实源；ABI 账 = 逐字不动。此偏离记录在案，供组 9 与后续变更参照。

## 止步与遗留登记（现状可疑语义，一律未修、已钉桩）

1. **`TaskReceiverTypes` 未并入中间层（N6 任务预授权路径「若有耦合收益为负就止步 basics 8 类并登记，允许」）**：中间层按 D5 落 basics 包内，而 `tny-game-common-scheduler` 依赖面仅 common-lifecycle/common-io——并入需新增 scheduler→basics 依赖（基础设施模块反向依赖游戏域模块，方向性负收益）或将中间层外移至 common-lang（放大基座模块发布面，违背 D5「收益全是仓内的，抽象放包内即够」同源的 P10/P11 判据）；其 static 注册块、非 final 类形态、匿名空子类 holder 均按 D5 留本类。转发段以测试钉桩替代收敛。
2. **`DemandTypes.check(String)/check(int)` 委托宽松 of 通道**（未命中返 null，兄弟门面为严格 NPE+消息）——疑似漏写；`demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed` 钉死，禁止顺手修，修复另案。
3. **`ItemTypes.ofAlias("")` 空串触发 `heads[0]` 下标越界**（`StringUtils.split` 剥空段后取首元素）——scheduler 侧同类入口上轮已裁决移除，basics 侧按零行为变更原样保留并钉桩。
4. **`ItemTypes.ofItemId(long)` 截断链恒落 id=0 段**：各级 `/10^k` 商恒 < `ID_TAIL_SIZE`(1e6)，经 `ofModelId` 百万段取整后全为 0——任意 long 输入实际解析到 id=0 的 ItemType（无注册则 null）。五分支用例（9999/1e4/9e11/9e15/9e18）钉死现状；语义修复另案。
5. **`FeatureOpenModes`（mould 包，同形态第 9 个 basics 门面）不在本组任务清单**：零改动；中间层在位，后续并入为纯机械活（登记备查）。
6. **度量口径提示（供组 9 / D9 如实报口径）**：公共签名逐字冻结下门面声明行本身即 M3 冗余载体（token 归一后跨门面同构），本组实测 8 门面 diff 为 +86/−72 且新增中间层 114 行——**净行数不降**，收益为消息模板/查找语义单一事实源与漂移面收敛；M3 注册表岛（基线 366 冗余行）预计仅小幅收窄，9.1 复扫若此项不达标属冻结约束而非实施欠账。

## 勾记

tasks.md 6.1、6.2、6.3 已勾（6.2 含上述「实现形态偏离说明」与「止步 1」两处按任务授权/技术必然的口径落点）。

## 环境备注

首跑终验时 `:tny-game-common-lang:compileJava` 因并行代理在途编辑 `NumberAide.java`（组 4/D8 工作面）报 4 处「找不到符号」；未触碰该文件，等待后复跑即绿。与本组变更无关。测试命令全程串行执行，未用 `--stop`。
