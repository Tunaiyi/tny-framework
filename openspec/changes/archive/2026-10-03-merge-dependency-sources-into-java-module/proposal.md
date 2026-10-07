# Proposal

## Why

tny.dependency-sources 是一个只注册单个任务（downloadDependencySources，把类路径依赖的 -sources 包批量下载到 build/dependency-sources）的 51 行约定插件，且只在根脚本 java 发布模块线的装配块应用——与 tny.java-module 的应用面完全重合。为一条装配线上的一个开发便利任务单独设立插件 id 与一行应用语句没有承载任何边界价值；而该任务的 group 名"dependency management"虽易让人联想依赖管理声明插件 tny.dependency-management（前名 tny.subprojects-baseline），并入那里会把任务成员扩大到全部子工程或在共享插件内耦合 java 线装配判定，两条路都会产生任务图差异。立项时经用户裁决，合并落点定为同线的装配插件 tny.java-module：任务成员由落点本身保证不变，行为面零差异（裁决记录见 design D1）。

## What Changes

- tny.dependency-sources.gradle 的 downloadDependencySources 任务块逐行迁入 tny.java-module.gradle，任务逻辑一字不改；接收文件头注释的职责段补一句本任务说明，兄弟插件清单删去 tny.dependency-sources 一项。
- tny.dependency-sources.gradle 退役（隔离目录法，与 merge-publish-gate-into-publish 变更的既有退役手法同型）；根脚本 configure(javaProjects) 装配块删除该插件的应用行。
- 除插件 id 与装配行消失外，任务名、任务组、所属工程集合与执行行为全部不变。

## Capabilities

### New Capabilities / Modified Capabilities

无。插件 id 收编与任务代码迁移属实现标识变更，downloadDependencySources 的任务名与行为均不变，不涉及任何规格条文与外部行为。skip_specs。

## Impact

- **受影响文件**：tny.java-module.gradle（吸收任务块与头注释收编）、tny.dependency-sources.gradle（退役）、根 build.gradle（装配线删一行）。活代码对该插件 id 的指称共三处（根脚本应用行、tny.java-module 头注释兄弟清单、文件自身），逐处消化后 grep 清零属验收项。
- **前置依赖**：按用户裁决"先改名后合并"，本变更在 rename-subprojects-baseline-plugin 变更实施归档之后实施；两变更都会编辑 tny.java-module 头注释，但落在不同行（改名变更改"边界"段的旧插件名指称，本变更改"兄弟插件清单"行），先后实施互不冲突。与 merge-publish-gate-into-publish 变更有两处相交——根 build.gradle 的 javaProjects 装配块（各删一相邻不同行：本变更删 tny.dependency-sources 应用行，对方删 tny.publish-gate 应用行）与 tny.java-module 头注释兄弟插件清单行（对方按其验收 grep 须清理该行的 tny.publish-gate 指称，本 design D2 已备按实施现场文本调整的预案）；两处编辑语义互不干扰、先后皆可实施，除此之外文件不重叠，两变更无顺序约束。
- **验收基线**：tasks --all 与 :tny-game-net:downloadDependencySources 实跑输出对迁移前基线零差异；全量构建绿。插件 id 不出现在任何任务名与发布产物面，预期除装配行外无任何可见差异。历史归档与 verification-notes 中的旧名指称不回改（历史记录原则）。
