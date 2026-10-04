# Tasks

本变更不触碰产品模块与公共 API，纯措辞统一（skip_specs）。gradle 命令本机需
`JAVA_HOME` 指向 JDK 21；文案对照抓样遵循 `revise-release-branch-flow` 归档卷宗的
环境口径（Corretto 21、正常 UTF-8 locale、剔除守护进程噪声行）。

## 1. 基线抓样

- [x] 1.1 改动前抓样留档：主仓执行
  `./gradlew releaseCut -PreleaseVersion=9.9.9 -PdryRun`、
  `./gradlew releaseTag -PreleaseVersion=9.9.9 -PdryRun`、
  `./gradlew releaseMergeBack -PdryRun` 三条（后两条在开发线上取拒绝文案），
  输出存 `terminology-baseline.txt`；同时 `grep -rn "冻结线\|发布容器\|容器分支"
  docs .claude/commands buildSrc/src/main/groovy` 的命中清单存
  `terminology-hits-before.txt`。完成判据：两文件存在且命中数与 19/10/22 基线同量级。

## 2. 术语替换（设计 D2 词组白名单，禁全词盲替换）

- [x] 2.1 `docs/release-process.md` 19 处：`冻结线` 改 `版本开发分支`、
  `发布容器`／`容器` 改 `版本发布分支`（简称"容器"首次出现处给括注 `（下称版本发布分支）`
  或直接展开全称）；按 D1 在角色总表首次出现处加规格映射
  `版本开发分支（规格条文中称开发线分支）`、`版本发布分支（规格条文中称发布分支）`；
  规格账本文件与 `openspec/changes/archive/` 一律不改。完成判据：该文件
  `grep -n "冻结线"` 零命中，`grep -n "容器"` 只剩非分支语义命中（如 Docker 容器）并逐条
  记录理由。
- [x] 2.2 `.claude/commands/tny/release.md` 10 处按同词表替换。完成判据：`冻结线` 与
  分支语义的 `容器` 零命中；三条发布命令的参数与任务名逐字未变。
- [x] 2.3 `buildSrc/src/main/groovy/tny.release.gradle` 22 处：中文计划、拒绝、完成文案中
  `容器`／`发布容器` 改 `版本发布分支`；英文 description 中 `container branch` 改
  `release branch`；`tny.git.gradle` 与 `tny/convention/GitFlow.groovy` 注释中按词表同步。
  任务名（releaseCut／releaseTag／releaseMergeBack）、参数名、判定逻辑、顺序、门禁
  与归档卷宗零改动。完成判据：脚本内 `grep -n "容器\|container"` 零命中（Docker 等
  他义命中逐条记录）；三任务的 doLast 控制流与判定点未变动。

## 3. 工位命名模板与 HTML 入库（设计 D3、D4）

- [x] 3.1 `docs/release-process.md` 流程一补特性工位命名式样
  `feat/<主>.<次>.x-<名>`（示例 `feat/5.8.x-actor-pool`），并加注记"名中版本号是目标
  预期而非承诺，延期不强制改名，以 PR 落点为准"；流程三／hotfix 相关步骤补
  `fix/<主>.<次>.<补丁>-<名>`（示例 `fix/5.7.9-sharedbuf`，补丁号对应实际目标发布分支）。
  不新增任何工具校验或 CI 检查。完成判据：两处命名式样与注记存在，脚本与工作流文件
  无因命名产生的改动。
- [x] 3.2 `docs/branch-model.html` 纳入版本管理（`git add`），并核对其术语与本册替换后
  口径一致、与 `docs/release-process.md` 无矛盾。完成判据：该文件不再是 untracked，
  且 `grep -n "冻结线\|发布容器" docs/branch-model.html` 零命中。

## 4. 收口

- [x] 4.1 文案回归对照：改动后再抓一次 1.1 的三条 dryRun 输出存
  `terminology-after.txt`；逐行与 `terminology-baseline.txt` 比对，差异必须全部落在
  被替换名词范围内（句式、顺序、判定点、拒绝路径均一致）。差异说明写入变更目录
  `rename-notes.md`。完成判据：`diff` 结果无名词外差异，主仓
  `./gradlew tasks --group release` 三任务在列。
- [ ] 4.2 提交（显式路径避免误扫并行会话在途文件）、推送 `5.7.x`、普通推送同步
  `main`（当前 main 是线直系后代）。完成判据：两端含本变更提交，工作区无本次遗留。
