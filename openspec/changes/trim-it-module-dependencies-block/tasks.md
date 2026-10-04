# Tasks

## 1. 基线抓样

- [ ] 1.1 抓基线存本变更目录 baseline/（捕获口径：JDK 钉 Corretto 21、正常 locale、不注入 JAVA_TOOL_OPTIONS、剔 daemon 噪声行）：`./gradlew :tny-game-integration-test:dependencies --configuration integrationImplementation` 解析集；`./gradlew :tny-game-integration-test:dependencies --configuration integrationRuntimeClasspath` 解析集；`./gradlew :tny-game-integration-test:integrationTest --dry-run` 任务图；`./gradlew tasks --all` 剔噪清单段；`wc -l` 两文件行数。

## 2. 收编实施

- [ ] 2.1 `tny.integration-test.gradle` 末尾新增 afterEvaluate 段——守卫 `project.name.endsWith('-integration-test')`（D1），each 循环与来由注释逐字迁入（D3）；插件头注释职责段补"集成测试专用模块的装配线夹具整体挂接"一句。`tny-game-integration-test/build.gradle` 删除该注释与循环区段。验证：`./gradlew projects -q` 通过；`wc -l` 模块文件 ≤80、插件文件 ≤250。

## 3. 零差异验收

- [ ] 3.1 复跑比对：两解析集对 1.1 基线**逐行零差异（第一判据；diff 非空无论方向即停回用户裁决，不静默吸收"时点修正补全漏挂"的行为变化，D2）**；`integrationTest --dry-run` 任务图一致；`tasks --all` 剔噪清单段零差异；`./gradlew clean build` 全绿——CollectionLockTest 等登记偶红按 fix-ci-unit-flakes 标准处置留痕。全部结论记 verification-notes.md。
