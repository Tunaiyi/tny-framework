# Proposal

## Why

并行会话已把 Gradle wrapper 从 8.5 升到 8.14.5（工作区未提交、无立册），本次治理链两册的实测把该升级的账算清了：升级引入了六条构建期弃用告警（五条"配置在锁定后被改写"与一条"LenientConfiguration.getArtifacts 弃用"，已用纯净克隆对照坐实为升级引出的存量形态而非册改动引入），同时 io.spring.dependency-management 停在 1.1.0，其对新 Gradle 的兼容修复全在 1.1.5 至 1.1.7。这六条告警在 Gradle 9 会转为构建失败——不立册处置，工具链升级就停留在"有人手动改了一行、账本不知"的高危状态；本册把升级决定补立为账、按零告警与发布元数据零漂移两条线完成适配。

## What Changes

- 把 wrapper 8.5 到 8.14.5 的升级追认为本册决策：`gradle/wrapper/gradle-wrapper.properties` 的工作区改动随本册提交，成为有验收判据的基线变更。
- io.spring.dependency-management 从 1.1.0 升到 1.1.7（补丁线，声明于根 `build.gradle` plugins 块）：这是六条告警处置的前置——1.1.5 起修复的正是 Gradle 8.8 以上环境的兼容假设。
- 排查并清零五条"配置锁定后被改写"告警：现象为 starter-basics 与 starter-net-netty4 的 api、compileOnly、testImplementation 等基配置在子配置解析后被施加排除规则（根因指向 `tny.java-module.gradle` 的 `configurations.configureEach` 排除块与依赖物化时序的交互，实验证伪过引擎边收缩、java 扩展块改写、对账守卫三个嫌疑）；先以最小实验定位触发时序，再把排除声明改到不迟于配置锁定的形态，逐工程复跑告警计数为验收。
- 清零一条 `LenientConfiguration.getArtifacts(Spec)` 告警：`tny.java-module.gradle` 的 `downloadDependencySources` 任务用 `copy { from 独立配置 }` 解析源码包，改为弃用指引的 artifactView 形态（行为保持：缺源码包仍记为 missing 跳过）。
- 两条 `:tny-benchmark:detachedConfiguration` 非工程上下文解析告警显式移交：归属并行会话在途的基准线（该文件此刻仍被编辑），本册不触碰 `tny-benchmark/`，在移交记录落档后从全仓清零判据中剔除。
- 全仓验收：`--warning-mode all` 下上述告警清零（移交件除外）、发布 POM 与 Gradle 模块元数据对升级前基线零漂移（按 openspec/config.yaml 零差异验收基线抓样口径）、配置期对账守卫与发布门禁行为回归、`./gradlew clean build` 全绿。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `gradle-build-style`：新增需求"构建工具基线升级与工具链告警归属成册"——构建工具版本变更必须以变更册承载验收；在某构建工具版本下出现的工具链兼容告警，MUST 在承载该版本升级的册内清零或书面移交归属，MUST NOT 以"默认输出不显示"为由静默积累。

## Impact

- 文件：`gradle/wrapper/gradle-wrapper.properties`（追认）、根 `build.gradle`（dependency-management 版本行）、`buildSrc/src/main/groovy/tny.java-module.gradle`（排除块时序改造＋downloadDependencySources 改写；该文件有并行会话在途改动的历史，实施前须先拉齐其已入库状态）。
- 受影响构建行为：全仓所有 java 线模块的排除规则施加时机（语义不变：spring-boot-starter-logging 与 log4j-to-slf4j 仍被全配置排除）；downloadDependencySources 任务（只读下载辅助任务，不入发布与门禁）。
- 下游与 starter：无公共 API 变化（不涉源码与 POM 依赖条目，验收线"发布元数据零漂移"即其保证）；受影响下游模块与对应 starter 清单因此为空。
- 关联既有判定：治理册归档卷 verification-notes 的环境注记、六告警归因实验（含 worktree jgit 弯路记录）与"升 1.1.7 为 Gradle 升级前置"的登记，是本册判据的出处；wrapper 升级后 8.14.5 的 fork JVM CDS 与 native-access 输出属 Gradle 发行版与 JDK 组合的环境噪声，不入库内整改。
- 风险面：排除规则时序改造若触碰配置锁定语义，可能改变发布 POM 的排除条目集合——以发布元数据零漂移判据兜住；若定位后改造代价超出本册，按规格新需求走书面移交回用户裁决，不带伤合入。
