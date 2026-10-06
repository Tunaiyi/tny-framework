# Proposal

## Why

实测两案模块 `build.gradle` 超 gradle-build-style 需求七的 80 行界线：`tny-game-integration-test/build.gradle` 85 行、`tny-benchmark/build.gradle` 84 行（2026-10-03 全仓审计，其余文件与约定插件均在界线内）。超行的主因各是一处**滞留模块文件的配置阶段程序性控制流**——集成测试模块的"装配线 java 模块整体挂 integration 夹具"each 循环、bench 模块 jmh 块的执行面规模 if/else-if/else 链与两个属性开关——两者均属需求一"程序性行为只容身于任务动作块或约定插件脚本"的既有违例形态（本仓先例：benchParams 解析循环已按 declarative-it-demo-isolation design D5 从同文件迁入插件，本册是同段逻辑的收尾）。修完行数回到界线、配置阶段控制流清零，两个问题同源一次消化。

## What Changes

- `tny.integration-test.gradle` 末尾新增 afterEvaluate 段：以 `-integration-test` 后缀命名约定（settings 权威注释定义的类别谓词）守卫，承载"moduleProjects 中 java 形态工程整体挂 integrationImplementation"循环；`tny-game-integration-test/build.gradle` 删除该循环与其注释（85 行回到 80 以内）。
- `tny.benchmark-module.gradle` 现有 afterEvaluate 段（benchParams 覆写所在）前扩入执行面规模选择：familyInclude 拼接、benchAll/benchInclude/benchScope 三分支的 includes/excludes 与缺省档六臂参数域、benchGc 画像与 benchFast 快档开关——全部读同一 benchmarkSuite 清单，优先级保持现状（benchParams 覆写最后）；`tny-benchmark/build.gradle` 的 jmh 块删除整段控制流只留静态参数声明（fork、迭代、resultFormat、resultsFile 等，84 行回到 80 以内）。
- 两册各自插件文件头"边界"句同步更新（"jmh 执行参数在模块构建文件"→ 静态参数在模块、规模选择在插件；集成测试插件补"夹具挂接收编"职责）。
- 行为零差异是硬红线：两册验收均含"配置解析集 before/after diff"（详见各自 design 的时序风险条款——集成测试循环若原实现存在依赖配置顺序的侥幸语义，diff 出差异即停回用户裁决，不静默放宽）。

## Capabilities

### New Capabilities / Modified Capabilities

无。gradle-build-style 主规格条文零改动（本册是按既有需求七/需求一修正实现违例，不新增规则）；benchmark-harness、integration-testing 行为契约不变。skip_specs。

## Impact

- **受影响文件**：`tny-game-integration-test/build.gradle`（−7 行）、`buildSrc/src/main/groovy/tny.integration-test.gradle`（+守卫循环与 afterEvaluate 段、头注释）、`tny-benchmark/build.gradle`（jmh 块收敛为纯声明）、`buildSrc/src/main/groovy/tny.benchmark-module.gradle`（afterEvaluate 扩段、头注释）。
- **顺序关系**：两册互不相干可独立实施；与活跃册 `expose-git-info-extension`（消费 tny.git 派生值）无文件交集。前批确立的捕获口径沿用（JDK 钉 21、正常 locale、剔噪清单比对）。
- **验收基线**：两文件行数回到界线内（wc 复核）；`:tny-game-integration-test:dependencies --configuration integrationImplementation` 与 `integrationRuntimeClasspath` 解析集 before/after 零差异；bench 侧 `jmhList -PbenchAll` 枚举 31 键、`jmhSuiteVerify` 对账、缺省/速览/`-PbenchAll`/`-PbenchInclude` 四形态选择面逐项 before/after 一致；`tasks --all` 剔噪清单零差异；两册各一次 `clean build` 全绿（偶红按登记标准处置）。

## 归档后勘误（2026-10-03，基线口径审计工作流揭出）

本册 Why 与 What Changes 把集成测试模块 `tny-game-integration-test/build.gradle` 的超行主因记为"滞留模块文件的配置阶段 each 循环（装配线 java 形态模块整体挂 integrationImplementation）"。后续对案卷 move-it-fixture-loop-into-plugin 的口径审计经现场复核证伪该记载：现树该文件的 dependencies 块（第 42 至 85 行）没有任何循环，全仓 grep `moduleProjects` 与 `git log --all -S` 检索循环文本均零命中——该循环从未进入过该文件的 git 历史。85 行超界的事实仍成立，不成立的是"主因是程序性控制流"的归因；由此派生的 move-it-fixture-loop-into-plugin 册的待迁对象不存在，该册须回炉重定超行构成与修复对象。本册两处行数实测（85 行与 84 行）与界线判定不受影响，bench 册前提（jmh 控制流链在位）经复核成立。本册归档原文不改，本段为勘误记录；教训（立项前提的现场引用必须附可复核证据件）已随口径一并固化于 openspec/config.yaml context。
