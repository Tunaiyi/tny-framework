# Design

## Context

动机与范围见 proposal.md 的 Why 与 What Changes；判据细则见本变更 specs 差量。开工前复核到的现状事实（均在本册立项当日实测）：

- 运行时 Gradle 8.14.5；pilot-binary-build-conventions 已归档，其规格差量（八改一增）已同步入主账本——本册差量以该入账后现文为底稿书写（pilot 归档审计的"新差量须以归档后现文为基"结论）。
- `ModuleSetting.groovy:33` 的自检读 `rootProject.ext.has('javaProjects')`，根 ext 五键已在试点 D4 收敛中删除（`ProjectsPlugin` 只创建类型化 `projects` 扩展，无 ext 兼容垫片），三目兜底空集合使判红分支永不可达；`ModuleCheckerPlugin.groovy:83-84` 注释仍把该职责委托给这条死路径。当前两个 `enableUnpublished` 声明者（`tny-benchmark`、`tny-game-integration-test`）都不属于 javaProjects，所以缺陷今天不炸，形态是给未来的错误配置拆掉配置期报红闸。
- BOM 门禁半配对：`tny.publish.gradle:39-40` 对命中共享仓谓词的发布任务注册 `dependsOn('checkPublishPrerequisites')`，该任务唯一注册点在 `tny.publish.gate.gradle:226`；`tny-game-bom/build.gradle` 自应用 `tny.publish` 但未应用 gate（拆分提交 0faf6edb 只补了根装配线两条集合），任何根级发布命令的 `:tny-game-bom:publish*` 都会 Task not found。在途册 redesign-devline-integration-model 的祖父轨陪跑任务（下一次 5.7 补丁发版，tasks 8.1）正走根级 publish。
- 接线类 Java 化的类型前提：`io.spring.gradle:dependency-management-plugin:1.1.7` 可在 Maven Central 解析（buildSrc 现有仓库块即可取）；`com.gradleup.nmcp:1.6.2` 的 all-in-one 构件含全部插件描述符且 Central 可解析；`me.champeau.jmh:jmh-gradle-plugin:0.7.3` 不在 Central、需 gradlePluginPortal——后两枚本册不动用（见 D2），但探针顺带记录其形态供后续册引用。
- 已实测的接线先例：pilot 探针一证实 8.14.5 预编译脚本插件嵌套 apply 仍抛 `UnknownServiceException: No service of type ClassLoaderScope`；探针二证实二进制插件按 id 运行时 apply 第三方插件成功。本轮评审期在 /tmp 8.14.5 沙箱补测：java-platform 工程拥有 `check` 任务（BOM 补 gate 的 `dependsOn 'check'` 有落点）；对未创建的 publication 属性做 apply 期即时读取会在配置期抛 unknown property（接线次序承重的实证）。
- 在途册冻结面（沿用 pilot D8 先例）：`tny.release.gradle`、`tny.integrate.gradle`、`tny.publish.gradle`、`tny.publish.gate.gradle`、`GitFlow.groovy`、`GitCli.groovy`、`docs/release-process.md` 与 `.github/workflows/` 除 build.yml 外的一切，本册不改动。

## Goals / Non-Goals

**Goals:**

- 把"载体三形态、接线按载体、长度按类计量"写进权威规格，消除 pilot 留下的 CLAUDE.md 与账本双轨不一致（P12 行为契约先于代码，pilot 规则开道先例）。
- 证明"第三方扩展编译期类型引用"路线在 buildSrc 落地可行（探针三三判据），并以此完成对账接线类 Java 化——pilot D1 的语言例外就此撤销。
- 完成三枚薄脚本同名替换（compile-baseline、bom-platform、module-setting），连同支撑类 `ModuleSetting` Java 化与自检重锚，使"删除与注册同提交 + 形态断言 + 基线入库"三项新验收纪律自本册起机械化。
- 拆掉 BOM 门禁半配对雷并在册内销掉 pilot 两项观察账，为 redesign 册的 8.1 演练打通根级发布通道。

**Non-Goals:**

