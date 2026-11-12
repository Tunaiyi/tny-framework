# Design

## Context

动机见 proposal.md - Why；行为契约见 specs/benchmark-harness/spec.md（差量）。本文件只记录现状技术约束与实现取向。

现状（均为仓库内实证）：

- 基准选择面单一事实位于 `tny-bench/build.gradle` ext 三清单：`benchAnchorRun`（运行正则）、`benchAnchorAlgoArms`（`-p algo` 参数域覆写六臂——实测 include 正则不匹配参数串，`-p` 是唯一可行的臂筛选通道）、`benchAnchorClasses`（清单面基名正则）。锚集 = Smoke ∪ PacketCodec ∪ Matrix `prod_*` 臂，共 27 参数组合。
- CI 现状：`bench-compile`（零计时，`jmhCompileGeneratedClasses` + `jmhList`，`jmhListVerify` 对空列表显式判红）跑全触发；`bench-nightly`（仅 schedule/dispatch）跑 `benchAnchorExport` → 产物 `results/bench-<yyyymmdd>-anchor.json` → gh-pages 曲线。
- 两族分类的既有事实形态：README「新基准是否入锚集，按其是否承担回归哨兵职责决定」——已是二分口径，但**无机械校验**：新基准不归任何列既不报错也不被观测（哨兵覆盖静默漏网）。
- 爆炸半径（族属进目录后，改动面扩至 tny-bench 的 Java **声明面**——移包/可见性/支撑件摘出，被测逻辑与调用图不变；Gradle DSL/CI job 标识符仍不在 codegraph 索引——`benchAnchorRun` 检索仅得无关 Java `run` 命中，故以全仓文本引用面为半径证据）：`benchAnchor*/jmhList/bench-compile/bench-nightly` 引用仅存在于 `tny-bench/build.gradle`、`.github/workflows/build.yml`、`tny-bench/README.md` 三处；根 `build.gradle` 的 `:tny-bench` 零发布守卫只认**模块坐标**不认任务名，不受改名波及；`gradle/integration-test.gradle` 命中的是 `includeDocker`，与 bench 无关。
- 目录分置的仓库实证（本轮 grep）：**跨族引用面**——矩阵族（常规）同包引用 `PacketCodecBenchmark.BenchTunnel`（同族嵌套类）、`AeadRfc7539`（package-private 类，构造器与 `selfTest/setNonce/seal/tag/auth/decryptInPlace` 全为包级成员）、`CryptoAlgorithmMicroBenchmark.Crc64Slicing`（嵌套类）与 `CRC64_INITIAL`（静态字段）、`VerifyAlgorithmsMicroBenchmark.sipHash64`（静态方法）；AeadTransport（开发族）引用 `AeadRfc7539`。**曲线数据面**——`results/` 仅当日产物 `bench-20261001-anchor.json`，本机无 gh-pages 分支、远端 ls-remote 不可见；bench CI 设施昨日才落地（git log 实证）——bname 随搬包的历史包袱 ≈0。

## Goals / Non-Goals

**Goals:**
- 两族分类成为**机械可校验**的单一事实：族属清单集中声明，完备性校验任务把「未归族基准」变为评审通道红灯。
- 执行通道（push + 夜间 + dispatch）计时运行常规族 ∪ 设施探针，曲线按合入累积数据点。
- 族属同时由**目录位置（可见性）与集中清单（CI 派生源）**双承载，双向对账防漂移；曲线 bname 随族子包新 FQCN 立主键（现数据面为空，迁移窗口成本 ≈0）。

**Non-Goals:**
- 不改 D3 基线参数（fork/迭代/窗口）——时长增长只来自选择面扩大。
- 不改门禁语义（告警仍仅信号，「回归判定人裁决纪律」Requirement 原样，不在本差量内）。
- 不改动任何基准的被测逻辑、不引入新基准类；源码调整仅限目录分置所需的 package 声明、成员可见性与支撑件摘出；不做基准数字的本仓自动对比断言。
- 不触碰根构建零发布守卫与 `includeDocker` 集成测试通道。

## Decisions

