# Apply Notes — convert-orchestration-to-java

本卷宗按任务组记录实施证据；正则选点判例表在组 1 起草、组 7 逐条勾验。

## 组 1 册门与并行核对

- 1.1 通过：`git log --since="2026-10-07 12:00"` 对编排五文件零命中（最近触碰为 10-06 22:44 的 5b1a26ff，其内容已在立项阅读锚定）；基线锚定当前 HEAD 无需顺延。redesign 在途册对五文件暂无新修复提交。
- 1.2 主规格核对：本册不新增脚本间相互引入（release-ops 为二进制实现类、内部按类组织，符合"约定插件接线规则按载体定"）；任务名字符串挂接（publish 谓词指向 checkPublishPrerequisites）属任务名非工程名点名，"共享构建脚本禁止枚举具体工程名"约束经既有谓词通道满足（legacy 登记表为数据文件非点名）。判例表初稿如下（本册面 13 处；发布族移交行见文末）。

## 正则选点判例表（组 1 起草，组 7 勾验）

| 文件与行 | 原运算符 | 语义选定 | 依据 |
|---|---|---|---|
| GitFlow.groovy:139 describe 结果 | `==~` | matches（全串） | 裸号 v<N.M.P> 判定，旧代 -RELEASE 形态必须拒绝（实测教训注记在册） |
| GitFlow.groovy:226 release 分支注入值 | `==~` | matches | 三段号形态校验 |
| GitFlow.groovy remoteReleasedPatches（Pattern.compile 处） | `m.matches()` | matches（显式） | 唯一解析点守卫，解引用行全串 |
| tny.integrate.gradle:26 num 闭包 | `=~`+find | **find** | 带前缀分支名全串不可达；误用 matches 静默 -1 的事故注记在册（e2e 第五轮） |
| tny.integrate.gradle:33/37 远端 release/dev 行过滤 | `=~`（findAll 真值） | find | 行含前缀 refs/heads/，非全串 |
| tny.integrate.gradle:48 autoMarkers | `=~`+.each | find | 提交说明中提取编号（部分匹配即得） |
| tny.integrate.gradle:57/113/185 分支形态门禁 | `==~` | matches | dev/release 全串形态白名单 |
| tny.integrate.gradle:76 lowerLines 过滤 | `==~` | matches | 同上形态判定 |
| tny.release.gradle:32 三段号 | `==~` | matches | 裸三段号规范 |
| tny.release.gradle:74 main 首发 `.0` | `==~` | matches | 尾段全串 |
| （登记）tny.integrate.gradle:34/38 `it - 'refs/heads/'` | 字符串减法 | Java 改 substring 前缀剥离 | 非正则，防误译登记 |
| （登记）release/integrate 内 `startsWith("refs/heads/…")` 两处 | 前缀比较 | 保持 startsWith | 非正则 |

发布族移交行（本册不改，交 convert-publish-family-to-java）：`tny.publish.gate.gradle` 形态段与豁免清单约七处、`tny.central.gradle` 分支守卫两处、`tny.publications.gradle` 与 `tny.publish.gradle`、`tny.github-packages.gradle` 合计三处——行号与选定随该册组 1.5 转录时现查现记。

## 组 2 基线快照入库

- 六样件与 README 入 `baseline/`（锚定 b410ea25，分支 5.7.x 为祖父登记维护线）：全量任务图（五编排任务与 description 全文在列）、`releaseCut -PreleaseVersion=9.9.9 -PdryRun` 祖父轨预览全文（成功路径样本）、`integrateMain/mergeUpward -PdryRun` 分支形态不合法的报错文案全文（等值判据样本各一）、`:tny-game-net` 派生 group/version（com.tnydev.game / 5.7.x-SNAPSHOT）、warm 耗时三连（2.21/2.24/1.94）。
- 口径注记：`-q properties` 于根工程取值为 unspecified 属现状（根不在 subprojects 派生面），派生样件按子工程抓取——改造前后同判据即等值。
- 避让清单维持：`tny-benchmark/results/bench-20261006-quick.json` 不入任何提交。

## 组 3 GitCli 转 Java

