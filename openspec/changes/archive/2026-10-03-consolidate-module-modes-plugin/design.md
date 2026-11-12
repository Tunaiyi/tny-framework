# Design

## Context

动机见 proposal.md - Why。机制事实补充：`Project.evaluationDependsOn(path)` 的公开语义是"在本工程配置完成之前，同步完成指定工程的评估"——这正是把"到齐才可点名"反转为"就地核对"的官方工具；调用必须发生在调用方自己的评估生命周期内（afterEvaluate 回调仍属于该窗口，projectsEvaluated 时则已失效），此合法性列为实施第一步探针验证，不许凭记忆下注。demo 蓝本的消费发生在 integrationTest 的 doFirst（执行期），彼时全部工程必然已评估，天然无盲区。

爆炸半径核查（规则要求，如实记录）：codegraph 不覆盖构建脚本；消费面三处——合同报红触发路径（含上轮实证的报表路径回归）、integrationTest 隔离字符串（端到端）、声明侧三个模块文件的构建图（dry-run 比对）。

两卷检索（M1）：moduleModes 的形状是"能力声明 + 存在性查询"，与运行时 @UnitInterface 声明即注册同族但更进一步——查询方强制被查询方完成评估，不需要全局登记表；命名采用用户给定的动词式方法名（enableApp/enableUnpublished），扩展名取"module modes（模块角色）"直译，不引入新词。

## Goals / Non-Goals

**Goals:**

- 一个插件、一处声明、零登记清单：根工程不再有跨文件约定的字符串键。
- 报表路径合同盲区修复：上轮探针 A 的场景恢复报红（严格增强，不减）。
- 行为零差异：全量构建、任务图、隔离字符串、发布物与现状一致。

**Non-Goals:**

- 线成员判定维持命名后缀（zero-enumeration 已落地，与本机制正交）。
- 不引入角色参数的花式配置（当前两角色各零参数；有真实第三角色时按既有 enum+enableXxx 形状扩展）。
- 不动 configure-on-demand 开关本身（用模型修复盲区，而非关掉性能特性）。

## Decisions

**D1 单一插件 tny.module-modes 与扩展形状。** 模块 `apply plugin: 'tny.module-modes'` 后声明 `moduleModes { enableApp() }` / `moduleModes { enableUnpublished() }`。实现：buildSrc 类 `tny.convention.ModuleModes`（持有 project 引用、EnumSet<Mode> 状态、`Mode{APP,UNPUBLISHED}`）；`enableUnpublished()` 的两个副作用原样承接 tny.unpublished——禁用本工程 publish 任务 + 自检"本工程不得属于根 ext 的 javaProjects"（自检在自身声明瞬间执行，根集合彼时已就绪，且不需要任何其他工程评估，无盲区）。查询侧静态方法 `ModuleModes.has(project, mode)`（未应用插件的工程返回 false，不抛错）。被否决备选：保留根清单但收进单一命名对象——否决：换皮的"到齐点名"，盲区原样存在。

**D2 零发布合同改为依赖边就地检查。** tny.project-checks 在根脚本对每个 moduleProjects 成员注册 afterEvaluate：遍历其全部 configurations 的 ProjectDependency，对依赖目标 `project.evaluationDependsOn(target.path)` 后查询 `ModuleModes.has(target, UNPUBLISHED)`，命中即报红（文案沿用双方路径列出的现有格式）。语义等价论证：任何"发布成员→不发布工程"的边必然在成员评估收尾时被核对，被依赖方哪怕排在字母表后面也会被强制提前评估；projectsEvaluated 版合同整体废除。副作用半径：强制评估改变被拉起的工程其评估时点提前——被拉起的对象只有被声明为依赖的工程（当前仅 bench/demo 等叶子角色，无自有依赖，提前评估无连锁）；此断言由验收 dry-run 任务图与全量构建比对兜底。回退分支（若第一步探针证明 afterEvaluate 内 evaluationDependsOn 非法或副作用失控）：合同保留 projectsEvaluated 形态，但清单缺失时不再静默——对"存在未评估的依赖目标"报"检查不完整"错误（把盲区从漏检变成可见失败），并回用户处复裁。

**D3 demo-isolation 消费改扩展直查，删除清单读取。** doFirst 内：本模块 integrationImplementation 的直接工程依赖逐个 `ModuleModes.has(dep, APP)` 过滤——执行期全部工程已评估，无需强制评估；空交集报红文案不变。文件头与注释里"清单键名互指"的段落整体删除。

**D4 废除面与遗留。** tny.demo-app.gradle、tny.unpublished.gradle 移隔离目录；`tnyDemoBlueprints`/`tnyUnpublishedProjects` 两键全仓 grep 清零属验收项；zero-enumeration-shared-scripts 的未勾任务 3.2/3.3 由本变更的探针集覆盖（其合同版本将被替换，不在旧形态上补验收——两变更归档顺序：先 zero-enumeration（记录被后续修正的注记），后本变更）。

## Risks / Trade-offs

- 【afterEvaluate 内 evaluationDependsOn 的窗口合法性】→ 实施第一步专项探针；有 D2 回退分支兜底，最坏不劣于现状。
- 【强制评估提前改变合同报红时机：从"配置末"提前到"依赖方评估收尾"】→ 报红更早不减；文案不变；探针样本比对。
- 【被拉起工程自身的 afterEvaluate/发布接线在非常规时点执行】→ 当前被拉集合为叶子角色工程（bench 无发布接线、demo 是发布线成员但被拉时仅提前完成自身评估——其自身 afterEvaluate 会提前跑，含其零发布检查注册幂等）；全量构建零差异兜底，若 demo 的提前评估引发图差异则改用"仅对非发布线目标强制评估，发布线目标读其自身评估结果"的收窄版。
- 【报表盲区虽修复，但"发布成员→发布成员"的依赖方向评估顺序变化引入配置耗时】→ 强制评估仅沿声明边发生、每目标一次，增量秒级可忽略；以 3.x 全量构建耗时对照记录。

## Migration Plan

批次一（探针先行）：在一次性临时形态里验证 afterEvaluate+evaluationDependsOn 合法性（成功/回退二选一后再动正式文件）。批次二：ModuleModes 类与 tny.module-modes 插件落地，三模块声明改写，两标记文件退役。批次三：project-checks 就地合同与 demo-isolation 直查改造。批次四：回归全案（探针 A 恢复红、B 放行、任务图/报表/发布物/全量零差异、键名 grep 清零）。每批失败即回退该批。

## Open Questions

无（合法性分叉有明确回退分支，不构成待决问题）。
