# tny-bench —— 全仓基准模块（benchmark-harness）

## 定位

- **全仓基准执行设施**（不限于网络域）：基准类按被测域放 `com.tny.game.bench.<domain>`，当前域：`net`。
- **刻意不叫 `tny-game-*`**：不落入根构建的模块装配线（发布/装配由该命名触发），本模块零发布；
  根 `build.gradle` 另有配置期守卫——任何 `tny-game-*` 模块声明对 `:tny-bench` 的依赖将构建失败并指明违例模块。
- 产出的是**基线数据**（`results/bench-<日期>-routine.json` 与 `baseline-*.md`），供优化变更引用作前后对比锚点——优化本身不在这里做。

## 两族分类与目录组织（split-bench-suites）

被测域子包之下**按族分置子目录**，目录位置本身即族属；CI 两面（评审枚举 / 执行选择）只从
`build.gradle` ext 的族清单（族子包正则）派生，`jmhSuiteVerify` 对「目录归属 ↔ 清单声明」双向对账，
漂移或未归族即评审通道判红。

```
com.tny.game.bench.net/
├── routine/    框架常规基准族——生产路径性能哨兵，CI 执行通道（push+夜间）计时运行
├── devtest/    开发测试基准族——决策取证/探索/设施自检，CI 永不自动计时，本地按名跑
└── shared/     跨族可复用支撑件（非基准执行实体，免于族分置；算法体零 diff 纪律）
```

| 基准类 | 族 | 归族理由 |
|---|---|---|
| `PacketCodecBenchmark` | routine | 报文全管线编解码吞吐基线（核心热路径哨兵） |
| `PipelineCryptoMatrixBenchmark` | routine | 生产装配矩阵；实验臂仅参数域取值，臂属选择面问题不属族属问题，执行面经 `-p algo` 选 `prod_*` 六臂 |
| `MessageQueueBenchmark` | routine | 发送缓存成本画像（组件级热路径哨兵） |
| `RespondFutureBenchmark` | routine | RPC future 配对成本画像（已知污染源见「基线纪律」，数据解读时标注） |
| `SmokeBenchmark` | devtest | 设施自检——但作为**执行链存活探针恒随执行通道运行**（成本 ≈1 组合，其条目不构成框架性能断言对象） |
| `CryptoAlgorithmMicroBenchmark` | devtest | 零 copy 与 verify 税课题的算法侧取证（`Crc64Slicing` 已摘出 shared） |
| `VerifyAlgorithmsMicroBenchmark` | devtest | 算法族可选项探索（`sipHash64` 已摘出 shared） |
| `LegacyPathsMicroBenchmark` | devtest | legacy vs 生产路径对照取证 |
| `AeadTransportMicroBenchmark` | devtest | Noise 传输持久态形态对照取证 |
| `AeadRfc7539` / `Crc64Slicing` / `SipHash64` | shared | 被跨族复用（常规族矩阵臂 ↔ 开发族宿主同源实现），摘出/整类迁入仅动声明面 |

**新增基准**：放进族子包即完成归族；放错或放裸（域根）都会被 `jmhSuiteVerify` 判红点名。
族属判断口径：承担回归哨兵职责（生产路径、需持续观测）→ `routine/`；为某次决策取证或探索 → `devtest/`。

## 运行（me.champeau.jmh 0.7.3 接线，基线参数声明于 `build.gradle`）

```bash
# 列常规族清单（CI 评审通道同款；族空匹配显式判红）
./gradlew :tny-bench:jmhList

# 列全量基准清单
./gradlew :tny-bench:jmhList -PbenchAll

# 族属完备性 × 目录双向对账
./gradlew :tny-bench:jmhSuiteVerify

# 缺省运行 = 常规族（含设施探针）+ 仓库声明的基线参数（可复现）
./gradlew :tny-bench:jmh

# 按名子集（单正则语义，多目标用 |；开发族取证也走这里）
./gradlew :tny-bench:jmh -PbenchInclude='PacketCodec|MessageQueue|RespondFuture'
./gradlew :tny-bench:jmh -PbenchInclude='.*bench\.net\.devtest\.CryptoAlgorithm.*'

# 全量矩阵（大预算，人工择窗执行）
./gradlew :tny-bench:jmh -PbenchAll

# 常规族落盘：跑完后复制为 results/bench-<yyyymmdd>-routine.json（CI 执行通道同款）
./gradlew :tny-bench:benchRoutineExport

# 分配画像（P3 裁决数据源）：每操作真实分配字节（JIT 逃逸分析消化后的净值）
# 注：profilers 不入 jmh 任务输入缓存——开关 -PbenchGc 后若结果未变，加 --rerun-tasks。
./gradlew :tny-bench:jmh -PbenchInclude='PacketCodec|MessageQueue|RespondFuture' -PbenchGc
```

## 缺省基线参数（D3 入库，`jmh{}` 块）

