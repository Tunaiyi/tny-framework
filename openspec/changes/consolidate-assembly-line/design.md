# Design

## Context

动机与范围见 proposal.md 的 Why 与 What Changes；本册为纯内部重构，规格工件按 `skip_specs` 合法缺席（执行依据是已入账的 gradle-build-style 现文三条：载体三形态容身之处、约定插件接线规则按载体定、长度界线按单类计量）。开工前复核到的现状事实（全部来自立项前四路对抗研究的开文件实查，评审引用提交一律以现查哈希为准）：

- 装配面现文（根 `build.gradle`，HEAD f6bea21a）：`configure(subprojects)` 第 48-51 行两应用行（io.spring.dependency-management 裸 id、tny.dependency-management）；`configure(projectsExt.gradleProjects())` 第 52-57 行四行（tny.publish、tny.publish.gate、tny.compile-baseline、tny.plugin-module）；`configure(projectsExt.javaProjects())` 第 58-67 行八行（com.gradleup.nmcp、tny.publish、tny.publish.gate、tny.compile-baseline、tny.java-module、tny.integration-test、tny.publications、tny.github-packages）。注册 id 共十八枚：预编译脚本十三枚（buildSrc/src/main/groovy）加二进制五枚（tny.projects、tny.module-checker、tny.module-setting、tny.compile-baseline、tny.bom-platform）。
- maven-publish 供给链实查：全仓四处应用（dependency-management.java… 即 `tny.dependency-management.gradle:17`、`tny.java-module.gradle:17`、`tny.publications.gradle:12`、`tny.github-packages.gradle:17`），其中只有 dependency-management 一处经 subprojects 段覆盖全部 54 个子工程；沙箱实证 java-gradle-plugin 不传递供给 maven-publish，gradleProjects 段四脚本均不应用它——tny-game-doc-gradle 正文第 22-35 行的 `publishing{}` 块与一切自身不声明 maven-publish 的子工程，唯一供给点就是 dependency-management。nmcp 的 `"Nmcp: plugin 'maven-publish' must be applied"` 是配置期末检查（探针复跑：同工程内晚于 nmcp 应用 maven-publish 仍构建成功），故供给条款的承重论证按"插件线与未自声明子工程的唯一供给"表述，不系于 nmcp 行的时序。
- `tny.dependency-management.gradle` 为共有面：在途册提交 34fc8b11（10-06 03:33）已把其第 78-80 行版本赋值改为 `rootGitFlow.projectVersion ?: Project.DEFAULT_VERSION` 回落形态（redesign D2）；该文件其余消费面（BOM 九条导入句插值、具名托管、generatedPomCustomization、组号派生）通读无按名/按谓词跳过分支。
- Java 编译墙实查：Groovy 形态的脚本经 `getByType(GitFlow)` 读版本派生；同一 buildSrc 内 Java 源码编译先于 Groovy 源码，置于 `src/main/java` 的 Java 实现类无法编译期引用 `GitFlow.groovy`（现仓先例：十个二进制类全在 `src/main/java`，无一引用 Groovy 类型）。`GitFlow.groovy` 与 `GitCli.groovy`、`tny.git.gradle` 属在途册修复面清单（2e378afd 触及前两者），本册原则上不碰。
- 幻影哈希清账：提交 46908c03 经 `git cat-file`、全量 reflog 与远端核对确认不存在；此前工作流叙述中"装配线 Java 化已交付"的印象失实——java-module 等保持 Groovy 是未开工。真实链条：2e378afd（redesign 发布族，八文件，与装配线脚本零交集）＋前一册五枚提交（350345c4、c582170e、48771b8f、f19ff158、f9755fe6，其中 350345c4 为 fix 前缀）。
- 探针四结论（/tmp/probe4 沙箱，Gradle 8.14.5，本册 design 引用时保留限定语）：二进制实现类经 `project.getPluginManager().apply(裸 id)` 运行时应用预编译脚本插件，根工程与子工程两处均成功（插件在位、命名扩展、ext 属性、注册任务四类断言齐备；调用方与脚本桥接类同处 buildSrc 类加载器作用域 `coreAndPlugins:settings[:]:buildSrc[:]`）——**该探针证明的是 ClassLoaderScope 通路可行，不证明具体重型脚本的装配次序语义等价**；预编译脚本作为应用发起方（目标无论脚本或二进制）一律复现 `No service of type ClassLoaderScope`，抛错点 `DefaultScript.createObjectConfigurationAction`。pilot design.md 第 49 行结论句"组合能力只在二进制形态存在"按发起方/被应用方两方向改写记录；`tny.dependency-management.gradle` 第 9-10 行"脚本体内 legacy apply 触发缺失（实测 Gradle 8.5）"注释随本册改写为 8.5 发现、8.14.5 复测复现的完整口径（该形态约束只限发起方方向，被应用方方向按本节结论可行，本册不使用）。

