# tny-bench —— 全仓基准模块（benchmark-harness）

## 定位

- **全仓基准执行设施**（不限于网络域）：基准类按被测域放 `com.tny.game.bench.<domain>`，当前域：`net`。
- **刻意不叫 `tny-game-*`**：不落入根构建的模块装配线（发布/装配由该命名触发），本模块零发布；
  根 `build.gradle` 另有配置期守卫——任何 `tny-game-*` 模块声明对 `:tny-bench` 的依赖将构建失败并指明违例模块。
- 产出的是**基线数据**（`results/bench-<日期>-anchor.json` 与 `baseline-*.md`），供优化变更引用作前后对比锚点——优化本身不在这里做。

## 运行（me.champeau.jmh 0.7.3 接线，基线参数声明于 `build.gradle`）

```bash
# 列锚集清单（CI PR 通道同款；空匹配显式判红）
./gradlew :tny-bench:jmhList

# 列全量基准清单
./gradlew :tny-bench:jmhList -PbenchAll

# 缺省运行 = 锚集 + 仓库声明的基线参数（可复现）
./gradlew :tny-bench:jmh

# 按名子集（单正则语义，多目标用 |；不再需要旧版 -PjmhArgs 引号杂技）
./gradlew :tny-bench:jmh -PbenchInclude='PacketCodec|MessageQueue|RespondFuture'

# 全量矩阵（大预算，人工择窗执行）
./gradlew :tny-bench:jmh -PbenchAll

# 锚集落盘：跑完后复制为 results/bench-<yyyymmdd>-anchor.json（nightly 通道同款）
./gradlew :tny-bench:benchAnchorExport

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
| 缺省选择面 | 锚集（见下），非全量 |

## 锚集（nightly 回归的固定选择面，design T4）

- `SmokeBenchmark`（设施自检）∪ `PacketCodecBenchmark`（管线级）∪ `PipelineCryptoMatrixBenchmark` 的
  `prod_*` 生产装配臂——经 JMH `-p algo=<六臂>` **参数域覆写**选择（实测：include 正则不匹配参数串，
  `:prod_` 式过滤 0 选中；`-p` 的 JMH 1.37 语义是覆写值域而非过滤，对未声明该字段的基准无排除效应）。
- 定义在 `build.gradle` 的 `benchAnchorRun`/`benchAnchorAlgoArms`/`benchAnchorClasses`（单一事实；
  **单正则 alternation 语义**——插件会把 includes 列表逗号拼接成单个 JMH 位置参数，多正则必须自拼 `|`）。
- 规模：3 基名 / 27 参数组合（6 臂 × 2 键形 × 2 尺寸 + Smoke 1 + PacketCodec 2），本地预算 ~13 分钟。

## 产物与引用约定（spec：结果产物结构化可对比）

- 新产物一律入 `results/bench-<yyyymmdd>-anchor.json`，JMH 自述完整参数/JVM/OS 环境头；同日期覆盖。
- **变更工件引用基准数字时 MUST 写明结果文件路径（含日期）与参数指纹**，例：
  `数据源：results/bench-20261001-anchor.json（-f2 -wi5 -i10 -w500ms -r1s，JDK21/aarch64）`。
- 曲线告警（CI `bench-nightly` job，github-action-benchmark，tool=jmh）仅作信号；**回归判定由人按"同窗对比"终裁**——
  夜间 runner 与本地机器不同窗，跨机数字只能提示方向，不能定案。
- 模块根散放的 `*-2026-09-*.json` / `pipeline-matrix-*.json` / `crypto-bench-2026-09-30.md` 为
  **历史口径数据**（results/ 约定之前的裁决证据），归档工件按旧路径引用者不回改。

## 基线纪律（D3，保留条款）

- 数据文件头记录 JDK/OS/CPU 与完整参数（JSON 产物已机械携带；md 手工记录仍需自带）。
- `-f ≥ 2`；对比锚点只在同参数下成立。
- **R1 警示**：微基准对"纯分配类"候选给出的数字是上界（JIT 逃逸分析在真实调用链可能标量替换）——据此的优化决策需真实管线场景复测。
- 已知污染源：`RespondFutureMonitor` 的 static 5s 全局定时器在 respond 基准期间后台运行（数据解读时标注）。

## 新增基准

放 `src/jmh/java/com/tny/game/bench/<domain>` 下，`@Benchmark` 注解即可（annprocess 随插件接线自动登记；
BenchmarkList 由 `jmhRunBytecodeGenerator` 生成）。跨包访问 protected 成员的装配（如 codec 注入）
参照 `PacketCodecBenchmark.inject` 的反射模式。新基准是否入锚集，按其是否承担回归哨兵职责决定（改动 `benchAnchor*`）。

## 历史注记（D2 废止）

- 旧态（2026-09-29 `optimize-net-hot-path` design D2）：无外部插件，annotation processor + JavaExec `bench`
  任务裸跑，配 `-PjmhArgs` 手工传参 + `benchCpSnapshot` 冻结 classpath 到 /tmp（防 Gradle 重建毒死 fork 类加载）。
- 废止理由与插件接线决策见 `openspec/changes/promote-netbench-to-tny-bench/design.md` T2
  （引号注入事故、快照补丁面、参数转抄成本 → 插件 fork 类路径固化 + 参数入块）。
