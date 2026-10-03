# Design

## Context
动机见 proposal.md - Why。现状消费点（实施期工作流审计修正版：三路扫描按键名、访问形态、文档面各自检索，去重后每个候选文件一名反驳核查员核查，最后完整性批判员收口；立项时本段把 commitId 与两个后缀常量的读取错记在 tny.release 名下、并整个漏掉了 tny.publish——release 经 `def git = rootProject.ext` 别名实际读的是 branchName、grgiter、parseBranchVersion，那类常量与提交号读取在 tny.publish 的门禁闭包内）：共五个消费文件——tny.release（别名单点，其下引用点经别名走，执行期读取）、tny.publish（validatePublishConsistency 闭包内别名，读 branchName、commitId、SNAPSHOT_PACK_SUFFIX、RELEASE_VERSION_SUFFIX、parseBranchVersion，任务执行期读取）、tny.publications（signing 块一处 rootProject.ext.branchName，配置期读取）、tny.dependency-management（version 派生一行 rootProject.ext.projectVersion，配置期读取，作用于全部子工程）、tny.central（centralCheck 守卫两处裸 branchName，执行期读取）。除上述文件外全仓再无第六处活代码消费；模块构建文件零命中；rootProject.ext 的遍历、序列化、按名反射形态经核查不存在，无"删键不报错但语义消失"的隐性读取面。tny.git 导出面二十键（常量 3、句柄 1、闭包 10、派生值 6）：其中 8 键全仓零读取、5 键仅本文件推导链自引用、7 键有跨文件读取方构成行为红线面（清单见 proposal.md 第二条 What Changes）。

爆炸半径核查：codegraph 不覆盖构建脚本（惯例记录）；行为面锚点=项目版本字符串（jar 文件名/POM version）、dryRun 文案、centralCheck 报红、组号对账——四者构成验收探针集。实施期审计发现此探针集覆盖不到执行期读取面：tny.publish 门禁的五个键只在共享仓发布任务链上执行（本地验收命令都不触发），tny.release 的 parseBranchVersion 只在 releaseRebaseBack 执行（dryRun 通道不进入），若只跑原四探针则这些键改错也全绿、零差异声明无证据；处置记入 D4。

两卷检索（M1）：消费方按类型向根工程拉取协作对象的形状是框架运行时既有先例（@UnitInterface 扫描装配）的构建期对应；"统一插件调用"不发明新机制。

## Goals / Non-Goals
Goals：跨插件契约类型化、可见（getByType 即文档）、失败早爆；ext 字典零残留（git 派生面）。Non-Goals：不并文件（250 行界线与职责分离双理由）；不动根 ext 的身份与成员事实（projectGroup/集合派生属第九条需求的根声明面）；不动 publish-gate 的 memo 缓存键（性质是缓存不是契约）。

