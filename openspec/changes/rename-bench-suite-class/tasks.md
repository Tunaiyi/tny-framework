# Tasks

## 1. 基线抓样

- [ ] 1.1 无前置（前两册已归档，现名现场直接操作）。抓基线存本变更目录 baseline/（捕获口径：JDK 钉 Corretto 21、正常 locale、不注入 JAVA_TOOL_OPTIONS、过滤 daemon 噪声行与空行）：`./gradlew tasks --all --console=plain` 剔噪清单段；`./gradlew :tny-benchmark:jmhList -PbenchAll` 全量枚举（listFile 转存 31 键）；`./gradlew :tny-benchmark:jmhSuiteVerify` 对账绿记录；`./gradlew projects -q` 评估绿记录。

## 2. 改名实施

- [ ] 2.1 `buildSrc/src/main/groovy/tny/convention/BenchSuite.groovy` 以 git mv 移名 `BenchmarkSuite.groovy`；类声明行（第 9 行）改 `abstract class BenchmarkSuite`；插件 `tny.benchmark-module.gradle` 两行随改——第 7 行括注 `（tny.convention.BenchmarkSuite）`、第 16 行 `extensions.create('benchmarkSuite', tny.convention.BenchmarkSuite)`（扩展名与属性名不动）；类头注释与历史变更名指称零触碰。验证：`./gradlew projects -q` 通过（类型解析为配置期常驻探测）。

## 3. 零差异验收

- [ ] 3.1 复跑比对：`tasks --all` 剔噪清单段对 1.1 基线逐行零差异；`jmhList -PbenchAll` 枚举 31 键与 `jmhSuiteVerify` 对账与基线逐行/逐项一致；`grep -rn "BenchSuite" build.gradle settings.gradle gradle buildSrc/src tny-*/build.gradle docs .github openspec/config.yaml openspec/specs` 大小写敏感现行面零命中（`BenchmarkSuite`、`benchmarkSuite`、`benchSuite` 字样与新名及前批形态互不混判，历史变更名连字符形态不在判据内）；`./gradlew clean build` 全绿——偶红按登记标准处置留痕。全部结论记 verification-notes.md。
