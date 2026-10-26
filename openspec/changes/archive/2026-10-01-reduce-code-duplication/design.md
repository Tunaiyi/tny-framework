# Design

## Context

动机与批次清单见 proposal.md - Why/What，不重述。此处只沉淀 shaping 约束。

**量化方法**（`baseline/dupscan.py` + `baseline/dupmerge.py`，可复跑）：M1 精确 token 克隆（窗口 80）、M2 字面量归一（+80）、M3 标识符归一的纯结构骨架（窗口 140）；滑动窗口重叠簇经并查集合并为"克隆岛"并按文件区间去重，避免原始簇计数虚高（初版原始簇把 protobuf 一个 5.2k 行模块夸称 79,931 冗余行，岛级去重后真实为 1,408）。基线报告 `baseline/dup-islands.md`。

**代码事实（四路只读侦察结论，重构设计的输入）**：
- protobuf 五格式类无公共父类，`CouchDB extends Json` 是族内已存在的复用先例；唯一外部消费点 `oplog/OpLogMapper.printFiles`；src/test 为空——重构前无任何行为钉桩。
- `AbstractFuture`/`FutureTask` 均为 Doug Lea JDK1.5 变体的同源拷贝，Sync 状态机逐字一致；全仓（含测试）零 `new/extends`；两者是已发布 public 类。
- `ObjectMap/WrapperObjectMap` 转换 getter 链 107 行克隆；已知语义分裂：`ObjectMap:159 getFloat(key,def)` 传 `float.class` 值存在必抛 CCE（Wrapper 侧已修 `Float.class`）——**属行为差异，本变更口径禁止抹平**。
- 注册表 9 类 `extends ClassImporter`（首轮教训：ClassImporter 非死代码，basics/net 13 个活跃继承点）；公共真实现已是 common-lang `EnumeratorHolder`，各类仅剩类型转发门面 + 常量 + 个别扩展点（`ItemTypes` 别名、`TaskReceiverTypes` 静态注册块、`Moulds.register` public、`Actions.getAll()` 异名）。
- reflect 两对逐字克隆：`CGlib/JSsistPropertyAccessor` 仅 invoke 实参写法差（`new Object[]{...}` vs varargs，对 `invoke(Object,Object...)` 语义等价）；`CGlibUtils/JavassistAccessors` 仅实例化目标差。
- net 8 个测试内嵌同构 `TestTunnel`（40-55 行 mock 样板）；同模块已有 `RpcContextFixture` 下沉先例。
- starter properties：`SpringBoot{Net,Rpc}BootstrapProperties` 与 data 三胞胎纯样板，侦察给出三条留存约束（链式 setter 协变覆写、字段初始化器、`@ConditionalOnMissingBean` 注册点）。

**爆炸半径工具说明**：项目规则要求 codegraph impact/callers 前置分析；实测本仓 codegraph 索引未覆盖本变更目标符号（`Protobuf2XmlFormat`、`AbstractFuture` 检索均落空），爆炸半径改以 grep 全仓调用面检索替代（结果已入 proposal.md - Impact），此为工具覆盖限制而非流程偏离。

**时序**：`fix-common-dormant-defects` verify 修复工作流（wf_51271e24）6/6 完成后全仓门禁 BUILD SUCCESSFUL（38s 增量全绿）；`NumberAide`、`ObjectMap` 面在该工作流有未归档改动，本变更必须等两归档落库后动工。

## Goals / Non-Goals

**Goals:**
- 消除 M1 精确克隆 ≥60%（2,653→≤1,060 冗余行）、M3 结构克隆 ≥40%（5,609→≤3,365），以同一脚本复扫度量。
- 每个被收敛族在收敛**前**已有行为钉桩测试（golden/契约测试），把"保行为"从口头承诺变成可执行断言（P13）。
- 全部 public 签名、类名、报文/序列化格式零变更。
- 为"同一 bug 修一侧漏一侧"的两处实证（ObjectMap 转换链、双 future）建立单一事实源。

