# Design

## Context

双通道现状、18 个 grgiter 读点清单、worktree 禁令的因果链与两个历史事故
（grgit 写 API 缺失回退、JGit worktree 解析证伪）见 proposal 与归档
`migrate-git-calls-to-grgit`、`upgrade-grgit-for-worktrees`。动机补充：用户在
`retire-grgit-channel` 前置讨论中确认 A 路线，并追问禁令缘由——讨论确认禁令
唯一技术理由是 JGit 缺陷。爆炸半径：grgiter 消费方实测仅 tny.release 与
tny.integrate 两插件加 GitFlow 自身（grep 全仓核实，codegraph 对 Groovy 解析到
类粒度不可用，沿用 grep 口径）。

## Goals / Non-Goals

- 目标：全仓 git 能力单通道（GitCli 门面）；worktree 构建可用；grgit/JGit 依赖清零。
- 非目标：不改任何任务的外部行为、错误文案与门禁语义；不动归档工件；不借机重构
  GitFlow 的对外签名面；完整克隆纪律在发布快速通道保留为运营建议。

## Decisions

### D1 方向取"全 CLI"，"全 grgiter"单点否决
全 grgiter 路线被三个独立实测事实否决，不进入讨论：① grgit 4.1.1 无分支创建写
API（回退提交 976896b4）；② JGit ls-remote 对附注标签不给解引用提交号，标签核验
做不了（定案注 tny.publish.gate.gradle:109）；③ worktree commondir 不跟随，升级
6.10.1 证伪（复验脚本在归档卷宗）。而全 CLI 不存在对应障碍——git 命令行是能力的
超集。备选"维持双通道现状"亦否决：禁令、依赖、认知成本三项持续征税，读点数量
（18）表明切换成本有界。

### D2 GitFlow 构造改为 CLI 门面注入，查询以方法对外（P4 封装不变量的形态迁移）
`GitFlow(GitCli cli, Map registry, String releaseVersion)`：构造期用 CLI 求六个派生值
（`rev-parse --abbrev-ref HEAD`、`rev-parse HEAD`、`log -1 --format=%cI`），此后不可变
——构造时序与只读契约承自 expose-git-info-extension/gitinfo-self-init 两册既有设计，
仅数据源换通道。原 grgiter 暴露字段删除，替换为包内私有的查询实现与显式方法面：
`branchNames()`、`tagNames()`、`statusEntries()`（porcelain v1 原始行，供脏检查解析）、
`trackingRemote(branch)`、`describeNearestTag()`。脏检查口径保持"仅跟踪文件变更"
（staged+unstaged 之并）：porcelain v1 按状态码分类，`??` 未跟踪行丢弃——等价映射
表写进注释，防止 porcelain 与 grgit status 的桶位差异造成误判。
备选"各插件直接各自调 GitCli"否决：查询口径（porcelain 分类、describe 二次过滤）
必须单点拥有，否则回到四处实现的老路。

实现名对照（verify 第一轮 WARNING 的处置，落地以本段为准）：方法面最终为
`trackedDirtyPaths()`（即原设计的 statusEntries，直接返回跟踪脏项路径并内置 ?? 行过滤）、
上游推定并入 `resolveRemoteName(branch)` 单方法（原设计的 trackingRemote 不再独立存在）、
最近标签保留原名 `gitTag()`（原设计的 describeNearestTag；其 describe 双保险过滤注释系初版
实测教训，保名即保上下文连续）。保留原名与本决定的核心条款"对外签名不变、不借机重构面"一致。

### D3 子进程环境钉 LC_ALL=C，解析只信数据行
GitCli.run 的 ProcessBuilder 环境统一注入 `LC_ALL=C`（git 消息与日期格式受 locale
影响；porcelain/rev-parse/for-each-ref 数据行不受影响，但 describe 报错文案、
status 头注释类输出会）。本仓零差异抓样口径已有同型教训（locale 未钉导致中文
描述行失真），同源风险在配置期一次修净。porcelain v1 格式为 git 官方稳定承诺，
使用 `--porcelain=1` 显式钉版防 v2 默认化漂移。

### D4 worktree 守卫整体删除，运营纪律降级表述
tny.git.gradle 的 gitdir 判据段与其存在理由（JGit NPE 防疫）一并删除；恢复条目表
中 linked worktree 行改写为"发布快速通道操作仍建议完整克隆：避免与并行会话争用
HEAD 与 index——这是运营纪律，不再由构建拦截强制"。worktree 冒烟列入回归（本布局
从此有了机器验证，替代原禁令的静态正确性）。
已知边界如实记录：detached HEAD 下 `rev-parse --abbrev-ref HEAD` 输出 "HEAD"，
分支形态判定按非法形态拒绝——与 JGit 时代行为一致（publish.yml 的 checkout -B
兜底因此保留），归因注释按 proposal 修正。

### D5 依赖摘除顺序：先替换后摘除，全程可编译
任务序按"GitFlow 换心→消费点切换→守卫删除→插件与版本目录摘除"排列，每步
`./gradlew :tasks` 可编译；grgit 声明最后删，删除前 `grep -rn 'grgit\|ajoberstar\|Grgit\|grgiter' buildSrc build.gradle gradle/libs.versions.toml` 先行归零。

## Risks / Trade-offs

- [过程战果留痕（verify 第一轮 SUGGESTION 处置）] 本变更的回归网络首次在真实代码路径上拦下
  崩溃：remoteRefNames 对 ls-remote 输出行直取 `split[1]`，遇无空白分隔行数组越界
  （e2e 第十四轮暴露，守卫化修复见提交 03150803）——"外部行为不变"类变更的回归价值实证，
  详情在 apply-notes/regression.md，归档后随卷宗可考。
- [每次配置多约 3-5 次子进程 fork（原 grgit 为进程内读 .git）] → 单次 fork 约 10ms、
  配置期一次性求值，合计 <100ms 相对 gradle 启动可忽略；真实发布链路本就走 CLI，
  未新增通道。
- [porcelain 状态码分类与 grgit status 桶位映射出错，脏检查漏放或误拦] → 等价映射
  表入注释＋专项单测样本（干净、跟踪修改、staged 新增、未跟踪、删除五态各一）；
  回归中 integrateMain/releaseCut 的脏检查断言覆盖。
- [describe 语义差异（CLI 的 --match glob 与 JGit 不同方言）] → 沿用既有双保险：
  取回后严格正则二次过滤（该注释教训原样迁移）。
- [worktree 解锁后有人在 worktree 跑发布快速通道任务，争用共享 refs] → 运营纪律
  入文档；mergeUpward/integrateMain 的普通推送拒绝路径天然兜一半（对方话术指路）。

## Migration Plan

单变更落地；灰度不需要（构建期代码，回退等于 revert）。落地后旧环境（CI runner）
无新增要求（git 版本 ≥2.18 即有 porcelain=1 与 for-each-ref 全部所需能力，
ubuntu-latest 与本机 2.54 满足）。

## Open Questions

（无——worktree 解禁的运营理由去留在探索轮已与用户确认：纪律保留、禁令删除。）
