# Design

## Context

动机与范围见 proposal.md 的 Why 与 What Changes；判据细则见本变更 specs 差量。本文件只记录"怎么做"的技术决策。开工前复核到的现状事实：

- 构建运行时为 Gradle 8.14.5（`gradle/wrapper/gradle-wrapper.properties`），而"约定插件互不 apply"约束的三处实测注记记的是 Gradle 8.5 结论——跨了两个次版本，不得未经复测沿用，这是探针一存在的理由。
- `buildSrc/build.gradle` 只有 `groovy` 与 `groovy-gradle-plugin` 两行插件声明：没有 `java-gradle-plugin` 注册块，没有测试依赖，buildSrc 目前不存在任何可执行测试的通道。
- buildSrc 共 17 个预编译脚本插件与 4 个 Groovy 支撑类（`tny/convention/` 下 GitCli、GitFlow、ModuleSetting、BenchmarkSuite）。其中 `tny.git` 与 `tny.module-setting` 已经是"脚本文件薄壳＋类型化支撑类"形态，属目标架构的现存样板，本批零改动。
- 根 `build.gradle` 第 25 至 39 行的 ext 块声明五个键：projectGroup、pluginLegacyGroup 两个字符串事实源，moduleProjects、javaProjects、gradleProjects 三个按命名约定派生的工程集合。真实代码级消费点经 grep 复核为六处脚本共九行（tny.bom-platform 1 行、tny.java-module 1 行、tny.dependency-management 1 行、tny.central 2 行、tny.module-checker 多项、tny.integration-test 1 行）加根脚本自身两行 `configure(...)`，另有一处模块构建文件消费标量键——`tny-game-doc-gradle/build.gradle` 以裸名 `pluginLegacyGroup` 声明例外组号（apply 阶段 4.4 全仓复扫补入本清单，教训：五键消费面的 grep 范围必须含模块目录，最初只扫了 buildSrc 与根脚本）；tny.plugin-module 与 tny.publications 只在注释中提及键名，无代码引用。后缀字面量判定（`-bom`、`-gradle`、`-integration-test`）在消费脚本中另有五处重复出现。
- `tny.module-checker.gradle` 共 107 行，头注释自述边界为三项配置期对账：发布构件组号对账（含"构建文件存在性断言"）、零发布合同沿依赖边就地核对、托管版本面与声明事实源对账守卫。三项都是"输入工程集合与事实、输出问题清单"的判定代码，即规格新增需求所指的必须携带单元测试的形态。
- 两个在途变更与本批的文件重叠情况：`redesign-devline-integration-model` 集中在 dev/线集成模型与发布线迁移（tny.release、tny.integrate、docs 线谱系），`consolidate-git-queries-into-gitflow` 集中在 tny.git 与 GitFlow 支撑类（本批视两者为冻结文件，见协调决策）。

## Goals / Non-Goals

**Goals:**

- 证明"预编译脚本插件退役、同名 id 二进制实现接替"的迁移机制可以零差异落地（以 tny.module-checker 一个插件为样本）。
- 证明配置期对账逻辑在 ProjectBuilder 单元测试下可获得脚本形态从未有过的验证通道（每个检查类至少一绿一红两个用例）。
- 把根脚本 ext 五键与其消费点收敛为一个类型化扩展 `tny.projects`，消除后缀字面量在多个脚本中的重复判定（单一事实源需求在装配侧的延伸）。
- 用双探针把二进制化路线最贵的两个架构未知数（8.14.5 的脚本嵌套 apply 行为、二进制内运行时 apply 第三方插件）在写生产代码之前一次性问清。
- 为 buildSrc 建立最小的可执行测试基建，并接入 CI 的 check 流水线。

**Non-Goals:**

