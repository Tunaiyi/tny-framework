# Design

## Context

动机见 proposal.md - Why。现状事实：tny-game-integration-test 的 dependencies 块第 55-56 行已声明 `integrationImplementation project(':tny-game-net-demo')`（被测栈声明即隔离对象的事实源候选）；demoProcessIsolation.targetProjects 现持同一信息的字符串抄本。integrationTest 属性 `it.demo.isolatedClasspath` 的消费方是 IT 用例自身（以该串启动 demo 子进程），其绿即装配正确的端到端证明。

爆炸半径核查（同前两个变更的格式）：codegraph 不覆盖构建脚本；消费面三处——integrationTest 属性读取（行为面主证）、任务图（dependsOn/forkEvery/shouldRunAfter）、tasks --all 清单——本设计三处零漂移。

两卷检索（M1）：本案的形状是"被依赖方自述角色、消费方按角色筛选"——模式卷无构建域行，最近先例是框架运行时的 `@UnitInterface` 声明即注册微框架（P2 依赖倒置的仓库内既成模式：能力需求声明对契约的要求，实现侧自贴标记，装配由扫描撮合）。本设计取同族思路的最小版：不引扫描设施，标记即插件应用存在性，撮合由本插件一行过滤完成。

## Goals / Non-Goals

**Goals:**

- 被测隔离对象收敛到唯一事实源（IT 的 integration 依赖声明），消灭第三处抄写。
- 消除跨模块字符串伸手：方向反转为"被依赖者自述、依赖者筛选"，双方都只谈自己。
- 事故防线语义保持：无隔离目标配置期报红。

**Non-Goals:**

- 不引入通用能力扫描/注册设施（@UnitInterface 那套是运行时 DI，构建期不需要）。
- 不动 benchSuite 扩展（其数据是本模块自述的族清单，无跨模块伸手与双源问题，复查结论维持）。
- 不改 classpath 拼接与 forkEvery 的机制语义。

## Decisions

**D1 标记插件 tny.demo-app 作为被依赖方的自述面；派生 = IT 直接工程依赖 ∩ 标记。** 标记实现为空内容约定插件（应用即自述，文件头写明"应用形态蓝本可作 integrationTest 受控隔离子进程；被 tny.it-demo-isolation 按存在性筛选"）。派生取 `configurations.integrationImplementation.dependencies.withType(ProjectDependency)*.dependencyProject` 中 `plugins.hasPlugin('tny.demo-app')` 者，顺序即声明序。依据：P2（IT 依赖"被标记的工程"这一契约，不依赖具体工程名）、P5（角色自述随 demo 模块走，隔离撮合随 IT 模块走，两个变更原因分家）；@UnitInterface 声明即注册的仓库内先例同构。被否决备选：targetProjects 字符串清单（declarative 变更 D1 原案）——否决：双事实源漂移与改名静默失效，用户复查定罪；从 integration 全部工程依赖自动发现而不加标记——否决：boot/data/redisson 诸工程皆非子进程蓝本，无标记则无从区分"被测应用"与"被测库"，隔离面反而失焦（标记正是这个语义区分本身）。

**D2 空派生集维持配置期报红，文案指向两个成因。** 集合为空时报红列出两种可能根因（IT 未在 integrationImplementation 声明应用蓝本 / 所声明工程未贴 tny.demo-app 标记），并提示本防线是串染事故后的显式交集——审计对象从"手抄清单"变为"两处理由的交集"，可核对性不降反升（清单抄错依赖不报错的旧病恰在本案消灭）。依据：P4（"必须存在受控目标"是不变量，每次求值都维持）、P13。

**D3 拼接与挂接逻辑整体保留，仅目标来源换线。** afterEvaluate 内校验 jar 任务与 runtimeClasspath 存在性、dependsOn 逐目标 jar、doFirst 无条件串接 rc.asPath 与 jar 绝对路径——与 declarative 变更终态逐行同构，单目标产物逐字节等值由"集合相同→拼接相同"直接成立。DemoProcessIsolation 类与模块声明块删除（不留死配置面，教训：退役要连根）。依据：零差异红线。

## Risks / Trade-offs

- 【integrationImplementation 依赖集顺序是否为声明序】→ Gradle 的 dependencies 集合保持插入序；多目标场景当前不存在（单目标），语义以声明序入文档即可。
- 【未来某库工程误贴 demo-app 标记混入 integration 依赖】→ 会同时成为隔离目标——但这正是标记语义的自洽后果（你声明它是应用蓝本并放进被测栈），比手抄清单的静默漂移更可审。
- 【demo 模块改名】→ 依赖声明行与工程路径联动由 Gradle 编译期保证（project(':新名') 写错立即报红），字符串清单时代的静默失联风险由类型系统接管。
- 【报红探针依赖摘除标记再恢复】→ 用备份文件恢复，与 declarative 变更同款纪律，探针脚本不落仓。

## Migration Plan

单原子批次：标记插件落地 → demo 贴标记 → it-demo-isolation 换派生 → 删扩展类与模块声明块 → 探针（空集红、恢复绿）→ IT/全量/基线回归。回滚为四文件整体 revert。

## Open Questions

无。

## 实施修正注记

- 撮合时机：D1/D3 原按 afterEvaluate 设想，实施实测踩中工程评估字母序（tny-game-integration-test 早于 tny-game-net-demo，标记未及应用致派生集空报红）；修正为 gradle.projectsEvaluated（全部工程评估完、任务图生成前），hasPlugin 判定稳定，装配语义与零差异论证不受影响。该教训与前两变更的 ClassLoaderScope 时序坑同族：跨工程读取他方插件应用状态必须在全工程评估后。
