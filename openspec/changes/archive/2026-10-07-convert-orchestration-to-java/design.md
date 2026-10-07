# Design

## Context

动机与并行协调前提见 proposal.md 首节；本册为纯内部载体迁移，规格工件按 `skip_specs` 缺席。开工前复核到的现状事实（本册立项当日开文件核对）：

- 编排线五文件全在 `buildSrc/src/main/groovy/`：`tny.git.gradle`（27 行，读 `gradle/branch-legacy-registry.txt` 并以 GitCli 门面一步创建根扩展 gitFlow）、`tny.release.gradle`（180 行，releaseCut/releaseTag 双语义任务）、`tny.integrate.gradle`（203 行，integrateMain/mergeUpward/retireGuard 三任务）、`GitFlow.groovy`（237 行，派生值与远端查询方法面）、`GitCli.groovy`（65 行，ProcessBuilder 通道门面）。
- `GitFlow`/`GitCli` 的消费面：编排线三脚本（本册随吸收退役）；发布族四脚本与 `tny.publish` 按类型/属性读取 `GitFlow`（Groovy 读 Java 类方向合法，零改动）；`ProjectsPlugin.apply` 尾部注入点现为 `findByName("gitFlow")` 加 `GroovyObject.getProperty` 弱型读取（装配线册 design D4 登记的编译墙降级），本册转正。
- 判定逻辑清单（拆分与单测对象）：版本号形态校验与占号黑名单（release 的 requireReleaseVersion/assertNotLegacyOccupied）、下一补丁号校验（GitFlow.remoteReleasedPatches 与 releaseTag 内 expected 计算）、开发线集成顺序检查（integrate 的 num 闭包与 lowerLines 判定）、合并结果完整性标记检索（autoMarkers 与 missing 判定）、退役检查（retireGuard 的未集成枚举与登记表行核对）、分支名与项目版本派生（GitFlow.parseBranchVersion/parseProjectVersion）、脏项口径（trackedDirtyPaths 的 porcelain 映射）、远端引用解析（remoteRefs 唯一解析点、remoteReleasedPatches 的解引用行计数、resolveRemoteName 上游推定）、标签形态（tagInfo 的 annotated/peel 映射与 describe 二次过滤）。
- 正则选点现状（本册触及文件内，判例表基础）：`==~`（matches 全串）用于 releaseVersion 三段号、main 首发 `.0` 尾段、integrateMain 的 dev 形态与 mergeUpward 的 release 形态、GitFlow 的 describe 结果与 release 分支注入值；`=~`/find 用于 num 闭包（`(\d+)\.(\d+)\.x$` 带前缀分支名——既有事故注记在册：误用 matches 令数值比较恒 -1、集成顺序与向上合并静默失效）、autoMarkers 的缺陷编号提取、remoteReleaseLines/remoteDevLines 的行过滤、releaseTag 的 propagated-as 提取。发布族四脚本与 `tny.publish` 另有约十处选点，本册不触、随判例表登记移交。
- redesign 在途册实况：其任务组 3/4 已合入（2e378afd 等），四文件现文即其最新形态；剩余 8.1/8.2 演练等真实发版事件，修复可能回落本册文件。跨册登记文件 `cross-volume-assembly-line.md` 已在 redesign 册卷宗备妥格式。
- 两卷方法论核对：模式卷第二节"同一操作多种协议可选"不适用（无策略分派需求）；本册复用既有先例——"薄壳＋类型化扩展经 getByType 点取"（GitFlow/ProjectsExtension 现状即该形状）与"通道与语义分居"（GitCli/GitFlow 分工，retire-grgit-channel 定型），转 Java 不改分工只改语言；门面先例 `tny-game-starter-*` 同向。

## Goals / Non-Goals

**Goals:**

