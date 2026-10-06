# Design

## Context

动机见 proposal.md - Why。沿革如实核对：simplify-demo-isolation-wiring 否决合并的原始理由是"接线须限专用模块、并入就要再造启用开关，开关本身是新机制"——并未预言今日通道。本次合并成立的原因不是措辞解读，而是事实变化：`-integration-test` 后缀判定已随 zero-enumeration-shared-scripts 成文为主账本第九条需求认可的命名约定通道（settings 第 48 行权威定义），闸门复用既有条约而非新造，原否决前提消失。

现状事实：tny.integration-test 由两处应用——根脚本 javaProjects 装配线行（51 个发布模块）与集成测试模块自身 plugins{}；tny.demo-isolation 仅集成测试模块应用，其接线在 `tasks.named('integrationTest')`（依赖 integration-test 先注册任务，模块 plugins{} 内顺序已保证）。合并后两插件同为 integration-test 文件，任务注册与接线同脚本内先后有序，不再依赖跨文件的引入顺序。

爆炸半径核查（规则要求，如实记录）：codegraph 不覆盖构建脚本；消费面三处——集成测试模块任务图（dry-run 比对）、integrationTest 隔离属性端到端（demo 子进程用例）、51 个普通模块的 integrationTest（闸门为假必须零动作，以 net:test dry-run 与全量比对兜底）。

两卷检索（M1）：条件守卫形态仓内先例充分（tny.publications 的 centralSnapshots 条件声明、tny.bench-suite 的参数域条件），本案同族。

## Goals / Non-Goals

**Goals:**

- 两级验证通道与其专用模块延伸职责归一文件；插件文件 19→18。
- 行为零差异：任务图、隔离字符串、防线探针、全量与发布物全部不变。

**Non-Goals:**

- 不改接线逻辑本体（整体迁移，除闸门包裹外逐行不动）。
- 不动 module-modes 角色体系与 tny-bench 等其它消费点。
- 不重命名 tny.integration-test（职责扩展仍在"两级验证通道"语义半径内，名字不需要动）。

## Decisions

**D1 闸门用已成文的命名约定，不用角色声明。** 接线段包裹 `if (project.name.endsWith('-integration-test')) { ... }`，注释指回 settings 约定权威定义。被否决备选一：新增第三角色 enableIntegrationTestHost()——"集成测试专用模块"是装配线成员（第九条需求的命名约定通道管辖），塞进横切角色插件违反两个通道的分工（角色管个体属性、命名管类别归属）；被否决备选二：以"本模块 integrationImplementation 存在 enableApp 依赖"为闸门——报表阶段依赖未必就位且判断依赖被依赖方评估状态，把配置期问题推到不确定时序。

**D2 迁移代码除包裹层逐行不动，接线时机随文件合一自然前移。** 原 demo-isolation 在模块 plugins{} 中晚于 integration-test 应用、tasks.named 即时执行；合并后接线与任务注册同脚本顺序执行，仍在本工程评估窗口内、任务图生成前——dependsOn、doFirst、forkEvery 三项语义无任何时点移动。集成测试模块 plugins{} 删除 demo-isolation 行后，该文件引入顺序约束（"须后于任务注册"）注释一并消失，属复杂度净减。

**D3 51 个普通模块的携带成本接受并证明。** 闸门为假的模块多执行一次字符串后缀判断（微秒级）与一个不注册的闭包定义；"多工程共享插件携带条件段"与 tny.publications 条件仓先例同族。若验收发现任何任务图差异即回退本变更（保持两文件现状），不带病合并。

## Risks / Trade-offs

- [单文件职责变宽（通道建设+专用模块接线）] → 两段各有独立头注释分节，边界注释写明"仅专用模块执行的延伸职责"；与拆成两文件的复杂度对比已在 D1 权衡，验收探针保证行为不变优先于审美。
- [根装配线未来若把 integration-test 插件应用到 -integration-test 后缀工程（不应发生：该后缀被 javaProjects 谓词排除）] → 双保险：闸门与谓词同约定，settings 约定注释为单一权威。
- [普通模块意外触发接线] → 3.2 以 net:test dry-run 任务图逐行比对专项兜底。

## Migration Plan

单原子批次：integration-test 追加闸门段（整体迁移 demo-isolation 接线）→ 模块删引入行 → demo-isolation 移隔离目录 → 验收四件套（1.1 基线在改造前抓：IT 与 net 的 dry-run 清单；改造后逐行比对；防线探针重放：摘 enableApp 报红/恢复绿；integrationTest --rerun 绿；clean build 绿 + tasks --all/m2/POM 零差异）。回滚两文件 revert。

## Open Questions

无。
