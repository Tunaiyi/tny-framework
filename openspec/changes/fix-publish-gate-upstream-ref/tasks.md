# Tasks

本变更不触碰产品模块与公共 API，项目规则中"公共 API 变更前置 JUnit 5 测试任务"不适用；
无规格差量（skip_specs）。gradle 命令在本机需 `JAVA_HOME` 指向 JDK 21；门禁 CLI 调用
本机执行追加 `-PgitExe=/usr/bin/git`。

## 1. 修复与验证

- [x] 1.1 把 `buildSrc/src/main/groovy/tny.publish.gradle:135` 的 `'@{upname}'` 改为
  `'@{upstream}'`，catch 兜底与取值逻辑原样不动。完成判据：`git diff` 该文件仅一行改动；
  同式命令实测 `JAVA_HOME=<corretto-21> git -C 仓库根 rev-parse --abbrev-ref
  --symbolic-full-name '@{upstream}'` 在 5.7.x 上输出 `github/5.7.x`（修复前为 fatal）。
- [x] 1.2 回归与行为验证：`./gradlew :tny-game-bom:checkPublishPrerequisites
  -PgitExe=/usr/bin/git` 通过（开发线不触存证段，证明改动不伤配置期与既有路径）；
  临时添加第二远端（`git remote add mirror <同一 URL>`）后执行
  `./gradlew releaseTag -PreleaseVersion=9.9.9 -PdryRun`（发布插件同样按上游推定远端，
  其拒绝文案应指向 `github` 而非"无法确定远端"——若报"已在目标容器执行"类文案则说明
  远端推定生效于插件侧；门禁侧同理由 1.3 的登记项在真实发布核对）。完成后
  `git remote remove mirror`。完成判据：两步输出如实记入变更目录 `verify-notes.md`。
- [x] 1.3 登记挂账合并项：发布分支形态下门禁存证段的真实远端推定（含多远端场景）随
  `revise-release-branch-flow` 挂账③在首个真实发布（5.7.9 或 5.8.0）一并核对，写入
  `verify-notes.md` 的交接段。完成判据：该段存在且指明并入的核对时点。
- [x] 1.4 提交并推送：单提交入库，推送 `5.7.x` 线并按既定快进模式将同一变更收上
  `main`（临时 worktree cherry-pick，零强推）。完成判据：两端 `git ls-remote` 均含
  本变更提交或其等价重放。
