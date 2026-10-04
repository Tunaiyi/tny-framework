# Proposal

## Why

`tny-game-integration-test/build.gradle` 实测 85 行，超 gradle-build-style 需求七的模块 80 行界线；超行主因是其 dependencies 块末段的配置阶段 each 循环（"把装配线 java 形态模块整体挂 integrationImplementation"），该形态同时属需求一"配置阶段不得出现循环"的违例。修法是治本合一：循环收进 `tny.integration-test` 约定插件（程序性行为的合规容身处），行数与违例一次消化。同仓先例同型：bench 模块的 benchParams 解析循环已按 declarative-it-demo-isolation design D5 迁入插件，本变更是集成测试侧的同一收尾。

## What Changes

- `tny.integration-test.gradle` 末尾新增 afterEvaluate 段承载该循环，守卫谓词取 `-integration-test` 后缀命名约定（settings 权威注释定义的类别谓词，需求九认可通道；插件同时被 javaProjects 线应用，守卫确保夹具挂接只发生在集成测试专用模块自身，java 线各工程零波及）。
- `tny-game-integration-test/build.gradle` 删除循环与其两行注释（85 行回到 80 以内），"被测栈与既有测试夹具"的来由说明移入插件段注释随行。
- 插件头注释职责段补"集成夹具整体挂接"一句，边界句不动。
- 行为红线：`integrationImplementation` 与 `integrationRuntimeClasspath` 的解析结果集 before/after 零差异——若原实现的 `hasPlugin('java')` 判定依赖配置求值顺序的侥幸语义、迁入 afterEvaluate 时点修正后解析集出现差异，**即停回用户裁决**（时点修正对语义的影响不许静默吸收，见 design D2）。

## Capabilities

### New Capabilities / Modified Capabilities

无。gradle-build-style 与 integration-testing 条文零改动，本变更按既有需求一/需求七修正实现形态。skip_specs。

## Impact

- **受影响文件**：`buildSrc/src/main/groovy/tny.integration-test.gradle`（afterEvaluate 新增段 + 头注释一句）、`tny-game-integration-test/build.gradle`（−5 行区）。
- **顺序关系**：与并册 `move-bench-suite-selection-into-plugin` 无共同文件；与 `expose-git-info-extension` 等活跃册零交集。前批确立的捕获口径沿用（JDK 钉 Corretto 21、正常 locale、剔噪比对）。
- **验收基线**：实施前抓 `./gradlew :tny-game-integration-test:dependencies --configuration integrationImplementation` 与 `--configuration integrationRuntimeClasspath` 解析集、`integrationTest --dry-run` 任务图、行数与 `wc` 记录；落地后两解析集逐行零差异、dry-run 一致、`tasks --all` 剔噪清单零差异、`projects -q` 与 `clean build` 全绿。