- 编排线三脚本与两支撑类完成 Java 化：三枚脚本 id 随吸收注销、立口 `tny.release-ops`，根编排区三行收一行。
- 撤销全仓最后一处弱型反射：`ProjectsPlugin` 注入点转 `GitFlow` 类型化读取。
- git 事实判定全部落入可单测纯函数（`GitFacts` 与任务内判定段），发布快速通道的四段门禁判定首次获得红绿用例。
- 正则选点判例表成为入库工件，发布族面登记移交。
- dryRun 与任务名对外契约零变化（前后等值样本兜底），与 redesign 册的并行协调条款落地。

**Non-Goals:**

- 不动发布族五枚脚本与其 dryRun/门禁语义（发布族册，硬闸门在前）；不立 `tny.publishing-conventions`、不注销 `tny.integration-test`/`tny.benchmark-module` 之外任何在册 id；不改 `docs/release-process.md` 与 `.githooks/commit-msg`；不改任何任务名、description 文案、报错文案与 dryRun 输出格式（逐字保持是判据不是约束例外）。
- 不合并 release-ops 与既有装配线入口为单一总线入口（合并册议题）。

## Decisions

### D1 与 redesign 册并行：基线现锚＋双向修复落点条款，不等该册归档

依据：用户立项指令明示并行协调；该册修复先例（fc0af9bb）落 release/integrate/GitFlow/GitCli 的窗口是 8.1/8.2 演练期。条款三条成文：其一，任务组 1 开工核对时 `git log --since=<本册立项> -- <四文件>`，有新修复提交则基线锚顺延至新 HEAD 并重抓；其二，若本册先行合入后演练需改 git 语义，修复落点为 `GitFacts`/`GitFlow.java`/`ReleaseOpsPlugin.java`，修毕在 `cross-volume-assembly-line.md` 追加登记（镜像 `DependencyConventionsPlugin` 先例）；其三，modify/delete 冲突时以 redesign 语义为准、本册产物随迁（rebase 纪律按 CLAUDE.md 分支同步规则）。被否决的备选：等 8.1 发版事件后再开工——发布事件不可控且发布族册硬闸门同样挂在该册归档，串行将令整合程序停摆一个发布周期；仅做支撑类不动三脚本——`GitFlow` Java 化一半（脚本仍 Groovy 读 Java 类合法）但注入点反射与判定单测两个目标都不落地，收益不足以另立一册。
依据原则：P12（行为不变、账目先行）、P5（册按变更原因划分：本册原因=载体语言，redesign 原因=发布模型语义，同文件不同原因）。

### D2 GitCli/GitFlow 转 Java 保持外部形态逐面兼容：Map 返回、方法名、final 只读

`GitCli.run` 返回 `Map`（LinkedHashMap，键 exit/out/err）而非 record：Groovy 消费方 `r.exit` 属性访问对 Map 即键取值，record 访问器名（`exit()`）会断 Groovy 点读且 getter 命名（`getExit`）与 Map 键语义不同——保持 Map 使 release/integrate（迁移期仍存在的过渡消费）与任何漏网调用零改动。`GitFlow` 的 `tagInfo` 同理保持 `[annotated:, commit:]` Map；公开方法名逐字（`remoteRefs`、`remoteReleasedPatches`、`resolveRemoteName`、`trackedDirtyPaths`、`branchNames`、`tagNames`、`legacyIdentity`、`parseBranchVersion`、`parseProjectVersion`、`gitTag`、`gitCommitDateTime`、兼容访问器 `gitCommitId`/`gitHeadCommit`/`gitBranchName`/`gitBranchType`/`isConfigChange`/`isLegacyEligible`/`isReleaseVersion`）；final 字段加 getter（Groovy 点读兼容）。`Date.format` 两口径按 java.time 逐点改写：commitTime 经 `%ct` epoch 秒以**系统默认时区**格式化（语义"提交时刻换算到 JVM 默认时区"随等值用例锁定），buildTime 默认时区当前时刻；此两处为归档评审点名的高危机械翻译，用例钉死。
被否决的备选：record/不可变值类型化——发布族五脚本的 Groovy 属性访问面要同册改写（撞发布族册硬闸门），且 Map 形态是既有对外契约（tagInfo 消费在 release 现文）。
依据：P6（实现改语言不折语义）、P11 精神（跨册消费面是合同）。

