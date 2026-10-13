# Tasks

本变更不触碰任何产品模块与公共 API，项目规则中"每个任务组附 `./gradlew :模块:test`"以
Gradle 层面的等价验证（dryRun 预览、只读探针、临时 bare 仓库演练、门禁任务直跑）替代。
所有 gradle 命令在本机需 `JAVA_HOME` 指向 JDK 21；保留的子进程调用若在本机执行需追加
`-PgitExe=/usr/bin/git`（与现状一致）。

## 1. 只读探针（决定 design.md 决策 D4 的分支点）

- [x] 1.1 以 gradle init script（`gradle -I` 一次性脚本，不进仓库）在真实仓库上执行三项只读
  探测：`grgiter.lsRemote(remote: 'github', tags: true)` 确认 JGit 经 SSH 远端认证可用，并打印
  返回 Map 的全部键名（重点核对附注标签解引用记录是 `refs/tags/v...^{}` 还是 JGit 改写形态，
  据此确定门禁取键写法）；`grgiter.branch.current().trackingBranch` 在当前无上游分支上确认
  返回 null 而非异常；`grgiter.status()` 在人为改动一个跟踪文件后确认能列出不干净项。
  完成判据：三项探测都有输出结论且不留任何本地变更。
- [x] 1.2 把探针结论写入本变更目录 `probe-jgit-remote.md`（认证是否可用、解引用键名原文、
  trackingBranch 与 status 行为），并据此拍板 D4 分支：门禁迁移走哪条腿。完成判据：文件存在
  且后续任务 3.1 的分支选择与它一致。

## 2. release.gradle：修复真实路径并收敛通道

- [x] 2.1 `releaseCutAndTag` 的真实执行路径按 grgit-core 4.1.1 实际接口修复：
  `git.grgiter.branch.create(name:, startPoint:, checkoutOnCreate:)` 改为
  `branch.add(name:, startPoint:)` 后接 `checkout(branch:)`；`tag.create(name:, message:)` 改为
  `tag.add(name:, message:)`；`push(remote:, refs:)` 改为 `push(remote:, refsOrSpecs:)`。
  同步把该任务内的 `runGit` 调用替换为插件 API：`status --porcelain` 脏检查改 `grgiter.status()`
  的 staged 与 unstaged 路径集合（design D5：清单文案随通道变形，阻断判定不变）；
  `rev-parse @{upstream}` 与 `remote` 列表改走 `branch.list()` 的 `trackingBranch` 字段与
  `remote.list()`；`ls-remote` 撞名检查改 `lsRemote(remote:, heads: true, tags: true)` 后按
  键名前缀客户端过滤。完成判据：任务体内不再有 `runGit(...)` 调用（`releaseRebaseBack` 除外）。
- [x] 2.2 `releaseRebaseBack` 通道收敛：`fetch refs/heads/线:refs/remotes/...` 改
  `grgiter.fetch(remote:, refSpecs:)`；`switch 线` 与 `switch -c 线 base` 改
  `grgiter.checkout(branch:, createBranch:, startPoint:)`；`merge --ff-only` 改
  `grgiter.merge(head:, mode: 'ONLY_FF')` 并把 JGit 异常映射回现有的"请手工完成"文案；
  开发线普通推送改 `grgiter.push(remote:, refsOrSpecs:)`。保留子进程的仅四项：
  `rebase`、`rebase --abort`、`rev-list --count`、`push --force-with-lease`（design D2），
  `-PgitExe` 继续覆盖这四项。更新 `release.gradle` 头注释：写明通道分界、保留项的理由
  （grgit 无独立变基、只有无条件强推）与上游功能冻结事实。完成判据：文件内 `runGit` 调用
  仅剩上述四项；`JAVA_HOME=<jdk21> ./gradlew releaseRebaseBack -PdryRun -PgitExe=/usr/bin/git`
  在当前 `5.7.8.release` 分支输出的计划与改动前逐段一致（计划文本结构不变，脏清单允许路径
  格式差异）；同一命令跑 `releaseCutAndTag -PreleaseVersion=9.9.9 -PdryRun` 预览正常。
