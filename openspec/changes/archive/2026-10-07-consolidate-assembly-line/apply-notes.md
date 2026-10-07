# Apply Notes — consolidate-assembly-line

本卷宗按任务组记录实施证据；基线抓样口径沿前册 `archive/2026-10-07-consolidate-binary-conventions/baseline/README.md`（Corretto 21、UTF-8 钉住、独立守护进程注册表 /tmp/tny-cbc-daemons、PATH 前置 /opt/homebrew/bin:/usr/bin 修正坏 /usr/local/bin/git）。

## 组 1 册门核对

- 1.1 D9 顺序核对：归档册 design D9 节登记"装配线册在【硬闸门：redesign 归档】之前，发布族册与 central 在闸门之后"——本册范围（dependency/java/plugin 三入口＋integration-test 与 benchmark 同名保留）不含发布族与编排线，与 D9 一致。幻影哈希清账：46908c03 在本册工件中的全部命中（proposal 第 5 段、design 现状事实第三条、本任务判据）均为"声明其不存在并给出真实链条 2e378afd＋前册五枚"的清账句，无事实性引用。
- 1.2 开工实况：锚定 HEAD f6bea21a；工作树含他人在途文件 `tny-benchmark/results/bench-20261006-quick.json`（基准进程在写，避让清单：本册任何提交不含该路径，全程显式路径 git add）。
- 1.3 skip_specs 生效：status 报 specs=skipped，validate --strict 通过且提示"zero deltas accepted"；specs 工件不创建，本册无规格差量。

## 组 2 基线快照重抓入库

- 十一件样件与 README 入库 `baseline/`（锚定 531e9c4b；tasks 3586 行、依赖 73/94、三线 POM 167/76/302、doc-gradle 供给面快照、两线 publish 任务名清单、两线外模块任务图、warm help 三连读）；前册样件不引用，本册比对一律以此为准。
- 耗时改造前读数入 help-timing-before.txt；避让清单维持：`tny-benchmark/results/bench-20261006-quick.json` 不入任何提交。

## 组 3 探针五与 jmh 供给预演（完整结论见 design 探针结论小节）

- 变体乙两分支等值成立（有值分支逐字同值；缺位分支得 "unspecified" 回落即现文 `?: DEFAULT_VERSION` 语义）；编译墙负例实测（Java import Groovy 源类 compileJava 找不到符号）；变体丁按 M1 维持否决。
- jmh 三例齐：缺 portal 解析失败、补 portal 根与子工程按 id 应用成功且类加载器同一、根带版本声明并存复现双声明硬失败——组 8.3 原子条款按此执行。
- 过程如实入账：沙箱首跑两处环境级失败（wrapper jar 复制层级放错 gradle/ 与 gradle/wrapper/；单 null 实参的边缘形态），均定位修正后取得判据；沙箱目录留存 /tmp/probe5 与 /tmp/probe5-jmh 待批准后清理，仓库零污染（git status 仅外部在途文件）。

## 组 4 dependency-conventions 立口

- 4.1 实现要点与设计的一处偏离如实登记：D4 变体乙原写"tny.git 薄壳追加注入行"，实现落在 `ProjectsPlugin` 尾部注入（gitFlow 缺席时留空不抛、消费端 `applyIdentity` 判空报红）——同一探针五结论（GroovyObject 属性协议单点弱型 + DEFAULT_VERSION 回落），少碰一枚文件且完全不碰 `tny.git.gradle`（该文件在 redesign 修复面清单内），design 已按本组实况回写。
- 4.2 测试三族全绿并核执行记录（CatalogNotationsTest 2、ProjectsPluginTest 3、DependencyConventionsPluginTest 1，均 0 failed）；`versionOf` 绿向在 ProjectBuilder 不可构造的边界随注释如实登记。
- 4.3 吸收式原子提交四件：删除 `tny.dependency-management.gradle`、gradlePlugin 注册 `tny.dependency-conventions`、根 subprojects 段两行并一、`ManagedVersionsCheck` 类 javadoc 与报错文案两处指针改指 `DependencyConventionsPlugin.BOM_IMPORTS` 常量注释（provenance 随迁）。
- 实施中发现并修复的机制事实（design 探针结论小节已回写）：`VersionCatalogsExtension` 于工程构建脚本评估时注册，根侧急切 apply 时刻子工程拿不到目录扩展（探针实录 `CATALOGPROBE :tny-benchmark byType?=false byName?=false` 与首炸报错）；原预编译脚本的 `libs` 走 buildSrc 编译期访问器（同一 toml 只读视图，buildSrc/settings 注释在册），二进制无该通道——托管声明段后置 `afterEvaluate`，时机等价论证（托管面首次消费在 projectsEvaluated 与解析期）写入类注释；4.5 七面样件（双依赖、三线 POM、doc-gradle 发布任务面、全量任务图）全部对基线零漂移，为该后置的等价性提供机械证明。
- 4.4 破坏探针两例：其一，注释根上入口应用行 → `:tny-game-doc-gradle` 配置期报红（maven-publish 唯一供给点断供即炸，锁"无条件、根侧、原位"三点）；其二，注释 `apply plugin: 'tny.git'` → 根配置期在 `tny.release` 的 getByType(GitFlow) 处即报 `Extension of type 'GitFlow' does not exist`（上游先炸），`applyIdentity` 判空分支为纵深防御；两例还原均 sha256 对账一致。
- 根脚本探针过程记录：临时探针行两进两出（换序修正一次），终态与基线逐字节还原。

