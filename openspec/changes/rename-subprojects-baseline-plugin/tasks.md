# Tasks

## 1. 改名实施
- [ ] 1.1 tny.subprojects-baseline.gradle 移名为 tny.dependency-management.gradle；根脚本装配线引入行、自身头注释（含"基线"措辞）、tny.plugin-module 与 tny.java-module 头注释指称同步更新；`./gradlew projects -q` 通过。

## 2. 零差异验收
- [ ] 2.1 `./gradlew clean build` 全绿；`./gradlew tasks --all` 对 openspec/changes/archive/2026-10-03-merge-demo-isolation-into-integration-test 基线（该变更 baseline 即最新形态）零差异；grep 活代码 tny.subprojects-baseline 零命中；结论记 verification-notes.md。
