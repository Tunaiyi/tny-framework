# 验证记录

## rename-bench-suite-to-benchmark-module（2026-10-03）

- 环境：JDK 钉 Corretto 21.0.12.1，正常 UTF-8 locale，daemon 噪声过滤口径（前批确立）。
- 1.1 前置与基线：rename-bench-to-benchmark 已归档（archive/2026-10-03-rename-bench-to-benchmark/）——本册 2.1 全部在模块改名后的现场（tny-benchmark/build.gradle）操作。基线存本目录 baseline/：`tasks --all` 剔噪清单 3457 行、`jmhList -PbenchAll` 全量枚举 31 键（自 listFile 转存）、`jmhSuiteVerify` 对账绿（routine 4 类/devtest 5 类/探针 1 类，全 9 类归族）、速览 13 键（沿用前册换代映射基线）。
- 2.1 两层改名：`tny.bench-suite.gradle` git mv 移名 `tny.benchmark-module.gradle`；插件内六处 `benchSuite` 字样换 `benchmarkSuite`（第 4/5/7/18 行注释、第 16 行 `extensions.create('benchmarkSuite', tny.convention.BenchSuite)` 类型参数不动、第 47 行报错文案前缀 D2），第 3 行历史路径"前身为 gradle/bench-suite.gradle"保留；类文件 `BenchSuite.groovy` 第 1、4 行 `tny.bench-suite` 指称改 `tny.benchmark-module`（类名与五属性名不动，D1）；模块 `tny-benchmark/build.gradle` 八行——第 4/28/75 行插件名指称、第 5 行 `id 'tny.benchmark-module'`、第 29 行块名与第 56/65/70 行三处读取随 `benchmarkSuite`；族正则、臂属清单、任务名、`-Pbench*` 属性、`reports/bench/` 路径段零触碰（Non-Goals）。`projects -q` 通过。
- 3.1 验收：`grep "tny\.bench-suite|benchSuite"` 双判据现行面零命中；`grep -i "bench-suite|benchsuite"` 复核命中面全部落四类豁免（插件第 3 行历史路径、插件第 9 行与类第 2 行与模块第 27 行的 `split-bench-suites` 历史变更名、类名 `BenchSuite` 声明与 import）——无第五类，design D4 判据达成；三探针复跑：枚举 31 键与基线逐行零差异、`jmhSuiteVerify` 对账绿、D2 报错文案活体探针（no-match include 触发判红，文案现含 `benchmarkSuite.routineFamily`）；`tasks --all` 剔噪清单段对基线逐行零差异（扩展名不入任务面的实测复证）；`./gradlew clean build` 一次全绿（347 任务 339 执行，零 FAILURE，无偶红）。
- 结论：插件层与扩展名收口完毕，benchmark 词族（模块、包根、插件 id、扩展）四层齐整；行为零变化的证据链为枚举/清单逐行零差异 + 对账与速览键集一致 + 全量构建绿。
