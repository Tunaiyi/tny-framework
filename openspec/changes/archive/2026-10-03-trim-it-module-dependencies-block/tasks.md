# Tasks

## 1. 基线抓样

- [x] 1.1 按 openspec/config.yaml context 中"零差异验收基线抓样口径"一条抓基线存本变更目录 baseline/（转存型样件先运行生成任务再转存；本步首抓与第 3.1 步复跑在同一 Gradle daemon、UTF-8 locale 确认钉住的环境下抓取）：`./gradlew :tny-game-integration-test:dependencies --configuration integrationImplementation` 控制台转存解析集；`./gradlew :tny-game-integration-test:dependencies --configuration integrationRuntimeClasspath` 控制台转存解析集；`./gradlew :tny-game-integration-test:integrationTest --dry-run` 任务图；`./gradlew tasks --all --console=plain` 剔噪清单段；`wc -l` 记录模块行数（85）、dependencies 块行界（第 42 至 85 行）、catalog 行数（115），并转存 `grep -n` 的六个 bundle 成员键现状定位（design Context 所列六处）。

## 2. 收编实施

- [x] 2.1 `gradle/libs.versions.toml` 在 `[libraries]` 节之后新增 `[bundles]` 节：`containerStack = [{ alias = "springBootStarter" }, { alias = "springBootAutoconfigure" }, { alias = "springBootStarterLog4j2" }, { alias = "log4jCore" }, { alias = "testcontainersJunit" }, { alias = "testcontainersMongodb" }]`，节上方写集中来由注释（收拢模块原第 74 至 75 行"官方 log4j2 集成替代手工拼装"与第 79 至 80 行"损坏帧告警捕获需真实后端"两组语义）；既有各节零触碰。`tny-game-integration-test/build.gradle` 的 dependencies 块：第 72、73、76、77、78、81 行六项语句与其两组随行注释替换为一条 `integrationImplementation libs.containerStack` 加一行指回 bundle 集中注释的说明句；第 62 至 63 行 P3 档公共来由注释与八条 `project(':...')` 语句原位不动；第 82、83 行 log4jApi、log4jSlf4j2Impl 留位并在其旁补一行说明绑定族另一半与 bundle 内 log4jCore 的同源来由。验证：`./gradlew projects -q` 通过（bundle 访问器拼写与形态的常驻探测；若 Gradle 实际生成的访问器名与 `libs.containerStack` 不符，以编译器报红给出的正确访问器名落地并记 verification-notes）；`wc -l` 模块 ≤80、catalog 增行后自身仍远小于任何界线。

## 3. 零差异验收

- [x] 3.1 复跑比对（与 1.1 同一 daemon、同一 locale 口径）：`integrationImplementation` 与 `integrationRuntimeClasspath` 两解析集对基线**逐行零差异（第一判据；diff 非空无论方向即停回用户裁决，不静默吸收——bundle 展开坐标集与移除的六条语句坐标逐项一致，design D3）**；`integrationTest --dry-run` 任务图一致；`tasks --all` 剔噪清单段零差异；`./gradlew clean build` 全绿——CollectionLockTest 等登记偶红按 fix-ci-unit-flakes 标准处置留痕。全部结论记 verification-notes.md。
