# 验证记录（fix-publish-gate-upstream-ref）

## 1.1 命令等价（修复前／后同场对照）

- 修复前形式：`git rev-parse --abbrev-ref --symbolic-full-name '@{upname}'` →
  `fatal: ambiguous argument '@{upname}': unknown revision or path not in the working tree.`
- 修复后形式：同式 `'@{upstream}'` → 输出 `github/5.7.x`（现仓库 5.7.x 的上游）。
- 代码 diff：`buildSrc/src/main/groovy/tny.publish.gradle` 仅 1 行。

## 1.2 双远端场景对照实验（全克隆 /tmp/gate-clone5：单分支克隆 + 临时容器分支
`9.9.9.release`（上游指向 `origin/5.7.x`）+ 第二远端 `mirror`，跑
`:tny-game-bom:checkPublishPrerequisites`）

- **修复前**（克隆自带未修版本，天然对照组）：
  `发布被阻断… - 无法确定用于核对发布标签 'v9.9.9' 的远端：当前分支没有配置上游远端，且仓库远端数量不是 1`
  ——尽管上游明明已配置，死代码使其恒走兜底，双远端下误拒。缺陷坐实。
- **修复后**（拷入修复文件复跑，同场景）：
  `发布被阻断… - 发布标签 'v9.9.9' 在远端 'origin' 不存在（或远端此刻不可达）…`
  ——远端推定成功且**选中的正是上游所在远端 origin**（双远端时兜底逻辑本无法二选一），
  校验推进到真实的标签存证检查。修复生效的直接证据。
- 开发线回归：主仓库 `./gradlew :tny-game-bom:checkPublishPrerequisites`
  BUILD SUCCESSFUL（9 actionable tasks，含既有豁免展示行），改动不伤配置期与既有路径。

## 1.3 挂账交接

- 发布分支形态下门禁存证段的真实发布路径核对，随 `revise-release-branch-flow`
  挂账③在首个真实发布（5.7.9 或 5.8.0）同池执行（本次克隆实验已覆盖远端推定逻辑本身，
  该登记针对真实容器 + 真实标签的端到端一次跑通）。

## 附带发现（新挂账，非本册修复）

- **Gradle 构建无法在 git linked worktree 中运行**：验证首跑时 worktree 配置期即报
  `Failed to apply plugin 'tny.git' > Could not create an instance of tny.convention.GitFlow
  > Cannot get property 'id' on null object`（`grgiter.head()` 在 linked worktree 返回空——
  JGit 5.13 对 `git worktree` 支持缺失）。影响两处：本仓"用 worktree 隔离发布操作"的
  挂账纪律暂不可行；一切基于 worktree 的自动化验证须改用完整克隆。建议另立变更评估
  （升级 grgit/JGit 或在 GitFlow 构造中做 linked-worktree 适配）。
