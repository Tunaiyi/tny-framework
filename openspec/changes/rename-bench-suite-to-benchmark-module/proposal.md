# Proposal

## Why

`tny.bench-suite` 的 "suite" 说的是族清单集合，但插件实际承担基准模块的整线约定（清单扩展贡献、枚举对账与落盘任务、参数覆写解析）；且模块与包根刚由 rename-bench-to-benchmark 从 bench 换到 benchmark，插件 id 与扩展名 `benchSuite` 停留旧词族，在同一模块文件里造成新旧并存的半截身份。用户定名改插件为 `tny.benchmark-module`（与 `tny.module-checker`、`tny.module-setting` 同谱系），扩展名随插件新名改 `benchmarkSuite`（用户裁决"id 与扩展名同动"；"Suite"一词在清单、对账与报错文案语境比"Module"更准确且与暂留的类名 `BenchSuite` 同词，扩展与类型读作 `benchmarkSuite`/`BenchSuite` 名实一致；类名收口可另批，与 module-setting 先例同节奏）。

## What Changes

- 文件 `tny.bench-suite.gradle` 移名 `tny.benchmark-module.gradle`（插件 id 随文件名派生）；内部六处随改——第 4/5/7/18 行注释的 `benchSuite` 字样、第 16 行 `extensions.create('benchSuite', …)` 改 `benchmarkSuite`（类型参数 `tny.convention.BenchSuite` 不动，类暂留）、第 47 行 `jmhListVerify` 报错文案的"检查 benchSuite.routineFamily"换扩展名前缀（属性名与 -PbenchAll 不动）；第 2 行"前身为 gradle/bench-suite.gradle"历史路径保留。
- 类文件 `tny/convention/BenchSuite.groovy` 第 1、4 行对 `tny.bench-suite` 的指称改 `tny.benchmark-module`；类名与五个属性名不动。
- 模块 `tny-benchmark/build.gradle` 八处——第 4/28/75 行插件名指称、第 5 行 `id 'tny.benchmark-module'`、第 29 行扩展块 `benchSuite {` 与第 56/65/70 行三处 `benchSuite.<属性>` 读取改 `benchmarkSuite`；族正则、臂属清单、任务名与 `-Pbench*` 属性名一律不动。
- 历史指称不回改：归档案卷（含 rename-bench-to-benchmark 册内 `benchSuite 清单` 等记录）、HANDOFF 与本账其他工件按记录原则保留。

## Capabilities

### New Capabilities / Modified Capabilities

无。`benchmark-harness` 六需求以"单一顶层模块""集中声明"类别语行文，插件 id 与扩展名不在条文内；`tny-bench` 等旧名在主规格的零命中已由前批 grep 实证。skip_specs。

## Impact

- **受影响文件**（2026-10-03 现场逐行清点，16 个出现点/16 行）：插件文件 `tny.bench-suite.gradle` 六行（第 4、5、7、16、18、47 行）、`BenchSuite.groovy` 类头两行（第 1、4 行，第 9 行类名声明不属指称面）、`tny-benchmark/build.gradle` 八行（第 4、5、28、29、56、65、70、75 行）。CI、docs、主规格、根脚本对两旧名零命中（grep 实测）。
- **前置约束**：`rename-bench-to-benchmark` 实施归档之后（用户裁决两本分工）——本册工件全部按模块改名后的现场（`tny-benchmark/build.gradle`）书写；该本已归档，前置满足。与其余在途变更无共同文件。
- **验收基线**：`tasks --all` 剔噪清单段对实施前自抓基线逐行零差异（任务名全部不动；插件 id 与扩展名经实测均不入任务清单面——枚举中 12 行 `jmhSuiteVerify` 为任务名按案卷不动，留档定性）；`:tny-benchmark:jmhList -PbenchAll` 枚举与 `jmhSuiteVerify` 对账、速览 `benchRoutineExport` 三探针绿（jmh 段读取链、族清单派生正则与报错文案的活体检验）；grep 现行面 `tny\.bench-suite` 与 `benchSuite` 双判据零命中（历史路径 `gradle/bench-suite.gradle`、类名 `BenchSuite` 与 `jmhSuiteVerify` 字样经 `-i` 复核仅四类豁免形态命中、大小写敏感判据本身零命中）；`clean build` 全绿。
