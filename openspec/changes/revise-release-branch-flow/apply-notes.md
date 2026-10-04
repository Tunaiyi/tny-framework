# 实施记录（revise-release-branch-flow 组 1 至组 4 执行结果）

## 基线校正（挂起记录恢复条件 3）

开工时两个并行前置变更已合入并改变了文件形态：`sweep-gradle-build-style`（提交 `0b4fca25`）
与 `adopt-gradle-official-dsl`（提交 `ba7ea66b`）把构建脚本迁入 buildSrc 约定插件、git 派生
事实改为类型化扩展 `gitFlow`（`tny.convention.GitFlow`），Gradle 基线升至 8.14.5。本变更
实施文件因此由 `gradle/release.gradle` 重基线为 `buildSrc/src/main/groovy/tny.release.gradle`；
三件工件的路径引用已同步校正（proposal、design、tasks 各自 Context/Impact/组 1 标题）。
`migrate-git-calls-to-grgit` 已于 2026-10-03 归档，其写通道统一目标由本变更定案终结
（终结说明即登记于本账本）。

## 组 1（插件重写为三步任务）——完成

提交 `ca6250dc`：`releaseCutAndTag` 拆为 `releaseCut`（切容器并推送、不建标签）与
`releaseTag`（容器 HEAD 建附注标签并单独推送）；`releaseRebaseBack` 更名重写为
`releaseMergeBack`（`git cherry` 补丁级去重后逐笔 `cherry-pick -x` 重放到线头、普通推送，
容器与标签任何路径不移动不推送；冲突自动 abort 回容器并打印人工等价命令）。
`--force-with-lease` 与变基族整体退役，`grep force-with-lease` 零命中。通道分界（D6）：
写与本地状态推进走 CLI（`-PgitExe`），纯查询走 grgit（`status`、`lsremote`、
`trackingBranch`、`remote.list`）——dryRun 预览实测不需要 gitExe。
判据核验：`./gradlew tasks --group release` 恰列三任务；`releaseCut -PreleaseVersion=9.9.9
-PdryRun` 计划含"标签由 releaseTag 在制品定型后创建"；开发线上 `releaseTag`/
`releaseMergeBack` 守护拒绝文案精确；旧任务名在 buildSrc 零残留。

## 组 2（临时 bare 仓库演练）——完成

方法口径：gradle 任务动作块锚定真实 `rootDir`，演练以 bash 逐字复刻任务动作块的 CLI 序列
（脚本 `/tmp/drill3/run.sh`，全程 file:// 远端，真实仓库与 GitHub 零触碰）。
- 2.1 常规序列：标签行+解引用行俱在（TAG-PAIR-OK）、零新提交跳过（SKIP-OK）、
  容器头==标签解引用（TRIPLE-OK）。
- 2.2 hotfix 序列：从标签切基、修复进容器、定型标签、线推进后重放——容器不可变
  （CONTAINER-IMMUTABLE-OK）、标签解引用仍指发布提交（TAG-IMMUTABLE-OK）、`-x` 出身记录
  在（PROVENANCE-OK）、`git cherry` 对账清零（CHERRY-OK）。
- 2.3 冲突路径：cherry-pick 冲突检出（CONFLICT-DETECTED）、abort 后线/容器/标签三方
  与执行前逐字节同值（RESTORE-OK）。
规格新增需求四场景对账见 `spec-scenarios-audit.md`；场景 4（事后重放构建过门禁）
登记为首个真实发布（5.7.9 起）的跟随核对项。

## 组 3（文档与命令）——完成

提交 `e97707b5`：`docs/release-process.md` 重写为三层分支模型总章（角色总表、七条生命
周期流程各按触发/命令/验证/禁止四段展开、契约变更声明、恢复手册、任务名映射）；
`.claude/commands/tny/release.md` 三步路径统一常规与 hotfix。旧任务名在两份文档中仅出现于
映射说明行。

## 组 4（仓库运维）——4.1/4.2/4.4 完成，4.3 留一项观察

- 4.1（提交 `35b8f8c7` 推送时执行）：`git push github 5.7.x:main` 一次性普通快进
  `315f54bf..35b8f8c7`，main 转正为开发顶点；GitHub 默认分支保持 main。
- 4.2：本地化石分支 `master` 已删（`git branch -d`，与旧 main 同头零独有提交）。
- 4.3：工作流通配改造完成并经 main 侧推送验证命中（Actions API 可见 `build` 工作流
  push/main 运行）；**线侧命中待观察**——`5.7.x` 分支当前分叉（远端一条 bench 回写提交、
  本地含基线会话已归档提交 `b4f6f595` 与本变更三提交），分叉收口（任一在途会话常规
  rebase 推送）后 `'*.*.x'` 通配将自然命中，与 main 同用一条目，无独立风险。此项不判完。
- 4.4：`decision-release-published-trigger.md`——`release.published` 触发器不纳入本变更，
  CI 发布通道维持 `workflow_dispatch` 口径。

## 挂账与提醒

- 提交卫生记录：本会话首次提交误将并行会话预暂存的 `.mcp.json` 等扫入（索引污染），
  已 `reset --soft` 复位后按路径分三批重做，`.mcp.json` 归还对方暂存区；教训：共享工作区
  提交必须用 `git commit -- <显式路径>` 而非依赖 `add` 后的索引快照。
- 根 `build.gradle:20` 注释更名已由基线会话提交 `b4f6f595` 顺带入库（内容正确、归属混入）。
- 下游迁移说明草稿在 `downstream-migration-notice.md`，投递时点为首个次版本线开线后，
  投递前需用户确认（任务 5.4 判据只要求文稿存在且与 3.1 声明一致，已满足）。
- `5.7.x-SNAPSHOT` 语义收窄已生效于文档层，实际生效时点=特性只进 main 的那一刻（4.1 完成）。
