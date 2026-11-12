# Proposal

## Why

现行 CI 基准通道把两类性质不同的基准混在同一选择面上：「开发测试基准」（为特定优化决策取证的算法对比、oracle 对照、设施自检类）与「框架常规基准」（守生产热路径性能的哨兵类）。后果双向都有洞：

1. **回归观察缺口**——`MessageQueueBenchmark`（发送缓存）与 `RespondFutureBenchmark`（RPC future 配对）是框架热路径，却不在任何自动执行面上；框架优化合入后，组件级性能是否受影响无人持续观测。
2. **执行面口径含糊**——「哪些基准该跑、哪些只是开发副产物」只存在于锚集正则的隐式选择里，新基准入列凭口口相传；而评审通道「编译全部、零计时」的契约并未给出「常规基准才需要跑计时」的正式通道。

本变更确立两族分类为契约，并让常规族获得「每次框架优化落地后即观测」的计时执行面（push + 夜间）。

## What Changes

- 确立**二分分类契约**：全部基准 MUST 归属「框架常规基准族」或「开发测试基准族」之一；分类集中声明于构建脚本（单一事实），评审通道对**分类完备性**校验——新基准未归族即判红，杜绝静默漏网。
- 常规族成员界定：报文全管线编解码基线 ∪ 生产装配矩阵臂（`prod_*` 六臂口径不变）∪ 发送缓存成本画像 ∪ RPC future 配对成本画像。设施自检冒烟基准归开发测试族，但作为**执行链存活探针**恒随执行面运行（成本≈1 组合，防静默空跑）。
- **族属分目录管理**：被测域子包内再按族设子包（常规族 / 开发测试族各一枚），基准类物理归属即族属；**基准执行实体之外的可复用支撑件**（算法实现、oracle 组件）不随宿主族走，设共享子包承载（grep 实证：常规族的矩阵装配臂直接复用现居开发族类中的组件，无共享结构则出现常规反向依赖开发的族义倒置）。目录归属与构建脚本清单声明由校验任务**双向对账**，漂移即红。FQCN 迁移窗口实证：gh-pages 曲线尚无历史数据点面（`results/` 仅当日 1 份产物、bench CI 设施昨日落地），搬包的重置代价 ≈0，宜即刻执行。
- 新增**计时执行面**：push（main/5.7.x）+ 夜间定时 + 手动触发时运行常规族（含设施探针），产出结果文件并更新历史曲线——每次框架优化合入后立得数据点。
- 评审通道（PR）**维持零计时**：编译检查覆盖两族全部源码（现有行为），清单枚举校验按族各自判非空。
- 开发测试族在 CI 中永不自动跑计时；开发者经按名子集入口本地按需执行（现有 `-PbenchInclude` 能力，不变）。
- 任务与产物改名：`benchAnchorExport` → `benchRoutineExport`；产物指纹 `bench-<日期>-anchor.json` → `bench-<日期>-routine.json`。历史 `results/*-anchor.json` 为旧口径证据不回改（沿用 README 既有「归档工件按旧路径引用者不回改」约定）。
- 门禁语义**不变**：曲线与阈值告警仍仅信号、人工同窗对比终裁（现有「回归判定人裁决纪律」Requirement 原样保留）。

## Capabilities

### New Capabilities

（无——本变更全部落在既有 capability 内。）

### Modified Capabilities

- `benchmark-harness`：
  - **新增 Requirement**「基准二分分类契约」——两族声明式分类、族属分目录放置、目录与声明双向对账、完备性校验、执行面唯常规族论。
  - **修订 Requirement**「CI 双通道职责边界」——执行通道触发面由「仅夜间」扩展为「push + 夜间 + 手动」，执行选择面由隐式锚集改为常规族（含设施探针）；评审通道零计时职责不变。
  - **修订 Requirement**「结果产物结构化可对比」——产物日期指纹的产出触发面扩展（push 亦产点），命名口径 anchor → routine。

## Impact

- `tny-bench/build.gradle`：ext 列表重组（常规族/开发测试族/设施探针清单改按**族子包正则**声明，替代现 `benchAnchorRun/benchAnchorAlgoArms/benchAnchorClasses` 三清单的隐式口径）、新增分类完备性与「目录归属↔清单声明」双向对账校验任务、`benchAnchorExport` 改名 `benchRoutineExport`。
- `tny-bench/src/jmh/.../bench/net/`：基准类按族移入子包——`net/routine/`（PacketCodec、PipelineCryptoMatrix、MessageQueue、RespondFuture）与 `net/devtest/`（Smoke、CryptoAlgorithm、VerifyAlgorithms、LegacyPaths、AeadTransport）；新增 `net/shared/` 承载跨族支撑件（`AeadRfc7539` 整类迁入、`Crc64Slicing` 自宿主类摘出、`sipHash64` 提为静态工具），相应成员放开子包外访问（模块零发布，不触及公共 API 合同）。已知跨族引用面（grep 实证）：Matrix→`PacketCodecBenchmark.BenchTunnel`（同族共迁消解）、Matrix→上述三件（shared 消解）、AeadTransport→`AeadRfc7539`（shared 后改 import）。曲线 bname 随新 FQCN 起线（gh-pages 现无历史数据点面，重置代价 ≈0；移前核对远端点数留作任务）。
- `.github/workflows/build.yml`：`bench-compile` 维持零计时、枚举校验按族判非空；`bench-nightly` job 改为执行面 job（触发条件加 push，产物/提交信息改 routine 口径）。
- `tny-bench/README.md`：分类表（10 个基准逐类归档，含族子包路径列）、命令面与术语（锚集 → 常规族/执行面）、目录组织说明（被测域 × 族两级子包）。
- **零发布守卫**（根 `build.gradle` 的 `:tny-bench` 装配线断言）不受影响——本变更不触模块定位。
- **无公共 API、无报文/协议格式变更——非 BREAKING**；下游模块与 starter 模块零波及（影响面 grep 实证：`benchAnchor*/jmhList/bench-compile/bench-nightly` 引用仅存在于 `tny-bench/build.gradle`、`.github/workflows/build.yml`、`tny-bench/README.md`）。
- CI 时长预算：执行面一次约 37 参数组合（现有 27 + MessageQueue 6 + RespondFuture 4），按 D3 基线参数估算共享 runner 上 25~40 分钟；仅 push/夜间通道承担，PR 时长不受影响。