### D1 族属双承载：目录分置族子包 + 清单集中派生 CI 面，双向对账（P13、P5）

基准类在被测域子包下按族分置：`net/routine/`（PacketCodec、PipelineCryptoMatrix、MessageQueue、RespondFuture）与 `net/devtest/`（Smoke、CryptoAlgorithm、VerifyAlgorithms、LegacyPaths、AeadTransport）——**目录位置本身可辨族属**（入库可发现性）。`build.gradle` ext 清单仍是 CI 选择面的**唯一派生源**：常规族/探针清单正则改按族子包前缀匹配（较逐类名枚举更短且类改名免疫）；Matrix 的 `-p algo` 六臂筛选保留为清单内参数域覆写——**臂属选择面问题，不属族属问题**，Matrix 整类依其承担生产装配臂职责归常规族。校验任务升级为两段对账：完备性（全量枚举 `-PbenchAll -l` 与三清单类粒度并集，未归族判红列名）+ **目录一致性**（类之包前缀 vs 声明族属，不一致判红），落实 spec 差量的双向对账条款。

- **验证回答（P13）**：完备性红灯由 `jmhListVerify` 同款空对账断言证明；目录漂移由包前缀对账分支证明（搬家不改清单、或改清单不搬家，当场红）；「族属调整两面随动」= 移包 + 清单一处改、另一处校验逼改。
- **被否决备选**：① JMH `@Group` 注解在基准类内声明族属——族属是 CI 观测策略而非被测物属性，注解把策略散落进基准源码，改策略要动代码、且形成双事实（违 P5）；② 纯目录约定、无清单（前缀即族）——约定不可验证，且选择面需表达 Matrix `-p` 参数臂与探针例外（开发族成员恒随执行面），目录单独派生不出这些声明结构；③ **仅清单平铺目录（本提案初态，上一轮用户曾裁决维持）**——目录不承载族属，入库可发现性依赖读构建脚本；当时否决目录方案唯一实质障碍「搬 FQCN 断曲线主键」经实证不存在（`results/` 仅当日 1 产物、无 gh-pages 数据点面），窗口随夜间历史积累即刻失效；用户以目录管理优先，本轮裁决推翻初态。

### D2 设施探针归开发测试族、恒随执行面（P13、M3）

`SmokeBenchmark` 类头自述「验证执行链」，属设施自检 → 族属列在开发测试族、物理同置 `net/devtest/`（目录与声明一致，进 D1 对账）；执行面正则恒并一枚设施探针项（类正则指向新包 FQCN，成本 ≈1 组合），产物/曲线照常携带其条目——noop 基线仅作执行链存活哨兵，数据解读时不构成框架性能断言。

- **被否决备选**：① 探针计入常规族——族语义被污染为「跑过的都算常规」，与新增设施类基准的归档判断纠缠；② 探针不跑、仅编译——失去「执行通道静默空转」的第二道哨兵（产物缺失可发现只兜结果文件，不兜「跑了一堆 0 分假象」）。

### D3 执行通道触发面 = push(main/5.7.x) ∪ schedule ∪ dispatch；PR 维持零计时（P12）

`bench-nightly` job 更名 `bench-routine`，`if:` 由 `schedule || workflow_dispatch` 改为 `push || schedule || workflow_dispatch`（等价排除 PR）；夜间 cron 不动（UTC18:37 错峰纪律沿用）。评审通道 `bench-compile` 触发面与零计时职责**原样保留**，仅内部升级为按族枚举校验（常规族非空 = 现 `jmhList` 语义随 D1 清单切换；开发族非空 + 完备性对账为新增断言）。

- **被否决备选**：① PR 也跑计时——直接违反既有「评审通道 MUST NOT 产出或上报任何计时数据」红线（P12：要动契约先改规格；本变更刻意保留该条），且共享 runner 噪声下 PR 级数字无同窗解释力、每 commit 30+ 分钟成本；② 维持仅夜间——回到「优化合入后平均等一天才见数」的现状缺口，正是本变更动机。

### D4 改名矩阵：anchor → routine 口径迁移（M3 命名即文档）

