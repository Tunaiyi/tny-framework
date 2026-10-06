# Proposal

## Why

远端引用查询（git ls-remote）目前散落在三个插件的任务代码里各自解析——上一变更的
remoteRefNames 数组越界崩溃正是"解析口径分散在调用点"的代价实证。GitFlow 已经承载了
本地查询的统一面（branchNames/tagNames/tagInfo/trackedDirtyPaths），远端查询是最后一块
仍散落在外的解析逻辑；把它收进 GitFlow 后，全仓"读 git"只有一个门面，解析口径单点拥有、
单点测试、崩也只崩一处。

## What Changes

- GitFlow 新增远端查询方法面：`remoteSnapshot(remoteName, patterns)`（一次 ls-remote，
  返回"引用名到提交号"映射，含附注标签的 `^{}` 解引用行——解析守卫收敛于此一处）及其
  派生方法 `remoteRefNames`（分支/标签存在性判定用）、`remoteReleasedPatches`（系列已发布
  补丁号集合，吸收现 GitCli.remoteTagPatches 的调用面）、`remoteBranchExists`。
- 三个插件的全部 ls-remote 调用点改走上述方法：tny.release 的 remoteRefNames 局部闭包删除、
  下一补丁号校验改调 remoteReleasedPatches；tny.integrate 的 remoteRefHas/remoteReleaseLines/
  remoteDevLines 三个闭包改派生自 remoteSnapshot；tny.publish.gate 的标签存证段 refMap 与
  下一补丁号段改走 GitFlow 方法。
- 收编完成的机械化验收：三个插件脚本中 `grep ls-remote` 零命中；GitCli 的直接调用者仅剩
  GitFlow 与各插件任务动作中的**执行类**命令（push/fetch/merge/switch/tag/reset）。
- 范围边界（有意不做）：任务动作上下文内与执行强耦合的状态查询（如记录目标分支原头的
  rev-parse、提取搬运标记的 log）留在原处——它们是动作序列的组成部分而非可复用查询，
  详见 design 的 scope 决定。

## Capabilities

### New Capabilities
（无。）

### Modified Capabilities
（无——本变更声明 `skip_specs: true`：纯内部结构收编，发布任务行为、门禁语义与错误文案零变化。）

## Impact

- `buildSrc/src/main/groovy/tny/convention/GitFlow.groovy`：新增远端查询方法面（含解析守卫）。
- `buildSrc/src/main/groovy/tny/convention/GitCli.groovy`：remoteTagPatches 的解析逻辑并入
  GitFlow.remoteSnapshot 派生链（GitCli 回归纯通道：只负责执行与取回，不再内置业务解析）。
- `tny.release.gradle`、`tny.integrate.gradle`、`tny.publish.gate.gradle`：ls-remote 调用点替换，
  头注通道句同步（"查询一律经 GitFlow"）。
- 文档：release-process.md 快速通道节的通道分界句微调一句。
- 回归基线：e2e 十八断言、门禁矩阵三例（标签存证与下一补丁号两条链是本次改动的直接受击面）。