| 项 | 值 |
|---|---|
| JDK | 21（toolchain 引用根 `javaVersion`；**Gradle daemon 须 ≤21**，见根 README 构建环境注记） |
| fork | 2（D3：-f ≥ 2） |
| warmup / measurement | 5 × 500ms / 10 × 1s |
| 结果格式 | JSON（`build/reports/bench/bench-result.json`） |
| 缺省选择面 | 常规族 ∪ 设施探针（见上），非全量 |

## 执行面（CI 执行通道的固定选择面）

- 常规族 4 类 ∪ 设施探针 Smoke，经 JMH `-p algo=<六臂>` **参数域覆写**选 `prod_*` 臂
  （实测：include 正则不匹配参数串，`:prod_` 式过滤 0 选中；`-p` 的 JMH 1.37 语义是覆写值域而非过滤，
  对未声明该字段的基准无排除效应）。
- 定义在 `build.gradle` 的 `benchRoutineClasses`/`benchFacilityProbe`/`benchRoutineAlgoArms`
  （单一事实；**族子包正则**——目录与声明由 `jmhSuiteVerify` 对账）。
- 规模：**37 参数组合**（6 臂 × 2 键形 × 2 尺寸 = 24 + PacketCodec 2 + MessageQueue 6 + RespondFuture 4
  + Smoke 探针 1）。**CI 实测（2026-10-01 首跑，commit 43a5e966）**：push→回写端到端 ≈18 分钟
  （含排队/checkout/编译，D3 计时本体占其中约三分之二）——快于预估区间 25~40 分钟，夜间/合入通道时长可接受。
- 触发：push（main/5.7.x）+ 夜间定时 + 手动（`build.yml` 的 `bench-routine` job）；PR 不触发计时。

## 产物与引用约定（spec：结果产物结构化可对比）

- 新产物一律入 `results/bench-<yyyymmdd>-routine.json`，JMH 自述完整参数/JVM/OS 环境头；同日期覆盖
  （同日多次执行以曲线端逐次累积为准，入库产物是当日快照）。
- **变更工件引用基准数字时 MUST 写明结果文件路径（含日期）与参数指纹**，例：
  `数据源：results/bench-20261001-routine.json（-f2 -wi5 -i10 -w500ms -r1s，JDK21/aarch64）`。
- 曲线告警（CI `bench-routine` job，github-action-benchmark，tool=jmh）仅作信号；**回归判定由人按"同窗对比"终裁**——
  夜间 runner 与本地机器不同窗，跨机数字只能提示方向，不能定案。
- 曲线 bname 主键含 FQCN：族分置（routine/devtest）自本变更起为新主键；此前无历史点位（迁移窗口实证）。
- 旧口径产物不回改：`results/bench-20261001-anchor.json` 与模块根散放的 `*-2026-09-*.json` /
  `pipeline-matrix-*.json` / `crypto-bench-2026-09-30.md` 均为**历史口径证据**，归档工件按旧路径引用者不回改。

## 基线纪律（D3，保留条款）

- 数据文件头记录 JDK/OS/CPU 与完整参数（JSON 产物已机械携带；md 手工记录仍需自带）。
- `-f ≥ 2`；对比锚点只在同参数下成立。
- **R1 警示**：微基准对"纯分配类"候选给出的数字是上界（JIT 逃逸分析在真实调用链可能标量替换）——据此的优化决策需真实管线场景复测。
- 已知污染源：`RespondFutureMonitor` 的 static 5s 全局定时器在 respond 基准期间后台运行（数据解读时标注）。
- shared 支撑件**算法体零 diff 纪律**：摘出/迁入只动 package、声明位与 import；实现逐行照抄。

## 新增基准

放 `src/jmh/java/com/tny/game/bench/<domain>/{routine|devtest}/` 下，`@Benchmark` 注解即可
（annprocess 随插件接线自动登记；BenchmarkList 由 `jmhRunBytecodeGenerator` 生成）。
跨包访问 protected 成员的装配（如 codec 注入）参照 `PacketCodecBenchmark.inject` 的反射模式。
被多个基准复用的实现放 `shared/`，不随宿主族走。归族与对账口径见上「两族分类」节。

## 历史注记（D2 废止）

- 旧态（2026-09-29 `optimize-net-hot-path` design D2）：无外部插件，annotation processor + JavaExec `bench`
  任务裸跑，配 `-PjmhArgs` 手工传参 + `benchCpSnapshot` 冻结 classpath 到 /tmp（防 Gradle 重建毒死 fork 类加载）。
- 废止理由与插件接线决策见 `openspec/changes/promote-netbench-to-tny-bench/design.md` T2
  （引号注入事故、快照补丁面、参数转抄成本 → 插件 fork 类路径固化 + 参数入块）。
- 2026-10-01 `split-bench-suites`：「锚集」隐式口径退役为「常规族/开发测试族/共享支撑件」三结构；
  执行通道触发面由仅夜间扩展至 push+夜间+手动；`benchAnchorExport`→`benchRoutineExport`。
