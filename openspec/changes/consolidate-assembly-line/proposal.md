# Proposal

## Why

用户裁决的构建脚本整合程序（buildSrc 约定插件全部 Java 实现、收编为少数总入口，终态 9 个注册 id）已走完两册：pilot-binary-build-conventions 证明了同名 id 二进制替换机制，consolidate-binary-conventions 把"载体三形态、接线规则按载体、长度按单类计量"写入权威账本并转化了三枚薄脚本与对账接线类。本册是批次路线（consolidate-binary-conventions 归档 design D9 节）中的装配线册：装配面现状仍是根脚本三个 configure 段共十四行应用语句、十三个预编译脚本插件与五枚二进制注册项并存；`tny.dependency-management`、`tny.java-module`、`tny.plugin-module` 三枚脚本的行为仍住在只能靠配置期输出与破坏探针验证的脚本载体里，`tny.integration-test` 与 `tny.benchmark-module` 两枚也尚未获得可单测的 Java 形态。立项前对抗研究（四路研究加逐条反驳，含 /tmp 沙箱实测）已定版三件事：其一，本册采保守收纳形态——总入口只吸收非冻结的装配线行为，发布族四脚本与编排线脚本仍由根脚本逐行引入（现行规格过渡期条款如此要求；"二进制入口按 id 收纳脚本插件"虽经探针四证明通路可行，但其端到端装配序语义等价未证，且发布族装配面正处于在途变更 redesign-devline-integration-model 的演练修复窗口）；其二，`tny.dependency-management.gradle` 是被在途册改写过的共有面（提交 34fc8b11 的版本回落行），吸收时须逐段随迁并预写跨册冲突处置；其三，此前研究叙述引用的提交 46908c03 经 `git cat-file` 实查在本仓与远端均不存在，属幻影哈希——装配线脚本保持 Groovy 不是"改动丢失"而是尚未开工，账目以 2e378afd 与前一册五枚提交为准。

## What Changes

- **立口 tny.dependency-conventions**：收编 `tny.dependency-management` 整枚脚本（id 随吸收注销）与根脚本 `configure(subprojects)` 段的 `io.spring.dependency-management` 应用行，两行并一行。入口内部应用次序逐字为 io.spring.dependency-management → idea → maven-publish → 托管配置体；**maven-publish（连同 idea）由该入口无条件应用于全部子工程**——它是插件线（tny-game-doc-gradle 的 publishing 块）与一切自身不声明 maven-publish 的子工程的唯一供给点，MUST NOT 条件化或裁剪。版本派生的回落语义（在途册 34fc8b11 按 redesign D2 写入的 `projectVersion ?: DEFAULT_VERSION` 形态）与其 provenance 注释逐段随迁入实现类；`ManagedVersionsCheck` 类 javadoc 指向脚本头注释的指针同提交改指新类头。
- **立口 tny.java-conventions**：收编 `tny.java-module`（id 注销）与 `tny.compile-baseline` 的注册 id（实现类 `CompileBaselinePlugin` 保留供入口按类复用），插入槽位钉死在根脚本 gate 行之后、integration-test 行之前（javaProjects 段原第 61、62 行相邻位）；入口按 250 行单类界线拆为入口类加若干装配类（sourcesJar 手写注册在前、`withSourcesJar` 复用在后的次序组合、configureEach 惰性排除面等三处形态险点在验收中断言逐条锁定）。
- **立口 tny.plugin-conventions**：收编 `tny.plugin-module`（id 注销），入口内部先应用 CompileBaselinePlugin 再落插件线专属配置——修复既有批次路线文本只把 compile-baseline 归入 java 线入口所留下的插件线缺口；gradleProjects 段终文为 publish、publish.gate 两行保持加 tny.plugin-conventions 一行引入。
- **tny.integration-test 与 tny.benchmark-module 同名 Java 化**：id 保留（`tny-game-integration-test` 与 `tny-benchmark` 的 plugins 块按 id 引用的先例在册），删除脚本文件与新增注册行锁死同提交；integration-test 的 `configurations.named(...)` 判空形态在 Groovy 现文里实际抛异常而非走报红分支，Java 化改 `findByName` 会使原不可达文案变为可达——该失败文案差异作为预期差异申报；benchmark 的 `BenchmarkSuite` 支撑类随同提交转 Java，两段 afterEvaluate 的注册次序、`-PbenchParams` 覆写优先级与 jmhList 产物文件传递的字段化替代在验收断言中逐条对应。
- **jmh 供给迁移（第三处第二层豁免的落定）**：`buildSrc/build.gradle` 增补 `gradlePluginPortal()` 仓库与 `me.champeau.jmh:jmh-gradle-plugin:0.7.3` 的 implementation 依赖，与 `tny-benchmark/build.gradle` plugins 块第 3 行 `version '0.7.3'` 字面量的退役同提交原子执行（探针三变体乙实证双声明并存为硬失败）；同文件第 48 行 `jmhVersion = '1.37'` 迁往 `gradle.properties` 键 `jmhVersion`，模块块改按属性读取（与 toolchain 读取 javaVersion 的先例同形），JMH 接线版本落点唯一化。
- **GitFlow 类型约束的解点决策随探针定版**：`tny.dependency-management` 的版本派生经 `getByType(GitFlow)` 读取 Groovy 支撑类，Java 实现类无法编译期引用它（buildSrc 先编译 Java 后编译 Groovy）。设计决策取"变体乙：tny.git 薄壳一行把派生版本注入类型化 ProjectsExtension"为主、"变体甲：按名取 gitFlow 后经属性协议弱型读取"为探针证伪后的降级；被否决的"变体丁：把实现类放进 src/main/groovy 借混合编译"以沙箱实证并记录否决理由。
- **探针四结论入账并移交**：二进制实现类运行时按 id 应用预编译脚本插件在 Gradle 8.14.5 实测可行（插件在位、扩展、ext、任务四类形态齐备），而预编译脚本作为应用发起方无论目标形态一律复现 ClassLoaderScope 缺失——pilot 结论句"组合能力只在二进制形态存在"按两个方向改写；"全行收纳"选项连同其前置（规格过渡期条款差量修订）移交发布族册，本册加防蔓延条款：入口实现不得引用发布族任何脚本 id。
- 无 **BREAKING**：全部改动为构建装配形态；发布构件的公共 API、任务名、发布产物坐标零变化，唯一申报的预期差异是 integration-test 的失败文案可达性（本册不新增未命名 maven 仓库声明）。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