ext `benchAnchorRun/benchAnchorAlgoArms/benchAnchorClasses` → `benchRoutine*` 三清单 + `benchDevSuiteClasses` + `benchFacilityProbe`（族清单改按族子包前缀正则，见 D1）；任务 `benchAnchorExport` → `benchRoutineExport`；产物 `results/bench-<yyyymmdd>-anchor.json` → `bench-<yyyymmdd>-routine.json`；git 提交信息 `nightly anchor results` → `routine benchmark results [skip ci]`（`[skip ci]` 防自触发纪律保留）。

- **兼容性**：曲线主键 bname **含 FQCN**——搬族子包使 PacketCodec/Matrix/Smoke 的 bname 变化；数据面实证为空（`results/` 仅当日 1 份、本机无 gh-pages、远端不可见），迁移窗口内新主键即刻立起；若移前核对（tasks 组 1）发现远端确有点位，由人裁决「接受旧序列断层」或「缓行搬包」。`results/` 既有 `*-anchor.json` 为旧口径入库证据，不回改、README 引用约定沿用「归档工件按旧路径引用者不回改」。
- **被否决备选**：保留 anchor 旧名——「锚集」语义是固定选择面，改名后新口径（常规族 ∪ 探针、按合入触发）与旧词诱导的直觉（夜间一次性快照）不符，命名误导即文档负债。

### D5 组件族入列不做参数瘦身（P13 预算实证替代直觉裁剪）

`MessageQueueBenchmark`（3 方法 × 2 size 参）与 `RespondFutureBenchmark`（2 方法 × 2 在册规模参）全参数组合入执行面，不预裁剪。理由：参数域均为生产可观测形态（禁用/启用、1k/10k 在册），裁剪决定应来自曲线噪声实证而非拍脑袋。执行面总组合 27 → 37，预算重估 CI 25~40 min，首次 dispatch 实测后把真实时长回填 README（验证任务）。

- **被否决备选**：仿 Matrix 只选子臂——组件基准无「实验臂混居」问题（Matrix 的臂筛选是被测类自身含探索臂的历史包袱），无实证即裁剪会永久丢失 10k 规模档的退化信号。
- **已知污染源**：`RespondFutureMonitor` static 5s 全局定时器随行——沿用 README 既有「数据解读时标注」纪律，不在本变更做代码处置（Non-Goal）。

### D6 跨族支撑件摘出 shared 子包，不随宿主基准族走（P1、P5）

矩阵族对开发族类成员的复用（Context 实证清单）决定了族子包之外必须有第三结构：新建 `net/shared/` 承载**基准执行实体之外的可复用支撑件**——`AeadRfc7539` 整类迁入（类与跨包所需成员由包级放宽为 public）、`Crc64Slicing` 自宿主类摘出为顶层类、`sipHash64` 提为静态工具类方法；原宿主基准与 Matrix 一律改为 import。对账校验只面向 `@Benchmark` 宿主类（支撑件无注册基准、天然不入 `-l` 枚举，无需豁免逻辑）。放宽可见性无公共 API 风险：`:tny-bench` 零发布（根构建守卫在案），P11 合同三问不适用。

- **被否决备选**：① 支撑件随宿主基准归 `devtest/`、routine 跨包 import——常规族哨兵反向依赖「开发测试」目录，族义倒置（读目录即误判依赖方向）；② 随使用方归 `routine/`——AeadTransport（开发族）反向依赖 routine，同一倒置；③ 复制进两个子包——双事实漂移，违单一事实纪律；④ 维持包级可见、Matrix 与 AeadTransport 全搬 routine——把取证类塞进哨兵族以回避可见性问题，混淆分类本身。

## Risks / Trade-offs