- 不放行后续批次：模块装配线（tny.java-module 等）、发布线（tny.publish 族）、总装配册的迁移各自另立变更，且发布线批次以 redesign-devline-integration-model 收口为硬闸门——本变更的验收通过只是机制证据，不构成任何后续批次的实施授权（proposal 已明文，此处为设计边界）。
- 不迁移 `tny.git`、`tny.module-setting` 两个薄壳脚本的脚本文件本体（它们的支撑类已是目标形态，薄壳留待总装配册处理）。
- 不引入新的外部依赖与插件版本变化，不改变任何面向用户的任务名、产物坐标与发布行为。
- 不做全仓 `.gradle` 文件的存量清扫。

## Decisions

### D1 实现语言取 Java，recorded assumption

二进制实现类与检查类用 Java 书写；三个以字符串编织 POM/XML 为主的脚本（tny.central、tny.dependency-management、tny.benchmark-module）若未来迁移则保留 Groovy。理由：项目 Java 纪律（命名、javadoc、许可证头）现成可复用，类型安全收益只有在编译期才能兑现，且 Gradle 官方插件开发文档以 Java/Kotlin 为一等形态。备选 Kotlin 被排除的唯一理由是仓库没有 Kotlin 生产代码，引入新语言层不值得。**此为 recorded assumption：用户以无参数 `/opsx:propose` 采纳了我方方案的默认值，apply 前一句改口即可换形，不影响规格条文（规格对实现语言中立）。**

apply 时点的已记录例外：接线类 ModuleCheckerPlugin 取 Groovy 而非 Java——它需要动态读取 io.spring.dependency-management 的托管版本扩展（该类型不在 buildSrc 编译类路径上，原脚本即以动态属性访问消费）并与 Groovy 支撑类 ModuleSetting 直接交互；三个判定检查类保持 Java。例外理由同步写在类 javadoc。

### D2 双探针先行，8.14.5 一次性沙箱，产物不入仓库

探针一（脚本嵌套 apply）：在 8.14.5 沙箱仓库复刻 8.5 时代的违例形态——一个预编译脚本插件 `apply plugin:` 另一个预编译脚本插件——记录当时的 ClassLoaderScope 报错是否仍复现。判据：不报错即"8.5 缺陷已修"，则后续总装配册可以采用脚本插件间组合；仍报错则既有绕行约束在脚本形态下继续有效。**注意本批试点不需要此项结论才能推进**（试点的二进制实现类之间允许直接引用类，不走脚本 apply 通道），探针结论记入本文件供后续册引用。

探针二（二进制内运行时 apply 第三方插件）：沙箱 buildSrc 注册一个最小二进制插件，其 apply 方法内执行 `project.getPluginManager().apply("io.spring.dependency-management")` 与 `apply("com.gradleup.nmcp.aggregation")`，验证不声明 `compileOnly` 依赖时是否抛 PluginId 无法解析。判据与降级：若失败，后续"总装配插件"设想降级为"根脚本保留第三方插件引入行"形态（当前根脚本本有此形态，降级即零改动）；若成功，总装配册才允许把第三方引入也收进二进制。**探针二的结论同样不阻塞本批试点**（module-checker 不 apply 任何第三方插件），但其成本是半小时沙箱，故与探针一并列前置而非押到批次尾，这是对抗审查"最贵赌注不得押最后"的落实。

探针脚本放 `/tmp` 沙箱，结论以完整句子记录在本文件"探针结论"小节，沙箱不入库。

#### 探针结论（2026-10-06 实测，Gradle 8.14.5，沙箱 /tmp/binary-probe-20261006）

