# Proposal

## Why

`tny-benchmark/build.gradle` 实测 84 行，超 gradle-build-style 需求七的模块 80 行界线；超行主因是 jmh 块内滞留的配置阶段控制流——`familyInclude` 拼接、`benchAll`/`benchInclude`/`benchScope` 三分支 if/else-if/else 链与 `benchGc`、`benchFast` 两个属性开关，属需求一"配置阶段不得出现分支"的违例形态。同文件先例已确立去处：benchParams 解析循环就是按 declarative-it-demo-isolation design D5 迁入 `tny.benchmark-module` 插件 afterEvaluate 的（"程序性不入模块文件"），本册把同一段规模选择的剩余控制流收进同一落点，一次修完行数与违例。

## What Changes

- `tny.benchmark-module.gradle` 的现有 afterEvaluate 段**之前**新增规模选择段：`familyInclude` 拼接、`benchAll`/`benchInclude`/`benchScope-quick` 三分支与缺省档（含六臂参数域）、`benchGc` 画像开关、`benchFast` 快档——全部写 `jmh` 扩展属性；benchParams 覆写保持段内最后（优先级现状不变）。
- `tny-benchmark/build.gradle` 的 jmh 块只留静态参数声明（jmhVersion、fork、迭代、resultFormat、resultsFile 等），删除整段控制流与随行注释（来由说明移入插件段）。
- 插件头注释边界句"jmh 执行参数与依赖声明在模块构建文件"同步改写为现状（静态参数与依赖在模块，规模选择在插件）。

## Capabilities

### New Capabilities / Modified Capabilities

无。benchmark-harness 与 gradle-build-style 条文零改动，本册按既有需求一/需求七修正实现形态；选择面语义（哪些基准在哪些参数下运行）零变化。skip_specs。

## Impact

- **受影响文件**：`buildSrc/src/main/groovy/tny.benchmark-module.gradle`（afterEvaluate 前扩段、头注释边界句）、`tny-benchmark/build.gradle`（jmh 块收敛，84 行回到 80 以内，插件文件 134 行加段后仍远低于 250 界线）。
- **顺序关系**：与并册 `move-it-fixture-loop-into-plugin` 无共同文件；与 `fix-ci-unit-flakes`、`refine-bench-routine-triggering`（已归档批的 CI 实况面）无文件交集。前批捕获口径沿用。
- **验收基线**：实施前抓缺省速览实跑 13 键、`-PbenchAll` 枚举 31 键、`-PbenchInclude` 定向单基准 `benchFast` 组合实跑、`tasks --all` 剔噪清单；落地后速览键集与参数域逐键一致、`-PbenchAll` 枚举一致、定向探针行为一致；`clean build` 全绿。`jmhList`/`jmhSuiteVerify` 与本次迁移无关（不读 jmh 块），作为旁证复跑必须同样一致。
