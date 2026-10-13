# Proposal

## Why

rename-bench-suite-to-benchmark-module 册的 D1 记录留有尾巴：实现类 `tny.convention.BenchSuite` 暂留、"类收口与 module-setting 先例同节奏可另批"。用户现给出另批字面：类改 `BenchmarkSuite`——与插件 id `tny.benchmark-module`、扩展名 `benchmarkSuite` 三层齐整，benchmark 词族在基准配置面彻底闭环（module-setting 先例正是同一节奏：id 与扩展先行、类名随后一册收口）。纯符号改名：五个清单属性、对账逻辑、任务与报错文案全部不动。

## What Changes

- `buildSrc/src/main/groovy/tny/convention/BenchSuite.groovy` 移名 `BenchmarkSuite.groovy`，类声明（第 9 行）随改；类头注释四行现状即新语境（首行已写 `tny.benchmark-module`），零文本改动随行。
- 插件 `tny.benchmark-module.gradle` 两行——第 7 行配置面条目"（tny.convention.BenchSuite）"与第 16 行 `extensions.create('benchmarkSuite', tny.convention.BenchSuite)` 的类型参数随改为 `BenchmarkSuite`（扩展名不动）。
- 历史指称不回改：`split-bench-suites`、`promote-netbench-to-bench` 等连字符小写形态的变更名指称与 `BenchSuite` 大小写敏感判据零重叠（插件第 9 行、类第 2 行、模块第 27 行保留）；归档与 HANDOFF 记录面不动。

## Capabilities

### New Capabilities / Modified Capabilities

无。类符号属 buildSrc 内部实现（模块作者经扩展名读属性、从不见类名），主规格零钉名（grep 实测）。skip_specs。

## Impact

- **受影响文件**：类文件本体（移名 + 类声明一行）、插件两行，共三处现行指称（2026-10-03 现场 grep `BenchSuite` 大小写敏感定界）；CI、docs、主规格、模块构建文件零命中。
- **顺序关系**：无前置——前两册（插件改名、扩展改名）均已落地归档，现名现场直接操作；与活跃五册（expose/fix-ci/refine-bench/central/revise）无共同文件。
- **验收基线**：类漏改即 `extensions.create` 类型解析失败、配置期评估报红（常驻探测器）；`tasks --all` 剔噪清单段逐行零差异（符号名不入清单面，前批实测）；`jmhSuiteVerify` 对账与 `jmhList -PbenchAll` 枚举 31 键复跑一致（清单读取链的活体检验）；grep `BenchSuite`（大小写敏感，`BenchmarkSuite` 字面不含该连续子串、历史变更名连字符形态不属判据）现行面零命中；`clean build` 全绿。