## Goals / Non-Goals

**Goals:**

- 把装配线三个 configure 段从十四行收为十行：立口 dependency-conventions、java-conventions、plugin-conventions，四枚 id（dependency-management、java-module、plugin-module、compile-baseline）随吸收注销，两枚（integration-test、benchmark-module）同名 Java 化保留。
- 让 dependency-management 与 java-module 的行为逻辑第一次获得可单测形态（托管导入面、组号版本派生面、sourcesJar/排除面等按"检查逻辑必须携带单元测试"需求红绿覆盖）。
- maven-publish 供给链、三段原行序、共有面回落语义三件事以 MUST 条款＋破坏探针＋样件断言锁死，防止入口化过程把已验证的装配事实改散。
- jmh 供给迁移（第三处第二层豁免）以同提交原子性落地，版本字面量全部归位单一落点。
- 探针四、变体探针（GitFlow 通道）的结论入本册 design 供发布族册与合并册引用；防蔓延条款在本册落地。

**Non-Goals:**

- 不收纳发布族四脚本与编排线三脚本（根脚本对脚本形态插件保持逐行引入——现行规格过渡期条款明文；"全行收纳"连同其规格差量前置移交发布族册）。
- 不触碰 `GitFlow.groovy`、`GitCli.groovy`、`tny.release.gradle`、`tny.integrate.gradle`、发布族五脚本、`docs/` 发布文档与 CI 工作流（在途册修复面）。`tny.git.gradle` 仅在变体乙选定且探针证明无行为漂移时触碰一行注入语句（见 D4 的撞车面评估与登记条款）。
- 不立 BOM 线入口、不改 `tny-game-bom/build.gradle`（终态 9 id 的 bom-conventions 属合并册）。
- 不合并 java-conventions 与 plugin-conventions 为单一反应式总入口（留合并册按目录页判据实测后定）。

## Decisions

### D1 收纳形态取保守三入口，不做全行收纳，recorded assumption

依据：现行主规格"约定插件接线规则按载体定"过渡期条款要求工程构建脚本对每个脚本形态约定插件各用一行引入；全行收纳必须同提交携带该 MUST 条款的差量修订，而发布族装配面正处 redesign 演练活跃修复窗口（其 8.1 祖父轨陪跑与 8.2 全生命周期演练的修复按先例落发布族四脚本与编排线），入口收纳发布族行等于把未收口册的行为漂移引入本册验收面。探针四证明的通路可行性按 D8 登记移交，发布族册可在"先收行后转换"两段式与本册同款保守式之间凭该证据抉择。
被否决的备选：其一，激进全行收纳（八行并一行）——需修订规格过渡期条款且撞在途窗口，否决；其二，只做同名 Java 化不立入口——把入口的次序契约与目录页义务推给下一册重复付费，且根脚本行数不减，违背用户"收编为少数总入口"裁决的方向性，否决。
依据原则：P12（行为契约先动须先改账本，本册不动账本所以不越形态）、P13（每个决定有验收探针）。

### D2 入口内部应用次序逐字等于被收编行原行序，槽位钉死

三段终文与槽位：subprojects 段两行并一（tny.dependency-conventions，原位第 48-51 行段）；gradleProjects 段 publish、gate 两行保持＋第 55 行 compile-baseline 与第 56 行 plugin-module 两行并为第 56 行位置一行 tny.plugin-conventions；javaProjects 段 nmcp、publish、gate 三行保持，第 61 行 compile-baseline 与第 62 行 java-module 并为一行 tny.java-conventions（槽位在 gate 之后、integration-test 之前），integration-test 行保持，publications、github-packages 两行保持。
逐字序依据：apply 期即时读取构成的硬依赖只有三条（integration-test 依赖 java-module 的 java 插件；publications 依赖 publish 注入的四个 ext 键、java-module 创建的 mavenJava publication 与根 gitFlow 扩展；nmcp 的 maven-publish 供给由 subprojects 段承担）；publish↔gate 之间、{compile-baseline, java-module} 窗口相对其他行的次序只由"逐字等于原行序"验收纪律锁定（提交 32273604 BOM 次序遮蔽事故的教训），范围文本据此写明唯一合法插入点。
目录页义务：三入口类 javadoc 各列内部应用次序与原行号对应表；java-conventions 跨多类装配按需求"每项行为指名其所在类"成文。
被否决的备选：按依赖拓扑排序取"任意不炸位置"——评审与实施各说各话且失去零差异比对锚，否决（P13）。

