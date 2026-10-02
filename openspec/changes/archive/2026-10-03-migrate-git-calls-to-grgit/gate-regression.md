# 门禁判定回归记录（任务 3.1 / 3.2）

## 3.1 分支定案：退让腿

按 `probe-jgit-remote.md` 的 D4 定案，`gradle/publications.gradle` 的门禁标签存证校验**不迁移**
（JGit 5.13 的 ls-remote 拿不到附注标签解引用后的提交号）。门禁文件在本次变更中零编辑；
当前工作区里该文件的 36 行差异全部来自并行进行中的 `central-publish-tnydev-group` 变更，
与本变更无关，标签存证校验代码块保持原样。

## 3.2 判定对照

- 改动前基线与改动后为同一文件内容，结论恒同构成立（无代码差异可比对）。
- 实际直跑一次留证：`JAVA_HOME=<corretto-21.0.12.1> ./gradlew :tny-game-bom:checkPublishPrerequisites -PgitExe=/usr/bin/git`
  在开发线 `5.7.x`（版本 `5.7.x-SNAPSHOT`）上 BUILD SUCCESSFUL——门禁四重本地校验全部通过；
  第 5 重标签存证校验只在发布分支形态触发，本次执行未涉及（其代码零改动，行为与
  `adopt-plain-ga-versioning` 归档时验证的语义一致）。
- 本变更对 `release.gradle` 的改动不在门禁执行路径上（门禁不读发布任务的闭包），
  两次校验相互独立。
