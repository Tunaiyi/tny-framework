# 实施记录摘要（apply 阶段各组结果）

## 各组结果

- **组 1（探针）**：JGit 经 SSH 访问真实远端可用；附注标签解引用提交号在 JGit 5.13 返回中
  缺失（定案：门禁不迁移）；status 未跟踪项归入 `unstaged.added`，排除该桶即与"只拦跟踪
  文件"口径一致。全过程只读，真实仓库零改动。详见 `probe-jgit-remote.md`。
- **组 2（release.gradle）**：真实执行路径的三处 API 错用（`branch.create` / `tag.create` /
  `push(refs:)`）按 4.1.1 实际接口修复；子进程仅保留 rebase、rebase --abort、
  rev-list --count、push --force-with-lease 四项。验证：`releaseCutAndTag -PdryRun` 在
  不带 `-PgitExe` 的情况下走通（脏清单、上游解析、远端撞名检查全经 grgit）；
  `releaseRebaseBack -PdryRun` 在开发线上按原拒绝文案正确拒绝；临时 bare 仓库演练
  （`/tmp/drill`）验证了切支、检出、附注标签、推送、拉取、建线检出、ONLY_FF 快进、
  推送的完整 API 序列——终态核对显示标签解引用仍指向原构建提交（证据语义保持），
  开发线快进到变基后头。
- **组 3（门禁）**：退让腿，`gradle/publications.gradle` 零编辑；门禁单模块直跑通过留证。
  详见 `gate-regression.md`。
- **组 4（文档）**：`docs/release-process.md` 快速通道与第 5 重校验段、
  `.claude/commands/tny/release.md` 的参数指引均已与新通道分界一致。
- **组 5（收口）**：`./gradlew tasks --group release` 与 dryRun 冒烟通过；配置期无异常。

## 受影响模块测试说明

本变更未触碰任何 `tny-game-*` 产品模块（全部改动在构建脚本与文档），故各组的
"运行受影响模块测试"以 Gradle 层等价验证替代（dryRun 直跑、单模块门禁直跑、临时仓库演练），
结果均已记录于上文与专项文件。

## 规格对照自查（任务 5.2）

release-versioning 的五个 Requirement 逐项确认不受影响：

1. **正式版版本串为裸三段号**：不受影响——版本派生逻辑在 `gradle/git.gradle`，本变更未触碰。
2. **快照坐标形态跨发布期保持稳定**：不受影响——同上，`parseProjectVersion` 零改动。
3. **发布资格由分支形态白名单决定**：不受影响——判定在 `publications.gradle` 门禁，
   本变更未编辑该文件（见 `gate-regression.md`）。
4. **历史后缀形态占用的版本号不得复用**：不受影响——黑名单读取与判定逻辑原样保留在
   `releaseCutAndTag` 与门禁两处，仅远端撞名检查换了查询通道，拒绝条件与文案不变。
5. **发布标签是正式版发布的前置存证**：不受影响——门禁该项明确不迁移；发布任务侧
   `tag.add` 默认产出附注标签，演练证明其解引用记录与 CLI `git tag -a` 同形（`^{}` 行在
   ls-remote 可见），门禁依赖的存证形态保持成立。

## 实施后补充：风格重构（用户直接指令，非行为变更）

verify 之后用户要求提高 `gradle/release.gradle` 的可读性、向 DSL 装配风格靠拢。重构把两个
任务的 `doLast` 主体拆为命名步骤闭包（校验、解析、计划文本、执行各归其位），任务体只剩
按序装配；分支形态正则提为共用常量。全部对外可见文本（任务名、description、计划与
错误与提示文案）逐字保留，检查顺序逐项不变。等价证据：重构后 `releaseCutAndTag
-PdryRun` 的计划输出与重构前逐行相同（含脏清单路径制格式）；开发线上 `releaseRebaseBack
-PdryRun` 的拒绝文案与重构前相同；`runGit` 子进程调用仍恰为四项
（rev-list/rebase/rebase --abort/lease 强推）；配置期冒烟通过。执行闭包内 grgit API
调用与参数与已演练版本逐字相同，未重复演练。