- 不建立总入口、不注销任何插件 id（那是装配线册与合并册的事）；`tny.git` 薄壳与 GitCli/GitFlow 不转 Java（消费方在冻结面）。
- nmcp、jmh 的 jar 不进本册 buildSrc 依赖（P10：本册无消费点）；nmcp/jmh 的版本声明行不动。
- 不改动 `tny.publish.gradle`、`tny.publish.gate.gradle` 本体（BOM 修复只加模块侧引入行）。
- 不做"统一标记插件"措辞修订（`tny.module-setting` 本册保留 id，该句尚未悬空，留给模块侧收编册）。
- 不回改已归档卷宗的原文（更正记入本册"文字账更正"小节，历史文本由归档件本身继续承担，见区块顺序需求的"注释条目直接删除——历史形态由版本库承担"同族纪律）。

## Decisions

### D1 本册范围取"规则开道＋探针＋三枚薄替换＋接线转换"，recorded assumption

被否决的备选：其一，整批全量 Java 化一次做完——发布族文件与编排线脚本受在途册硬闸门与冻结面约束，整批必撞车，且单册 diff 超出零差异可验收面；其二，本册连 `tny.git` 与 GitCli/GitFlow 一起转——它们被 `tny.release.gradle`、`tny.integrate.gradle` 以 Groovy 动态形态消费（Map 返回、`r.exit` 属性取值、`==~`/`=~` 混用共 26 处正则选点），转换必然触碰在途册演练标的，违背 pilot D8 冻结先例；其三，只做规则与探针、代码全部后移——`ModuleSetting` 自检修复（CRITICAL-1）与 BOM 拆雷都等不起，且三枚薄脚本是零消费方风险的最小验证面。
依据：P12（规则开道先行）、P5（变更原因分区：本册只承载"形态试点扩面与缺陷修复"一个原因）。

### D2 类型化路线只对 io.spring.dependency-management 在本册落地，版本单一落点迁至 buildSrc

探针三先行验证三判据（带类型编译通过；子工程按 id 应用成功；经类型化取回的 `dependencyManagement` 扩展实例的类加载器与 buildSrc 判定同一），通过后：`buildSrc/build.gradle` 增 `implementation 'io.spring.gradle:dependency-management-plugin:1.1.7'`（版本字面量按 pilot D5 先例以注释写明与主仓版本目录的关系——buildSrc 类路径独立于主仓目录，注释即登记义务），根 `build.gradle` 退役同插件的带版本 `apply false` 声明行，`configure(subprojects)` 的按 id 应用行保留（类改由 buildSrc 类路径供给）。这正是本册 ADDED 需求"第三方插件版本双声明点被判违例"场景的实施对象。
被否决的备选：反射式动态访问——零新增依赖且第二层零豁免，但把类型安全收益（pilot D1 的价值主张"收益只有在编译期才能兑现"）让渡掉，且接线类转 Java后仍以反射读 dm 扩展属于自设障碍；三枚第三方 jar 一次全迁——jmh 还需给 buildSrc 补 gradlePluginPortal() 仓库，而本册无任何消费点，属 P10 明确禁止的提前供给。
探针失败的降级：接线类 Java 化改反射形态（一行 `Method.invoke`），根声明行保留，nmcp/jmh 形态结论仅供后续册；降级形态记录进本册"探针结论"小节。
依据：P13（把架构断言换成可运行验证）、P10（版本落点迁移只随真实需求走）；先例：spring-framework 的 buildSrc 对 kotlin-gradle-plugin 等第三方插件即取 implementation 依赖单点形态。

### D3 零发布合同自检重锚类型化扩展并补测试，形态保留"声明即时报红"

