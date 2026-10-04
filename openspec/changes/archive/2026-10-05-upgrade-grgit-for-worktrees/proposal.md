# Proposal

## Why

git linked worktree 中运行任何 gradle 命令都在配置期崩溃：`tny.git` 插件构造 `GitFlow` 时
`grgiter.head()` 返回空，报 `Cannot get property 'id' on null object`，错误完全不指向真实
原因（worktree 环境），每个撞上它的人都要从头考古（本会话实测：验证实验第一次就折在这里）。
原假设"升级 grgit/JGit 即可修复"已被一手实验证伪：JGit 6.10.1（grgit 5.3.2 自带版本）
对同一 linked worktree 解析 `resolve("HEAD")` 仍返回 null，而 `getBranch()` 正常——根因是
JGit 的 FileRepository 解析符号引用时不跟随 `commondir` 重定向到公共 refs 目录，5.x 与
6.10 皆然。因此本变更的落点从"升级修复"改写为"把不可用变成说得清的不可用，并把发布隔离
纪律落成可行形态"。

## What Changes

- `tny.git` 插件在打开仓库前检测 linked worktree 环境（gitdir 位于公共 `.git/worktrees/`
  之下即命中），命中时抛出指向性的清晰错误："gradle 不能在 git linked worktree 中构建
  （JGit 上游限制，升级不解决），发布与验证操作请使用完整克隆"；主仓与完整克隆行为零变化。
- 发布隔离纪律定案为**完整克隆**：`docs/release-process.md` 快速通道一节补环境说明，
  `revise-release-branch-flow` 挂账①（worktree 隔离不可行）就此销案。
- 证伪结论入册归档：明确"不要再用升级 grgit/JGit 这条路尝试修复"，留 JGit 上游 watch
  项；归档卷宗 `2026-10-04-fix-publish-gate-upstream-ref/verify-notes.md` 中
  "JGit 5.13 不支持 linked worktree"的粗定性由本册 design.md 的精确定性取代（归档卷宗
  不改写，本册是权威更正）。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无。环境适配与错误体验修复，不涉及任何发布行为契约；主账本 `release-versioning` 与
`gradle-build-style` 全部需求判定不变，故 `.openspec.yaml` 声明 `skip_specs: true`。）

## Impact

- **构建脚本**：`buildSrc/src/main/groovy/tny.git.gradle`（或 `tny/convention/GitFlow`
  构造入口，取检测最自然的一处）新增环境检测与报错；`build.gradle:2` 的 grgit 版本
  **保持 4.1.1 不动**（升级无收益，避免叠加变量）。
- **文档**：`docs/release-process.md` 一段；归档卷宗不动，更正记录入本册 design.md。
- **产品模块与下游**：无。主仓与完整克隆上的构建行为逐字不变，无 BREAKING。
- **销账**：`revise-release-branch-flow` 移交挂账①由本册关闭；`fix-publish-gate-upstream-ref`
  verify-notes 的附带发现条目由本册结论接棒。