（无——本册不改变 gradle-build-style 任何需求文本；三形态载体、接线规则按载体、单类计量三条现行文正是本册的执行依据。变更声明 `skip_specs: true`，specs 工件合法缺席。）

## Impact

- **buildSrc**：新增实现类与装配类约十九个（dependency-conventions 入口＋CatalogNotations 支撑类；java-conventions 入口＋拆分类；plugin-conventions 入口；integration-test 拆三类；benchmark-module 拆类＋BenchmarkSuite 转 Java）；删除脚本文件四枚（dependency-management、java-module、plugin-module 吸收注销）＋两枚同名替换（integration-test、benchmark-module）＋compile-baseline 注册行注销；`buildSrc/build.gradle` 增 jmh 依赖与 portal 仓库、注册块净变化＋5/−4；对账检查类的 javadoc 指针改写。
- **根构建脚本**：三个 configure 段十四行应用语句收为十行（subprojects 两行并一、gradleProjects 四行改三、javaProjects 八行改六）；顶部引入区与编排线应用行（git/release/integrate/central/nmcp.aggregation）零触碰——该区域是在途册 redesign-devline-integration-model 的修复面，两区在文件内空间可分，rebase 按分支同步规则处理。
- **模块构建文件**：仅 `tny-benchmark/build.gradle` 两行（plugins 块去 jmh 版本号、jmh 块版本改读属性）；其余六十三枚模块文件一字不动。
- **gradle.properties**：新增键 `jmhVersion=1.37`（真实取用点为 tny-benchmark 的 jmh 块，符合"每个键必须有真实取用点"）。
- **测试与 CI**：新增 Java 装配类随附 ProjectBuilder/纯判定单测按"检查逻辑必须携带单元测试"需求入既有 buildSrc 测试档；`.github/workflows/` 零改动（buildSrc 测试步骤多年在位）。
- **下游与发布产物**：已发布 API 与全部 starter 模块零感知；零差异十样件（任务图、双配置依赖、三线 POM、benchmark 与 integration-test 任务图样件）逐字节比对，唯一申报差异见 What Changes。
- **在途变更协调**：与 redesign-devline-integration-model 的修复面交集只有 dependency-management 的版本回落行（共有面，随迁＋跨册修订登记条款成文）；发布族与编排线七枚脚本零触碰；本册开工不依赖该册收口（批次路线 D9 将装配线册排在发布族册的硬闸门之前）。
