# Design

## Context

动机与用户裁决记录见 proposal.md - Why。实施前事实核对（2026-10-03 现场）：两插件正文——`tny.cache-policy.gradle` 为 configurations 域的 `resolutionStrategy.cacheChangingModulesFor 0` 与 `cacheDynamicVersionsFor 0` 两行；`tny.compile-encoding.gradle` 为 `tasks.withType(AbstractCompile)` 与 `tasks.withType(Javadoc)` 两段 configureEach 的 `options.encoding = encoding`（encoding 属性来自根 gradle.properties）。两插件在根脚本 gradleProjects（52、53 行）与 javaProjects（59、60 行）两块各成对应用；均不注册任务、不建扩展、不导属性。旧指称现场 grep 七处：根四行、`tny.plugin-module.gradle:4` 兄弟清单括注、`tny.java-module.gradle:5` 类别枚举括注、`tny.java-module.gradle:74` 编译参数段"编码由 tny.compile-encoding 共享块承担"注释。爆炸半径核查：codegraph 符号图不覆盖构建脚本；消费面经全仓引用扫描定界为上述七处加 BOM 线之外无第三应用点（configure(subprojects) 块不含这两件，bench 与 demo 不在装配线故自带编码声明，覆盖面不动）。

## Goals / Non-Goals

Goals：一个主题一个文件（编译基线两参数合一）；两条装配线各减一行；配置效果逐字节不变。Non-Goals：不改缓存参数值与编码取值来源；不把两线覆盖面调整到 subprojects 共享基线（覆盖面扩大属行为变化，用户裁决为保持双线各引）；不触碰 java-module 第 74 行注释所辖的 -parameters 与 fork 参数（其"留本线不并入共享块"的既有差异注释纪律原样保持，仅改指称名）。

## Decisions

**D1 落点为双线共用的合并插件，而非并入 tny.java-module（立项时经用户裁决）。** 并入 java-module 会剥掉插件模块线的两项基线（tny-game-doc-gradle 的编码回退 daemon 区域设置、动态版本恢复默认缓存），属行为变化；若 java-module 与 plugin-module 各写一份即违主账本"共享配置块禁止逐字复制"。合为 `tny.compile-baseline` 一块、两线各引一行，是覆盖面不变前提下的最小收敛。
**D2 命名取 compile-baseline 而非 repositories-baseline 等候选。** 两块共同主题是"编译行为与环境解耦"（编码钉定与零缓存皆消除隐式环境依赖），baseline 一词与既有装配线词汇一致；两配置域（configurations 与 tasks.withType）在头注释分述，避免读作只关编译任务。
**D3 新文件头注释顺带修正一处失实指称。** cache-policy 头注释"仓路由在 tny.repositories"所指插件不存在——仓库路由自 centralize-resolution-config 起由 settings.gradle 的 dependencyResolutionManagement 单点声明；合并件头注释按现状改写，属触碰即改纪律的应然动作。
**D4 正文两块逐字迁入、顺序保持"先缓存后编码"。** 两配置域声明互不引用，顺序本无因果；保持相对原序是为让逐字 diff 验收最直白（新文件两块与两旧文件正文一一对应）。

## Risks / Trade-offs

- 风险：合并件应用顺序变化引发配置期交互。缓解：两插件均无任务注册、无扩展、属性读取即时求值（encoding 由 gradle.properties 在配置期可读），与两线内其余插件无引用关系；POM 与 jar 逐字节判据兜底。
- 风险：java-module 第 74 行注释改名后"编码由 tny.compile-baseline 承担"的指称与"参数留本线"的差异说明易被误读为要并入。缓解：该行只改共享块名，括注结构原样保留，差异来由（sweep D6 与 verification-notes 引用）不动。
- 权衡：合并少一行装配语句但多一名新词——收益在注释分叉三处归一与主题文件化，代价为新 id 的认知成本，与前两桩合并变更同型账。

## Migration Plan

第一步，抓基线（tasks --all 剔噪清单段、:tny-game-net 的 POM 与一次 jar 产物、全量构建绿记录）。第二步，新建 tny.compile-baseline.gradle（正文逐字、头注释合写并修 D3 失实指称）；根脚本两装配块各两行改一行；三处指称收编；两旧文件入隔离目录。第三步，评估与零差异验收，记 verification-notes.md。回滚为两旧文件回位、根脚本恢复四行、三处指称还原。