## 组 5 java-conventions 立口

- 5.1-5.2（d876c9ca）：入口与五段装配类落地，两处实施期顺序判断如实登记——mavenJava 创建必须留在根配置窗口内（其后的 tny.publications 在根配置期即按名引用该发布物做 sign 挂接），公共依赖段是唯一后置到评估收尾的段（版本目录通道），均写入入口类 javadoc；`addProvider` 经 javap 钉死为 Groovy 形态 `implementation libs.x` 的 Java 落点（javadoc options 的 addBooleanOption 声明于 CoreJavadocOptions 接口，转型注释同义）。判定单测 `JavaConventionsDecisionTest` 覆盖 tester 检索三向与模块名派生两向。
- 5.3 原子切换三件：删除 `tny.java-module.gradle`（204 行）、注册 `tny.java-conventions`、根 javaProjects 段 compile-baseline 与 java-module 两行并一（注释登记组 6 前 gradleProjects 段保留 tny.compile-baseline 注册 id 的过渡事实）。
- 5.4 验证：buildSrc 测试绿、根 help 绿；`:tny-game-net` 双依赖清单与 java 线 POM 对基线逐字节一致；全量任务图零漂移（sourcesJar 手写注册+withSourcesJar 复用的组合形态、五测试依赖与 tester 挂接的边全部原样）；`:tny-game-net:tasks --all` 中 sourcesJar 在场计数 1（无双注册）；描述符 implementation-class 指向 JavaConventionsPlugin。

## 组 6 plugin-conventions 立口

- 6.1-6.2（一笔提交实现与接线判定）：入口内先按类应用 CompileBaselinePlugin 再落插件线专属行为，原行序逐字复刻；本线 sourcesJar 的 duplicatesStrategy 为 INCLUDE（与 java 线 EXCLUDE 的形同义异差异按需求一注释条款写明）；groovy 源目录经 SourceSet 公开扩展容器按名取用（与 Groovy 形态同一对象，不触内部类型）；首稿两处编译错误（SourceSet 无公开 getGroovy()、局部变量遮蔽 java.io 包名）当场以 javap 定案修复，未入任何提交。
- 6.3 原子切换四件同提交：删除 tny.plugin-module.gradle、注册 tny.plugin-conventions、**注销 tny.compile-baseline 注册行**（两线吸收完毕，实现类保留按类复用）、根 gradleProjects 段两行并一（publish、gate 两行保持原位）。
- 验证：buildSrc 测试绿、根 help 绿、全量任务图零漂移、插件线 POM 与 doc-gradle 发布任务面对基线逐字节一致、plugin-conventions 描述符指向实现类、compile-baseline 描述符已从产物中消失（计数 0）。
- 防呆教训二次登记：`git rm` 已暂存的删除路径 MUST NOT 再进 `git add` 的 pathspec（组 5 提交分裂一次、组 6 前置一次，均以软回滚重做）；根治规则已写入本卷宗，组 7 起提交命令只列存在路径。

## 组 7 integration-test 同名 Java 化