**Non-Goals:**
- 不修任何缺陷（含 `ObjectMap.getFloat` 的 CCE、`NumberAide.sub` null 分支可疑语义、protobuf Html 不转义差异）——发现即钉桩入遗留账目，修复另立变更。
- 不做 proposal 不动清单的 8 族收敛；不为凑指标做伤筋动骨的层级重构。
- 不引入新依赖/构建插件（扫描脚本只作规划期与收口度量工具，不进构建）。
- 不改 `ClassImporter`、`EnumeratorHolder` 本身。

## Decisions

### D1 特征测试先行：golden 钉桩是收敛的前置门票（P13）
每个收敛族第一步是"重构前通过、重构后必须仍绿"的行为钉桩：protobuf 输出文本 golden（五类 × print/printToString/merge 往返 + 转义特殊值 + 现状差异如实钉）、双 future 状态机语义（cancel/await/done/reset 可达路径）、14 个转换 getter × 两侧实现（含 `float.class` CCE 现状钉桩）、注册表 of/check/option 契约矩阵。
**否决备选**：(a) 依赖编译通过+既有模块测试——对零测试族（protobuf、oplog、starter-basics）等于无网兜底，P13 判据不满足；(b) 先收敛后补测试——失去"保行为"证明力。

### D2 protobuf：包内共享件 + 门面委托，不新建公共抽象层（P8①、P10、P11、M1 先例）
转义/八进制十六进制/token 工具段提包私有 `final` 工具类；`printFieldValue` 类型 switch 以"装饰策略参数"（引号器+转义器函数）收敛为单实现，四类门面各自传入本类既有装饰组合；`print(Message,gen)` 字段遍历骨架按先例 `AbstractExpr`（模板方法骨架是 P8 认可的两类继承用途之一）收敛，Xml/Html/Json/JavaProps 保留类名与全部 public 静态签名作薄委托；`merge` 解析层 Tokenizer 共享、括号/正则差异参数化。**保持 Html 不转义、JavaProps ENUM 不加引号等实测差异**——共享实现以装饰参数表达差异，不为"统一"抹平。族内 `CouchDB extends Json` 先例不动。
**否决备选**：(a) 抽 public `ProtobufFormat<T>` 接口+实现族——P10 判据：说不出服务哪些仓外场景；且发布面新增接口=长期合同（P11），收益全是仓内的，抽象放包内即够；(b) 生成代码统一五格式——引入构建期代码生成依赖，违反 Non-Goals；(c) 顺手修 Html 转义——行为变更（游戏运营侧可能依赖现状输出），入遗留。

### D3 双 future：Sync 状态机下沉包内共享实现，门面签名冻结（P4、P11）
`AbstractFuture`/`FutureTask` 各留现 public 骨架（构造器、`implements` 关系、方法签名逐字不动），内部把逐字相同的 Sync 状态机段（`tryAcquireShared/tryReleaseShared/innerGet/innerSet/innerCancel/reset` 等）收敛到包私有共享实现；`FutureTask` 独有的 callable/RUNNING/innerRun 留本类。零仓内引用+已发布 = 只做"单一事实源"，不做删除或 deprecate（那是 API 变更，越出本变更口径）。
**否决备选**：(a) 删一保一——public 面删除属破坏性（P11 三问全否），另案；(b) 让 `FutureTask extends AbstractFuture`——改消费者可见类层级 + `RunnableFuture` 语义耦合进 `Future` 基类，LSP 面（P6）收益为负。

### D4 转换链收敛：共享引擎 + 参数化承载两侧差异，CCE 缺陷"原样搬家"（P4、规则"禁止顺带修改"）
14 个转换 getter 的取值链收敛到共享私有引擎；`float.class` vs `Float.class` 差异作为引擎入参由两侧门面各自传入——ObjectMap 的缺陷行为被特征测试钉住并原样保留。缺陷记入 `common-modules-audit-2026-09-30` 遗留账目（升级措辞：从"两份实现语义分裂"变为"单一实现上的一处显式参数"），修复另立变更。
**否决备选**：顺手统一为 `Float.class`——外部可观察行为变更（CCE→正常返回），违反本变更"纯保行为"口径与项目 apply 规则；这是用户已确认口径的直接推论。