- [共享 runner 噪声 × 触发面扩大 → 告警评论变频繁] → 门禁语义不动（`fail-on-alert: false`、人裁决终裁），噪声抖动在同窗复测纪律下被过滤；阈值调参留作运维观察项（Open Questions）。
- [同日多次 push + 产物同日期指纹 → 当日仅末次运行入库] → 曲线端逐次累积不受影响，入库产物定位是「当日快照供同窗复测引用」，语义可接受；不加序号指纹（复杂化引用约定，收益低）。
- [执行面 job 与 e2e 同窗排队挤占并发] → 执行面不设 `wait` 依赖、独立 job 失败不阻断其他 job；push 频率低（合入节律），接受。
- [完备性对账依赖 `-PbenchAll` 全量枚举，未来基准量增长线性拉长评审通道] → `-l` 仅枚举不计时，成本秒级，不构成风险。
- [远端 gh-pages 可能存在本机不可见的既有数据点 → 搬包断 bname 序列] → tasks 组 1 设「移前核对远端点位」任务；确有点位则人工裁决（接受断层／缓行搬包／回退仅清单方案）。
- [支撑件摘出误改算法实现，使既往取证基准失效] → 摘出仅限声明面（嵌套→顶层、修饰符放宽、import 调整），算法体零 diff 为评审红线；`-PbenchInclude` 对开发族的重执行能力摘出前后不变，数字对基线。
- [移包使 codegraph 已索引的类位置过期，影响后续影响分析] → 移包为声明面变更、调用图不变；实施后执行 workspace 增量 reindex。

## Migration Plan

1. 本差量规格先行落地（P12）。
2. 核对远端 gh-pages 曲线点位数（风险条款前置；有点位则人工裁决后再继续）。
3. `tny-bench/src`：支撑件摘出入 `net/shared/`（AeadRfc7539 整类 + Crc64Slicing + sipHash64，访问面放宽）→ 基准类按族移入 `net/routine/`、`net/devtest/`（package 声明 + import 修正，同 commit 原子完成）。
4. `tny-bench/build.gradle`：族清单/参数域声明切族子包正则（D1）→ 两段对账校验任务 → `benchAnchorExport → benchRoutineExport` 与产物指纹（D4）。
5. `build.yml`：`bench-compile` 命令升级 + `bench-nightly → bench-routine` 触发面扩展（D3/D4）。
6. `tny-bench/README.md`：分类表 + 目录结构说明 + 命令面与两类产物口径。
7. 首验与回填：`workflow_dispatch` 跑 `bench-routine` 全链（产物落盘 → 曲线新主键起线 → 提交回写）→ 首个自然 push 观察 → 实测时长回填；codegraph 增量 reindex。
8. 回滚策略：单 revert 即回 anchor 旧口径（旧 FQCN 一并复原）；曲线 bname 维度双向可续，无数据迁移。

## Open Questions

- 常规族数据点变密后，`alert-threshold` 150% 是否需收紧（如 120%）——待 push 通道积累 ≥2 周曲线噪声样本后运维调参，不影响本差量与任务分解。

## 爆炸半径摘要（规则要求）

| 引用面 | 文件 | 处置 |
|---|---|---|
| 10 基准类 + `AeadRfc7539` | `tny-bench/src/jmh/.../bench/net/` | 族移包 `routine/`+`devtest/`，支撑件摘出 `shared/`；调用面不变，仅 package/import/可见性（D1/D6） |
| Matrix→BenchTunnel/AeadRfc7539/Crc64Slicing/sipHash64 | 同包引用（grep 实证 L73/214/242/289/302/568/593/603） | 第一件同族共迁消解，后三件经 shared 消解（D6） |
| `benchAnchor*` ext 三清单 | `tny-bench/build.gradle` | 切族子包正则 + 改名 + 扩容（D1/D4） |
| `benchAnchorExport` / 结果路径 | `tny-bench/build.gradle`、`build.yml` L100/L121 | 改名同步 |
| `jmhList/jmhCompileGeneratedClasses` | `build.yml` L85 | 命令切换为按族校验（D3） |
| `bench-nightly` job 名与 `if:` | `build.yml` L87-89 | 更名 + 触发面扩展 |
| `:tny-bench` 零发布守卫 | 根 `build.gradle` L22-33 | 不受影响（只认模块坐标） |
| `*-anchor.json` 历史产物 | `tny-bench/results/`（入库区） | 不回改（D4 兼容性） |