- 探针一结果：**8.14.5 仍然复现** Gradle 8.5 时代的缺陷。沙箱中预编译脚本插件 pluginA 对 pluginB 执行 `apply plugin: 'pluginB'`，配置期抛 `Failed to apply plugin 'pluginA' → org.gradle.internal.service.UnknownServiceException: No service of type ClassLoaderScope available in project services`，报错原文与 8.5 注记一致。结论："约定插件之间不互相 apply"的过渡期约束在脚本形态下继续有效；根 `build.gradle:52-53` 注释无需改口径；未来总装配册不得假设脚本插件可组合，组合能力只在二进制形态存在。
- 探针二结果：**成功，两形态均通过**。buildSrc 注册的 Java 二进制插件 `BinaryApplyPlugin` 在 apply 方法内不声明任何 compileOnly 依赖、以 id 字符串运行时执行 `project.getPluginManager().apply(...)`，对 `io.spring.dependency-management`（1.1.7）与 `com.gradleup.nmcp.aggregation`（1.6.2）两枚第三方插件都应用成功。生效前提是根工程 `plugins {}` 块以 `apply false` 声明该插件及版本（jar 因此进入根 buildscript classpath，PluginManager 按 id 沿类加载器父作用域解析命中）。结论含义：后续总装配册可以把"对子工程 apply 第三方插件"的编排收进二进制插件，根脚本只保留带版本的 `apply false` 声明行；D9 第 6 条的降级形态（根脚本保留三行应用）确认**不需要启用**，但本批试点不触碰根脚本 L48-50 的第三方应用行（Non-Goals 范围外）。
- 注记边界：根 `build.gradle:15` 的旧注释"buildSrc 作用域无法解析父构建类路径插件（实测 8.5）"与本探针结论的适用面不同——旧注记针对的是 buildSrc 编译期类引用（import 第三方类需 compileOnly 依赖），本探针验证的是运行期按 id 字符串 apply（不需要编译期类引用）。两条并存不矛盾，本批不改该注释。

### D3 试点对象取 tny.module-checker，recorded assumption

备选是 tny.github-packages（最小、90 行、有既有端到端证据）与 tny.module-checker。取后者的理由：它是对账逻辑的集大成者，正好检验新增需求"检查逻辑必须携带单元测试"的落地形态；它的三项对账相互独立，天然给出类拆分边界；且它消费 ext 五键最全，与 D4 的收敛改造同域共振，一次改动同时验证两条机制。代价是它比 github-packages 复杂（107 行对 90 行含守卫），但试点要证明的是最难样本而非最易样本。

拆分形态：`ModuleCheckerPlugin`（实现 `Plugin<Project>`，仅做时机接线——组号对账与托管版本面对账在 `gradle.projectsEvaluated` 回调里调用检查类并聚合问题清单抛 GradleException，零发布合同逐成员工程在 `afterEvaluate` 收尾调用检查类抛首条违例，与原脚本时机逐字一致）；`GroupAlignmentCheck`、`UnpublishedContractCheck`、`ManagedVersionsCheck` 三个纯逻辑类，输入为工程集合与事实值、输出为问题清单字符串列表，不持有 Gradle 生命周期对象（便于 ProjectBuilder 直接构造输入）。原 `tny.module-checker.gradle` 文件删除，`buildSrc/build.gradle` 的 gradlePlugin 块以同名 id `tny.module-checker` 注册实现类——同一 id 两形态不并存已是规格差量需求一的明文判据。零发布合同一项依赖沿依赖边强制评估被依赖方的 Gradle 语义，纯逻辑类的单测覆盖判定函数本体（复验 CRITICAL-2 修复后 ProjectEdge 清单输入的映射判定收回检查类，红绿方向都在；接线类只保留逐边强制评估与角色声明位采集），强制评估时机的端到端语义仍由主构建的破坏探针承担——这一分工在检查类的 javadoc 里写明。

### D4 根 ext 五键收敛为 tny.projects 类型化扩展，recorded assumption（收敛范围）

新建 `ProjectsExtension`（Java 类）：两个字符串属性 projectGroup、pluginLegacyGroup 由根脚本一次性设值（值仍是字面量，但全仓只出现这一处，与现状相同）；三个工程集合暴露为方法 `moduleProjects()`、`javaProjects()`、`gradleProjects()`，内部按命名约定惰性派生；另暴露谓词 `isBom(Project)`、`isGradlePlugin(Project)`、`isIntegrationTest(Project)` 三个判定方法，供消费脚本替换重复书写的后缀字面量（消费点复核见 Context：五处）。注册形态取"根脚本一行 `apply plugin: 'tny.projects'`"而非 `extensions.create` 裸调用：tny.projects 是二进制插件（`ProjectsPlugin` 内 `extensions.create("projects", ProjectsExtension.class)` 并设默认值），与仓库"能力以插件为交付单元"的既有心智一致。注册块 id 前缀沿用 `tny.`。

