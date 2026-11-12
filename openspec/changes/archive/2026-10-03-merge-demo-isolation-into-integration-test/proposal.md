# Proposal

## Why

用户复问"tny.demo-isolation 能否合并进 tny.integration-test"。此前两次否决（simplify-demo-isolation-wiring 的 D3 与其实施修正注记）的理由都是：接线只应发生在集成测试专用模块，合并就需要为 51 个普通发布模块再造一个"本模块启用"的开关——用新机制省一个文件不划算。该前提今天不再成立：`-integration-test` 后缀作为"集成测试专用模块"的判定约定已随 zero-enumeration-shared-scripts 成文（权威定义在 settings.gradle 命名约定注释，主账本第九条需求认可的命名约定通道）。用它作闸门，合并不再引入任何新机制。合并后"两级验证通道"及其在专用模块的延伸职责（受控隔离子进程装配）收在一个文件里，链路不再跨插件跳转，插件文件数 19→18。

## What Changes

- tny.integration-test 尾部增加一段：仅当 `project.name.endsWith('-integration-test')` 时执行演示应用子进程隔离接线（dependsOn 集成运行时类路径、执行前就地核对 enableApp 交集、非空校验两成因文案、隔离 classpath 拼装、每类独占进程）——接线代码自 tny.demo-isolation 整体迁移，除闸门条件外逐行不改；文件头职责与边界注释同步。
- tny-game-integration-test/build.gradle 删除 `id 'tny.demo-isolation'` 引入行；tny.demo-isolation.gradle 移入隔离目录退役。
- 普通发布模块经根装配线应用同一插件时闸门为假、零动作：任务图、integrationTest 行为与现状逐字节不变（dry-run 比对与全量零差异负责证实；这一"多模块携带不触发代码"的形态与仓内既有条件声明同族，不新增机制）。
- 防线不变：交集为空执行前报红、摘除 enableApp 声明即触发——上一变更的探针集原样重放。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。合并属 buildSrc 内部文件归组，外部行为零变化；主账本第九条需求（命名约定通道）恰好是本合并所依赖的机制，条文无需改动。skip_specs 声明零规格差量。

## Impact

- **受影响文件**：buildSrc/src/main/groovy/tny.integration-test.gradle（追加闸门段）、tny-game-integration-test/build.gradle（删一行引入）、tny.demo-isolation.gradle（退役移隔离目录）。
- **消费面**：integrationTest 任务及其隔离属性、任务图、CI 三档调用——全部逐字节不变。
- **决策沿革**：本变更正式推翻 simplify-demo-isolation-wiring 时代"不合并"的两条记录（其 design D3 撤回注记），推翻的正是它当时声明的触发条件——"出现不引入新机制即可表达的闸门时再议"；条件已成熟，沿革在 design 与归档摘要留痕。
