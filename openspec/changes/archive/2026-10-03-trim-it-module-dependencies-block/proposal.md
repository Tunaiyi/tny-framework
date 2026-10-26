# Proposal

## Why

`tny-game-integration-test/build.gradle` 实测 85 行，超 gradle-build-style 需求七的模块 80 行界线。本册前身（move-it-fixture-loop-into-plugin）把超行主因记为"dependencies 块末段滞留的配置阶段 each 循环"，该记载经口径审计工作流现场复核证伪——该循环从未进入过这个文件的 git 历史（归档审计册已落勘误）。重定后的事实归因：超行来自 dependencies 块（第 42 至 85 行共 44 行）里集成夹具依赖面的逐次正当增长，块内全部是声明式语句与来由注释，无任何程序性控制流，需求一并不违例；唯一违例是需求七的行数界线。界线要回到 80 以内，可收形的来源是块内的外部库坐标群：容器与全栈夹具用到的十项外部库（JUnit、Mockito、Spring Boot、log4j2 族、Testcontainers 族）各自单行书写并各带来由注释，而版本目录本身具备 bundles 分组形态（catalog 现未使用），把它们收编为一个目录内 bundle 后，模块只写一行挂接语句，行数与逐条噪音注释一次消化；工程间 `project(':...')` 依赖按需求九的装配线事实留在模块，不强行下沉——把 `libs.*` 坐标整体搬进 buildSrc 插件会迫使外部坐标脱离版本目录唯一事实源，属新增违例，故排除该路线。

## What Changes

- `gradle/libs.versions.toml` 新增 `[bundles]` 节（catalog 首次使用分组形态），定义 `containerStack` bundle：收编 springBootStarter、springBootAutoconfigure、springBootStarterLog4j2、log4jCore、testcontainersJunit、testcontainersMongodb 六项容器/全栈夹具外部坐标；原有条目一律不动，bundle 只以 alias 引用。原分散在各语句旁的来由注释（log4j2 官方集成替代手工拼装、损坏帧告警捕获需真实后端）收拢为 bundle 定义上方一段集中注释。
- `tny-game-integration-test/build.gradle` 的 dependencies 块：P3 容器/全栈档与日志装配对应的六行外部库语句及其两组随行注释（log4j2 starter 注、log4j-core 注）替换为一条 `integrationImplementation libs.containerStack` 挂接语句加一行指回 bundle 集中注释的说明句；junitJupiter、mockitoJunitJupiter、log4jApi、log4jSlf4j2Impl 四项留在原位（夹具测试 API 与被测栈依赖混排、留位注释各有归属，收进同一 bundle 反而搅浑语义）；P3 档头部"全部挂 integration 配置"的公共来由注释留在原位覆盖工程依赖与 bundle 两条语句。实测口径净减八行（85 回到 77）。
- 行为红线：`integrationImplementation` 与 `integrationRuntimeClasspath` 的解析结果集 before/after 逐行零差异——bundle 展开后坐标集必须与移除的六条语句完全一致；若出现任何差异（无论方向），**即停回用户裁决**，不静默吸收。

## Capabilities

### New Capabilities / Modified Capabilities

无。gradle-build-style 条文零改动（本册按既有需求七修正行数形态）；integration-testing 行为契约不变（挂接坐标与配置名零变化）。skip_specs。

## Impact

- **受影响文件**：`gradle/libs.versions.toml`（新增 `[bundles]` 节与集中注释）、`tny-game-integration-test/build.gradle`（dependencies 块 −7 行区）。
- **顺序关系**：与并册 `move-bench-suite-selection-into-plugin`、`split-publish-consistency-closure`、`expose-git-info-extension` 及全部活跃册零文件交集；前批捕获口径沿用，本册基线按 openspec/config.yaml context 中"零差异验收基线抓样口径"一条执行（引用权威文本，不另立口径）。
- **验收基线**：实施前抓 `./gradlew :tny-game-integration-test:dependencies --configuration integrationImplementation` 与 `--configuration integrationRuntimeClasspath` 解析集、`integrationTest --dry-run` 任务图、`tasks --all` 剔噪清单段、模块文件行数与 dependencies 块行界记录；落地后两解析集逐行零差异（第一判据）、dry-run 一致、`tasks --all` 剔噪清单零差异、模块 ≤80 行、catalog 既有键零触碰、`projects -q` 与 `clean build` 全绿。
- **下游波及核对**：bundle 成员键均已在 `[libraries]` 中存在且被现树引用，新增节不改变任何既有访问器；根 settings 与 buildSrc settings 共用同一份 catalog 文件（buildSrc/settings.gradle 已有 `from(files('../gradle/libs.versions.toml'))` 只读视图先例），bundle 访问器对两侧同时可见，零配置改动。