- `GitCli.java` 落地（Map 返回键 exit/out/err、`-PgitExe` 解析、LC_ALL=C、双流顺序读取、require 文案逐字，类头全部沿革注释随迁）；同一提交 `git rm GitCli.groovy`。
- 验证：`-p buildSrc test` 全绿、根 `help` 绿、全量任务图对基线一致（Python 正则剔噪后逐行等值）；坏 gitExe 负例报红路径可达（文案实况与同型说明已改写入任务判据句）。

## 组 4 GitFacts 与门禁判定类

- 任务书修正一处：原 4.1"未实现前提交并记录红状态"对全新类机械不可行（编译失败非测试红），按装配线册组 4 同型判据修正为"用例与判定类同批、红绿向指用例内两路径"，任务文字已改写。
- `GitFacts`（14 纯函数）、`ReleaseGateCheck`（9 判定，文案逐字承 tny.release 原句）、`IntegrationGateCheck`（7 判定+两形态谓词，承 tny.integrate 原句）落地；用例 21 个（GitFacts 11、Release 8、Integration 5 中部分合并计）全绿，`seriesKey` find 语义回归钉（dev/5.7.x→5007）、`releasedPatchesFromRefs` 解引用行独占、幂等标签三态、退役双检查各违例向均入列。
- 首跑一处红：missingMarkers 对"命中行截断"用例期望与实现保守语义（截断即缺失）不符——确认保守向正确（防检索失败被当无缺失），修用例期望并在实现 javadoc 成文。

## 组 5 GitFlow 转 Java 与注入点转正

- `GitFlow.java`（实现 `GitVersionSource` 契约，公开方法名与字段 getter 逐字、`tagInfo`/`remoteRefs` Map 返回保持、解析全委托 GitFacts）＋`GitVersionSource.java` 落地；`ProjectsPlugin` 注入段由 GroovyObject 反射改契约读取（装配线册 design D4 登记的编译墙降级就此回收，其原文补注留待收口统一处理）；`ProjectsPluginTest` 替身同批改实现契约（三用例语义不变）。同一提交 `git rm GitFlow.groovy`。
- 发布族消费面冒烟：`tny.publish.gate.gradle` 等四脚本读 `gitFlow.SNAPSHOT_PACK_SUFFIX`/`RELEASE_VERSION_SUFFIX`（Groovy 实例访问 Java 静态字段合法）与 `parseBranchVersion`（方法签名保持）经配置期与两线 POM 生成路径验证——`:tny-game-net`/`:tny-game-doc-gradle` 双 POM 对装配线册终态样件逐字节一致。
- 验证：`-p buildSrc test` 全绿（84 用例）、根 `help` 绿、派生值 group/version 与基线一致、全量任务图内容零差异（3562 行；比对剔噪口径增列 `> Task` 进度行并记 README 注记——基线抓时 buildSrc 重编译的进度残留，非任务图内容）、坏 gitExe 负例仍走构造器报红（`Could not create an instance of type tny.convention.GitFlow`）。

## 组 6 ReleaseOpsPlugin 立口与三脚本吸收

- `ReleaseOpsPlugin.java`（总入口，类 javadoc 载三段式目录页与 tny.git 布局边界沿革）＋`releaseops/` 三接线类（ReleaseChannelTasks、IntegrationChannelTasks、ChannelSupport）落地；五任务名、group、description、doLast 三段式与全部计划/报错文案逐字承三脚本。
- 同提交切换五件：删除 tny.git.gradle、tny.release.gradle、tny.integrate.gradle；buildSrc/build.gradle 注销三 id 并新增 tny.release-ops 注册行；根 build.gradle 编排三行并一（原行位置注释登记行序契约）。提交 d51f8a8d。
- 验证：根 `help` 绿；插件描述符 `tny.release-ops.properties` 指向 ReleaseOpsPlugin 且三旧描述符从产物消失（形态断言）；`tasks --all` 五编排任务名与 description 对基线零差异。

## 组 7 全量回归

