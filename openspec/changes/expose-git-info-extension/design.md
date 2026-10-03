# Design

## Context
动机见 proposal.md - Why。现状消费点（grep 实证）：tny.release 的 `def git = rootProject.ext` 与 git.branchName/git.grgiter/git.commitId/git.parseBranchVersion/git.SNAPSHOT_PACK_SUFFIX/git.RELEASE_VERSION_SUFFIX 等；tny.publications 与 tny.dependency-management、tny.central 各一处 rootProject.ext 键读取。tny.git 导出面十九键（常量 3、句柄 1、闭包 8、派生值 6 上下）。

爆炸半径核查：codegraph 不覆盖构建脚本（惯例记录）；行为面锚点=项目版本字符串（jar 文件名/POM version）、dryRun 文案、centralCheck 报红、组号对账——四者构成验收探针集。

两卷检索（M1）：消费方按类型向根工程拉取协作对象的形状是框架运行时既有先例（@UnitInterface 扫描装配）的构建期对应；"统一插件调用"不发明新机制。

## Goals / Non-Goals
Goals：跨插件契约类型化、可见（getByType 即文档）、失败早爆；ext 字典零残留（git 派生面）。Non-Goals：不并文件（250 行界线与职责分离双理由）；不动根 ext 的身份与成员事实（projectGroup/集合派生属第九条需求的根声明面）；不动 publish-gate 的 memo 缓存键（性质是缓存不是契约）。

## Decisions
**D1 扩展挂根工程、类置 buildSrc、字段动态类型。** 消费者统一 `rootProject.extensions.getByType(tny.convention.GitInfo)`；GitInfo 的 grgiter 字段用 Groovy 动态类型——buildSrc 编译期零第三方 classpath（与 adopt 变更 D9 的"buildSrc 不引插件栈"约束一致），运行时类加载器可达 grgit 已被根脚本先行应用保证。被否决备选：类型化 Grgit 字段——否决理由：迫使 buildSrc 声明 grgit 依赖，双处版本维护正是需求三要消灭的形态。
**D2 推导逻辑逐行迁移，不顺手重写。** parseBranchVersion/gitTag 等闭包原样转方法，其实测教训注释（describe 无负向锚、过渡期已知局限）随行——行为等值以版本字符串与 dryRun 文案比对兜底，重写不在本变更半径。被否决备选：借机把读操作迁回 grgit 强类型 API——那是 migrate-git-calls-to-grgit 的既有收口范围，不重开。
**D3 失败形态变化如实声明。** 原先在 tny.git 未加载时 ext 裸取返回 null/缺键、深处才炸；改后 getByType 立即抛"扩展不存在"。属可观测性增强，验收记录两处文案差异即可，不视为回归。

## Risks / Trade-offs
- [release 的 git.grgiter 深链调用较多，漏改一处编译/评估即红（动态属性不存在错误早爆）] → 与 D3 同理，风险自暴露。
- [Groovy 动态字段失去类型检查] → 契约靠单一写入方（tny.git）+ 探针面兜底；类型化的收益在"取扩展失败即错"这一层已实现。
- [两变更顺序颠倒实施] → tasks 首步声明前置检查（依赖 rename 完成）。

## Migration Plan
前置确认 rename 完成 → GitInfo 类 + tny.git 重写 → 四消费者切换 + ext 字典删除 → 探针集验收（版本字符串/dryRun 文案/centralCheck/组号对账/tasks/m2/POM 零差异）。回滚六文件整体还原。
