# Proposal

## Why

全仓 CPD 风格克隆扫描（2,330 个 Java 文件，三匹配模式，重叠簇合并去重后）实测：**精确复制克隆 2,653 行、结构克隆 5,609 行**，集中于 42+86 个"克隆岛"（基线见 `baseline/dup-islands.md`）。多副本并行已经产生真实代价：两轮审计中 WrapperObjectMap/ObjectMap 同一转换链修 bug 只修了一侧（`ObjectMap.getFloat` 的 `float.class` 恒抛 CCE 至今仍在）、AbstractFuture/FutureTask 同源状态机两份拷贝、protobuf 五个格式类几乎整文件互抄且**零测试覆盖**。当前 `fix-common-audit-findings` 与 `fix-common-dormant-defects` 已收口、全仓门禁绿，正是做纯保行为去重的窗口。

## What Changes

纯内部重构变更：**零外部行为变更、零公共签名变更、零报文/序列化格式变更**（口径经用户确认为"纯保行为重构"）。收敛判定标准 = 保行为特征测试先行 + 模块测试零回归。

纳入的收敛批次（按预期收益排序）：

- **tny-game-protobuf 格式族**（精确克隆 ~1,400 行，最大单模块热点）：`Protobuf2{Xml,Html,Json,JavaProps}Format` 四类互抄的转义/八进制十六进制工具层、类型 switch 值渲染、字段遍历骨架、解析 Tokenizer 层收敛到包内共享实现；`CouchDB extends Json` 已是仓库内证明可行的先例。重构前先建 golden 测试钉死五类输出文本（含 Html 值渲染不转义的现状差异——如实钉桩不顺手修）。公共静态门面签名逐字保持。
- **common-lang worker 双 future**（~250 行逐字克隆）：`AbstractFuture`/`FutureTask` 的 Sync 状态机段共享到包内实现，两个 public 门面与签名不动（全仓零引用，外部发布面保守处理）。
- **common-lang 转换取值链**（107 行）：`ObjectMap`/`WrapperObjectMap` 的 14 个转换 getter 收敛到共享转换引擎；**两侧现存语义差异（`float.class` vs `Float.class`）原样参数化保留，禁止顺手修复**，该缺陷记入遗留账目。
- **common-lang NumberAide 算术段**（4×20 行同形 + mod）：五运算符表驱动收敛私有 helper，公开方法签名不动（有 `NumberAideTest` 兜底；注意 `sub` 的 null 分支可疑语义按现状钉桩）。
- **common-reflect 两对平行类**（96+83 行逐字克隆）：`CGlib/JSsistPropertyAccessor`、`CGlibUtils/JavassistAccessors` 抽内部共享骨架，public 工厂签名与 protected setter 保留。
- **basics 注册表族**（9 类 × 65-70 行 ≈ 366 行结构克隆）：`Behaviors/Actions/Abilities/DemandParams/ItemTypes/DemandTypes/Features/Moulds` + scheduler `TaskReceiverTypes` 的枚举门面转发段收敛到泛型中间层；**`ClassImporter` 本身不动**（basics/net 有 13 个活跃继承点，首轮误判教训在册），各类常量、别名入口、可见性差异（`Moulds.register` public、`Actions.getAll()` 命名）逐一保留。
- **tny-game-net 测试脚手架**（~260 行）：8 个 session/transport 测试各自内嵌的 `TestTunnel` mock 样板下沉为共享 fixture（同模块已有 `RpcContextFixture` 先例）。纯测试代码，不触产品面。
- **starter 配置属性族**（87×2 + 51×3 行结构克隆）：`SpringBoot{Net,Rpc}BootstrapProperties` 与 starter-data 三 `*StorageAccessorFactoryProperties`/`Async*FactoriesProperties` 提公共访问器父类；按侦察的三条风险点处置（链式 setter 协变覆写留子类、字段初始化器留子类、`@ConditionalOnMissingBean` 注册点不动）。
- **data EntityManager 批处理 default 方法体**（4×~24 行）：`insert/update/save/deleteEntities` 同形循环收敛为单个 `applyEach`，抽象方法签名与接口契约不动。