### D3 dependency-conventions 无条件承载 idea 与 maven-publish 的全子工程供给，MUST 条款成文

入口内部次序逐字 io.spring.dependency-management → idea → maven-publish → 托管配置体；MUST NOT 按线成员条件化供给（插件线 doc-gradle 的 `publishing{}` 块求值期即需 publishing 扩展，java-gradle-plugin 不传递供给的实证在案），MUST NOT 新增跳过谓词（对账守卫 ManagedVersionsCheck 对全构件模块读 dm 扩展，任何跳过即复刻 ModuleSetting 兜底空转的先例事故），MUST NOT 下放为模块自报（枚举违例＋半配对事故 e0c51c69 的执行期迟报教训）。
破坏探针锁定三点（无条件、根侧、原位）：临时删除任一子工程的 dependency-conventions 供给面，`./gradlew -q help` 配置期报红（module-checker 或 doc-gradle 的 publishing 块），还原 sha256。
回落语义随迁：34fc8b11 的 `projectVersion ?: Project.DEFAULT_VERSION` 行连同 D2 provenance 注释逐段搬入实现类，注释基线以 HEAD f6bea21a 现文为准（该文件相邻注释与 group 行此前已被 pilot c1cfddb8 改写）；跨册处置条款成文——Groovy 脚本删除后 redesign 演练若需改动版本回落形态，修复落点改为 `DependencyConventionsPlugin` 的 Java 类并在 redesign 册 apply-notes 登记跨册修订。
依据：P4（供给不变量集中守护）、P13（三点判据探针化）；模式卷 M1：根侧单行引入＋入口内按类组合为前册 ProjectsPlugin/GitFlow 既有形态，照抄。

### D4 GitFlow 类型约束解点：变体乙为主、变体甲为降级、变体丁否决，探针五先行

变体乙：`tny.git` 薄壳在创建 gitFlow 扩展后追加一行，把派生版本值注入类型化 `ProjectsExtension`（新增 `derivedProjectVersion` 属性），dependency-conventions 与后续消费方按类型读取——完全类型化、消费面收敛进既有单一事实源扩展；代价是触碰 `tny.git.gradle` 一行（该文件在 pilot D8 冻结名单内，但在途册修复先例落点为 GitFlow/GitCli/release/integrate 而非薄壳，撞车面近似零；登记为条件性豁免并附跨册修订条款）。
变体甲：入口按名 `getByName("gitFlow")` 后经属性协议弱型取 `projectVersion`——零触碰冻结面，但降级类型安全；仅作探针证伪后的 fallback，注释登记来由。
变体丁（把撞墙实现类放 `src/main/groovy` 借混合编译）：沙箱实证其可编译后否决——违背十枚二进制类全在 `src/main/java` 的先例、绕过 javac 编译期检查、与合并册删除 groovy 载体终态冲突（M1 条款：旧先例适用时不另起炉灶）。
探针五（组 3，沙箱不入库）三判据：编译通过、组号与版本两属性对当前 Groovy 形态逐字等值、任务图零差异；推荐变体乙，证伪降级甲。依据：P4（不变量的读取通道类型化）、P13。

### D5 compile-baseline 注册 id 注销、实现类双入口按类复用；plugin-conventions 内部先应用它

批次路线原文只把 compile-baseline 登记给 java-conventions，留了插件线缺口的坑（id 注销后 gradleProjects 段第 55 行语义悬空；实施者以删行消解报错会造成编码钉定与零缓存策略静默丢失——零差异三件套不覆盖任务编码与解析缓存面）。裁定：`CompileBaselinePlugin` 保留为普通实现类（不再注册 id），java-conventions 与 plugin-conventions 各按原行位应用其语义；两入口目录页注释写明"共享行为按类复用非逐字复制"以自证需求一边界。验收增样：doc-gradle 与任一 java 线模块的编译任务编码选项与 resolution 缓存策略快照对基线一致。依据：P5（编码基线因"缓存时效与区域设置"独立变更原因存在，与线专属装配分职责）、需求一共享块收编条款。

