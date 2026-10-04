# Tasks

> 本变更零公共 API、零 Java 源码改动（仅 Gradle 脚本与工作流脚本），无 JUnit 前置测试义务；
> `:tny-bench` 无测试源集，组验证以选择面探针与清单校验任务替代模块测试。

## 1. tny-bench/build.gradle：速览规模声明与导出命名

- [x] 1.1 在 ext 增加速览清单 `benchQuickClasses`（全管线、发送队列、RPC 配对三类加设施探针的常规族子集，与既有族清单同处集中声明；加密矩阵类不入），`jmh` 块新增 `benchScope` 属性开关：取值 `quick` 时缺省选择面改用速览清单（此时 `benchRoutineAlgoArms` 参数域自然无命中类，保留不删），缺省与取值 `full` 维持现状全族行为。验证：`./gradlew :tny-bench:jmhList` 输出维持常规族七条基名；`./gradlew :tny-bench:jmh -PbenchScope=quick -PbenchFast` 选择面输出恰好十三个参数组合且含设施探针、无矩阵条目。
- [x] 1.2 `benchRoutineExport` 目标文件名随规模区分：`quick` 时写 `results/bench-<日期>-quick.json`，否则维持 `results/bench-<日期>-routine.json`；产物缺失判红逻辑不变。验证：本地各跑一次两规模导出，`results/` 同日期两文件并存、互不覆盖。
- [x] 1.3 组验证：`./gradlew :tny-bench:jmhCompileGeneratedClasses :tny-bench:jmhList :tny-bench:jmhSuiteVerify` 全链退出零；`jmhSuiteVerify` 对速览子集不涉及族漂移（族属声明结构未动，应照常全绿）。

## 2. .github/workflows/build.yml：内容门禁与执行参数

- [x] 2.1 新增作业 `bench-scope-gate`（秒级）：仅 push 事件用 `git diff --name-only` 对比事件携带的前后提交，命中"模块 `src/`、任一 `build.gradle`、根 `gradle.properties`、`gradle/libs.versions.toml`、`tny-bench/` 任意文件"则输出 framework-change 为真，否则为假；前序提交不可达时输出真（安全侧多跑）；schedule 与 workflow_dispatch 事件恒输出真。验证：本地以样例文件清单手工走判定脚本，文档清单与源码清单各判一次，结果符合预期。
- [x] 2.2 `bench-routine` 声明依赖该门禁作业，并把触发条件改为：push 场合需门禁输出为真，定时与手动恒放行。验证：静态核对 `if` 表达式覆盖三种事件语义；同轮其他作业（unit、integration、bench-compile）不受 `needs` 牵制。
- [x] 2.3 计时步骤按触发场合传递规模：push 传 `-PbenchScope=quick`，schedule 与手动不传（即完整规模）。验证：与 1.1 的本地探针命令一致对应。
- [x] 2.4 曲线存储步骤的序列分组名 `name` 改为携带分支与规模（形如 `Routine quick (5.7.x)`、`Routine full (main)`，由触发上下文变量与 2.3 的规模同源拼接）。验证：静态核对拼接式与 2.3 的规模变量同一来源，无第二种口径。
- [x] 2.5 组验证：`python3 -c "import yaml"` 解析通过；作业拓扑（needs 链）无环；`bench-compile` 步骤命令零改动。

## 3. tny-bench/README.md：口径文档

- [x] 3.1 执行面章节改写为两规模口径：速览清单成员与"矩阵级退化最迟夜间显现"的裁决说明、完整清单成员、各规模的实测时长预算（速览约七至九分钟、完整约二十分钟）。验证：表中成员与 `benchQuickClasses`、`benchRoutineClasses` 两份清单逐类比对一致。
- [x] 3.2 产物引用约定增加规模标识（`-quick.json` 与 `-routine.json` 并存语义、同日期覆盖限定同规模），变更工件引用数字时须同时注明规模。验证：README 每条新命令逐条本地执行可达预期选择面。
- [x] 3.3 组验证：README 无残留"每次合入跑全套"旧口径表述。

## 4. 实况验证与收口

- [x] 4.1 文档类合入实况：推送一次仅改文档或案卷的提交，观察 Actions 中 `bench-scope-gate` 判定为非框架改动、`bench-routine` 状态为 skipped、分支无新自动回写提交。验证：三个观察点各留一次记录（run 页面截图说明或 run 号）。
- [x] 4.2 框架合入实况：等待或构造一次含源码改动的合入，观察 `bench-routine` 以速览规模放行并约九分钟内完成，曲线新增对应"分支乘速览"分组的数据点，产物落 `results/bench-<日期>-quick.json`。验证：run 时长、曲线分组名、产物文件名三点核对。
- [x] 4.3 完整规模实况：手动触发一次 workflow_dispatch，确认恒放行、跑三十七组合、分组名为"分支乘完整"，且既有未分组历史数据点不再增长（只追加新组）。验证：产物条目数为三十七、分组键生效。
- [x] 4.4 收口：`openspec validate refine-bench-routine-triggering --strict` 通过；把 4.1 至 4.3 的实况证据记入本变更目录 `verification.md`；核对差量与账本，备 /opsx:verify 与归档。
