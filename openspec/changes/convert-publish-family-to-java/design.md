# Design

## Context

动机、闸门三条件与影响面见 proposal.md；本册为预立册——工件基于 2026-10-07 现文撰写，全部实施任务组在任务组 1 的闸门核对通过前不启动。立项当日核对到的锚点事实：

- 发布族五脚本现量：`tny.publish`（49 行：凭据 ext 四键、发布任务谓词挂接 `checkPublishPrerequisites`、属性断言 doFirst）、`tny.publish.gate`（236 行：七段核对合流、根级"分支+提交+标签"记忆化、fail-closed、豁免清单文件 `gradle/publish-prerelease-exemptions.txt`）、`tny.publications`（123 行：mavenJava 配置方、签名挂接、POM 元数据与 licenses/scm 段、未命名 maven 仓为 Nexus 路由默认名、centralSnapshots 声明）、`tny.github-packages`（58 行：githubPackages 命名仓注入与凭据守卫）、`tny.central`（116 行：nmcp 聚合挂根、发布分支守卫、Central 准入完整性前置校验、超时与凭据 providers 读取）。
- 前册实证可继承：nmcp all-in-one 构件（`com.gradleup.nmcp:1.1.6.2` 形态）经 buildSrc 供给后按 id 应用成功且扩展实例类加载器同一（探针三预演记录）；双声明并存为硬失败（同记录）——根两枚带版本声明行的退役必须与 buildSrc 供给同提交。
- 编排线册交付后本册前提成立：`GitFlow`/`GitCli` 为 Java 类，gate 与 central 的类型化读取无编译墙；正则选点判例表含本册移交行。
- redesign 册三个规格差量（release-versioning、central-publishing、branch-integration-gates）将整体改写 gate 七段与 central 校验的语义输入——本册拆类表按**其归档后现文**定稿是任务组 1 的硬性动作，本册工件中的段名与文案引用均为 2026-10-07 现文快照。

## Goals / Non-Goals

**Goals:**

- 五枚脚本同名 Java 化（id 保留），gate 七段判定拆纯函数类并首次配红绿单测，publish↔gate 建内部原子对。
- nmcp 供给点迁 buildSrc 单一落点（根两行退役同提交），dryRun 与任务名对外契约零变化。
- 判例表移交行落地；基线九样件在闸门后重抓并全绿。

**Non-Goals:**

- 不立 `tny.publishing-conventions`/`tny.central-ops` 入口、不改 `tny-game-bom` 自报行（合并册；id 保留即该模块文件零改动的直接理由）。
- 不改门禁段序、报错文案、豁免清单文件格式（逐字保持是判据）；不触碰 redesign 册归档前语义（等它）。

## Decisions

### D1 闸门即任务组 1，不过闸不动任何实施文件

核对动作五条（见 tasks 组 1）：redesign 册从活动目录消失且三个差量入账（主规格现文可查到三能力的归档后需求名）；编排线册归档目录存在；重读五脚本与三能力现文、与本册锚点事实做差比对并把差异回写本册 design（差异不为零时以现文为准修订拆类表，修订记录入本册 apply-notes）；重抓全部基线；判例表移交行成文。被否决的备选：现在就转换、归档后二次跟进——两遍语义随迁必然产生"转换版落后于 redesign 版"的中间态提交史，正违反"以归档后现文为基"的入账纪律；预立的正是这个等待。
依据：P12（行为契约先定稿再动代码，此处"契约"由 redesign 册持有）、pilot 放行边界条款（发布线批次以 redesign 收口为硬闸门，归档摘要已成文）。

### D2 五枚 id 全部保留、同名换轨

每枚一个原子提交：实现类入库＋注册行新增＋脚本删除（防呆沿装配线册教训：`git rm` 后不再对删除路径执行 `git add`）。根装配行、BOM 自报行、`publish.yml` 命令行全部零改动。被否决的备选：借本册一并立 publishing-conventions 入口收纳——入口化要求改 `tny-game-bom` plugins 块与根段行（第 2 层改写属合并册既定节奏，且 gate 语义若随入口重排会与本册"逐字保持"判据互相污染归因）；被否决备选二：id 转 Java 的同时注销——模块侧引用即断，违背 id 生命周期集中在合并册"吸收与注销同提交"的成文路线。
依据：P5（本册唯一变更原因是载体语言）、装配线册 integration-test/benchmark-module 同型先例。

### D3 gate 拆类：七段判定纯函数化，记忆化与通道留接线

`PublishGateCheck`（新，包 tny.convention.publish）：输入为事实快照（当前分支、身份登记、版本串、远端引用映射、标签信息、已发布补丁号、豁免清单行、路由仓库名清单），输出问题清单；七段合流顺序＝原 problems 追加顺序逐段成用例（每段一绿一红，含"远端不可达 fail-closed 报缺证"用例——输入直接给 null 映射）。接线类 `PublishGatePlugin` 只做：注册 `checkPublishPrerequisites`（名字串惰性 `dependsOn 'check'` 的既有教训注释随迁）、根级记忆化（按"分支+提交+标签"键的缓存语义逐字保持，缓存载体从脚本 ext 改实现类静态 Holder 并注明）、GitFlow 类型化取数。`tny.publish` 侧谓词闭包（`publishesToSharedRepository` 全串/前缀/ToMavenLocal 排除）拆 `PublishTaskNaming` 纯函数＋用例（含 ToMavenLocal 链不误挂回归——门禁只约束共享仓的既有语义）。
被否决的备选：判定留在闭包——七段门禁是本框架最重的 fail-closed 逻辑，无单测即延续"靠发版事故回归"的现状（P13）；把记忆化也拆纯函数——生命周期语义属接线，D3 先例（pilot checker 分工）。
依据：P1、P5、P13；先例：装配线 `DemoIsolationCheck`/`BenchmarkSuiteCheck` 拆法。