`ModuleSetting` 转 Java 时，`enableUnpublished()` 的成员判定改为 `project.getRootProject().extensions.getByType(ProjectsExtension.class).javaProjects()` 按类型拉取，扩展不在位直接抛错、MUST NOT 保留任何 `has()` 兜底；随附单元测试三向：线内命名（`tny-game-core` 形态）声明即违例（断言文案含工程路径与 javaProjects 字样）、线外命名（`-integration-test` 后缀或无 `tny-game-` 前缀）不抛、根未应用 tny.projects 时报"扩展不在位"（复用 `ProjectsExtensionTest` 的 withParent 夹具）。端到端保留一条破坏探针：临时给 `tny-game-net/build.gradle` 加 `id 'tny.module-setting'` 与 `moduleSetting { enableUnpublished() }`，`./gradlew :tny-game-net:help` 配置期应报红，探针后 sha256 对账还原。API 外形保持 Groovy 消费方兼容（`enum Mode`、`has(Mode)`、`static enabled(Project, Mode)`、`void enableXxx()`），三个模块的 `moduleSetting {}` 声明块不动。
被否决的备选：把成员判定并入 `UnpublishedContractCheck` 的依赖边核对——报错位置退到被依赖方评估期，失去"声明处即时、文案直指本工程"的语义（改造前的活的形态），且两类校验的变更原因不同（P5）；保留 `has()` 兜底仅改键名——兜底吞判正是本次事故本体，禁止重蹈。
依据：P4（"零发布与发布线成员互斥"是该扩展的不变量，自检是守约通道，修复即恢复封装）、P13（三向单测＋一条端到端探针）；pilot 新需求"二进制实现的检查逻辑必须携带单元测试"对 Java 化后的支撑类判定即刻适用。

### D4 BOM 门禁修复取模块侧补一行，而非入口内自动应用或根侧新装配段

`tny-game-bom/build.gradle` 的 plugins 块在 `id 'tny.publish'` 之后补 `id 'tny.publish.gate'`（与根装配线两条集合的原行序同构：publish 先、gate 后）。
被否决的备选：其一，`tny.publish` 内部自动应用 gate（原子对形态）——`tny.publish` 此刻仍是预编译脚本插件，脚本间嵌套 apply 在 8.14.5 仍复现类加载器缺陷（pilot 探针一），该形态只能在发布族二进制化时建立（归合并册，本册 ADDED 需求已为其开门）；其二，根脚本新增"按 isBom 谓词的装配段"——需要扩展新增派生集合与根各加一行，生命周期止于合并册，且给一个即将退役的形态加临时装配，改动面大于收益。模块侧一行属模块声明面，不在"共享构建代码禁点名"禁列；`java-platform` 拥有 `check` 任务，gate 的 `dependsOn 'check'` 有落点（沙箱实测）。
依据：P5（缺陷在哪漏就在哪补——补行落点即当初漏点）、D2 of 0faf6edb（门禁拆分的成对引入语义）；修复差异登记为本册零差异判据唯一预期差异豁免（任务图面：`:tny-game-bom` 新增 `checkPublishPrerequisites` 节点与挂接边；POM 面不受影响——`generatePomFileForMavenJavaPublication` 与 `publishToMavenLocal` 均不命中门禁谓词）。

### D5 三枚同名替换的机械化验收：删除与注册同提交、形态断言、基线五样本入库

每枚替换在单个提交内完成"删除 `buildSrc/src/main/groovy/tny.<id>.gradle` ＋ `buildSrc/build.gradle` gradlePlugin 块新增注册行 ＋ 实现类文件"；验收形态断言两条：产物插件描述符（`build/pluginDescriptors/tny.<id>.properties` 或 jar 内 META-INF）的 implementationClass 指向二进制实现类；对账或门禁的破坏探针堆栈判红帧落在实现类且无脚本桥接类帧。本册首提交在 `openspec/changes/consolidate-binary-conventions/baseline/` 入库基线五样本（全量任务图、`tny-game-net` 双配置依赖清单、java 线与插件线双 POM）与 HEAD 的 `:tny-game-bom:publish --dry-run` 报红记录，抓样执行 context 的"零差异验收基线抓样口径"（同 daemon、UTF-8 locale 钉住、转存型样件先运行生成任务）。
理由：pilot CRITICAL-1 实证"分提交丢删除、并存脚本静默优先、行为判据全绿失明"，形态断言是唯一免疫的判据；"只记行数"的 apply-notes 被归档审计判事后不可复核，入库样本是 fix-dependency-version-governance 的既有先例（模式卷 M1：同类问题已有解法，照抄先例而非另起）。
依据：P13、P12；先例名：`openspec/changes/archive/2026-10-03-fix-dependency-version-governance/baseline/`。

