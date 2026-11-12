# Tasks

## 1. 基线抓样（改造前）

- [x] 1.1 抓两份 dry-run 有序任务清单存变更目录 baseline/：`./gradlew :tny-game-integration-test:integrationTest --dry-run` 与 `./gradlew :tny-game-net:test --dry-run`；记录 `./gradlew :tny-game-integration-test:integrationTest --rerun` 全绿耗时。

## 2. 合并实施（design D1-D3）

- [x] 2.1 tny.integration-test 尾部追加闸门段：`if (project.name.endsWith('-integration-test')) { ... }` 内整体迁入 tny.demo-isolation 的接线（注释分节、指回 settings 命名约定权威定义；接线体逐行不改）；文件头职责/边界注释同步更新。
- [x] 2.2 tny-game-integration-test/build.gradle 删除 `id 'tny.demo-isolation'` 引入行及其顺序约束注释；tny.demo-isolation.gradle 移入 /tmp/gradle-retired-quarantine/；`./gradlew projects -q` 通过。

## 3. 验收（零差异与防线）

- [x] 3.1 任务图逐行比对：重复 1.1 两份 dry-run 与基线 diff 零差异（集成测试模块与普通模块两侧都验；出现任何差异即回退本变更不合并，按 design D3 执行）。
- [x] 3.2 防线重放：摘 tny-game-net-demo 的 enableApp 声明 → `:tny-game-integration-test:integrationTest` 执行前报红（两成因文案）→ 恢复绿、文件级一致；`--rerun` 全绿。
- [x] 3.3 零差异收尾：`./gradlew clean build` 全绿；tasks --all 对 consolidate 归档基线零差异；grep 全仓 tny.demo-isolation 零引用（历史注记除外）；全部结论记入 verification-notes.md。