### D3 判定拆入 GitFacts 纯函数类；GitFlow 保留通道簿记

`GitFacts`（同包，静态纯函数）收：`parseBranchVersion`、`parseProjectVersion`（含 release 分支注入值前两段一致校验）、`isBareTripleVersion`、`nextPatchExpected`（补丁号集合→期望值）、`porcelainTrackedPaths`（脏项行映射）、`parseLsRemoteLines`（唯一守卫解析，行不足两段丢弃的既有教训固化）、`releasedPatchesFromRefs`、`describeTagOrNull`（严格二次过滤）、`extractMarkers`（autoMarkers 的缺陷编号提取）、`sumGrepHits`（完整性检索行求和）。`GitFlow` 变薄：构造期派生与 cli 编排保留（属通道簿记），方法委托 `GitFacts`。release/integrate 的业务判定（形态白名单、占号黑名单核对、顺序检查、标记缺失判定、退役核对）以**入参为事实、输出问题清单**的静态方法拆入 `ReleaseGateCheck`/`IntegrationGateCheck`（任务动作内只做采集与抛错），红绿用例随附——满足"检查逻辑必须携带单元测试"。三脚本并入 `ReleaseOpsPlugin.apply` 时任务注册闭包结构逐字保留（doLast 内采集→判定→报错三段式），description/dryRun 文案零改动。
被否决的备选：判定留在任务闭包内——四段门禁首次可单测是本册立项理由之一（P13）；拆独立插件类（git/release/integrate 三二进制类保三 id）——与终态 9 id 收编方向相反、留下三枚过渡 id 待合并册再注销（P10 反例：为省一步多付一册）。
依据：P1（派生/解析与门禁判定与任务编排三类变更原因分离）、P5、P13。

### D4 验证设计：dryRun 前后等值＋分支场景沙箱＋判例表入库

对外契约面（任务名、description、dryRun 计划文案、报错文案）判据：基线抓当前分支可安全执行的命令面——`./gradlew tasks --all`（含五任务与 description 全文）、`releaseCut -PreleaseVersion=9.9.9 -PdryRun`（在 5.7.x 登记线走祖父轨预览分支，只读）、`integrateMain -PdryRun`（在 main 分支执行只预览——现分支不合法时**报错文案本身**也是前后等值样本，捕获非零退出的 stderr 全文即判据，不强行制造合法态）；真实执行路径不验证（写操作演练属 redesign 8.1 的实况通道）。分支形态差异场景（main/dev/release/祖父线四形态下 releaseCut/releaseTag/integrateMain 的判定分岔）以 `ReleaseGateCheck`/`IntegrationGateCheck`/`GitFacts` 单测覆盖（输入构造，不触真实 git）。判例表成文为本册 `apply-notes` 附表（文件、行号、原运算符、选定 matches/find、依据、本册转否、发布族移交行另表）。checker 四既有探针复跑（组号、零发布、托管、供给）确认注入点转正未破坏装配线。
依据：P13（每判据可运行）、D2 前册先例（形态断言＋样件入库＋破坏探针）。

### D5 正则选点逐条判例（本册面）

num 闭包保持 find（事故注记随行）；`==~` 五处（三段号、`.0` 尾段、dev 形态、release 形态、describe 结果、注入值前缀）保持 matches；`=~` 其余（autoMarkers、行过滤、propagated-as）保持 find。Java 化时**先抄表再翻译**：每个 `Pattern.compile(...).matcher(x).matches()/find()` 与表行一一对应，走查按表勾验。发布族与 `tny.publish` 的约十处在表内标注"移交发布族册"，本册 grep 判据禁止越界改动（防蔓延条款镜像装配线组 9.2）。
依据：归档评审 round-1 高危机械翻译项的既定处置（判例表+验收核对）；P13。

## 爆炸半径检查摘要（codegraph 与 grep 双口径）

