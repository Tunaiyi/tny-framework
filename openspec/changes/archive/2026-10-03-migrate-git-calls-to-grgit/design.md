# Design

## Context

动机见 proposal.md 的 Why。这里只铺技术事实与设计约束。

**两套 git 通道的现状。** `gradle/git.gradle` 引入 grgit 插件（org.ajoberstar.grgit 4.1.1，
底层 JGit 5.13）并以 `rootProject.ext.grgiter` 暴露；版本派生、门禁前四重校验、
`releaseCutAndTag` 的建分支建标签推送都从这一个对象出发。而 `release.gradle` 另定义了文件私有的
`runGit` 闭包（子进程调用，支持 `-PgitExe` 覆盖二进制），`publications.gradle` 的门禁在标签存证
校验里也自带一份 `runGit`。经下载 grgit-core 4.1.1 源码 jar 逐类核对，确认三件事：
一是 `releaseCutAndTag` 真实执行路径上的 `branch.create(...)`、`tag.create(...)`、
`push(refs: ...)` 在 4.1.1 中都不存在——分支服务只有 add/change/list/remove/status，标签服务
只有 add/list/remove，推送的引用列表参数名是 `refsOrSpecs`；由于 `-PdryRun` 预览在这些调用之前就
返回，真实路径从未执行过，缺陷没有暴露。二是 4.1.1 存在可替换裸命令的操作：status、
lsRemote（只支持 remote/heads/tags 三个参数，没有模式过滤）、fetch（remote/refSpecs）、
checkout（branch/createBranch/startPoint）、merge（head/mode，含 ONLY_FF）、remote.list、
branch.list 返回的 Branch 带 trackingBranch 字段。三是不存在独立 rebase 操作，push 只有
无条件 `force`（无 force-with-lease）。

**仓库环境事实。** 唯一远端 `github` 为 SSH 协议（`git@github.com:Tunaiyi/tny-framework.git`）；
本机存在无上游的分支（如当前的 `5.7.8.release`），门禁与发布任务的"单远端兜底"路径是活路径。
Gradle 的 configuration cache 在本项目未启用（`gradle.properties` 中注释状态），grgit 与配置
缓存不兼容的问题对本项目无碍。

**插件生态调研结论。** axion-release（1.21.2，需 JDK17 与 Gradle 7.6+）从最近标签推导版本
并提供打标签的 release 任务；nebula-release 与 reckon 同样按标签状态派生版本。三者的版本
模型都与 release-versioning 规格"版本由分支形态派生"冲突，且都不提供切支、变基合回的工作流。
grgit 上游已功能冻结（GitHub 仓库 2025 年 9 月归档迁 SourceHut，最新 5.3.2 且 5.0 起只发
Maven Central）。结论：没有任何插件能整体接管本项目的发布流程，可用空间在"把裸命令收敛到
已装的 grgit"。

**爆炸半径分析。** 按项目规则先尝试 codegraph：`codegraph_symbol_search` 检索
`parseBranchVersion` 等只命中 Java 源码符号，本仓库的 codegraph 索引不覆盖 `.gradle` 脚本，
故以全仓 grep 为准。`runGit` 是 `release.gradle` 与 `publications.gradle` 各自文件私有的闭包，
无跨文件调用方；`rootProject.ext` 的 git 符号消费方为 `git.gradle`（定义）、`release.gradle`、
`publications.gradle`、`central.gradle`（只读 `branchName`）；`-PgitExe` 的引用点在
`release.gradle`、`publications.gradle`、`docs/release-process.md` 与 `.claude/commands/tny/release.md`
四处。改动范围封闭在这四个文件加变更目录内。

## Goals / Non-Goals

**Goals:**

- `releaseCutAndTag` 与 `releaseRebaseBack` 的真实执行路径（非 dryRun）第一次做到"可运行"，
  方法与参数名对齐 grgit 4.1.1 实际接口。
- 两个脚本里的 git 操作收敛为单一通道原则：grgit 能表达的一律走 grgit，表达不了的
  （变基族与 lease 强推）留在子进程，且文件头注释如实写明分界理由。
- 门禁五重校验的判定结果、fail-closed 语义、记忆化行为逐项不变。
- 为 JGit 远端通道的可用性与返回形态建立可执行的验证手段，验证不过有明确退路。

**Non-Goals:**

- 不升级 grgit（保持 4.1.1），不新增任何插件或依赖。
- 不改版本派生逻辑、门禁判定规则、`central.gradle`、CI 工作流。
- 不实现发布流程新功能（例如自动核对制品仓）；两任务的对外任务名、参数名、dryRun 预览
  结构保持原样。

## Decisions

**D1：只用已装的 grgit，不引入新插件；裸命令能覆盖的项全部换 grgit API。**
依据 P10 三次法则与框架既有先例：grgit 已是本仓库 git 访问的既定抽象（`git.gradle` 起头、
三处脚本消费），本变更是向它收敛而不是另起炉灶。被否决备选：
（a）axion-release / nebula-release / reckon——三者版本由标签推导，直接违反 release-versioning
规格"版本由分支形态派生"的需求文本，且接管不了切支与合回，否决；
（b）在构建脚本里直接写 JGit 底层 API——比 `runGit` 更啰嗦且丧失 grgit 的 Groovy 友好面，
等于用一种手写替换另一种手写，否决；
（c）保持现状只修三个错用点——八类裸命令继续依赖本机 git 二进制与 `-PgitExe` 变通，
错失收敛机会，否决（用户已确认扩大范围）。