### D6 三枚实现类的等价迁移要点

`CompileBaselinePlugin`：`configurations.configureEach` 的两条缓存策略与 `tasks.withType(AbstractCompile/Javadoc).configureEach` 的 encoding 钉法逐句等价，`encoding` 属性经 `project.property("encoding")` provider 读取（pilot 需求二"属性读取 SHALL 优先 providers"）。`BomPlatformPlugin`：经 `project.getParent().getExtensions().getByType(ProjectsExtension.class)` 取根扩展（替代脚本的 `parent.extensions`），constraints 派生循环、`isIntegrationTest` 排除与按名排序逐句等价（程序性生成留在载体内，符合需求一）。`ModuleSettingPlugin`：一行 `extensions.create("moduleSetting", ModuleSetting.class, project)`，`ModuleSetting` 构造入参类型随 Java 化。三枚头注释/类 javadoc 按需求七承接原脚本文件头的职责边界与 provenance 逐段随迁，并指名目录页义务暂不适用（各为单类单元）。
依据：模式卷 M1——"薄壳＋类型化支撑类经 getByType 点取"是本仓既有形态（GitFlow、ProjectsExtension 两先例），照抄不另起炉灶；适配器先例（`ScriptExprContext`）不适用，理由：buildSrc 侧是编译期类型供给问题而非运行期接口翻译问题，解法是依赖声明不是适配器。

### D7 接线类 Java 化的等价面与时机分工

`ModuleCheckerPlugin.java` 三段时机逐字保留（组号对账与托管版本面对账在 `gradle.projectsEvaluated`、零发布合同在 `moduleProjects().afterEvaluate` 逐边）；`configure-on-demand` 已评估过滤、自依赖边不进强制评估、`evaluationDependsOn` 沿边语义原样；dm 托管快照读取改 `DependencyManagementExtension.getManagedVersions()` 类型化调用；`rootProject.project(d.path)` 弃用替代保留；对 `ModuleSetting` 的交互恢复编译期类型。类头 javadoc 承接原 Groovy 文件全部职责边界与三检查类分工注记，并把"D1 语言例外"改写为"例外于本册随类型化路线撤销"的完整沿革句。按 pilot D3 分工，接线类不写单测，红绿由三类既有单测＋本册新增两条端到端破坏探针承担。
依据：P1（判定与接线分离已是试点交付形态，本册只换语言不换形状）。

### D8 观察账两项与文字账更正的登记方式

两项观察账在本册任务清单指名执行（托管版本面端到端探针：临时把 `gradle.properties` 一个守卫族版本键扰动一个小版本，判红、逐项列出声明值与生效值、还原、sha256 对账；耗时对照：改造前基线一次、全替换完成后一次，warm daemon `./gradlew help` 三取，劣化超 3 秒即登记移交后续合并册）。文字账四处更正（proposal 消费面枚举漏支撑类、五路复验 PASS 声称范围不实、任务 7.2 登记缺位、谓词数量记载三实际四）与 grep 范围教训改写（扫描面必须覆盖根构建脚本、约定插件载体、模块构建文件、buildSrc 支撑类四类）记入本册 apply-notes 的"归档账目更正"小节，归档时随本册卷宗可检索；已归档卷宗原文不回改。CLAUDE.md 的归档前裸路径引用在规则开道任务中改写为 `openspec/changes/archive/2026-10-07-pilot-binary-build-conventions`。
依据：需求"构建工具基线升级与工具链告警归属成册"的同族纪律（移交必须指名承接）；P13。

### D9 批次路线图与 9 个 id 终表（供后续册引用，本册不执行）

