# Tasks

## 1. 前置确认与基线抓样

- [x] 1.1 前置确认：rename-bench-to-benchmark 变更已实施归档（未归档即停，D3 分层时序）。抓基线存本变更目录 baseline/（捕获口径：JDK 钉 Corretto 21、正常 locale、不注入 JAVA_TOOL_OPTIONS、过滤行含 daemon 告警/进度/汇总/耗时与空行）：`./gradlew tasks --all --console=plain` 剔噪清单段；`./gradlew :tny-benchmark:jmhList -PbenchAll -q` 全量枚举 31 键（自 `:tny-benchmark:jmhList` 任务的 listFile 转存）；`./gradlew :tny-benchmark:jmhList :tny-benchmark:jmhSuiteVerify -q` 对账绿记录；`./gradlew :tny-benchmark:benchRoutineExport -PbenchScope=quick` 速览 13 键与产物路径记录；`./gradlew projects -q` 评估绿记录。

## 2. 改名实施

- [x] 2.1 插件与扩展两层：`buildSrc/src/main/groovy/tny.bench-suite.gradle` 以 git mv 移名 `tny.benchmark-module.gradle`；内部六处随改——第 4/5/7/18 行注释的 `benchSuite` 字样换 `benchmarkSuite`（同行 `:tny-benchmark` 归属注已是现名不动，D3）、第 16 行 `extensions.create('benchmarkSuite', tny.convention.BenchSuite)`（类型参数不动）、第 47 行报错文案"检查 benchmarkSuite.routineFamily 与 -PbenchAll"（D2）；第 3 行"前身为 gradle/bench-suite.gradle"历史路径不动。类文件 `tny/convention/BenchSuite.groovy` 第 1、4 行对 `tny.bench-suite` 的指称改 `tny.benchmark-module`（类名与五属性名不动，D1）。模块 `tny-benchmark/build.gradle` 八行——第 4/28/75 行插件名指称随改、第 5 行 `id 'tny.benchmark-module'`、第 29 行块名与第 56/65/70 行三处读取 `benchSuite` 换 `benchmarkSuite`；族正则四条、臂属清单、jmh 段结构取值一字不动。任务名、`-Pbench*` 属性名、`reports/bench/` 与 `dev/bench/` 路径段一律不动（Non-Goals）。验证：`./gradlew projects -q` 通过（应用行与配置期读取链即探）。

## 3. 零差异验收

- [x] 3.1 复跑比对：`tasks --all` 剔噪清单段对 1.1 基线逐行零差异（插件 id 与扩展名不入清单面为前批实测前提；枚举行中 12 处 `jmhSuiteVerify` 为任务名不动、留档定性，出现任何其它差异即停回用户处）；`jmhList -PbenchAll` 枚举 31 键、`jmhSuiteVerify` 对账、速览 13 键三探针与基线逐行/逐键一致（族清单读取链的活体检验——扩展漏改当场报"扩展不存在"红）；`grep -rn "tny\.bench-suite\|benchSuite" build.gradle settings.gradle gradle buildSrc/src tny-*/build.gradle docs .github openspec/config.yaml openspec/specs` 现行面零命中；`grep -ri "bench-suite\|benchsuite"` 复核命中面仅四类豁免（历史路径、类名、`jmhSuiteVerify` 任务名、归档记录），多出一类即漏改；`./gradlew clean build` 全绿——CollectionLockTest 同签名偶红按 fix-ci-unit-flakes 登记标准处置留痕。全部结论记 verification-notes.md。