消费方改写清单（apply 阶段逐文件执行，行号以当前 HEAD 为准）：根 build.gradle 删除 ext 块 L25-39 换成插件行与注释；L55、L61 的 `configure(gradleProjects)`/`configure(javaProjects)` 改为 `project.extensions.getByType(ProjectsExtension).gradleProjects()` 结果集（Groovy 侧可经 `projects` 扩展名点取，脚本读类型化扩展是仓库既有形态——GitFlow 同例）；tny.bom-platform、tny.java-module、tny.dependency-management、tny.central、tny.integration-test、tny.module-checker（二进制化后在类内）改读扩展。**java-module.gradle:117 的 `endsWith('-tester')` 判定不属于五类命名约定，保持原样不动**——它是 tester 宿主排除谓词，若顺手收进 tny.projects 反而发明一个没有第二消费方的新约定，触碰即改原则不要求发明。configure-on-demand 兼容性：派生集合的求值只遍历 `rootProject.subprojects` 的名字与组，不触发任何工程评估，与原 ext 块语义等价，注释保留该论断。

### D5 buildSrc 测试基建取 JUnit 5 + ProjectBuilder，recorded assumption（框架选择）

`buildSrc/build.gradle` 增加：`java-gradle-plugin` 插件与 `gradlePlugin { plugins { ... } }` 注册块（id `tny.module-checker`、`tny.projects` 两条）、`testImplementation` 的 `org.junit.jupiter:junit-jupiter` 与 `com.google.guava` 不引入（零新增外部依赖原则下 ProjectBuilder 由本地 Gradle 分发 API 自带，JUnit 5 为唯一新增测试依赖，版本进 `gradle/libs.versions.toml` 遵守单一事实源需求）、`tasks.withType(Test).configureEach { useJUnitPlatform() }`。版本目录消费规则：buildSrc 不在版本目录的共享使用面内（buildSrc 有独立 classpath），测试依赖版本以 buildSrc 侧显式版本字面量声明并在注释说明与主仓版本目录的关系（避免为 buildSrc 再立一套目录桥接机制）。测试命名随项目 Java 测试纪律。

### D6 CI 接入：buildSrc 测试搭现有 check 档，不新建作业

`build.yml` 的既有 check 作业在跑主构建 check 之前（或合并为一条命令 `./gradlew -p buildSrc test` 前置步骤）执行 buildSrc 测试，失败即作业红——"未过检的构建逻辑不得进入主构建配置"（新增需求第二场景）的机械化。不建独立作业的理由：试点只有一个测试集，独立作业的路由与并发成本大于收益；批次 2 以后测试面扩大再议拆分。

### D7 零差异验收沿用既有口径，破坏探针复验红绿

基线：改动前 `./gradlew tasks --all` 任务图、代表性模块 `dependencies` 清单、双 POM 与 `gradle-build-style` 既有"零差异"用词一致。特殊过滤：buildSrc 自身参与生命周期变化会引入 `-p buildSrc` 与插件解析行，沿用 consolidate 时的任务图前缀过滤先例处理。对账逻辑改二进制后的红绿判据以破坏探针复验：临时改根组号属性值使组号对账报红、临时给某 `-integration-test` 模块加 `publishing` 配置使零发布合同报红、两处各跑一次恢复原状（sha256 校验恢复），证明"迁移没有把判红能力迁丢"。托管版本面对账的破坏探针成本较高（需制造 BOM 版本漂移），以该检查类的 ProjectBuilder 违例用例＋主构建一次正向通过为充分证据，端到端破坏形态挂观察账。