后续册次：装配线册（`tny.dependency-conventions`、`tny.java-conventions`、`tny.plugin-conventions` 立口，`java-module`、`compile-baseline`、`plugin-module`、`dependency-management` 吸收即注销，同提交锁死）→ 【硬闸门：redesign-devline-integration-model 归档且其三个规格差量入账本】→ 发布族册（publish/gate 原子对、publications、github-packages、central 转换）→ 编排线册（git/release/integrate 与 GitCli/GitFlow，含 26 处正则选点判例表）→ 合并册（终态乙四模块 plugins 块改写、module-setting 等 id 溶解、需求"统一标记插件"措辞修订、buildSrc 清空 Groovy 并删除 `groovy`/`groovy-gradle-plugin` 两行）。终态 9 个注册 id：`tny.projects`、`tny.module-checker`、`tny.dependency-conventions`、`tny.java-conventions`、`tny.plugin-conventions`、`tny.bom-conventions`、`tny.release-ops`、`tny.central-ops`、`tny.benchmark-conventions`。全部退役动作集中在各册"行为吸收＋id 注销"同提交完成；总入口内部接线次序逐字等于被收编行原行序（BOM 线原序与 java 线相反，入口不得共用同一顺序常量——评审确证的接线表条目）。
依据：pilot 放行边界条款（试点不放行后续批次，各册另立）；本节仅登记路线，不构成实施授权。

## 爆炸半径检查摘要（design 规则要求的 codegraph 与 grep 双口径）

- codegraph `analyze_impact`：`ModuleSetting.groovy:31`（modify）direct 1／total 1／risk low；`ModuleCheckerPlugin.groovy:60`（modify）direct 1／total 1／risk low。Groovy 预编译脚本文件的符号边视图有限，按"无隐藏调用方"佐证读，不单独立为权威。
- grep 权威口径：`ModuleSetting` 消费面＝三个模块的 `moduleSetting {}` 声明（`tny-benchmark/build.gradle:13`、`tny-game-integration-test/build.gradle:14`、`tny-game-net-demo/build.gradle:7`）＋ `ModuleCheckerPlugin.groovy:104` 沿边 `enabled` 查询＋ `tny.integration-test.gradle` 的应用蓝本核对段；`tny.compile-baseline`／`tny.bom-platform`／`tny.module-setting` 的按名引用＝根装配线两行＋BOM 一行＋三个模块各一行；接线类应用点唯一（`build.gradle:39`）。`rootProject.ext` 残余动态读取全仓扫描仅 `ModuleSetting.groovy:33` 一处（即 D3 修复对象）。

## 探针结论（探针三，2026-10-07 实测，Gradle 8.14.5，沙箱 /tmp/probe3-cbc，产物已删除不入库）

- **dm 目标形态（变体 A，buildSrc 声明 implementation 依赖、根脚本零第三方声明）三判据全过**：`DependencyManagementExtension` 编译期类型引用装配成功；子工程由二进制插件按裸 id 运行时应用成功（等价 `configure(subprojects)` 的应用行在根无声明的情况下照常解析命中，`subAppliedByBareId=true`）；经类型化取回的扩展实例与插件实现类同由 buildSrc 作用域类加载器加载（`classloaderIdentity=true`，两侧加载器同为 `ClassLoaderScopeIdentifier.Id{coreAndPlugins:settings[:]:buildSrc[:](export)}`）。D2 的降级分支不需要启用，根脚本的 `io.spring.dependency-management` 带版本声明行可退役，版本单一落点移至 buildSrc 依赖声明。
- **变体 B（根带版本 `apply false` 声明行与 buildSrc 依赖并存）为硬失败**：报错原文 `Error resolving plugin [id: 'io.spring.dependency-management', version: '1.1.7', apply: false] > The request for this plugin could not be satisfied because the plugin is already on the classpath with an unknown version, so compatibility cannot be checked`。由此把 D2 的执行约束升级为原子条款：**buildSrc 增加该 implementation 依赖与根脚本退役对应声明行必须是同一提交**，不存在可观察的双声明过渡态；任务 7.1 与 7.3 合并为一个提交执行。
- **nmcp 顺带记录（供发布族册引用）**：all-in-one 构件 `com.gradleup.nmcp:nmcp:1.6.2` 经 buildSrc 供给后按 id 应用成功且类加载器同一；其插件在应用时强制要求 `maven-publish` 已在位（报错原文 `Nmcp: plugin 'maven-publish' must be applied`），故后续总入口内部接线次序必须保证 maven-publish（经 publications 或显式）先于 nmcp——本行与"BOM 线原行序 publish 先于 nmcp"的既有接线表条目互为印证。
- **jmh 顺带记录（供装配线册引用）**：`me.champeau.jmh:jmh-gradle-plugin:0.7.3` 在仅 mavenCentral 的 buildSrc 仓库下解析失败（`Could not find me.champeau.jmh:jmh-gradle-plugin:0.7.3`），补 `gradlePluginPortal()` 后按 id 应用成功且类加载器同一；届时 `buildSrc/build.gradle` 的仓库块需要增补该仓库，模块 plugins 块携带版本号申请已在类路径插件的退役规则同时适用。

