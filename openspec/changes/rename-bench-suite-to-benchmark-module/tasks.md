# Tasks

## 1. 前置确认与基线抓样

- [ ] 1.1 前置确认：rename-bench-to-benchmark 变更已实施归档（未归档即停，D3 分层时序）。抓基线存本变更目录 baseline/（捕获口径：JDK 钉 Corretto 21、正常 locale、不注入 JAVA_TOOL_OPTIONS，过滤行含 daemon 告警与空行）：`./gradlew tasks --all --console=plain` 剔噪清单段；`./gradlew :tny-benchmark:jmhList -PbenchAll --console=plain` 全量枚举（应零命中扩展名字样，复证 D4）；`./gradlew :tny-benchmark:jmhList :tny-benchmark:jmhSuiteVerify -q` 对账绿记录；`./gradlew projects -q --console=plain` 评估绿记录。

## 2. 改名实施

- [ ] 2.1 插件与扩展两层：`buildSrc/src/main/groovy/tny.bench-suite.gradle` 以 git mv 移名 `tny.benchmark-module.gradle`；内部六行随改——第 4 行同行只改 `benchSuite` 字样（`:tny-benchmark` 归属注若前置归档则已是现名，不动）、第 5 行职责段扩展名、第 7 行"benchSuite 五清单"改 `benchmarkModule 五清单（tny.convention.BenchSuite）`（类名保留，D1）、第 16 行 `extensions.create('benchmarkModule', tny.convention.BenchSuite)`、第 18 行注释、第 47 行报错文案前缀（D2）；第 2 行"前身为 gradle/bench-suite.gradle"历史路径不动。类文件 `tny/convention/BenchSuite.groovy` 第 1、4 行 `tny.bench-suite` 指称改 `tny.benchmark-module`（类名与属性名不动）。模块 `tny-benchmark/build.gradle` 八行：第 4、28、75 行插件名指称、第 5 行 `id 'tny.benchmark-module'`、第 29 行块名 `benchmarkSuite` 改 `benchmarkModule` 与第 56/65/70 行三处读取随行；族正则与臂属清单取值一字不动。任务名、`-Pbench*` 属性名、`reports/bench/` 与 `dev/bench/` 路径段一律不动（Non-Goals）。验证：`./gradlew projects -q` 通过（配置期读取链即探）。

## 3. 零差异验收

- [ ] 3.1 复跑比对：`tasks --all` 剔噪清单段对 1.1 基线逐行零差异（扩展名不入清单面为 D4 实测前提，任何差异即停回用户处）；`jmhList -PbenchAll` 枚举与基线逐行零差异、`jmhSuiteVerify` 对账绿、速览键集与 rename-bench-to-benchmark 换代对照表一致（族正则值未动的直接检验）；`grep -rn "tny\.bench-suite\|benchSuite" build.gradle settings.gradle gradle buildSrc/src tny-*/build.gradle docs .github openspec/config.yaml` 现行面零命中（插件第 2 行与类文件 package 行的历史路径/包名字样不属两判据形态，留档确认）；`./gradlew clean build` 全绿——CollectionLockTest 同签名偶红按 fix-ci-unit-flakes 登记标准处置留痕。全部结论记 verification-notes.md。
