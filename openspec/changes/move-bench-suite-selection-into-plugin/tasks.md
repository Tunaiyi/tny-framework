# Tasks

## 1. 基线抓样

- [ ] 1.1 抓基线存本变更目录 baseline/（捕获口径：JDK 钉 Corretto 21、正常 locale、不注入 JAVA_TOOL_OPTIONS、剔 daemon 噪声行）：`./gradlew :tny-benchmark:benchRoutineExport -PbenchScope=quick` 速览实跑键集与计数；`./gradlew :tny-benchmark:jmhList -PbenchAll` 全量枚举 31 键；`./gradlew :tny-benchmark:jmh -PbenchInclude='.*Smoke.*' -PbenchFast` 定向快档实跑（选择生效与产物路径记录）；`./gradlew tasks --all` 剔噪清单段；`wc -l` 两文件行数。

## 2. 收编实施

- [ ] 2.1 `tny.benchmark-module.gradle` 在既有 benchParams afterEvaluate 段**之前**新增规模选择段：`familyInclude` 拼接、`benchAll`/`benchInclude`/`benchScope-quick` 三分支与缺省档（含六臂参数域）、`benchGc`、`benchFast` 逐字迁入（D1 顺序、D3 不压缩），模块原注释的来由说明随行；插件头注释边界句"jmh 执行参数与依赖声明在模块构建文件"改写为"静态参数与依赖声明在模块，执行面规模选择在插件 afterEvaluate"。`tny-benchmark/build.gradle` 的 jmh 块删除控制流段只留静态参数。验证：`./gradlew projects -q` 通过；`wc -l` 模块 ≤80、插件 ≤250。

## 3. 零差异验收

- [ ] 3.1 复跑比对：速览实跑键集与计数对基线逐键一致；`-PbenchAll` 枚举逐行一致；定向快档探针行为一致（选中集与产物生成）；`tasks --all` 剔噪清单段零差异；`./gradlew clean build` 全绿——CollectionLockTest 等登记偶红按 fix-ci-unit-flakes 标准处置留痕。全部结论记 verification-notes.md。
