# Design

## Context

动机见 proposal.md - Why。现场核对（2026-10-03）：`tny-game-integration-test/build.gradle` 85 行，dependencies 块末段为待迁循环——注释两行（"被测栈与既有测试夹具同步"来由）加"moduleProjects 中 java 形态者整体 add integrationImplementation"的 each 五行区。`tny.integration-test.gradle`（152 行）有两类宿主：根脚本 javaProjects 装配线（各发布模块也应用本插件获得两级验证通道）与本模块 plugins 块自装——收编段必须只对后者生效，故守卫取 `-integration-test` 后缀类别谓词（settings 权威注释定义，需求九认可通道）。

## Goals / Non-Goals

Goals：模块回到 80 行内、配置阶段循环清零、夹具挂接集合语义不变。Non-Goals：不改挂接集合定义（仍为"装配线 java 形态模块整体"）；不动插件其余段（integration sourceSet、两级通道、隔离撮合、docker 属性面）；不给 javaProjects 宿主的 integration 配置新增任何依赖（守卫段整段跳过）。

## Decisions

**D1 落点=插件文件末尾新增 afterEvaluate 段，守卫 `project.name.endsWith('-integration-test')`。** 后缀谓词是命名约定通道而非工程名枚举；afterEvaluate 时点使被判定方的评估状态最全（`hasPlugin('java')` 的信息完整度高于原配置阶段时点）。
**D2 时点修正的诚实边界与停止条款。** 原实现求值于本模块配置阶段，彼时多数 moduleProjects 尚未评估，命中集合依赖配置/按需激活顺序（本仓开 configuration-on-demand）；迁入 afterEvaluate 后判定信息更全，挂接集合可能"持平或补全原漏挂"。判据为 `integrationImplementation` 与 `integrationRuntimeClasspath` 两解析集 before/after 逐行零差异——**diff 非空即停回用户裁决，无论方向**："补全漏挂"属行为变化，不并入标识收编册静默落地。
**D3 形态逐字不压缩。** each 结构与两行来由注释原样迁入（与 bench 册 D3 同纪律）；压缩形态或改用 findAll 单行皆另册。

## Risks / Trade-offs

- 风险：`moduleProjects` ext 可读性——根 ext 于配置最早段定义，插件 afterEvaluate 恒可读（java-module 夹具挂接同款先例）。
- 风险：javaProjects 宿主误触发挂接——后缀守卫整段跳过，且有解析集零判据兜底。
- 权衡：为守零差异红线接受"若 D2 停机则本册回炉重新定范围"的可能，不赌侥幸。

## Migration Plan

第一步，抓基线（两解析集、dry-run、剔噪清单、行数）。第二步，插件加段、模块删段、头注释补句。第三步，验收（解析集零差异第一判据），记 verification-notes.md。回滚为两段还原。