### D4 publish 内部按类应用 gate 成原子对

`PublishPlugin.apply` 首段 `project.getPlugins().apply(PublishGatePlugin.class)`（二进制互引为"约定插件接线规则按载体定"明文允许；PluginManager 幂等使根与 BOM 的显式 gate 行成无害重言）；挂接谓词的 `tasks.matching{...}.configureEach` 惰性形态与名字串 dependsOn 逐字保持。收益：0faf6edb 型半配对在结构上不可能再现（publish 在则 gate 必在）。登记为 design 事实供合并册入口化时继承。
被否决的备选：保持两枚独立互不感知——半配对缺陷刚修完就重归风险面，否决。
依据：P4（"发布任务必被核对"是 publish 的不变量，守约通道进构造）。

### D5 central 与 nmcp 供给迁移

`tny.central` 实现类内部 `apply("com.gradleup.nmcp.aggregation")` 后按名取扩展配置（nmcpAggregation 块三属性与超时 providers 链逐字）；同提交四件：buildSrc 增 `implementation 'com.gradleup.nmcp:nmcp:1.6.2'`（版本注释义务沿 dm/jmh 先例）、根两枚带版本声明行退役、根 `apply plugin: 'com.gradleup.nmcp.aggregation'` 行删除（入口内部应用）、javaProjects 段 `com.gradleup.nmcp` 裸 id 行保留（探针三实证通道，幂等）。`centralUpload` 的不可逆语义（推送 Portal）保持既有 `-PdryRun` 预览通道并纳入前后等值样件；完整性前置校验判定拆 `CentralIntegrityCheck` 纯函数＋用例（聚合成员缺 mavenJava/jar/sources/javadoc 各判红方向）。
依据：探针三预演实证；需求"不可逆编排提供预览通道"。

### D6 验证基线全部在闸门后重抓

九样件口径沿用装配线册（Python 正则剔噪）＋本册专属三条：`publish --dry-run` 两线任务名清单（门禁挂接边与 centralSnapshots/githubPackages 仓任务全列）、`centralUpload -PdryRun` 与 `centralBundle` 干跑输出、gate 七段红绿用例入 CI；checker 四破坏探针复跑；耗时阈值复读。任何 POM 比对对**新基线**，旧装配线册样件只作历史。
依据：P13；D1 的"差异不为零即现文为准"逻辑同样适用于样本。

## 爆炸半径检查摘要（codegraph 与 grep 双口径）

- codegraph `analyze_impact`（modify，五脚本文件）：Groovy 脚本视图 direct 0／low——佐证不权威（pilot 先例声明），以 grep 为准。
- grep 权威：五枚 id 的应用点＝根 javaProjects/gradleProjects 行、`tny-game-bom` 四行、编排入口无；`checkPublishPrerequisites` 字符串消费＝publish 谓词与 `docs/release-process.md` 叙述；`mavenJava`/`pluginMaven` 发布物名被 central/gate/publish 谓词三方按名消费（名字冻结）；`publish.yml` 消费 `generateReleaseIndex`、`publishAllPublicationsToGithubPackagesRepository` 等任务名（全冻结）；nmcp 扩展名 `nmcpAggregation`/`nmcp` 读取点＝central 与 publications。

## Risks / Trade-offs

- **[redesign 归档时语义演进超出本册锚点快照]** → D1 第五条硬动作（重读回写）兜底；最坏情形=拆类表局部重排，不动 id 与形态结论。
- **[gate 记忆化改静态 Holder 引入跨构建态残留]** → 记忆键含分支+提交+标签三元组（与原语义同），测试构建与新构建键值必然不同；用例钉"键不同即重查"。
- **[nmcp 供给迁移触 .github 工作流解析]** → publish.yml 消费的是任务名与命令行非声明行；干跑样件全列兜底。
- **[五枚同名转换体量集中]** → 每枚独立提交独立回滚，gate 与 central 各占两组，可跨会话分次执行。
- **[文件净增延续]** → 五脚本约 582 行 Groovy 换约 900 行 Java 加约 500 行测试；收益＝七段门禁与 Central 校验首次可单测、半配对结构免疫、id 与文案对外零漂移。

## Migration Plan

组 1 闸门核对（五动作全过才进组 2）→ 组 2 新基线入库 → 组 3 gate（测试先行→实现→同提交换轨）→ 组 4 publish＋原子对 → 组 5 publications → 组 6 github-packages → 组 7 central＋nmcp 供给四件同提交 → 组 8 全量回归 → 组 9 收口与跨册销假。回滚：逐枚提交 revert；组 7 回滚连带根两行恢复（同提交对偶）。

## Open Questions

- 合并册入口收纳次序（publishing-conventions 先收 java 线还是 BOM 先行）——本册产物就绪后在合并册定。
- gate 豁免清单文件是否随 redesign 归档演进格式——组 1 差异回写时定，本册按现文迁移。

## 兼容性小节豁免说明

不改发布产物与公共 API；对外任务名、仓名、文案逐字保持（新基线样件机械证明），按项目 rules 不设 Compatibility Impact 小节。
