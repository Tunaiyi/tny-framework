# Proposal

## Why

上一变更（zero-enumeration-shared-scripts）落地了自报登记机制，实施与探针暴露三个问题，用户定调统一收编：

1. 发明的键名散落跨文件——根工程上的 `tnyDemoBlueprints`、`tnyUnpublishedProjects` 两个清单键是私人造词，靠"两处注释互指"维持一致性，正是文字规则禁止的私设代号形态。
2. 两个单职责标记插件（tny.demo-app、tny.unpublished）本质同一件事："声明本工程的横切角色"。角色还会继续增加（用户点名了集成测试应用等），逐个建标记插件、逐个建清单是斜率错误的扩张。
3. 配置期合同存在按需评估盲区（上轮探针实证）：仓库开启 configure-on-demand，零发布合同在"全部工程评估完成"时读根清单；单独跑 `:tny-game-net:dependencies` 这类报表任务不触发被依赖工程评估，清单缺员，非法依赖静默放行——改造前的点名版本在此场景能报红，这是真回归。登记方"到齐才可被看见"的模型在部分评估下不成立。

统一为单一角色插件同时消解三者：模块只 apply 一个插件并在其扩展里用方法声明角色；消费侧不再依赖"到齐的清单"，而是在自身评估期内沿依赖边把被依赖方拉起评估后直接读它的声明（Gradle 公开 API `evaluationDependsOn` 的既有用途），盲区从模型上消失，根工程清单与发明键名整体废除。

## What Changes

- 新增单一约定插件 `tny.module-modes`（文件 tny.module-modes.gradle）：应用它的模块通过扩展方法声明角色——`moduleModes { enableApp() }`（可作为 integrationTest 受控隔离子进程的应用蓝本）、`moduleModes { enableUnpublished() }`（本工程不发布：即刻自检"不得属于发布线成员"并禁用本工程 publish 任务，语义承接 tny.unpublished）。角色存储在该工程自己的扩展对象里，不再向根工程登记清单。
- 消费侧改造（盲区修复）：tny.project-checks 的零发布合同改为"依赖边就地检查"——每个发布线成员在自身评估收尾阶段遍历其声明的工程依赖，对被依赖方调用 `evaluationDependsOn` 强制评估后读其 module-modes 声明，违例报红列双方路径；tny.demo-isolation 的撮合改为在执行前动作沿同样的强制评估路径读取应用蓝本声明。登记模型从"到齐点名"改为"按需核对"。
- 废除 tny.demo-app.gradle、tny.unpublished.gradle 两个标记文件与根工程两张清单键；tny-game-net-demo / tny-bench / tny-game-integration-test 三个工程的声明改写为 moduleModes 方法调用；"键名两处注释互指"的约定整体消失（无键可指）。
- 回归修复验收：上轮探针 A（`:tny-game-net:dependencies` 报表路径下的非法依赖）必须恢复报红；探针 B（摘除角色声明后同一依赖放行）维持；全量构建、任务图、发布物零差异照旧。
- 不改变对外可见行为（报红时机与覆盖面只会更强不会更弱），不涉及主账本条文，skip_specs 声明零规格差量。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。本变更是 buildSrc 内部角色声明机制的收编与合同时机修复；gradle-build-style 各需求文本（含上一变更拟新增的"禁止枚举工程名"条款）语义不变——自报角色从"登记进根清单"改为"声明在工程自身的 module-modes 扩展"，仍属该条款定义的自报登记通道。

## Impact

- **受影响文件**：新增 tny.module-modes.gradle；tny.demo-app.gradle 与 tny.unpublished.gradle 退役（隔离目录法）；tny.project-checks / tny.demo-isolation 消费逻辑改写；tny-game-net-demo、tny-bench、tny-game-integration-test 三个模块构建文件声明改写；tny.demo-isolation 文件头随之更新。
- **消费面**：合同覆盖面对象不变（tny-bench 与集成测试模块的角色、demo 蓝本唯一性），检查时机从"配置末统一扫"变为"依赖边触发的就地检查"，检测能力严格不减弱（报表路径恢复报红）。
- **关系**：本变更建立在 zero-enumeration-shared-scripts 之上并接管其未完成的 3.2/3.3 验收（其合同盲区正是本变更修复对象；两变更一起归档前账本以最终形态为准）。
