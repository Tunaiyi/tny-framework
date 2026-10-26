# Design

## Context

动机见 proposal.md - Why。现场核对（2026-10-03）：`tny-benchmark/build.gradle` 的 jmh 块（第 47 行起）含静态参数八行（jmhVersion 至 resultsFile，留模块）与控制流段（familyInclude 拼接一行、三分支 if/else-if/else 约九行、缺省档参数域、benchGc 与 benchFast 两个 if 段约十行，迁插件）。`tny.benchmark-module.gradle` 第 127 行起已有 afterEvaluate 段解析 `-PbenchParams`（declarative-it-demo-isolation design D5 的收编物），其头部注释明言"程序性中间变量累加不落模块文件"——本册迁入段与它同落点同性质。插件体内第 16 行局部变量 `suite` 即 benchmarkSuite 扩展实例，新段直接引用。

## Goals / Non-Goals

Goals：模块回到 80 行内、jmh 块配置阶段控制流清零、四种执行面形态（缺省/速览/全量/定向+快档）的选择结果逐键不变。Non-Goals：不动 jmh 静态基线参数（D3 纪律入模块声明面）；不动 `jmhList`/`jmhListVerify`/`jmhSuiteVerify`/`benchRoutineExport` 任务体；不改 CI（属性名与任务名零变化）。

## Decisions

**D1 落点=插件现有 afterEvaluate 段之前，benchParams 覆写仍最后。** 同一工程的 afterEvaluate 回调按注册顺序执行——新段先注册先执行，既有 benchParams 段后注册后执行，"显式 `-PbenchParams` 覆盖缺省参数域"的现状优先级原样保持。
**D2 时点等价论证。** 原实现全部在模块 jmh 块（配置阶段）顺序求值；迁入后静态参数仍在配置阶段、选择属性移到 afterEvaluate——每个属性只有唯一写入点，jmh 任务读取发生在任务执行期（远晚于 afterEvaluate），最终值全等。`hasProperty` 读命令行 `-P` 属性在两个时点结果一致（属性表在配置开始前已定型）。
**D3 逐字迁入不顺手压缩。** familyInclude 拼接式、三分支条件式、benchFast 参数组均逐字迁移（含来由注释随行），与并册 D3 同纪律——形态再优化另册。

## Risks / Trade-offs

- 风险：jmh 插件扩展在插件 afterEvaluate 时点的可达性——既有 benchParams 段已在同一时点操作 `jmh.benchmarkParameters`，路径实证可用。
- 风险：`-PbenchScope=quick` 组合 excludes 的生效面影响速览键集——验收逐键比对兜底。
- 权衡：无。

## Migration Plan

第一步，抓基线（速览 13 键实跑、`-PbenchAll` 枚举 31 键、`-PbenchInclude='.*Smoke.*' -PbenchFast` 定向实跑、剔噪清单）。第二步，插件扩段与边界句、模块 jmh 块收敛。第三步，验收（三探针复跑一致、清单零差异、`clean build` 绿），记 verification-notes.md。回滚为两段还原。