### D5 注册表族：泛型中间层，`ClassImporter` 与装载入口零触碰（P8①、P5、M1 先例）
新增 basics 包内泛型中间层（形态照抄 `AbstractExpr` 先例：`Abstract<Facade>Importer<T>` 承载 `EnumeratorHolder` 转发六件套 + 私有构造 + register 骨架），9 类各留常量、`ofAlias` 等自有扩展与**各自现有可见性/异名**（`Moulds.register` 保持 public 覆写、`Actions.getAll()` 保留为薄委托别名）；`TaskReceiverTypes` 的 static 注册块与非 final 修饰留 scheduler 本类不并入。`GameEnumClassLoader::createSelector(Xxx::register)` 消费形态不动。
**否决备选**：(a) 直接改 `ClassImporter` 加泛型转发——13 个活跃继承点（basics/net），爆炸半径远超本变更必要面，且首轮"ClassImporter 死代码误判"教训在册；(b) 表驱动注解处理器生成注册——引入构建期机制，收益不成比例（P13）。

### D6 net 测试脚手架：共享 TestTunnel 落 test 源集，照 `RpcContextFixture` 先例（P10 第三次、纯测试面）
`com.tny.game.net` test 包内新增共享 `TestTunnel` fixture（含 resetSession/onOpen/onClose/doDisconnect/isActive/双地址桩），8 个测试改为引用；各测试若有个性化覆写（RacingTunnel 的竞态钩子）以子类/参数保留。**不引入 java-test-fixtures 插件**——先例在同模块 test 源集内，跨模块复用需求未出现（P10：一次抽象服务单一场景=过度）。
**否决备选**：维持内嵌——8 处 ≥3 次法则已满，且 mock 语义分叉已是两轮修复中测试缺陷的成因之一。

### D7 starter properties：公共访问器提父，协变覆写留子类，绑定面回归兜底（P8、P11 例外申明）
`SpringBoot{Net,Rpc}BootstrapProperties`、data 三 `*Properties` 各提同包 public abstract 父承载访问器样板；**子类保留**：链式 setter 的协变覆写（返回子类类型，下游 `new XxxProperties().setServer(s)` 不断链）、字段初始化器与懒造默认值、类上全部 Spring 注解。P11 例外，理由：新增消费者可见父类是纯增量层级变化（ instanceof 只增不减、无方法签名/属性键变更），Spring 绑定沿层级链可查（侦察确认），以 starter 模块编译+装配回归钉住。
**否决备选**：(a) 放弃收敛——properties 样板是 starter-* 横切重复的可见源（侦察 B 路实测 5 类同构）；(b) 提为 package-private 父——public 子类继承 package-private 父的 public 方法，Spring 反射绑定在非模块路径下的可访问性风险，收益不变风险新增。

### D8 NumberAide 表驱动 + EntityManager default 体内收敛（P5、P11 零触）
`NumberAide` add/sub/multiply/divide/mod 的 null 处理+BigDecimal/BigInteger 分支+7 路 `isAssignableFrom` 基本类型分派，收敛为单一私有"分派器 + 运算符 lambda"；`sub` 的 `one==null` 返回 other 可疑语义按现状进分派器（先补等价性钉桩再动）。公开五方法签名不动。`EntityManager` 四个批量 default 方法体收敛到单个 `default int applyEach(Collection<E>, ToIntFunction<E>)`，抽象方法与 4 个 default 签名不动（实现类覆写面不变）。
**否决备选**：`NumberAide` 维持——活跃消费面（MVEL/GraalJS 暴露 + basics），一份五段拷贝正是下轮审计的雷；放弃等于把最大已知活跃热点让渡。

### D9 复扫度量口径固定（P13）
收口验证用 `baseline/dupscan.py` + `baseline/dupmerge.py` 原样复跑（同窗口、同最小区块 20 行、同排除目录），只比 M1/M3 岛级冗余行合计；不做基线重算。风险：结构重构会把部分"岛"推下窗口阈值——度量目标是趋势而非精确归因，报告入变更目录 `baseline/dup-after.md` 供人工抽查。

## Risks / Trade-offs