### D8 与两个在途变更的协调：本批文件冻结清单

本批不改动：`tny.release.gradle`、`tny.integrate.gradle`、`tny.git.gradle`、`GitFlow.groovy`、`GitCli.groovy`、`docs/release-process.md`、`.github/workflows/` 下除 build.yml 外的一切。tny.module-checker 的零发布合同部分读取 `tny.module-setting` 的角色声明，该接口在 redesign-devline-integration-model 收口前视为稳定；若 apply 期间发现该在途册动了 ModuleSetting 的读接口，本批 checker 二进制化暂停该项、其余两项照常推进（回退路径，不扩范围）。

### D9 recorded assumptions 汇总（用户一句话可改，均不影响规格条文）

1. 实现语言 Java（D1）；2. 试点对象 tny.module-checker（D3）；3. 扩展注册形态 `apply plugin: 'tny.projects'`、扩展名 `projects`、消费面含三个谓词方法（D4）；4. 测试框架 JUnit 5、版本字面量声明在 buildSrc 侧（D5）；5. CI 搭车 check 档不建独立作业（D6）；6. 探针降级形态取"根脚本保留第三方引入行"（D2）。

### 原则与先例依据（对照 docs/design 两卷）

- D3 三检查类拆分：P5（按变更原因划分——三项对账各自溯源不同规格文档，会因不同原因被修改）、P1（判定逻辑与 Gradle 生命周期接线分离，接线是易变的实现侧）、P13（可验证性——纯输入输出类才有单测通道）。
- D4 收敛扩展：P4（命名约定的三类后缀判定是构建装配的不变量，收进单一类型后由扩展守护，脚本各自字面量判定等于人人自抄一份不变量）；P10（`-tester` 判定只有一处消费方，按三次法则拒绝为其发明新谓词）。先例：buildSrc 已有"薄壳脚本＋类型化扩展经 `extensions.getByType` 点取"的既有形状（GitFlow 由 tny.git 承载、ModuleSetting 由 tny.module-setting 承载），D4 与 D3 的注册和读取形态照抄该先例（模式卷第二节 M1 检验：同类问题框架内已有解法，未另起炉灶）。
- D2 探针与 D7 破坏探针：P13（把不可验证的架构断言换成可运行的验证动作）。规格开道（Migration Plan 步骤 1）：P12（行为契约变更先于代码变更）。
- D1、D5、D6 属 Gradle 官方形态选型与工程流水线接入，两卷未覆盖该问题类型，无适用编号，依据为官方文档与仓库 CI 现状。D8 依据为 proposal 已载明的并行册协调合同，非设计原则问题。
- 兼容性小节豁免说明：本批不改动任何发布构件的公共 API（零差异验收即为该论断的机械证明），故按 rules 不设 Compatibility Impact 小节。

### 爆炸半径检查摘要（design 规则要求的 codegraph 与 grep 双口径）

- codegraph `analyze_impact`（tny.module-checker.gradle，modify 口径）：direct 0、total 0、risk low——脚本文件无跨符号调用边可析，该结果按"无隐藏调用方"读，不单独作数。
- grep 全仓复核（权威口径）：`tny.module-checker` 的应用点唯一（根 `build.gradle:47` 一行；ext 块删除后行号前移，现文为 39 行）；四处按名提及均为注释（tny.dependency-management:27、tny.java-module:116、tny.integration-test:115、settings.gradle:84），插件 id 二进制化后不变，四处注释语义继续成立无需改动。根 ext 五键消费面见 Context 第三条清单；根 `build.gradle:52-53` 注释自述"约定插件之间不互相 apply（实测 Gradle 8.5）"即探针一的复测对象，本批不触碰该行脚本（探针在沙箱做）。

## Risks / Trade-offs