## Risks / Trade-offs

- **[探针三判据之一不成立（类加载器不同一或 apply 失败）]** → D2 已内置降级：接线类改反射一行、根声明行保留、其余批次结论标注"未验证"；不阻塞本册其余目标。
- **[buildSrc 与根 buildscript 双作用域类遮蔽未被完全排除]** → 本册落地即单声明点（根行退役），双声明并存形态仅作为探针的对照变体存在，且 ADDED 需求场景三把"双声明"定为评审违例，机械化拦截。
- **[ModuleSetting Java 化后 Groovy 消费面形态失配（闭包委托、静态查询）]** → API 外形保持清单（D3）＋配置期 `./gradlew help` 与全量 `check` 回归＋三个声明块零改动即最直接的兼容证据。
- **[BOM 补 gate 使任务图漂移被误读为回归]** → 唯一预期差异已申报，修复前 HEAD 报红记录与修复后 dry-run 成对入库 baseline 卷宗。
- **[在途 redesign 册修复循环与 BOM 文件撞车]** → 本册对 redesign 冻结面零触碰；BOM 一行是纯增引入，该册计划内改动在四个根侧脚本；若 8.x 演练真回改 BOM，rebase 冲突为同行相邻，按分支同步规则 rebase 解决。
- **[buildSrc 编译面再扩大（新增约 8 个类）致配置劣化]** → 该项正是 D8 在册执行的观察条目；超阈值登记移交，不构成回退条件。
- **[文件数与代码量净增]** → pilot 已诚实申报同账：本册把 Groovy 约 105 行脚本与 196 行接线/支撑换为约 160 至 200 行 Java 加测试，换取的是例外撤销、类型安全与守卫复活；读面收益按新 ADDED 目录页条款验收。

## Migration Plan

实施顺序（tasks.md 逐组对应）：账目更正与规则开道文字（CLAUDE.md 路径＋差量定稿自检）→ 基线五样本与 HEAD 报红记录入库 → 探针三沙箱验证并回填 → D3 自检修复（测试先行）→ `ModuleSetting` Java 化＋`tny.module-setting` 同名替换 → `tny.compile-baseline`、`tny.bom-platform` 同名替换 → buildSrc 增 dm implementation 依赖＋接线类 Java 化→根声明行退役 → BOM 补 gate 一行与 dry-run 断言 → 观察账两项在册执行 → 零差异全样本比对＋`./gradlew -p buildSrc test`＋全量 `check`＋形态断言逐枚复核 → 触碰文件按 gradle-build-style 全部需求走查 → 收口 verify 与归档。
回滚策略：各组独立提交，`git revert` 单提交回退；替换组"删除＋注册"同提交使回退成对恢复；根声明行退役的回退即还原一行脚本；探针与观察账任务不产生持久变更。生产分支任何一组未过零差异即停，不带病进入下一组。

## Open Questions

- 装配线册是否把 `tny.java-conventions` 与 `tny.plugin-conventions` 进一步并为单一按类型反应式的 `tny.conventions`（spring 终形，再减一个 id）——不影响本册任何工件，装配线册按"零差异＋目录页判据"实测后定。
- 终表词尾是否统一（`-conventions` 与 `-ops` 并存）——合并册收口时定，规格不涉命名审美。

## 兼容性小节豁免说明

本册不改动任何发布构件的公共 API，模块运行时行为零变化；BOM 任务图一处差异已在 D4 明文申报并登记豁免，发布产物 POM 与模块元数据逐样本零差异由验收兜底。故按项目 rules 不设 Compatibility Impact 小节。