- **[零测试族 golden 钉不全] → 缓解**：protobuf golden 用现实现输出快照（跑一次存期望文本），覆盖每类型字段×每格式、未知字段、扩展、repeated 下标；Html 不转义等差异专门单列用例；golden 先行合入后，任何后续行为漂移会被同批测试暴露。
- **[回归破坏] → 缓解**：每族独立提交、族级模块测试绿才进下一族；收口全仓 `./gradlew test`（etcd 排除沿用）；回滚策略=按族 revert，克隆共享件为包内实现 revert 无跨模块涟漪（D7 的父类是唯一跨类层级新增，revert 即还原）。
- **[并发同文件面] → 缓解**：wf_51271e24 修复的 `NumberAide` 高精度路径等改动必须先随 dormant-defects 归档落库（Context 时序条款）；开工前 `git status` 复核两归档目录已移入 `archive/`。
- **[度量口径漂移/假阴性] → 缓解**：M2 与 M1 基线同值（字面量归一未增岛）说明本仓克隆多为逐字拷贝，D9 阈值稳定；复扫若某族岛数不减但单岛变小，人工核对可接受。
- **[Trade-off] 不动清单 8 族约 1,500+ 行结构冗余留在原地**——换取消除"为收敛而破坏序列化/绑定语义"的风险；否决理由逐条在册（proposal），下轮若要动需按 BREAKING 立约。
- **[Trade-off] `AbstractFuture/FutureTask` 仓内零引用仍保留双门面**——发布合同（P11）优先于仓内洁癖；删除另案。

## Migration Plan

1. 前置：`fix-common-audit-findings`、`fix-common-dormant-defects` 归档落库 + 全仓门禁绿（已达成部分：wf_51271e24 完成后 BUILD SUCCESSFUL 38s）。
2. 批次顺序（文件面互斥，可按组回滚）：protobuf（D1→D2）→ lang 三族（D3/D4/D8-NumberAide，同模块串行）→ reflect（D5 前置的 7.1 型钉桩）→ basics/scheduler 注册表 → net 测试脚手架 → starter properties → data EntityManager → 复扫收口 + 记忆账目。
3. 部署形态：库仓库分支 5.7.x 常规合并；无数据迁移、无发布顺序约束（公共签名零变更意味着 jar 增量兼容，无需下游同步升级）。
4. 回滚：逐族 git revert；golden/钉桩测试独立于实现收敛提交，revert 实现后保留测试即成下轮基线。

## Compatibility Impact

公共面净变化矩阵（发布 jar 视角，全部为"零删除/零改签"）：

| 面 | 变化 | 下游可见性 | 结论 |
|---|---|---|---|
| protobuf 五类 `print/merge/printToString/printField(s)` | 签名逐字不动，实现体内挪 | 唯一外部点 oplog.printFiles | 无感 |
| `AbstractFuture/FutureTask` 全部 public 成员 | 签名/语义不动 | 仓内零引用；外部未知 | 合同冻结 |
| `ObjectMap`（含 `getFloat` CCE 现状）/`WrapperObjectMap` | 语义逐路径钉桩保留 | net 两处 new；MapAccessors | 无感（缺陷原样） |
| `NumberAide` 五算术方法 | 签名不动 | MVEL/GraalJS/basics 活跃 | 无感 |
| reflect 两对 + 注册表 9 类 + `ClassImporter/EnumeratorHolder` | public 成员冻结；中间层落包内 | protoex/net/data/scheduler 消费面 | 无感 |
| starter `*Properties` 5 类 | **新增 public abstract 父类**（层级纯增量）；属性键/方法签名/链式 setter 返回类型不变 | Spring 装配面 | P11 例外（D7），starter 回归钉 |
| 报文/JSON/ProtoEx 序列化格式 | 零触碰（DTO/oplog 已列不动清单） | — | 不适用 |

升级要求：下游 **无需**任何代码或配置改动即可升级本批构件；无 BREAKING 标注项，release-note 仅记"内部去重 + ObjectMap 缺陷账目更新"。

## Open Questions

- `JavassistAccessors` 与 `CGlibUtils` 共享骨架落哪个包（reflect 本包 or 新建包内子包）——不影响批次划分与签名，实施期按就近原则定。
- protobuf `merge` 解析层收敛深度（仅工具共享 or 连 Generator 状态机同并）——以 golden 先行、实施期按"能过即收"渐进，不预先承诺极限。