- **[8.14.5 探针结果推翻"绕行约束自然消亡"预期]** → 试点不消费探针一结论（二进制类之间直接引用即可），探针只服务后续册；结论无论方向都记入本文件，规格措辞不依赖任一方向。
- **[ProjectBuilder 单测与真实构建语义偏差：ext 动态属性、Groovy 闭包求值时机在测试宿主里不完全一致]** → 检查类设计为纯函数输入输出（D3），把 Gradle 生命周期依赖全部留在薄接线类里；接线类不写单测、由零差异与破坏探针（D7）承担，偏差面被压到判定逻辑本体。
- **[module-checker 的 projectsEvaluated 时机在扩展改读类型化后取集合快照还是活视图]** → 原脚本 `moduleProjects.each` 在回调内读 ext 集合，等价于回调时刻的派生快照；扩展方法每次调用重新按名字过滤 subprojects，语义同为"回调时刻现算"，无时序差异——迁移时在实现类 javadoc 记录该等价性论断，破坏探针验证。
- **[根 ext 键删除会波及本批清单外的动态引用（如 `-P` 属性注入、外部脚本）]** → 已 grep 全仓 `.gradle`；`PprojectGroup` 之类属性注入不存在（键是 ext 不是 project property）；风险残余在仓外消费方（文档站脚本），归档验证跑全量 `./gradlew check` 与 `publish` dryRun 兜底。
- **[buildSrc 引入编译期后配置耗时增加]** → 试点只编译两个插件单元（约 6 个类），Gradle 对 buildSrc 有增量编译；耗时若劣化超 3 秒挂账观察，不构成回退条件。
- **[文件数与认知面净增（21 → 约 27＋测试类）]** → 这是已诚实申报的代价（proposal Why），换取的是可单测与类型安全；扫读验收靠规格差量新增的"总装配入口类头注释目录页"判据兜底，试点仅两单元无此压力，判据提前立为后续批次护栏。

## Migration Plan

实施顺序（tasks.md 逐条对应）：

1. 规格落地（归档前差量即生效文本）＋ `CLAUDE.md` 执行要点第二句改写。
2. 双探针执行并回填本文件"探针结论"小节。
3. buildSrc 测试基建（D5 的注册块与测试依赖），先以恒真测试打通 `./gradlew -p buildSrc test` 与 CI check 档（D6）。
4. `tny.projects` 插件与扩展实现＋单测；根脚本与六个消费脚本切换读取、删除 ext 块。
5. `tny.module-checker` 二进制化（三类＋接线类＋单测），删除脚本文件。
6. 零差异验收（任务图/deps/POM）＋破坏探针复验（D7）。
7. 全量 `./gradlew check` 与 publish dryRun 兜底；文档与 provenance 注释随迁核对（规格需求七）。

回滚策略：步骤 4、5 各自独立提交，`git revert` 单提交即可分别回退；脚本文件删除在 revert 后自动恢复；buildSrc 基建（步骤 3）被回退时其测试文件一并失效，无悬挂状态。生产分支上任何一步未过零差异即停止，不带病进入下一步。

## Open Questions

- 归档后判据歧义预登记（复验 SUGGESTION，入账后若被按宽读法质疑在本册修口径）：ManagedVersionsCheck 的 GUARD_COORDS 常量表与版本目录同名坐标字符串的关系属"对账探针输入"而非"依赖声明"，需求三"不得复制条目表"的适用边界需要澄清；需求六"以注释段落标题对应脚本区块名"对纯对账类插件（无仓库/依赖/任务区块）的适用解释为"未涉及的区块省略"，段落标题保留对账语义名。

- buildSrc 是否需要独立的 `settings.gradle` 与依赖锁定文件治理（Gradle 8.x 后 buildSrc 可用 build-logic 替代形态）——不影响本批任何工件，留待批次 2 的总装配册评估。
- 后续批次（模块装配线、发布线、central 文本重排类）的迁移顺序与是否逐批引入 Spotless/Checkstyle 到 buildSrc——各批立册时再定，本批不预设。
