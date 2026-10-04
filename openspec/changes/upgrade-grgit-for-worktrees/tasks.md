# Tasks

本变更不触碰产品模块与公共 API，无规格差量（skip_specs）。gradle 命令本机需
`JAVA_HOME` 指向 JDK 21。验证现场可复用 `/tmp/gate-clone5`（完整克隆）与
`/tmp/gc-wt`（其 linked worktree），若已被系统清理则按下述命令重建。

## 1. 环境检测与指向性报错（设计 D1）

- [x] 1.1 在 `buildSrc/src/main/groovy/tny.git.gradle` 的 `grgit.open` 之前加入
  linked worktree 检测：解析工作区 `.git` 指向的 gitdir，路径命中
  `<...>/.git/worktrees/<name>` 形态即抛 `GradleException`，文案含三要素
  （不能在 linked worktree 构建／JGit 对公共目录 ref 解析的上游限制、升级 grgit/JGit
  不解决／请改用完整克隆并附一条 `git clone` 示例命令）；另在 `GitFlow` 构造前对
  `head()` 判空留通用兜底，异常信息内嵌原堆栈摘要。完成判据：主仓
  `./gradlew tasks --group release` 与完整克隆内构建均不触发新报错（正常路径零变化）。
- [x] 1.2 反向验证不误伤：临时建一个 git 子模块现场（或复用 `obsolete/` 外任意非
  worktree 布局），确认检测不命中、构建正常；detached HEAD 主仓场景报的是兜底文案而非
  worktree 文案。完成判据：两种现场的实际输出记入变更目录 `verify-notes.md`。

## 2. 报错触发验证（正向）

- [x] 2.1 在 `/tmp/gc-wt`（不存在则：`git clone --no-checkout <repo> /tmp/gate-clone6
  && git -C /tmp/gate-clone6 worktree add /tmp/gc-wt2 5.7.x` 重建等价现场）运行
  `JAVA_HOME=<corretto-21> ./gradlew help`：必须命中 1.1 新报错且含"完整克隆"指引，
  不得再出现 `Cannot get property 'id' on null object`。完成判据：报错原文贴
  `verify-notes.md`。

## 3. 纪律落地与销账（设计 D2）

- [x] 3.1 `docs/release-process.md` 快速通道一节新增"发布与验证的执行环境"短段：
  发布相关 gradle 操作（三步任务、publish、演练验证）在完整克隆中进行，理由一句带
  worktree 限制与升级证伪的指向（引用本册 design.md Context 证据表）。完成判据：
  该段落存在且与报错文案口径一致。
- [x] 3.2 销账登记：变更目录 `verify-notes.md` 写明 `revise-release-branch-flow`
  挂账①由本册关闭、归档卷宗 verify-notes 的粗定性以本册 design.md 为准（不改归档），
  并把 JGit 6.10.1 复验三步（getBranch／resolve／gitdir 位置）写成
  `jgit-worktree-recheck.sh` 落本目录作 watch 工具。完成判据：两文件存在，脚本单独
  可重跑。

## 4. 收口

- [x] 4.1 主仓全量冒烟：`./gradlew tasks --group release` 三任务在列，
  `releaseCut -PreleaseVersion=9.9.9 -PdryRun` 计划输出与挂账前逐行一致（回归证明）。
  完成判据：零异常。
- [x] 4.2 提交（显式路径，防索引污染教训延续）、推送 `5.7.x`、按既定 worktree
  cherry-pick 模式上收 `main`。完成判据：两端 ls-remote 含本变更提交或等价重放。