- [x] 2.3 写路径演练（design D6）：在临时目录 `git init --bare` 造远端、克隆出工作副本，
  用一次性 gradle init script 对克隆仓库依次执行 2.1 修复后的同一组 API 序列
  （branch.add 带本地分支起点、checkout、tag.add 附注、push 两个 ref、fetch、merge ONLY_FF），
  在 bare 远端核对：新分支存在、`v...{}` 解引用记录在、重放提交按预期。完成判据：演练
  全部断言通过且真实仓库与 GitHub 远端零触碰。

## 3. publications.gradle：门禁标签存证校验迁移

- [x] 3.1 按 1.2 拍板的分支执行。迁移腿（探针通过时）：`gradle/publications.gradle` 中
  `rev-parse @{upstream}`、`remote` 列表、`ls-remote` 三处改 `grgiter` 对应 API（取键写法用
  1.1 实测的解引用键名原文），删除门禁内的 `runGit` 闭包与 `gitExe` 读取；JGit 传输异常统一
  catch 后走既有"标签不存在（或远端不可达）"拒绝文案（design D4/D5：fail-closed 保持）。
  退让腿（探针失败时）：门禁保持现状不改，在本变更目录 `probe-jgit-remote.md` 追加一段
  "门禁不迁移的定案"记录原因，任务 3.2 仍执行（跑基线对照确认零改动）。完成判据：所选腿
  的判据达成且另一腿的改动零出现。
- [x] 3.2 门禁判定回归：先取基线——若 3.1 已改完，用改动前的脚本内容（`git stash` 或从
  `5.7.x` 分支取原版）在当前分支跑 `JAVA_HOME=<jdk21> ./gradlew :tny-game-bom:checkPublishPrerequisites -PgitExe=/usr/bin/git`
  记录结论与 findings 原文；改后同命令再跑一次（迁移腿不再需要 gitExe）。两次结论必须同为
  "放行"或同为"拒绝且逐项文案对应"（脏清单格式差异按 D5 允许）。若本地存在未跟踪脏项
  不影响门禁则照常执行；测试依赖（check 含 test）如需缩短可改跑单模块已在命令中体现。
  完成判据：对照记录写入本变更目录 `gate-regression.md`，两结论一致。

## 4. 文档与命令说明同步

- [x] 4.1 更新 `docs/release-process.md`：第 5 重校验中 `git ls-remote` 与 `-PgitExe` 的表述
  改为门禁现状（迁移腿：走 grgit，不再依赖 git 二进制；退让腿：维持原表述并加一句"发布任务
  侧已收敛"）；"快速通道"一节补两个任务保留的四项 CLI 与 gitExe 适用范围。完成判据：文中
  gitExe 描述与 `release.gradle`、`publications.gradle` 实际引用点逐一对得上。
- [x] 4.2 更新 `.claude/commands/tny/release.md`：`-PgitExe` 指引从"凡涉及 git 子进程的任务"
  改为"仅 `releaseRebaseBack` 的变基族与 lease 强推仍需要"（迁移腿下预览类任务不再需要）。
  完成判据：命令文件中的参数指引与两个 gradle 脚本的 `findProperty('gitExe')` 实际位置一致。

## 5. 集成收口

- [x] 5.1 全配置期冒烟：`JAVA_HOME=<jdk21> ./gradlew tasks --group release` 与
  `./gradlew releaseCutAndTag -PreleaseVersion=9.9.9 -PdryRun -PgitExe=/usr/bin/git` 各跑一次，
  确认配置期（`git.gradle` 在配置期开仓库）与任务执行期均无异常，dryRun 计划输出完整。
  完成判据：两条命令零堆栈退出；结果摘要记入变更目录。
- [x] 5.2 对照 openspec 的 release-versioning 规格逐条自查：本变更未改版本派生、未改五重
  校验判定、未改标签存证语义（skip_specs 的前提成立）；把自查结论一段话写入变更目录。
  完成判据：五个 Requirement 逐项标注"不受影响，理由"。
