# Proposal

## Why

git 命令行通道的调用样板在三个约定插件里逐字重复：`tny.release.gradle:13-16`、
`tny.integrate.gradle:13-15` 各自维护同一段"解析 -PgitExe → 构造 runGit → 包装 requireGit"
三行模板，`tny.publish.gradle` 的 release 标签存证段（约 118 行起）还藏着第三种实现变体
（providers.exec 自建 runGit 闭包）与其内部的远端名推定局部闭包（GitFlow 已有同名下沉实现）。
同一件事出现三份写法正是维护分叉的温床——`gradle-build-style` 规格"共享配置收编"条款
（MUST NOT 在两个及以上插件脚本中逐字复制同一配置块）的精神同样适用于此。

## What Changes

- GitCli 从"静态方法持有者"升级为**项目绑定的通道门面**：新增 `GitCli.forProject(project)`
  工厂方法，产出的实例自带 rootDir 与 gitExe 解析，暴露 `run(List)`、`require(List, String)`、
  `remoteTagPatches(String remote, String base)` 三个实例方法。各插件的三行模板收敛为一行
  实例获取，调用点从 `runGit([...])` 改为 `git.run([...])`（机械替换，语义不变）。
- `tny.publish.gradle` 的 release 标签存证段与下一补丁号段统一改用同一 GitCli 实例：
  删除 providers.exec 版 runGit 局部闭包与远端名推定局部闭包（后者复用 GitFlow.resolveRemoteName）；
  providers.exec 的"非零退出不抛出、输出按空处理"语义由 GitCli.run 返回 exit 码的形态等价承接，
  记忆化缓存与 fail-closed 拒绝文案不动。
- 三个插件头部与 `tny.git.gradle` 的通道分界注释同步：分界描述从"写走 git CLI（-PgitExe）"
  更新为"写与执行期引用查询统一经 GitCli 门面实例（-PgitExe 由门面解析）"；历史设计决策
  （revise-release-branch-flow 的 D6）已归档不改写，由注释中的变更沿革一句衔接。
- **GitFlow 与 GitCli 两个类本身不合并**（本提案经分析后的明确否决项，理由见 design D1）：
  前者是配置期不可变派生事实的载体（grgit 查询侧），后者是执行期命令行通道（写侧），
  是两个变更原因的物理分界；历史上 grgit 写操作事故（grgit 4.1.1 无 branch create 而回退 CLI，
  归档变更 migrate-git-calls-to-grgit 与 976896b4）正是这条分界存在的理由。

## Capabilities

### New Capabilities

（无——纯内部重构，不新增外部可观察行为。）

### Modified Capabilities

（无——不改变任何规格需求：发布任务行为、门禁语义、错误文案承诺均保持原样；
`gradle-build-style` 的"共享配置收编"条款是本次重构的依据而非修改对象。）

本变更声明 `skip_specs: true`（纯内部重构不改变外部行为，按规格治理规则不得虚构需求凑校验）。

## Impact

- `buildSrc/src/main/groovy/tny/convention/GitCli.groovy`：实例化门面改造（新增 forProject 与
  实例方法，现有静态方法收敛为内部实现）。
- `buildSrc/src/main/groovy/tny.release.gradle`、`tny.integrate.gradle`：模板删除与调用点改写，
  两文件各净减约 10-15 行。
- `buildSrc/src/main/groovy/tny.publish.gradle`：标签存证段与补丁号段的重复实现删除，净减约
  25-35 行（该文件当前 273 行，接近约定插件 250 行红线的余量压力顺带缓解——如触碰即改后
  仍超线则按规格拆段，拆段方案见 design 风险条目）。
- 回归基线：`drill/model4-sim/e2e-gradle.sh` 十八断言、门禁矩阵（main 拒绝/维护分支错号/
  祖父线放行三例）、`retireGuard` 与 `mergeUpward` 冲突回滚路径。无外部行为变化，无文档承诺变化。