**D2：`git rebase`、`rebase --abort`、`rev-list --count`、`push --force-with-lease` 保留子进程。**
依据 P6"实现必须守约，不许打折"：grgit 的推送只有无条件 `force`，把它当 lease 用是拿更弱的
语义冒充契约（lease 的价值恰在"远端被别人推进过就拒绝"）；grgit 没有独立变基操作，pull 内嵌的
rebase 布尔不能表达"把当前分支重放到指定 base"。`rev-list --count` 是变基前置计数，与变基同族，
一并留在 CLI 使分界只有一条线。被否决备选：JGit 原生 `Rebase` API（经 buildscript 引入）——
JGit 5.13 的变基冲突语义与 CLI 不一致、且同样没有 force-with-lease，引入双份底层依赖，否决；
grgit `force: true` 替代 lease——违反契约，否决。

**D3：真实路径的 API 错用按 4.1.1 实际接口修复。** 建分支改为
`branch.add(name:, startPoint:)` 后接 `checkout(branch:)`（对齐原 `git switch -c` 的
"创建并检出"语义；JGit 建本地分支起点为本地分支时不设上游，与 CLI `autoSetupMerge` 默认一致）；
建标签改为 `tag.add(name:, message:)`（`annotate` 默认 true，即附注标签，等价 `git tag -a`，
满足门禁对解引用记录的依赖）；推送改为显式使用 `refsOrSpecs` 参数名。被否决备选：
升级 grgit 到 5.3.2 再按 5.x 文档编写调用——本次已拍板不叠升级变量，且 5.x 文档站同样指向
`refsOrSpecs` 形态的推送，升级并不能让 `refs:` 与 `create(...)` 变得可用，否决。

**D4：门禁的远端查询迁移以"只读探针验证"为前置门槛，验证不过该项退回子进程。**
`ls-remote` 换 `grgiter.lsRemote(remote:, tags: true)` 后，传输认证从系统 ssh 换成 JGit 自带
实现（JGit 5.13 默认 JSch），个人密钥若是新格式（如 ed25519）存在加载失败风险；探针即用真实
远端做一次只读查询，同时确认附注标签解引用记录在返回 Map 中的键名形态（`^{}` 后缀被 JGit 保留
成何种拼法决定门禁两行判定的取键方式）。探针通过则迁移并删除门禁的 `runGit` 与 `gitExe`；
探针不过则门禁该项保留现状，本次仅完成 `release.gradle` 侧（发布任务本身已经走 grgit 推送，
其可用性风险由 D6 的本地演练兜底）。依据 P13"每个设计决定都能回答哪个测试证明它成立"：
该分支决定由探针任务给答案，而非拍脑袋。被否决备选：迁移代码同时保留 gitExe 双通道自动回退——
一个校验函数两条通道，行为矩阵翻倍且掩盖密钥环境问题，否决。

**D5：判定不变的前提下允许文案细节随通道变形。** 脏文件清单从 porcelain 的 `XY 路径` 行改为
grgit `status()` 的 staged/unstaged 路径集合（判定"有未提交变更即阻断"不变，重命名在 JGit
下呈加删两条而 CLI 呈一条 R，均不影响阻断判定）；门禁"远端此刻不可达"的既有文案保持，异常
（含认证失败）统一捕获走同一条拒绝文案，fail-closed 语义原样。依据 P12：外部可观察的行为契约
（发布放行与否、任务成功与否）逐项不变，只有工具输出措辞随实现通道微调，不构成规格差量，
故维持 skip_specs。被否决备选：为措辞逐字对齐而包装转换层——为输出格式再造一层抽象违反
P10 的克制，否决。

**D6：真实写操作的验证一律落在本地临时仓库。** 分支创建、附注标签创建、普通推送这些会改
远端状态的动作，用 `init` 一个临时 bare 仓库并以 `file://` 作为远端来演练（克隆、跑任务、
核对 bare 中的 ref），不碰 GitHub；只有只读操作（ls-remote、fetch 探针）允许打真实远端。
依据 P13：每个修复点都有可运行的证明。被否决备选：等下次真实发布顺带验证——真实发布窗口
不是试错位，发布分支上的失败半程（建了分支没推出去）需要人工收拾，否决。

## Migration Plan

按 tasks.md 的任务组执行：先探针（只读，决定 D4 分支），再改 `release.gradle` 并本地临时
仓库演练，再改门禁，最后同步文档与命令说明。回滚策略：全部改动集中在四个文本文件，
单个 commit 落地，`git revert` 即完整回滚；门禁改动独立成 commit，可单独回滚。

## Risks / Trade-offs

- [JGit SSH 密钥加载失败导致门禁远端校验不可用] → D4 探针前置把关；失败即该项不迁移，
  门禁行为与今天完全一致。
- [lsRemote 无模式过滤，改为拉全量标签后客户端过滤] → 本仓库标签量级为几百条，单次开销可
  忽略；换来的是门禁不再依赖 git 二进制。
- [附注标签解引用记录的键名形态与预期不符] → 探针打印真实返回的键名后再定取键写法（D4）；
  即便写错，失败方向是把合法发布误判为存证缺失而拒绝，属 fail-closed 可自纠，不会产生错误放行。
- [grgit 上游功能冻结，Gradle 大版本升级时可能受阻] → 接受：本变更不依赖其新特性，
  且在 `release.gradle` 头注释中记录该事实，升级 Gradle 时将其列为检查项。
- [真实发布路径修复后首次执行仍可能有环境差异] → D6 的临时仓库演练覆盖写路径全流程；
  首次真实发布建议保留 `-PdryRun` 预览一步，与既有人工流程一致。
