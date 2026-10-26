# Proposal

## Why

发布工具链对 git 的调用分两套：项目已经引入的 grgit 插件（`gradle/git.gradle` 以
`rootProject.ext.grgiter` 暴露），以及 `gradle/release.gradle` 与 `gradle/publications.gradle`
里手写的 `git` 子进程命令。经与 grgit-core 4.1.1 源码逐一核对，发现两个问题：其一，
`releaseCutAndTag` 真实执行路径（非 `-PdryRun` 预览）调用了 4.1.1 中不存在的方法与参数名
（`branch.create(...)`、`tag.create(...)`、`push(refs: ...)`；4.1.1 的实际接口是
`branch.add(...)`、`tag.add(...)`，推送引用列表的参数名是 `refsOrSpecs`），这条路径至今
没有真实执行过，一旦真跑会当场抛出方法缺失错误，属于潜伏缺陷。其二，仍有八类原始 git
子进程调用散落在发布脚本与发布门禁中，其中大部分 grgit 已有等价 API 可用；换成插件调用后
不再依赖本机 `git` 二进制，还能绕开本机需要 `-PgitExe=/usr/bin/git` 变通的架构不匹配问题。

插件生态调研结论（详见 design.md）：没有第三方 Gradle 插件能整体接管"切发布分支、打标签、
推送、变基合回"这套工作流；axion-release、nebula-release、reckon 这类发布插件全部按"从
git 标签推导版本号"运作，与本项目 release-versioning 规格"版本由分支形态派生"直接冲突。
因此本变更不引入新插件，只用好已装的 grgit。

## What Changes

- 修复 `releaseCutAndTag` 真实执行路径的三处 API 错用：建分支改用 `branch.add(...)` 后接
  `checkout(...)`，建附注标签改用 `tag.add(...)`，推送改用 grgit 4.1.1 实际存在的参数名。
- `gradle/release.gradle`：脏工作区检查、上游远端解析、远端列表、远端分支与标签撞名检查、
  拉取远端开发线、检出分支、`merge --ff-only` 快进、开发线普通推送，改走 grgit API；
  `git rebase`、`git rebase --abort`、`git rev-list --count`、`git push --force-with-lease`
  保留子进程命令行（grgit 没有独立的变基操作；其推送只有无条件强推，保护性弱于
  force-with-lease，不能打折替代）。`-PgitExe` 机制保留，但作用范围缩小到上述四个命令。
- `gradle/publications.gradle`：发布门禁的标签存证校验中，`git rev-parse @{upstream}`、
  `git remote`、`git ls-remote` 三处子进程调用改走 grgit API（远端查询走 JGit 通道）；
  门禁内删除 `runGit` 闭包与 `gitExe` 读取。"查不到即拒绝"的 fail-closed 语义原样保持：
  远端不可达或认证失败抛出异常时，捕获后按"标签在远端不存在"的既有文案拒绝发布。
- 文档与命令说明同步：`docs/release-process.md` 中 `-PgitExe` 的适用范围描述、
  `.claude/commands/tny/release.md` 的使用指引随改动更新。
- grgit 插件保持 4.1.1，不在本次变更中升级（重构与升级两个变量不叠加）。
- 版本派生逻辑与发布门禁五重校验的需求文本不变，本变更不修改任何对外行为契约。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无。本变更是构建脚本的内部等价重构加潜伏缺陷修复：制品版本串、门禁判定结果、
标签存证语义等 release-versioning 规格条目全部保持不变，故 `.openspec.yaml` 声明
`skip_specs: true`，沿 `fix-ci-unit-flakes` 等先例。）

## Impact

- **构建脚本**：`gradle/release.gradle`（两个发布任务的全部 git 调用方式）、
  `gradle/publications.gradle`（标签存证校验一处，门禁其余四重校验为纯本地判断不受影响）。
- **文档与命令**：`docs/release-process.md` 第 5 重校验描述与快速通道说明、
  `.claude/commands/tny/release.md` 的 `-PgitExe` 使用指引。
- **产品模块与下游**：无。受影响文件全部是构建脚本与文档，不触碰任何 `tny-game-*` 模块、
  公共 API 或 starter 模块，无下游兼容性影响。
- **依赖**：不新增；grgit 4.1.1（JGit 5.13 后端）继续作为唯一 git 插件。
- **关键风险（在 design.md 展开）**：门禁的 `ls-remote` 改走 JGit 后，SSH 远端
  （`git@github.com:...`）的认证通道从系统 ssh 换为 JGit 自带实现，个人密钥格式能否被
  JGit 接受必须在实现阶段先做只读验证；验证不过则该项退回子进程实现，其余改动照常。