- 7.1 三段拆分落 `tny.convention.integration` 子包：入口 `IntegrationTestPlugin`（任务注册段、docker 端点解析、门控接线）、`IntegrationSourceSetConventions`（源集与 extendsFrom 通道）、`DemoIsolationCheck`（受控隔离目标交集与三处 fail-fast 的判定纯函数）。判定类单测 `DemoIsolationCheckTest` 覆盖放行向、交集空向（含"无"清单形态与带冒号路径列表的逐字断言）、缺 jar 与缺 runtimeClasspath 各自报红向，36 用例全绿且核过执行记录。
- 预期差异申报（design D6 预告兑现）：runtimeClasspath 存在性判定由原 `named(...)==null`（named 对缺失配置实际抛异常、设计报红分支不可达）改为 `findByName(...) != null`，原脚本设计的报红文案自此可达；文案措辞逐字保留原脚本，注释就地写明。
- 7.2 原子切换两件同提交：删除 `tny.integration-test.gradle`（156 行）、gradlePlugin 注册 `tny.integration-test` 指向 `IntegrationTestPlugin`；id 不变故根 javaProjects 段与 `tny-game-integration-test/build.gradle:9` 的 plugins 引用零改动。
- 7.3 验证：integrationRuntimeClasspath 531 行解析树中 logback-classic 与 log4j-to-slf4j 命中数 0（惰性排除面对后建 integration 配置照常生效，configureEach 形态保持的证据）；线外模块与全量两份任务图对基线**任务行集合零差异**（Python 正则剔噪后 72/72、3562/3562 行）；描述符指向二进制实现类。
- 比对方法学登记：本机 `grep` 实为 ugrep，`-E` 的 `\]` 转义与空行模式行为与 GNU grep 有别，此前 shell 侧剔噪出现假性差异；任务图样件的权威剔噪改用本卷宗内 Python 正则（actionable/空白行/BUILD/Starting a Gradle/Incubating/Problems report/Deprecated Gradle/warning-mode/docs.gradle.org），后续各组沿用此口径。

## 组 8 benchmark 同名 Java 化、BenchmarkSuite 转 Java 与 jmh 供给迁移

- 提交编排如实登记：本组三件（8.1-8.3）合为一笔原子提交——实现类引用 jmh 公开类型要求 buildSrc 依赖先行，而 jmh 依赖入 buildSrc 与 tny-benchmark 携带版本号行的退役不可分步（探针三预演实证双声明并存为硬失败），BenchmarkSuite 的 Groovy 文件删除与同名 Java 文件入库亦不可分步（同类名双源文件编译冲突）；任务清单原"实现一笔、切换一笔"在本组被依赖拓扑强制合并，两笔拆分只在无此耦合的组成立。
- 拆分落 `tny.convention.benchmark` 子包：入口（四任务注册与两段 afterEvaluate，D1 覆写次序逐字保持；`ext.listFile` 动态属性以 final 局部变量承载等价替代，任务名与 finalizedBy 边不变）、`BenchmarkSuiteCheck` 判定纯函数（族正则全部 Matcher.find 语义，与 Groovy 等号波浪线等值——判例表条目：两处违例用例覆盖 find 部分匹配防 matches 误用）、`BenchmarkSuite` 转 Java（managed property 抽象类两语言等价，模块 `benchmarkSuite {}` 声明块零改动）。
- API 钉版过程三处失误当场修正未入账史：mainClass 为 Property 形态、MapProperty 无 clear/replace（整图覆写用 set(Map)）、benchScope 判断次序 property 先取会炸；反射取 `jmhRunBytecodeGenerator.generatedResourcesDir` 首跑接线错位（指向当前任务而非生成任务），报错即改为 `tasks.named("jmhRunBytecodeGenerator")` 目标——原 Groovy 动态访问的运行时求值语义保留。
- jmh 供给迁移：buildSrc 增 `gradlePluginPortal()`（jmh 不在 Central，探针实测）与 implementation 依赖（版本单一落点注释登记）；`tny-benchmark/build.gradle` 两处字面量各自归位——plugins 块去 `version '0.7.3'`，`jmhVersion = '1.37'` 改 `providers.gradleProperty('jmhVersion').get()`，gradle.properties 增键 `jmhVersion=1.37`（取用点注释齐备，值零变化）。
- 8.4 执行面验证：选择段四形态经 /tmp init 脚本内省与原文语义一致（缺省=族竖线正则并六臂参数域、-PbenchParams 单臂覆写居末生效、-PbenchAll 全开 `.*`、-PbenchScope=quick 排除面且不带参数域）；`jmhList` 实跑成功（7 个基名条目，产物路径 `tny-benchmark/build/tmp/jmhList/jmhList.txt` 与原 temporaryDir 同位，finalizedBy 的 `jmhListVerify` 判红逻辑同步演练——空清单时显式报红文案逐字为原脚本句）；`jmhSuiteVerify` 实跑通过；`:tny-benchmark` 任务图对基线 83/83 零差异；描述符指向 `BenchmarkModulePlugin`；根配置与 buildSrc 测试（含 `BenchmarkSuiteCheckTest` 绿红两向）全绿。

## 组 9 次序契约、防蔓延与目录页复核

