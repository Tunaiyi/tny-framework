# Design

## Context
动机见 proposal.md - Why。事实核对：门禁现状覆盖面＝javaProjects 装配线行（根 build.gradle 第 64 行）+ tny-game-bom plugins{}；tny.publish 覆盖面另含 gradleProjects（经 tny.plugin-module 头部）。两文件重复的谓词体：三个任务名等值判断 + startsWith('publish') && !endsWith('ToMavenLocal')（逐字相同，仅闭包名不同）。合并体量 36+150−重复段≈176 行。

爆炸半径核查（规则要求，惯例记录）：codegraph 不覆盖构建脚本；受影响行为面恰一处——tny-game-doc-gradle 的任务图与发布拒绝语义（用户批准的向规格收紧）；java 线与 BOM 面逐字节不变。

两卷检索（M1）：合并后单文件双职责段（属性断言/一致性门禁）各自分节注释，同 tny.integration-test 闸门段的既有形态先例。

## Goals / Non-Goals
Goals：一个主题一个文件；重复谓词根除；doc-gradle 补上 release-versioning 本已要求的拒绝语义。Non-Goals：不改任何断言逻辑与文案（迁移逐行保留，仅谓词合流）；不动 publications/central/publish-plugin 退役件；不处理 checkPublishPrerequisites 对 doc-gradle 的 check 任务依赖新增（java 线由发布链保证 check 存在，doc-gradle 有 java-gradle-plugin 亦存在，无缺件风险，验收覆盖）。

## Decisions
**D1 合并方向选"gate 并入 publish"而非反向。** tny.publish 是三条线共同应用的底座（java/插件/BOM 全经过它），门禁并入即自动覆盖三条线；反向合并需要给 gradleProjects 补应用新 id，改线不改文件。依据：改动面最小、覆盖语义即需求。被否决备选：新建 tny.publish-safety 第三个 id——否决理由：为合并再造一层命名。
**D2 谓词合流为局部 def，两处挂接共用。** 迁移后端内单一 `def publishesToSharedRepository = { ... }`（保留现注释含 D7 历史），属性断言的 tasks.matching 与门禁挂接共用之；两文件历史差异（publish 版曾在 D7 前用 startsWith('publishToMavenLocal') 旧语义）已消一，逐字核对确认现存两份一致后合流（实施第一步 diff 两谓词，若有任何语义差异立即停下回用户处）。
**D3 doc-gradle 覆盖为向规格对齐，验收按"允许差异清单"执行。** tasks --all 差异预期为 tny-game-doc-gradle:checkPublishPrerequisites 一行及其挂接不改变其它任务名（挂接是既有 publish 任务的依赖边，dry-run 清单核对边名不新增 java/BOM 侧）；差异超出预期清单即停。

## Risks / Trade-offs
- [doc-gradle 的 validatePublishConsistency 消费 publishing.repositories 与 git 派生值，其行为此前未经门禁检验] → dev 线实跑断言必须绿（版本 5.7.x-SNAPSHOT 与开发线形态匹配），探针覆盖。
- [合并后 176 行接近半界，未来加断言仍有余量] → 若超界按主账本需求七再拆，不预先设计。

## Migration Plan
前置 diff 两谓词（D2）→ 迁移与合流 → 四处引用收编 + gate 文件退役 → 验收（允许差异清单逐行核对 + 零差异项 + 三工程实断言）。回滚五文件还原。
