# Proposal

## Why

用户定名：`tny.project-checks` 改为 `tny.module-checker`。现名"project-checks"没有说出检查对象——该插件承载的是对**构件模块**两条硬合同的配置期对账：发布构件组号单一事实源核对（central-publishing D2）与零发布合同（consolidate-module-modes-plugin D2，沿依赖边核对不发布角色），检查的对象是模块成员与模块间关系，"module-checker"直陈检查者身份。纯标识变更：对账逻辑、报红文案、执行时机（`gradle.projectsEvaluated`）全部不变。

## What Changes

- `tny.project-checks.gradle` 以 git mv 移名 `tny.module-checker.gradle`（插件 id 由文件名派生随之更换）；根 `build.gradle` 唯一应用行 `apply plugin: 'tny.project-checks'` 随改。
- 文件头注释补一行命名来由（同 rename-subprojects-baseline-plugin 先例的"工程标识名指向内容重心"句式）；头注释既有内容（职责、边界、历史路径"前身为 gradle/project-checks.gradle"）不动。
- 本文件第 4 行与第 46 行报错文案对 `tny.module-modes` 的指称**不属本变更范围**——那是 rename-module-modes-plugin 的收编面，两变更交叉处理按各自工件的现场 grep 预案。
- 历史归档与记录面（含 HANDOFF 与本账其他变更工件）的旧名指称不回改。

## Capabilities

### New Capabilities / Modified Capabilities

无。主规格 `gradle-build-style` 需求一以"buildSrc 约定插件"类别语描述程序性校验的容身之处，全账本无一处钉该插件名；改名不触碰任何条文。skip_specs。

## Impact

- **受影响文件**：改名主体 1 个、根 `build.gradle` 应用行 1 处。现场 grep `tny.project-checks` 现行面仅此两处（插件文件自身第 1 行"前身为 gradle/project-checks.gradle"为历史路径指称，不在改名面）。
- **顺序关系**：与 rename-module-modes-plugin 交叉于同一文件（该变更需改本文件的第 4、46 行指称，本变更移动文件名），先后落地均可、无强约束，后落地一方按实施时现场文件名为准。与在途 rename-bench-to-benchmark 无共同文件，但有一处指称随序问题须记：该变更计划按实况改写 tny-bench/README.md 第 7 行守卫句，改写后会点名本插件现名——本变更先落地则该句自然写新名 tny.module-checker（其工件已载"按实况改写"）；若该变更先落地，README 将写入旧名 tny.project-checks，而本变更的验收 grep 面不含各模块 README，该残留属 rename-bench-to-benchmark 收口责任，两本互不代改。与已归档各变更无共同文件。
- **验收基线**：该插件无扩展名、无任务注册（对账在配置阶段 `gradle.projectsEvaluated` 闭包内直接执行），任务图预期逐行零差异是构造性质；`tasks --all` 剔噪清单段对实施前自抓基线零差异、`./gradlew projects -q` 通过、`./gradlew clean build` 全绿、grep 活代码 `tny\.project-checks` 零命中。