### D6 integration-test 与 benchmark-module 同名 Java 化保留 id，形态险点清单入验收

保留 id 的理由与 module-setting 在册先例相同：两模块（`tny-game-integration-test/build.gradle:9`、`tny-benchmark/build.gradle:5`）plugins 块按 id 引用，第 2 层裁决令其不改。拆类按 250 行单类计量：integration-test 约 234-265 行折 Java 拆三类（入口接线＋源集通道＋受控隔离核对，末类为可单测判定类随附红绿用例——"检查逻辑必须携带单元测试"适用）；benchmark 拆入口加任务组类，`BenchmarkSuite` 支撑类（27 行，未冻结）同提交转 Java。
三处形态险点逐条点名并断言：其一，java-module 的 sourcesJar 手写 `tasks.register` 在前、`java.withSourcesJar` 命名复用在后——保留 withSourcesJar 自建路径会产生 51 项任务图静默漂移形态（sweep-gradle-build-style D5 实测记载），两段写反则配置期同名注册硬失败，验收锁定"手写注册在前＋复用在后"组合；其二，`configurations.configureEach` 惰性排除必须保持惰性形态（改成容器快照即时遍历，后建的 integration 系配置吃不到 logback/log4j-to-slf4j 排除）；其三，benchmark 两段 afterEvaluate（第 125-155 行族属清单段、第 160-169 行 benchParams 覆写段）注册次序决定 `-PbenchParams` 覆写优先级（D1 明文），`ext.listFile` 跨任务动态属性字段化时代替断言任务名、finalizedBy 边与产物路径一致。
预期差异申报：integration-test 第 132 行 `named('runtimeClasspath')` 判空在 Groovy 现文恒抛异常、报红文案分支实际不可达；Java 化改 `findByName` 后该文案可达——两种失败措辞的差异写入验收判据，属把"意外异常"改为"设计报红"的修复，非行为漂移。依据：P1（接线/通道/核对三类变更原因分离）、P13。

### D7 jmh 供给迁移原子条款

`buildSrc/build.gradle` repositories 增 `gradlePluginPortal()`、dependencies 增 `implementation 'me.champeau.jmh:jmh-gradle-plugin:0.7.3'`（版本落点注释义务沿 dm 先例），与 `tny-benchmark/build.gradle:3` 的 `version '0.7.3'` 字面量退役同提交（探针三变体乙：双声明硬失败，无过渡态可退）。同文件第 48 行 `jmhVersion = '1.37'` 迁 `gradle.properties` 键 `jmhVersion=1.37`，jmh 块改 `jmhVersion = jmhVersion.toProperty()` 式读取——与同文件 toolchain 读 `javaVersion` 先例同形，满足需求三"版本只出现在事实源两处＋键有真实取用点"；被否决备选"保留模块字面量并登记豁免"让 JMH 版本游离于事实源之外（第三套落点），否决。`jmhList/jmhListVerify` 对账面因值不变而零差异。依据：需求三先例（D5 注释义务）、P10（portal 仓库只在真实需求出现的本册加，不提前）。

### D8 探针四结论入账与移交、防蔓延条款

本册 design 探针结论小节完整登记：通路可行＋限定语（未证重型脚本装配序等价）＋发起方/被应用方双向口径修正（pilot 原句改写）。义务移交发布族册：其入口化若需收纳仍为脚本形态的族内 id，必须先以真实发布族脚本做端到端装配序探针，且同提交携带过渡期条款的规格差量。防蔓延条款写入验收：grep 三入口实现类源码，出现 `tny.publish`、`tny.publish.gate`、`tny.publications`、`tny.github-packages`、`tny.central` 任何 id 字符串即评审违例——本册入口只允许应用核心 Gradle 插件（按类）、io.spring dm（裸 id，探针三变体甲实证）与二进制实现类（规格明文允许）。

## 爆炸半径检查摘要（codegraph 与 grep 双口径）

