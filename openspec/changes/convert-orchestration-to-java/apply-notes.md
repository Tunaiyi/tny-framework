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
