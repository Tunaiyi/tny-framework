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

## 执行面（CI 执行通道，两个规模）

选择面的单一事实在 `build.gradle`：族清单 `benchRoutineClasses`/`benchDevSuiteClasses`/`benchFacilityProbe`
（族子包正则，目录与声明由 `jmhSuiteVerify` 对账）与规模清单 `benchQuickRun`（速览子集声明）。

| 规模 | 成员 | 组合数 | 谁在用 |
|---|---|---|---|
| 速览 quick | 全管线、发送队列、RPC 配对三类加设施探针（加密装配矩阵除外） | 13 | 触及框架源码的合入（CI push 经内容门禁放行，传 `-PbenchScope=quick`） |
| 完整 full | 常规族全部，矩阵臂经 `-p algo` 选 `prod_*` 六臂 | 37 | 夜间定时、手动触发、本地缺省直接执行 `./gradlew :tny-bench:jmh` |

- 触发规则（`build.yml` 的 `bench-scope-gate` 内容门禁）：push 仅在改动触及模块源码目录、任一构建脚本、
  根 `gradle.properties`、`gradle/libs.versions.toml` 或 `tny-bench/` 时以速览规模计时；纯文档、规格案卷、
  流水线脚本的合入**不计时也不回写**；定时与手动恒完整规模；PR 永不计时（基准编译与族校验由
  `bench-compile` 全触发承担）。矩阵级退化最迟在下一次夜间完整运行显现，属既定裁决而非漏洞。
- 时长预算（实测）：完整规模约十八至二十一分钟（commit 43a5e966 首跑与 00e4c9da 二跑实证）；
  速览规模按同参数比例推算约七至九分钟，待首个实况合入回填。
- 曲线序列分组：github-action-benchmark 的分组键为"Routine 规模 (分支)"，
  main 与 5.7.x、速览与完整互不混线，劣化告警只在同组近期序列内比较。

## 产物与引用约定（spec：结果产物结构化可对比）

- 产物文件名同时携带日期与规模：完整规模入 `results/bench-<yyyymmdd>-routine.json`，
  速览规模入 `results/bench-<yyyymmdd>-quick.json`（`benchRoutineExport` 依 `-PbenchScope` 自动区分）。
  JMH 自述完整参数/JVM/OS 环境头；同日期覆盖限定在同一规模之内，逐版对比要求同规模成立。
- **变更工件引用基准数字时 MUST 写明结果文件路径（含日期与规模）与参数指纹**，例：
  `数据源：results/bench-20261001-routine.json（完整规模，-f2 -wi5 -i10 -w500ms -r1s，JDK21/aarch64）`。
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
- 2026-10-02 `refine-bench-routine-triggering`：执行通道加内容门禁（非行为性合入不计时不回写）；
  计时拆为速览与完整两个规模（`-PbenchScope=quick` 与缺省完整）；曲线序列按"分支乘规模"分组；
  产物文件名增加规模标识。