- codegraph `analyze_impact`（delete 口径）：`tny.dependency-management.gradle` 与 `tny.java-module.gradle` 均 direct 0／total 0／low——预编译脚本文件无跨符号调用边可析，按"无隐藏调用方"佐证读，不单独立为权威（pilot 同款口径说明）。
- grep 权威口径（立项研究实查）：dependency-management 消费面＝根 subprojects 段一行＋ManagedVersionsCheck javadoc 指针一处＋doc-gradle 与 BOM 线的 maven-publish 隐性供给面；java-module 消费面＝根 javaProjects 段一行＋integration-test 脚本对其 java 插件的求值期依赖＋`tny.central` 的构件完整性核对面；plugin-module 消费面＝根 gradleProjects 段一行；integration-test 与 benchmark-module 消费面各＝根一行加模块 plugins 块一行。obsolete/ 目录与 settings.gradle 注释对五枚脚本 id 的引用零代码级。

## Risks / Trade-offs

- **[共有面 modify/delete 撞车]**：dependency-management 回落行若在 redesign 演练中需再改，本册已删脚本会成 modify/delete 冲突 → D3 已写处置条款（修复落点转 Java 类＋跨册修订登记），且该文件除回落三行外其余面在途册无改动史。
- **[GitFlow 通道变体乙触碰 tny.git.gradle 一行]** → 列入在途册修复面概率低（先例落点不含薄壳）；探针五先行验证等值性，证伪即降级变体甲（零触碰）；冲突时按分支同步规则 rebase。
- **[入口化后配置耗时上升]**：三入口合计新增类加载与 lambda 注册，前册劣化实测 0.3 秒（阈值 3 秒）→ 组 10 耗时观察账复读，超阈登记移交。
- **[形态险点漏网导致静默漂移]**（编码选项、缓存策略、排除面、覆写优先级均不被三件套常规样件覆盖）→ D5/D6 已把这些面逐一变成显名样件与断言；对照实验组（doc-gradle publishing 块）纳入基线。
- **[文件数与代码量净增]**：Groovy 约 656 行五脚本换 Java 约 1030-1190 行加测试约 550 行（前册同账口径延续）——真实收益为可单测、类型安全与根脚本装配面十四行减至十行；文件读面由目录页条款兜底（P13 验收判据）。
- **[一册全包单册体量大于前册]**：研究核算两册切不缩验收面只增固定开销（每册基线/耗时/回归/走查/收口四至五组）→ 采一册全包并以"组间独立提交、任一未过零差异即停"控回滚粒度。

## Migration Plan

组 1 册门核对（D9 顺序复核、幻影哈希不入任何工件、`git log` 现查）→ 组 2 基线以 f6bea21a 锚定重抓十一项样件入库（七件常规＋doc-gradle publishing 快照＋publish 任务名清单两份＋benchmark/integration-test 任务图两份，warm 耗时前读；不引用前册样件）→ 组 3 探针五（GitFlow 通道）与 jmh 供给沙箱预演，结论回填 → 组 4 dependency-conventions 立口（删脚本/注册/根两行并一/回落随迁/ManagedVersionsCheck 指针/javadoc 口径改写，同提交）→ 组 5 java-conventions 立口 → 组 6 plugin-conventions 立口 → 组 7 integration-test 同名 Java 化 → 组 8 benchmark-module＋BenchmarkSuite＋jmh 迁移 → 组 9 次序契约与防蔓延 grep 复核＋三入口目录页核对 → 组 10 全量回归（十样件比对、CodOD 单工程样件 `./gradlew :tny-game-net:tasks`、`-p buildSrc test`、`check --continue`、`publish --dry-run`、耗时后读、13 条需求走查）→ 组 11 收口（verify、用户批准归档、探针四移交指针与跨册条款复核登记）。回滚：每组独立提交，吸收类组以"实现类＋注册行＋根行改写＋脚本删除"单提交为单位 revert。

## Open Questions

- 合并册是否把 java-conventions 与 plugin-conventions 并为单一按类型反应式入口——不影响本册，合并册按目录页判据实测后定。
- 发布族册选"先收行后转换"还是同款保守形态——由发布族册凭探针四结论与其时 redesign 收口实况决定，本册只留移交指针。

## 兼容性小节豁免说明

本册不改动任何发布构件公共 API 与产物内容（十样件零差异为机械证明，唯一申报差异为 integration-test 失败文案可达性，属报红形态修复非行为契约变更），故按项目 rules 不设 Compatibility Impact 小节。