- dryRun 等值逐条对基线：`releaseCut -PreleaseVersion=9.9.9 -PdryRun` 祖父轨计划文案逐字一致（首轮比对出现差异系当时工作树含本册未提交文件、脏树 warn 块逐一点名所致；提交后复比内容一致，终判通过）；`integrateMain -PdryRun` 与 `mergeUpward -PdryRun` 在 5.7.x 分支形态不合法的报错文案全文对基线逐字一致（非零退出照录）；`retireGuard` 无组 2 前基线样件（如实登记——其等值依据为报错文案逐字承脚本加 IntegrationGateCheck 用例双违例向覆盖）。
- 九样件比对：本册基线四样件加装配线册归档基线三 POM、deps、两任务图样件全部一致（Python 剔噪口径同装配线册 README）。
- `-p buildSrc test` 全绿；`check --continue` BUILD SUCCESSFUL；`publish --dry-run` 任务图绿且 BOM 门禁节点在位（计数 1）。
- CodOD 单工程样件 `:tny-game-net:tasks` 开/关两态比对：原始 diff 仅四行差异，均为开关自身的信息行首词（"Configuration on demand is an incubating feature."与弃用告警语），任务名集合终判一致（净差异 0 行）。
- warm 耗时三连 2.02/2.05/2.10 秒，对基线 2.21/2.24/1.94 无劣化（阈值 3 秒）。
- 长度界线拆类修复（7.3 走查发现 GitFlow.java 286 行、IntegrationChannelTasks.java 272 行越过主规格"扫读测试与长度界线"需求的单类 250 行界线）：GitFlow 远端查询段（remoteRefs 两个重载、remoteRefNames、remoteReleasedPatches、remoteBranchExists、resolveRemoteName）整体移入新支撑类 `GitRemoteQueries.java`，本地引用查询段（trackedDirtyPaths、branchNames、tagNames、tagInfo）移入 `GitLocalRefs.java`，git 输出读行小工具上收 `GitFacts.outLines/outLinesNonBlank`；IntegrationChannelTasks 四个私有辅助（requireRemote 集成面文案、head10、listProperty、remoteBranchesMatching）移入 `ChannelSupport`。GitFlow 与 IntegrationChannelTasks 的公开/既有方法名逐字保留为委托，ReleaseOpsPlugin 目录页与两 registrars 不受影响。拆类后复验：`-p buildSrc test` 18 个测试类全绿（结果时间戳核对非缓存跳过）、`tasks --all` 全量 3551 行内容对基线逐字一致、`releaseCut dryRun` 计划段六行逐字一致。
- 比对环境口径教训（登记）：`tasks --all` 等含发布面任务的比对命令 MUST 使用默认 `GRADLE_USER_HOME`——`~/.gradle/gradle.properties` 的 mavenCentral 凭据决定 centralSnapshots 发布任务是否创建（凭据守卫设计），隔离 user home 会整段缺 204 行 CentralSnapshots 任务，属比对环境差异而非构建差异。基线抓取即用默认口径。
- 触碰即改两处：GitFlow.java 许可头 Apache 条款文本在迁移时误写为"or as required by applicable law or"，已改回标准"Unless required by applicable law or agreed to in writing"；ChannelSupport 中拆类残留的无引用小段 branchLines 删除。

## 组 7.3 主规格 13 条需求走查表

