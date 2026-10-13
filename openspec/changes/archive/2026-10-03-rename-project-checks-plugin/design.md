# Design

## Context

动机见 proposal.md - Why。实施前事实核对（2026-10-03 现场）：插件文件 51 行、头注释自述"本插件负责且仅负责两项校验"——发布构件组号对账与零发布合同，实现均为 `gradle.projectsEvaluated` 配置阶段闭包内的程序性对账（违例收集后统一报红），**无扩展、无任务注册、无属性导出**；根 build.gradle 第 41 行引入注释与第 42 行应用行是该插件在装配层的唯一现场，41 行注释不含插件 id 不随改。内容级旧 id 指称经全仓 grep `tny\.project-checks` 定界仅一处（根 build.gradle 应用行），插件自身的现行关联只有文件名；头注释第 1 行"前身为 gradle/project-checks.gradle"是历史路径字样（不含 tny 前缀、不属该判据命中面），随改名原样保留。

## Goals / Non-Goals

Goals：插件 id 见名知义（module-checker 直陈"检查构件模块者"）；对账行为逐字节不变。Non-Goals：不改本文件第 4、46 行对 `tny.module-modes` 的指称（那是 rename-module-modes-plugin 的收编面）；不改对账逻辑与报红文案结构；不回改历史归档与 HANDOFF 记录面。

## Decisions

**D1 落点即文件移名与唯一应用行。** 无扩展、无任务名、无属性键——插件的对外标识面只有 id 一处，改名影响面构造性收敛到两行（文件名、根应用行）——内容级旧 id 指称仅根应用行一处，这是本批改名变更中影响面最小的一桩。
**D2 头注释补命名来由一行、既有职责与边界句不动。** "前身为 gradle/project-checks.gradle"历史路径指称按历史记录原则保留；新增一行以"工程标识名 tny.module-checker 指向本文件内容重心——构件模块两条硬合同的配置期对账"句式交代命名（与 rename-subprojects-baseline-plugin 先例同型）。
**D3 与 rename-module-modes-plugin 的交叉按现场处理。** 两变更共编本文件（该变更改第 4、46 行指称、本变更移动文件名），无强先后：本变更先落地则该变更在 `tny.module-checker.gradle` 上操作；rename-module-modes-plugin 先落地则本变更移动已改指称后的文件。各自 tasks 已写明按实施时现场文本执行，无静默冲突面。

## Risks / Trade-offs

- 风险：改名后 `tasks --all` 出现非预期差异。缓解：该插件无任务注册，差异无从产生；判据仍按剔噪逐行零差异执行，一旦超出预期差异即停止实施、回到用户处请示。
- 风险：报红文案含"声明处：tny.module-modes enableUnpublished"字样，若 rename-module-modes-plugin 先行落地、文案已更新，本变更实施者误判为漂移。缓解：D3 已声明该行为对方收编面，本变更不触碰。
- Trade-off："module-checker" 与既有 "module-modes、module-setting" 词族相近，读者可能联想两者相关——实际一个声明角色、一个核对合同，确属同一"模块横切治理"主题的两翼，词族相邻反而助记。

## Migration Plan

第一步，抓基线（剔噪 `tasks --all` 清单段）。第二步，git mv 移名、根应用行随改、头注释补命名来由一行。第三步，验收（评估、零差异、grep 清零、全量构建绿），记 verification-notes.md。回滚为两文件原位还原。
