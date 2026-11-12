# Design

## Context

用户提议两问：GitFlow 与 GitCli 能否合并为一个类？重复的 runGit/requireGit 样板能否封装进
GitCli？现状盘点见 proposal（三处同款模板加一处 providers.exec 变体）。约束：gradle-build-style
的共享收编条款与约定插件 250 行长度界线；通道分界（写走 CLI、仓库状态查询走 grgit）自
revise-release-branch-flow 的 D6 起是既有设计决定；发布任务与门禁的对外行为（错误文案、
fail-closed 语义、记忆化）不许变。

## Goals / Non-Goals

- 目标：一个通道一个实现——git 命令行的"解析可执行文件、执行、取退出码与双流输出、失败即抛"
  逻辑在仓库里只存在一份；插件净行数下降。
- 非目标：不合并 GitFlow 与 GitCli 两个类（D1 给出否决论证）；不改任何任务的外部行为与文案；
  不顺手重组插件文件结构。

## Decisions

### D1 两个类保持分立，不合并（P5 按变更原因划分的直接应用）

GitFlow 的变更原因是"派生事实的形态演化"（分支名到版本、登记表、远端名推定——全部配置期求值、
构造后不可变）；GitCli 的变更原因是"命令行通道的执行机制"（进程派生、双流读取、退出码约定——
执行期反复调用）。合并后一个类背两个变更原因，且会把"grgiter 句柄可达写操作"的误导信号固化进
类型结构：本仓库有真实事故史——grgit 4.1.1 缺 branch create 等写 API，曾把写操作误投 JGit 通道
导致回退提交 976896b4（证据见归档变更 migrate-git-calls-to-grgit），CLI 与 grgit 分立的物理边界
就是防这类事故的护栏。备选方案"GitCli 并入 GitFlow 为方法组"否决：配置期对象持有执行期通道的
职责混淆，且 GitFlow 构造需要 gitExe 注入会破坏"唯一写入方一步构造"的既有契约（expose-git-info
extension 的构造时序）。保留的合并动作只有一项已在上一变更完成：resolveRemoteName 下沉 GitFlow
（纯查询，归属正确）。

### D2 GitCli 升级为项目绑定门面（消重手段）

新增静态工厂 `GitCli.forProject(project)`：构造实例时解析 `project.findProperty('gitExe') ?: 'git'`
与 `project.rootDir`，实例暴露 `run(List): Map`、`require(List, String): Map`、
`remoteTagPatches(String remote, String base): List<Integer>`。原静态方法收敛为实例方法或私有
实现，全仓调用点统一走实例。插件头部三行模板换为一行
`def git = GitCli.forProject(project)`，约 60 个 `runGit([`/`requireGit([` 调用点机械改名
`git.run([`/`git.require([`（脚本化替换加人工复核）。
备选"抽象基脚本或共享 .gradle 片段被各插件 apply"否决：Groovy 脚本插件在 buildSrc 作用域嵌套
应用有类加载器服务缺失前科（tny.git.gradle 头注记载，Gradle 8.5 实测），共享逻辑用类不用脚本。
备选"保持现状仅加注释"否决：不解决分叉温床，也不满足规格收编条款精神。

### D3 publish 门禁的执行段统一走门面，语义等价三要素核对

providers.exec 版 runGit（release 标签存证段）改为 GitCli 实例后，等价性逐项核对：
① 非零退出行为——providers.exec 配 ignoreExitValue 后按空输出走 fail-closed 拒绝，GitCli.run
返回 exit 码不抛出、调用方同样只读 out（远端不可达时输出为空）→ 等价；② 守护进程与执行环境的
架构匹配问题（-PgitExe 的存在理由）两种实现同样依赖显式路径，不劣化；③ 记忆化键与拒绝文案
原样保留。远端名推定局部闭包删除，改调 `gitFlow.resolveRemoteName(currentBranch)`（上一变更
已下沉的同一实现，行为差异仅在"无上游且多远端"时同样返回 null 走拒绝文案，核对通过）。
**风险预案**：tny.publish.gradle 现 273 行已超 250 线（红线对象是"约定插件脚本"，该文件为
历史超限文件——触碰即改条款适用）：本次删除重复段后若仍超线，把门禁七段拆为
tny.publish.gate.gradle 同目录姊妹插件脚本（装配线加一行引入），拆段作为任务列明，禁止以
"整文件重写"名义顺手重组。

### D4 通道分界注释的沿革衔接

三个插件头注与 tny.git.gradle 的 D6 表述更新为"写与执行期引用查询统一经 GitCli 门面（-PgitExe
由门面解析），仓库状态查询走 grgit（GitFlow.grgiter）"；追加一句沿革：分界决策出自归档变更
revise-release-branch-flow 的 D6，门面化由本变更完成。归档工件与 openspec 账本均不改写。

## Risks / Tradeoffs

- [机械改名 60 余调用点引入漏改/误改] → e2e 十八断言全量回归加三例门禁矩阵复测；改名用
  固定模式全文替换，替换后 grep 旧标识符零残留作为任务验收项。
- [providers.exec 与 ProcessBuilder 在极端场景（输出量、编码）行为差] → 现有调用均为小输出
  git 命令；GitCli.run 头注已记双流读取边界；回归覆盖 tag 存证与期望号两个实际调用点。
- [publish 拆段触碰历史超限文件引发扩散] → 拆段仅移动代码不改语义，若删重后已低于 250 线
  则不拆（任务写为条件步骤）。

## Migration Plan

单变更一次落地（纯内部重构无数据迁移）；回退等于 git revert 单提交。与
redesign-devline-integration-model 的剩余窗口任务（8.1/8.2）无耦合——门面不改变任何任务
参数与文案。

## Open Questions

（无。）