- 9.1 根装配段终文与逐字序对照：subprojects 段一行（原两行，入口内 io.spring dm→idea→maven-publish→配置体承前）；gradleProjects 段 publish、gate 保持原位加 plugin-conventions（原 compile-baseline、plugin-module 两行位）；javaProjects 段 nmcp、publish、gate 三行保持，java-conventions 占原 compile-baseline 与 java-module 两行位、integration-test 保持、publications 与 github-packages 保持——被收编行的相对交错次序全部原位复刻，目录页与行内注释逐项指名所在类。javaProjects 段一处组 6 遗留的"改造前保留注册 id"过时注释按触碰即改改写为完成时（本组提交）。
- 9.2 防蔓延 grep：五枚入口与装配类源码中发布族/central id 的命中逐条核为 javadoc/注释的边界描述句（"本插件不做什么"点名），代码体内 `apply` 目标仅为核心插件、io.spring dm 裸 id、二进制实现类与 integration/benchmark 自身——零发布族脚本 id 应用，D8 防蔓延条款成立。
- 9.3 形态断言总表：五枚新描述符（dependency-conventions、java-conventions、plugin-conventions、integration-test、benchmark-module）implementation-class 全指向二进制实现类；compile-baseline 描述符从产物消失（注册注销生效）；groovy 脚本余八枚（central、git、github-packages、integrate、publications、publish.gate、publish、release）与 design 预期清单一致；根脚本装配段终文十四行收为十行应用语句。

## 组 10 零差异与全量回归

- 10.1 九样件终比全部一致（任务图、net 双依赖、三线 POM、benchmark 与 integration-test 任务图、doc-gradle 发布任务面），终态样件入库 `final/`；比对剔噪以本卷宗登记的 Python 正则口径（两侧同法）。
- 10.2 configure-on-demand 单工程样件：`--no-configure-on-demand` 与默认开启两形态下 `:tny-game-net:tasks --all` 任务名集合 57 项完全一致——入口执行期经根工程取 ProjectsExtension 的通道在单工程评估场景验证通过。
- 10.3 全量 `check --continue` 绿（8 executed、171 up-to-date，输入未变的合法增量）；`publish --dry-run` 任务图构建绿且 BOM 门禁节点在场；warm help 三连 2.04-2.07 秒，对本册改造前 2.09-2.13 秒无劣化（微降）。
- 10.4 触碰文件按主规格 13 条需求逐条走查通过：声明式与容身之处（新入口与装配类均在二进制载体三形态内；删除与注册逐组同提交；描述符逐枚断言）、惰性形态（新类全部 register/configureEach/withType、providers 读属性、零 tasks.create）、单一事实源（jmhVersion 归位 gradle.properties 键并注释取用点；buildSrc 两处第三方版本落点各一且注释登记关系；零手写坐标）、托管对账（行为不变且行为面样件一致，jmh 与集成排除面零漂移）、Groovy 词法（触碰脚本行为声明式引入与注释、无分号无 spread）、区块顺序（javaProjects 与 gradleProjects 段终文逐字保序，入口内段落对应脚本区块并注释分段）、注释来由（原五处脚本头全量随迁入类 javadoc，含 51 项差异定罪、串染事故、D1 覆写次序、logback 双绑定等实测教训；本册无新增不可逆编排，dryRun 条款不适用原因登记）、长度界线（走查曾对 308 行入口作"申报保留"处理——规格现文无豁免场景，该处理不当；随即拆出 BenchmarkExportConventions 与 BenchmarkSelectionConventions 两类，入口降至 196 行、D1 两段 afterEvaluate 注册次序随选择类整体搬移逐字保持；拆后复验：-PbenchParams 单臂覆写与缺省六臂内省不变、jmhList 7 条目与 jmhSuiteVerify 9 类归族实跑不变、benchmark 任务图对基线一致、buildSrc 测试全绿）、触碰即改（组 9 改写一处过时注释、根引入区注释口径随供给迁移更新）、存量对账（八枚残留脚本零改动，obsolete 目录零触碰）、禁点名（入口与装配类零工程名字面量：`-tester`、`-integration-test` 均经 ProjectsExtension 既有谓词或文档注释豁免；反射取 jmh 生成目录为运行时属性协议非点名）、检查逻辑单测（DemoIsolationCheck 与 BenchmarkSuiteCheck 各含绿红用例入 CI）、接线规则（入口零脚本 id 应用、零双声明点、按类复用 CompileBaselinePlugin 符合二进制互引条款）。

## 组 11 收口

- 核验（/opsx:verify 2026-10-07）结论：除收口程序两项外无实现类严重问题；规格维度按 skip_specs 声明为不适用；独立取证含 16 类界线全量（最大 217 行）、双声明点交叉核查（零命中）、模块自引行在场确认、design 回写抽核（探针四结论与组 4 注入点回写俱在）。
- 一条 WARNING 当场修正：design 迁移计划"基线以 f6bea21a 锚定"改为实况"531e9c4b 锚定（构建面等同 f6bea21a）"。
- 任务 11.2 落笔：跨册登记文件 `openspec/changes/redesign-devline-integration-model/apply-notes/cross-volume-assembly-line.md` 新建（探针四指针与发布族册前置、共有面跨册修订条款、8.1 通道状态三项），未改动在途册既有文件。