| 需求 | 本册判定 | 依据 |
|---|---|---|
| 工程配置写声明式语句 | 通过 | 根编排区三行并一为单条 apply 语句；程序性行为全部位于任务注册闭包 doLast 动作块与 buildSrc 二进制类 |
| 任务注册与配置惰性形态 | 通过 | 五任务均 tasks.register(...)，配置段无 getByName 强制物化 |
| 依赖版本与坐标单一事实源 | 不适用（未触） | 本册零依赖坐标改动 |
| 托管版本面配置期对账 | 不适用（未触） | 同上 |
| Groovy 语言纪律 | 通过 | 编排三脚本与 GitFlow/GitCli 两 Groovy 类删除，无新增 Groovy 文本 |
| 区块顺序与版面组织统一 | 通过 | 根入口行留在原三行位置，行序契约注释在位（原行序由 ReleaseOpsPlugin 内部次序逐字承载） |
| 注释解释来由与预览通道 | 通过 | ReleaseOpsPlugin 类 javadoc 目录页列全部三段行为并指名所在类；-PdryRun 覆盖四个变更型任务 |
| 扫读测试与长度界线 | 通过（经拆类修复） | GitFlow 241、GitRemoteQueries 84、GitLocalRefs 71、GitFacts 238、IntegrationChannelTasks 238、ChannelSupport 129、ReleaseChannelTasks 227、ReleaseOpsPlugin 85、GitCli 99、ReleaseGateCheck 127、IntegrationGateCheck 118 行，全部 ≤250；根 build.gradle 71 行 ≤80；两起越线修复过程见上段 |
| 存量违例触碰即改 | 通过 | 许可头缺陷文本与死代码 branchLines 两处违例在触碰文件内即时修正 |
| 共享脚本禁止枚举具体工程名 | 通过 | 新增类零工程名点名；五任务名字符串经 1.2 判定属门禁挂接非点名，判例表已登记 |
| 工具链基线升级归属成册 | 不适用（未触） | 本册零版本/工具链改动 |
| 二进制检查逻辑携带单元测试 | 通过 | GitFacts 11 用例、ReleaseGateCheck 8、IntegrationGateCheck 5（三判定类全绿在案） |
| 约定插件接线规则按载体定 | 通过 | tny.release-ops 仅二进制单形态（注册行唯一，无同名脚本并存）；根脚本按装配线以单行 apply 引入；无脚本插件相互应用新增 |

判例表勾验：1.2 起草的表覆盖五文件全部 `==~`/`=~` 出现行——GitFacts 以 `Pattern.matcher(...).find()` 与 `matches()` 按表逐行落地（seriesKey 用 find 并有用例钉回归），发布族移交行原样保留待 convert-publish-family-to-java 转录，表与实现一致，勾验完成。

## 组 8 收口

- `/opsx:verify convert-orchestration-to-java` 报告：实现面零 CRITICAL（两条 CRITICAL 为 8.1/8.2 收口步骤自身）；两条 WARNING 与一条 SUGGESTION 全部按用户指令在归档前处置完毕（见下三条）。
- WARNING 1 修复：`ProjectsPlugin.java` 类 javadoc 的注入说明段原样描述转正前形态（GroovyObject 弱型反射、"根脚本现序 tny.git 先于本插件应用"），已改写为契约注入现况——读取经 `GitVersionSource`、根脚本序实名 `tny.release-ops`；旧反射仅保留"就此撤销"的沿革句。编译验证绿。
- WARNING 2 处置（任务 5.1 末句"consolidate-assembly-line 变更的 design.md 设计决策 D4 降级注记回写为'本册转正'"的执行落点）：已归档 change 目录 `openspec/changes/archive/2026-10-07-consolidate-assembly-line/design.md` 决策 D4 所登记的 GroovyObject 弱型反射编译墙降级，其效力于 convert-orchestration-to-java 归档时刻终结——GitFlow 已是 Java 类，注入经 GitVersionSource 契约（`ProjectsPlugin.java` 类 javadoc 与实现同文在案）。按"已归档 change 目录不追改"惯例以本条登记即为回写载体，已归档 change 目录的原文不追改。
- SUGGESTION 1 处置：retireGuard 缺前基线样件的实况补样义务移交 redesign-devline-integration-model——已作为附加条款写入其 `apply-notes/cross-volume-assembly-line.md`（与 8.2 先行合入条款同段登记），redesign-devline-integration-model 任务组 8.1 演练执行 retireGuard 时补抓真实输出样件入库。
- 任务 8.2 回写执行完毕：redesign-devline-integration-model 的
  `apply-notes/cross-volume-assembly-line.md` 追加第 4 条（本 change 已先行合入、git 语义
  修复落点含按单类 250 行界线拆出的两个支撑类、retireGuard 实况补样附加条款）；
  convert-publish-family-to-java 的 apply-notes 留档其任务 1.2 前置满足的机械复核依据。
  本 change 全部 16 个任务至此闭户。
