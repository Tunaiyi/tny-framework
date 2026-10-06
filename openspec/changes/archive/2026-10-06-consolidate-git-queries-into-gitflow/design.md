# Design

## Context

三层分工现状（通道 GitCli／事实 GitFlow／流程=任务动作）与其各自守护的规矩，在变更
retire-grgit-channel 的讨论中成型；本变更把"远端查询"这最后一块散落在任务动作里的
解析逻辑收进 GitFlow。用户在立项询问"为什么流程不能合并进 GitFlow"——该问题的完整
答复作为本设计的否决记录（D2）入账本存档。爆炸半径实测：三个插件中 ls-remote 相关
调用闭包共 6 个（release：remoteRefNames 闭包＋assertNextPatchNumber 经 GitCli；
integrate：remoteRefHas/remoteReleaseLines/remoteDevLines；gate：标签存证 refMap＋
下一补丁号），全部为读取；执行类命令（push/fetch/merge/switch/tag/reset）不在本变更范围。

## Goals / Non-Goals

- 目标：读 git 单门面——任何 ls-remote 解析只存在于 GitFlow 一处；GitCli 回归纯通道
  （只管执行与取回，不再内置业务解析）。
- 非目标：不合并流程动作进 GitFlow（否决理由见 D2）；不动动作强耦合的状态查询
  （rev-parse 记录原头、log 提取搬运标记——理由见 D4）；不改任何外部行为。

## Decisions

### D1 GitFlow 远端查询方法面：单一解析点派生
`remoteRefs(String remoteName, List flagsAndPatterns = []): Map`（引用名→提交号，含
`^{}` 解引用行；空白分隔守卫解析——上一变更的数组越界教训固化于此唯一位置）；
派生方法：`remoteRefNames(remote, heads, tags)`（键集合，供存在性与前缀撞名判定）、
`remoteReleasedPatches(remote, base)`（`^{}` 行正则过滤出补丁号集合，吸收
GitCli.remoteTagPatches）、`remoteBranchExists(remote, branch)`（精确键查询）。
全部调用点改为派生方法；错误文案与判定时序不动。

### D2 流程动作不并入 GitFlow——四条独立否决理由（用户问题的账本答复）
备选方案"各插件不再调 GitCli，一切收进 GitFlow（含 push/merge/switch 等流程动作）"被否，
四条理由各自独立成立：
1. **快照诚实性**：GitFlow 的 branchName/commitId 是构造期定格的快照（其不变量即
   "构造完成即不可变"，P4）。若切分支、合并由 GitFlow 自己的方法执行，同一对象将在
   动作后继续报告动作前的状态——事实对象被自身动作变成说谎者。现行任务代码清楚这一
   边界，动作后用新命令重取实时值；合并会诱导"GitFlow.branchName 是实时的"错觉。
2. **两个变更原因合一**（P5）：GitFlow 因派生形态演化，流程动作因发布程序演化——
   合并即造出又一个"什么都在里面"的类，恰是前三次收编（通道收编、门禁拆分）反向而行。
3. **规格容身条款**：gradle-build-style 规定多步程序性行为 MUST 住任务动作块或插件
   脚本；GitFlow 是扩展数据对象，两者都不是。收流程需先给规格开"例外居所"，代价大于收益。
4. **通道可见性**：发布写操作全走 CLI 是 D6 以来的安全边界；`git.run(['push'...])`
   在任务代码里可被评审逐条点名，包进 GitFlow 方法后写通道对评审变黑盒——对承载生产
   写操作的通道，黑盒化是减分项。
结论：合并的可达上限是"**不直接调 GitCli 做任何查询**"，本变更取满这一半。

### D3 GitCli 回归纯通道
remoteTagPatches 的解析移入 GitFlow（D1），GitCli 只保留 forProject/run/require——
"怎么执行一条 git 命令"归它，"命令输出怎么解释成业务事实"归 GitFlow。

### D4 范围边界：动作强耦合的状态查询留在原处
integrateMain 记录 main 原头、mergeUpward 记录目标原头用的 rev-parse，以及 autoMarkers
的 log——它们读取的是**本任务动作序列自己正在改的状态**，是动作簿记而非可复用查询；
搬进 GitFlow 反而制造"方法读取自己刚写的位置"的往返。保留在任务动作内，验收 grep 的
白名单里注明。

## Risks / Trade-offs

- [remoteRefs 返回 Map 的键序与原 List 语义差异引入判定顺序变化] → 调用点全部为
  containsKey/any 匹配判定，无顺序依赖；e2e 十八断言覆盖三条判定链。
- [ls-remote 对 file:// 远端与 https 远端行为差异（认证提示混入 stdout？）] → 现行
  调用已同一实现承受（e2e 用 file://，门禁矩阵用本机 https），收编不改变执行方式。

## Migration Plan

单变更一次落地，回退等于 revert；无数据迁移。与 redesign-devline-integration-model 的
窗口任务无耦合（其文档若先归档，本变更头注句以其定稿为基线）。

## Open Questions

（无。）
