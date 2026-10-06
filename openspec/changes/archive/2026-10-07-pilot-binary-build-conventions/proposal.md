# Proposal

## Why

用户裁决评估把 buildSrc 从 17 个预编译 Groovy 脚本插件迁移到 Conventions + Plugin + Extension 的二进制实现形态（探索工作流已完成可行性审查）。结论：方向可行，且既有"约定插件互不 apply"约束经取证证实是 Gradle 8.5 脚本插件类加载器缺陷的绕行方案而非价值观，二进制形态下自然消亡；但收益必须诚实改口——编写态文件总量不减反增（17 脚本＋4 支撑类换为约 27 个实现类再叠加测试类），真实价值是**把无法测试的配置期对账与门禁逻辑变成 ProjectBuilder 可单测的检查类**、类型安全、根脚本装配编排收敛与字面量单点化。对抗审查同时判定：不得整批推进，第一刀只做最小试点，且开工前必须先修规格账本（审查草案点名的编号错位与"区块顺序"判据缺位两处硬伤在本册修正后执行）。

本变更承载：规格开道（批次 0，修正版）＋ Gradle 8.14.5 双探针（把最贵的架构问题前置验证，不押在末批）＋ 试点批次 1（一个域的二进制化，证明机制、不证明放行）。

## What Changes

- **规格开道**：`gradle-build-style` 能力按现文逐条对号修订八条需求——需求一（程序性行为容身之处扩至二进制实现类；"共享配置块收编为可组合的共享约定插件"的装配形态更新）、需求二（二进制实现内同等禁用立即创建与按名点名求值的对应 API）、需求三（允许实现内经 VersionCatalogsExtension 按别名取用，继续禁止手写坐标字符串）、需求五（Groovy 词法纪律显式限定只约束脚本文本，实现类受项目 Java 纪律约束）、需求六（新增二进制类的区块顺序判据：装配方法按脚本区块同序组织）、需求七（文件头块注释并列类 javadoc 同等义务）、需求八（250 行界线沿用至实现单元；"三十秒三问"对跨类分散行为的替代判据：总装配类头注释 MUST 提供该装配单元行为的清单式总览）、需求十（"共享构建脚本"合称扩为共享构建代码含二进制实现）；新增一条需求"二进制实现的检查逻辑必须携带单元测试"。`CLAUDE.md` 执行要点第二句同步改写（二进制约定插件之间允许相互 apply 并以 plugins.withType 反应式接线；过渡期内预编译脚本插件之间维持不互 apply）。
- **双探针（8.14.5 一次性沙箱，不入仓库）**：其一，复测脚本插件嵌套应用的 ClassLoaderScope 行为（三处"实测 Gradle 8.5"注记的结论不得未经复测沿用）；其二，二进制实现内以 id 字符串运行时 apply 第三方插件（io.spring.dependency-management、com.gradleup.nmcp）是否受 buildSrc 作用域限制——探针失败则后续总装配册降级为"根脚本保留第三方引入行"形态。探针结论记入本册 design.md 并反哺规格措辞。
- **试点批次**：buildSrc 补建 java-gradle-plugin 注册块、插件元数据与 JUnit/ProjectBuilder 测试基建；新增 `tny.projects` 类型化扩展（收敛根脚本 ext 五键与四处重复的后缀字面量判定）；`tny.module-checker` 二进制化为首个样板（同名 id 薄替换、三项对账拆为可单测检查类、删除其脚本文件——同一 id 两形态不并存）；`tny.git` 与 `tny.module-setting` 两个既有类型化扩展零改动（本就是目标形态样板）。
- **零差异与放行边界**：验收基线沿用既有用词（任务图、依赖清单、双 POM 零差异，buildSrc 自身生命周期行按前缀过滤先例）；对账红绿判据以破坏探针复验（临时改组号属性、临时给零发布模块开发布，仍须报红）。本变更明文：试点通过仅证明机制可行，**不放行**模块装配、发布线、总装配等后续批次（各自另立册，且发布线批次以在途 redesign-devline-integration-model 收口为硬闸门）。
- 无 **BREAKING**：全部模块 build.gradle、任务名、发布产物坐标零变化。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `gradle-build-style`：八条需求文本按二进制形态扩写判据（逐条见 What Changes），新增一条"二进制实现的检查逻辑必须携带单元测试"需求；既有全部场景保留，新增脚本-二进制双形态判据场景。

## Impact

- 规格与规则：`openspec/specs/gradle-build-style/spec.md`（经本变更差量归档落地）、仓库根 `CLAUDE.md` 执行要点第二句。
- buildSrc：`buildSrc/build.gradle`（注册块与测试依赖）、新增 `src/main/groovy|java` 下的 tny.projects 扩展与 tny.module-checker 二进制实现、新增 `buildSrc/src/test/` 首批单测、删除 `buildSrc/src/main/groovy/tny.module-checker.gradle`。
- 根构建脚本：`build.gradle` 第 25 至 39 行 ext 五键块删除、改为一行 `apply plugin: 'tny.projects'` 与派生集合读取（约减 15 行）；经全仓 grep 复核的消费方为六个插件脚本共九行代码级引用（`tny.bom-platform`、`tny.java-module`、`tny.dependency-management`、`tny.central`、`tny.integration-test`、`tny.module-checker`，后者随二进制化在类内读取）与根脚本自身两行 `configure(...)`，另含一处模块构建文件对组号标量键的裸名消费（`tny-game-doc-gradle/build.gradle:3`，apply 阶段复扫补入），后缀字面量重复判定五处改读扩展谓词（详单与保持不动项见 design.md 决策 D4）；`tny.plugin-module` 与 `tny.publications` 仅在注释中提及键名，本批改注释措辞即可，其余插件脚本本批不动。
- CI：`build.yml` 增加 buildSrc 测试执行档（试点单元纳入 check 流水线）。
- 在途变更协调：与 `redesign-devline-integration-model`、`consolidate-git-queries-into-gitflow` 零文件重叠约束已核（本批不碰发布线、git 编排与 integrate 文件）；本变更归档不阻塞两者。
- 下游与发布产物：零影响（零差异验收兜底）。