## Decisions
**D1 扩展挂根工程、类置 buildSrc、字段动态类型。** 消费者统一 `rootProject.extensions.getByType(tny.convention.GitInfo)`；GitInfo 的 grgiter 字段用 Groovy 动态类型——buildSrc 编译期零第三方 classpath（与 adopt 变更 D9 的"buildSrc 不引插件栈"约束一致），运行时类加载器可达 grgit 已被根脚本先行应用保证。被否决备选：类型化 Grgit 字段——否决理由：迫使 buildSrc 声明 grgit 依赖，双处版本维护正是需求三要消灭的形态。
**D2 推导逻辑逐行迁移，不顺手重写。** parseBranchVersion/gitTag 等闭包原样转方法，其实测教训注释（describe 无负向锚、过渡期已知局限）随行——行为等值以版本字符串与 dryRun 文案比对兜底，重写不在本变更半径。被否决备选：借机把读操作迁回 grgit 强类型 API——那是 migrate-git-calls-to-grgit 的既有收口范围，不重开。
**D3 失败形态变化如实声明。** 改造前若 tny.git 未加载，各消费点对 ext 键的读取会在该键的读取处抛"未知属性"异常（Groovy 对不存在的动态属性取值不返回 null）——失败点分散在每键各自的读取行上，且只有代码执行走到该路径才暴露；改造后读取点收敛为每个消费文件的一处 getByType 调用，扩展不在位即抛"找不到该类型的扩展"，报错时点从执行期提前到插件应用期，时序契约经类型声明可见。属可观测性增强，不视为回归。实施期反驳核查纠正了本条初稿"返回 null/缺键、深处才炸"的不确陈述。
**D4 验收面补强两类执行期门禁探针，锚点按同环境口径重抓。** 原四探针触不到 tny.publish 门禁与 tny.release 的发布线读取（见爆炸半径核查段的审计发现），补两类探针：其一在开发线直接运行 `./gradlew :tny-game-net:checkPublishPrerequisites`，门禁应绿，覆盖 branchName 与 SNAPSHOT_PACK_SUFFIX 的开发线读取路径；其二在一次性本地克隆（`git clone` 本仓库到 /tmp，随后把工作树现行文件整备覆盖进克隆，克隆的远端即本机仓库、零外网）上检出两个发布形态临时分支——形如 9.9.9.release 的合规形态覆盖 parseBranchVersion 与 commitId 读取（发布标签存证走远端核对，本仓库无 v9.9.9 标签时按 fail-closed 拒绝文案输出；该克隆上跑 `releaseRebaseBack -PdryRun` 会在读完 branchName 与 parseBranchVersion 之后、于 tny.release 既有的 split 转换形态处报红（改造前后同样报红，文案即判据），同段覆盖这两处读取；分支与标签列表的 grgiter 读取由 releaseCutAndTag -PdryRun 样本覆盖），形如 9.9.release 的缺第三段形态覆盖 RELEASE_VERSION_SUFFIX 拒绝文案；比对完成后删除克隆目录，主工作树全程不动。方案沿革如实记录：本探针最初设计为 `git worktree`，改造前抓样实测暴露 grgit 4.1.1（JGit 实现）不识别 worktree 的指针式 .git 文件，tny.git 装载即在派生提交号处抛空引用，克隆方案两遍（改造前后）同机制抓取，判据自洽。两类探针的改造前样本于实施 2.1 之前在同一钉定环境补抓。另据 tasks 3.2"同环境口径重抓件为准"处置实施期发现的两个环境漂移：归档 tasks-all 锚件抓于 Gradle 8.5 与 tny-game-codec-protoex 构件恢复之前，与现行环境差 201 行且差异全部为版本脚注行与该模块任务行整族新增；本机 m2 库在归档锚件之后因其他在途册的发布历史长高 212 行（现行件为归档件严格超集）——两判据均改以本册 baseline/ 的改造前重抓件为锚，重抓件与归档件的差集结构如实记录。

## Risks / Trade-offs
- [release 的 git.grgiter 深链调用较多，漏改一处编译/评估即红（动态属性不存在错误早爆）] → 与 D3 同理，风险自暴露。
- [Groovy 动态字段失去类型检查] → 契约靠单一写入方（tny.git）+ 探针面兜底；类型化的收益在"取扩展失败即错"这一层已实现。
- [两变更顺序颠倒实施] → tasks 首步声明前置检查（依赖 rename 完成）。
- [同一机器另有并行会话正在改动本仓 buildSrc 构建脚本] → 实施期已两次观测到本册目标文件被外部窗口写入（两次读取之间行号与内容有差）；处置：动手前重读全部目标文件核实现场，复验改造前基线是否被外部写入污染，污染则按同一钉定口径重抓；若并行窗口持续活跃则暂停实施并向用户求裁决，不在并发写入的树上落笔。
- [门禁探针依赖远端可达性] → 标签存证核对经 `git ls-remote` 读远端，远端不可达时按其既有 fail-closed 语义输出"标签不存在"文案，该形态与本地无标签一致，比对判据不受影响；探针只读远端，不产生推送。

## Migration Plan
前置确认 rename 完成 → GitInfo 类 + tny.git 重写 → 五消费者切换（根 build.gradle 注释行随改）+ ext 派生字典删除 → 探针集验收（版本字符串/dryRun 文案/centralCheck/组号对账/tasks/m2/POM 零差异，另加 D4 的两类门禁执行期探针；tasks 与 m2 判据锚定本册改造前重抓件）。回滚按八个触碰文件整体还原（tny.git、GitInfo 新类、release、publish、publications、dependency-management、central、根 build.gradle）。