- codegraph `analyze_impact`：`GitFlow.groovy:27`（modify）direct 1／low；`tny.release.gradle:16`（rename id 口径）direct 0／low——Groovy 脚本符号边视图有限，佐证不作权威（pilot 同款说明）。
- grep 权威口径：`GitFlow` 按类/按名引用＝编排三脚本（本册退役）＋发布族四脚本与 `tny.publish`（Groovy 读 Java 兼容，零改动）＋ `ProjectsPlugin`（本册转正）＋注释性引用若干；`GitCli` ＝编排三脚本＋`GitFlow`＋`tny.git` 薄壳；`tny.git`/`tny.release`/`tny.integrate` 三 id 的应用点唯一在根脚本三行；`releaseCut|releaseTag|integrateMain|mergeUpward|retireGuard` 的任务名字符串在 `tny.publish.gate`（门禁读标签/分支经 GitFlow 不读任务）、`docs/release-process.md`、`.github/workflows/publish.yml`（经 ./gradlew 命令行）与判例表登记——任务名零改动即全兼容。

## Risks / Trade-offs

- **[redesign 演练修复与本册撞同文件]** → D1 三条款（现锚、落点迁移、语义优先）＋开工核对提交史；冲突概率评估：该册组 3/4 代码已合入，剩余为演练驱动的偶发修复，窗口内概率中低。
- **[Date.format 双时区语义翻译出错]** → D2 点名的高危项：等值用例先行（用固定 epoch 断言 commitTime 字符串），翻译后跑同一用例。
- **[dryRun 样本受分支形态限制、覆盖面不足]** → 以判定类单测补足分支分岔矩阵；真实发版链路由 redesign 8.1 演练继续覆盖（两册互补，不假造执行面验证）。
- **[GitFlow 公开方法为 Groovy 动态调用方（漏网脚本）在新旧类间行为差]** → Java 方法加 `@groovy.transform` 无关；Groovy 调 Java getter 点读为既有语言保证；全仓 grep 调用点逐一过表兜底。
- **[注入线脚本的 legacy registry 解析（readLines+findAll+collectEntries）Java 化偏差]** → 解析拆 `GitFacts.parseLegacyRegistry(List<String>)` 纯函数＋用例（注释行、空行、`|` 分段 trim 三向）。
- **[文件数/行数净增延续整合程序总账]** → 编排面 Groovy 约 712 行换 Java 约 1000 行加测试约 500 行；收益为四段门禁首次可单测与反射清零（pilot 起一贯的诚实账）。

## Migration Plan

组 1 册门与并行核对（redesign 触碰史、基线锚定、判例表起草）→ 组 2 基线入库（任务图、dryRun 等值样本、注入线现值记录、耗时）→ 组 3 GitCli 转 Java（等价断言：现分支一次 `rev-parse` 冒烟）→ 组 4 GitFacts 与判定类测试先行（红→实现→绿）→ 组 5 GitFlow 转 Java＋ProjectsPlugin 注入转正（checker 四探针复跑）→ 组 6 ReleaseOpsPlugin 立口吸收三脚本（同提交：三脚本删除、注册三注销一新增、根三行收一行）→ 组 7 全量回归（九样件复比、dryRun 等值逐条、`-p buildSrc test`、`check --continue`、`publish --dry-run` 含门禁挂接、13 需求走查含判例表勾验）→ 组 8 收口（verify、批准归档、跨册登记回写与发布族册指针复核）。回滚：组间独立提交；吸收组单提交 revert 成对恢复。

## Open Questions

- `tny.release-ops` 与未来 `tny.publishing-conventions` 是否并为单一发布工程入口——合并册按目录页判据定。
- integrateMain 推送失败话术中的恢复指引文案是否随 redesign 演练修订——若修订，落 `IntegrationGateCheck` 用例期望值，判例表同步。

## 兼容性小节豁免说明

本册不改发布构件公共 API 与产物内容；对外任务名、描述、dryRun 与报错文案逐字保持（等值样件机械证明），故按项目 rules 不设 Compatibility Impact 小节。