**显式不动清单**（否决理由见 design.md）：oplog `Consume/ReceiveRecord` 孪生（JSON 键即字段名，提父破坏日志下游解析）、starter-basics `Entry/ListDTO` 十胞胎（ProtoEx 编译期工具 `SimpleFieldOptions` 不上溯继承链，提父有 wire 面风险）、net 异常构造器族（Java 无构造器继承，语言约束性样板）、lifecycle 三阶段类（合并=删 public 类名=BREAKING）、`CapacityObjectStorer` 接口 default 克隆（无法共享 helper，强收破坏实现方）、`EmptyImmutableList/Set`（P10 仅第二次出现且差异超泛型，容忍）、`ItemModel/NamespaceExplorer` 重载阶梯（default 委托的实现方绑定面微妙，暂缓）、tools/bench 与 `ContactService`（收益/风险比不足）。

**门禁与度量**：全仓 `./gradlew test -x :tny-game-namnspace-etcd:test` 零失败；收口用同脚本复扫，M1 精确克隆冗余行较基线（2,653 行）下降 ≥60%、M3 结构克隆较基线（5,609 行）下降 ≥40%，复扫报告入变更目录。

无新增能力、无规格差量——按项目规则声明 `skip_specs: true`。无任何 BREAKING：所有 public 签名、类名、消费者可见的类层级不变（唯一层级变化为 starter properties 新增父类，属纯增量且仅 Spring 装配内可见，design 中单独裁决）。

## Capabilities

### New Capabilities

无（纯内部重构，不引入规格）。

### Modified Capabilities

无（零行为变更，无 Requirement 触及）。本变更 `.openspec.yaml` 已声明 `skip_specs: true`。

## Impact

- **受影响模块（产品代码）**：`tny-game-protobuf`、`tny-game-common-lang`、`tny-game-common-reflect`、`tny-game-basics`、`tny-game-common-scheduler`（仅 TaskReceiverTypes 转发体）、`tny-game-starter-data`、`tny-game-starter-net-netty4`、`tny-game-data`。
- **受影响模块（仅测试代码）**：`tny-game-net`（8 个测试文件改引用共享 fixture）、`tny-game-common-lang`/`-reflect`/`-basics` 新增行为钉桩测试。
- **下游消费面**（grep 全仓实测；codegraph 索引未覆盖本次目标符号，爆炸半径以调用面检索替代并在 design 注明）：
  - protobuf 格式类：全仓唯一外部使用点 `tny-game-oplog/OpLogMapper → Protobuf2JsonFormat.printFiles`；发布 jar 的 `print/merge/printToString` 静态签名不动。
  - `AbstractFuture/FutureTask`：仓内零引用；已发布 public 类，按外部消费保守处理。
  - `ObjectMap`：`tny-game-net` 两处 new（`BaseRelayServeInstance/BaseNetAccessNode`）；`WrapperObjectMap`：`MapAccessors` 工厂。行为零变更即消费面零波及。
  - `JavassistAccessors`：protoex/net/data 跨模块使用；`CGlibUtils` 本模块+测试。
  - 注册表族：经 `GameEnumClassLoader::createSelector(Xxx::register)` 装载入口消费，入口签名不动。
  - starter properties：Spring 绑定面，装配行为不变；对应 starter-* 模块回归。
- **依赖/构建**：无新增依赖；扫描脚本随基线入 `baseline/`（规划证据，不进构建）。
- **时序约束**：在 `fix-common-audit-findings`、`fix-common-dormant-defects` 归档落库后启动——本变更的 `NumberAide`、`WrapperObjectMap/ObjectMap` 文件面与 verify 修复工作流（wf_51271e24 已 6/6 完成）重叠，先归档后去重避免账目打架。
- **记忆账目**：收口时同步更新 `common-modules-audit-2026-09-30`（`ObjectMap.getFloat` float.class 缺陷划入遗留、去重否决清单入册）。
