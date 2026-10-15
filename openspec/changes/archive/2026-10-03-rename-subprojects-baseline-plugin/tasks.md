# Tasks

## 1. 改名实施
- [x] 1.1 tny.subprojects-baseline.gradle 移名为 tny.dependency-management.gradle；根脚本装配线引入行、自身头注释（含"基线"措辞）、tny.plugin-module 与 tny.java-module 头注释指称同步更新；`./gradlew projects -q` 通过。

## 2. 零差异验收
- [x] 2.1 实施前抓基线：`./gradlew tasks --all --console=plain | sed '/^BUILD SUCCESSFUL in /d'` 的有序任务清单段（构建耗时尾行每次不同，比对范围限定清单段，与同批变更口径统一）存本变更目录 baseline/（账本内 merge-demo-isolation 归档仅存两份 dry-run 清单、无 tasks-all 样本，立项时引用有误，2026-10-03 核查更正为自抓同型）；改名后复跑同式对本基线逐行 diff 零差异；`./gradlew clean build` 全绿；grep 活代码 tny.subprojects-baseline 零命中；结论记 verification-notes.md。
