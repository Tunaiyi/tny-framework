# Tasks

> 本变更不触碰公共 API（无 JUnit 前置测试义务）；`:tny-bench` 无测试源集，组验证以
> `compileJmhJava`/清单枚举/`-PbenchFast` 选择面探针替代 `:模块:test`。
> 族属双承载（D1）+ 支撑件摘出（D6）后，改动面含 tny-bench 的 Java **声明面**（package/import/可见性），算法体零 diff 为评审红线。

## 1. 前置核对

- [x] 1.1 核对远端 gh-pages 曲线历史点位数（Actions 运行记录与 gh-pages 分支数据文件）。验证：无点位 → 直进组 2；确有点位 → 按 D4 兼容性条款停下交人工裁决（接受序列断层／缓行搬包／回退仅清单方案），裁决记录进变更目录。
- [x] 1.2 留底基线：`./gradlew :tny-bench:jmhList -PbenchAll` 全量枚举输出存档（类名 × 方法 × 参数组合清单）。验证：37 组合基线面与 README 记载一致，作为移包后对账基线。

## 2. tny-bench/src：支撑件摘出与族移包

- [x] 2.1 建 `net/shared/` 并迁入跨族支撑件：`AeadRfc7539` 整类（类及其被跨包成员：构造器、`selfTest`、`setNonce/seal/tag/auth/decryptInPlace` 由包级放宽为 public）；`Crc64Slicing` 自 `CryptoAlgorithmMicroBenchmark` 摘出为顶层类；`sipHash64` 自 `VerifyAlgorithmsMicroBenchmark` 提为 shared 静态工具。验证：`./gradlew :tny-bench:compileJmhJava` 通过；摘出 diff 仅限声明面与 import，算法体逐行零改动。
- [x] 2.2 族移包：`PacketCodecBenchmark/PipelineCryptoMatrixBenchmark/MessageQueueBenchmark/RespondFutureBenchmark` → `net/routine/`；`SmokeBenchmark/CryptoAlgorithmMicroBenchmark/VerifyAlgorithmsMicroBenchmark/LegacyPathsMicroBenchmark/AeadTransportMicroBenchmark` → `net/devtest/`；package 声明与 import 全量修正（Matrix 引用 shared 三件与同族 `BenchTunnel`，AeadTransport 引用 shared `AeadRfc7539`）。验证：`compileJmhJava` 通过；全量枚举条目 = 1.2 基线逐一仅 FQCN 前缀变化。
- [x] 2.3 组验证：`./gradlew :tny-bench:jmh -PbenchFast` 开发族与常规族各按名子集可被选中（选择面探针，不出计时结论）。

## 3. tny-bench/build.gradle：族清单、对账校验与导出任务

- [x] 3.1 ext 清单切族子包口径：`benchRoutineRun/benchRoutineClasses = ['.*\.bench\.net\.routine\..*']`；新增 `benchDevSuiteClasses = ['.*\.bench\.net\.devtest\..*']`、`benchFacilityProbe = ['.*\.bench\.net\.devtest\.SmokeBenchmark.*']`；`benchAnchorAlgoArms` → `benchRoutineAlgoArms` 六臂参数域覆写保留（臂属选择面不属族属，D1）。验证：`./gradlew :tny-bench:jmhList` 仅列 routine 条目，不含 devtest 任何类。
- [x] 3.2 新增两段式校验任务 `jmhSuiteVerify`：① 完备性——全量枚举（`-PbenchAll` 口径）与「routine ∪ devtest ∪ 探针」类粒度并集对账，未归族判红列名；② 目录一致——类包前缀与声明族属双向核对（搬而未登记／登记而搬偏均判红）。沿用 `jmhListVerify` 的「空清单显式判红」形状。验证：现状全绿；临时从 `benchDevSuiteClasses` 摘一项 → 红且点名（验后还原）。
- [x] 3.3 `benchAnchorExport` → `benchRoutineExport`：产物 `results/bench-<yyyymmdd>-routine.json`；缺省 `jmh{}` 选择面 = routine 族正则 ∪ 探针正则，`benchmarkParameters` algo 六臂随改名。验证：`jmh -PbenchFast` 缺省选择面输出 37 组合且含 Smoke 探针条目；旧任务名执行报 task not found。
- [x] 3.4 组验证：`./gradlew :tny-bench:jmhCompileGeneratedClasses :tny-bench:jmhList :tny-bench:jmhSuiteVerify` 全链退出 0。

## 4. .github/workflows/build.yml：通道改造

- [x] 4.1 `bench-compile` 步骤命令追加 `:tny-bench:jmhSuiteVerify`（评审通道 = 编译 + 按族枚举 + 完备性与目录双向对账，仍零计时）。验证：本地以步骤同款命令序列跑绿（引用 3.4），yml 命令与之一字不差。
- [x] 4.2 `bench-nightly` job 更名 `bench-routine`：`if:` 扩为 `push || schedule || workflow_dispatch`；调用改 `benchRoutineExport`；回写提交信息 `routine benchmark results [skip ci]`；`github-action-benchmark` 的 `output-file-path` 不变。验证：静态核对 yml——job id、if、run 行与 3.3 任务名一致，除 `results/` 历史与注释外无残留 `anchor` 字样。
- [x] 4.3 组验证：yml 内全部可本地化命令均已在组 3 复现（本机无 gh CLI，真实 CI 行为留组 6）。

## 5. tny-bench/README.md：口径文档

- [x] 5.1 目录组织与分类表：新增「被测域 × 族 × shared」三结构说明；10 个基准逐类归位表（族属 + 子包路径 + 判定理由列）；设施探针特例（devtest 族成员、恒随执行面、条目不构成框架断言）；shared 支撑件清单与「算法体零 diff 纪律」。术语「锚集」→「常规族/执行面」全篇迁移。验证：表格与 ext 清单、目录实况三方逐类比对一致。
- [x] 5.2 命令面更新：`jmhList`（族清单语义）、`jmhSuiteVerify`、`benchRoutineExport` 示例；`-PbenchInclude` 开发族示例改指 `net.devtest.` 新 FQCN；产物引用约定改 `bench-<yyyymmdd>-routine.json`，保留「旧 `-anchor.json` 为历史口径证据不回改」注记。验证：README 每条命令逐条可执行（长跑以 `-PbenchFast` 替代验证选择面）。
- [x] 5.3 组验证：README 无残留 `benchAnchor` 任务引用、无失效 FQCN。

## 6. 首验、回填与收口

- [x] 6.1 本地 `jmh -PbenchFast` 全选择面快跑记录（37 组合确认 + 时长基线），给出 CI 预算区间（D5 预估 25~40 min 的对照）。
- [ ] 6.2 `workflow_dispatch` 触发 `bench-routine` 全链首验：产物 `results/bench-<日期>-routine.json` 落盘、回写 commit 带 `[skip ci]` 无自触发、gh-pages 曲线以新 bname 主键起线。验证：三处各留可核对证据（run 链接、commit sha、curve 页面）。
- [x] 6.3 负向验证评审通道：功能分支临时新建一个无 @Benchmark 归族的探针类 → `bench-compile` 完备性红且点名；再搬一个 routine 类而不改清单 → 双向对账红；两处验后还原转绿。
- [x] 6.4 首个自然 push（框架优化合入）后核对执行通道自动运行的数据点与实测时长，回填 README 预算小节（D5 实证闭环）。
- [x] 6.5 收口：`openspec validate split-bench-suites --strict` 通过；`codegraph_reindex_workspace` 增量重建后抽查移包类新位置可检索；差量与账本核对，备 /opsx:verify 与归档。
