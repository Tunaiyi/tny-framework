# Tasks

## 1. 册门核对

- [x] 1.1 复核批次路线前提：确认 `openspec/changes/archive/2026-10-07-consolidate-binary-conventions/design.md` D9 节将装配线册排在 redesign 硬闸门之前、本册范围（三入口＋两枚同名保留）与 D9 登记不冲突；确认幻影哈希 46908c03 在本册工件中仅出现于清账句（声明其不存在并给出真实链条），不得作为事实引用——验证：`grep -rn "46908c03" openspec/changes/consolidate-assembly-line/` 的每处命中其上下文均为清账表述，且 D9 顺序与范围核对结论记入 apply-notes
- [x] 1.2 现查开工基线：`git log --oneline -3` 与 `git status --porcelain` 记录实况（工作树若含他人在途文件，登记避让清单，不 stash 不覆盖）；验证：实况记入 apply-notes 组 1 小节
- [x] 1.3 核对 `skip_specs: true` 声明生效：`openspec status --change consolidate-assembly-line --json` 中 specs 工件为 skipped；验证：命令输出截图性文字记入 apply-notes

## 2. 基线快照重抓入库

- [x] 2.1 以开工 HEAD 锚定，按 context"零差异验收基线抓样口径"（Corretto 21、UTF-8 钉住、独立守护进程注册表、PATH 修正坏 git——沿前册 baseline/README 成文口径）抓取常规七样件入 `baseline/`：全量任务图、`tny-game-net` 双配置依赖清单、三线 POM（java 线、插件线、BOM）、warm help 三连耗时；验证：README 记锚定哈希与样件行数，全部非空
- [x] 2.2 抓取本册新增四面样件入 `baseline/`：`tny-game-doc-gradle` 的 publishing 块与编译任务编码选项快照（`./gradlew :tny-game-doc-gradle:compileGroovy --dry-run` 加属性读取记录）、java 线与 BOM 线的 publish 任务名清单（`publish --dry-run` 行集）、`tny-benchmark` 与 `tny-game-integration-test` 的任务图样件（`tasks --all` 分模块）；验证：四样件入库且 README 增列
- [x] 2.3 验证：组 2 样件清单与锚定提交哈希记入 apply-notes；`git status` 除在途避让文件外对仓库零污染

## 3. 探针五（GitFlow 通道）与 jmh 供给沙箱预演

- [x] 3.1 GitFlow 通道变体乙沙箱验证：最小工程复刻"tny.git 薄壳一行注入 ProjectsExtension 新属性 derivedProjectVersion＋Java 实现类按类型读取"，三判据逐条记录：编译通过、对当前 Groovy 形态的 `projectVersion`/`DEFAULT_VERSION` 回落两分支取值逐字等值、任务图零差异；证伪则按 D4 改验变体甲并记录降级来由；结论完整句子回填 design 探针结论小节
- [x] 3.2 jmh 供给预演：沙箱复刻 buildSrc 增 `gradlePluginPortal()`＋jmh implementation 后按裸 id 应用 `me.champeau.jmh` 成功且扩展类加载器同一；同提交移除版本号形态与携带版本号形态各跑一次（后者应复现双声明硬失败，作反向判据）；结论回填
- [x] 3.3 验证：design 探针小节无占位残留；`rm -rf` 沙箱目录，仓库零污染

## 4. dependency-conventions 立口

- [x] 4.1 实现 `DependencyConventionsPlugin` 与 `CatalogNotations` 支撑类：入口内部次序逐字 io.spring dm→idea→maven-publish→配置体；九条 mavenBom 导入句与具名托管条目经 `providers.gradleProperty` 读族版本键、经 VersionCatalogsExtension 按别名取目录条目（不复制条目表）；34fc8b11 回落行语义与 D2 provenance 注释、原脚本头注释全量入类 javadoc（目录页义务：逐项行为指名所在方法/类）；变体乙同提交在 `tny.git.gradle` 追加一行注入并改写其头注释，`ProjectsExtension` 增派生版本属性与 javadoc。任务序修订入账：原 4.1"先写测试"针对尚不存在的类无法编译执行（该模式适用既有类的修复型任务，如前册组 4），本组改为实现先行、测试紧随且先于消费方切换提交，仍满足"检查逻辑必须携带单元测试"需求
- [x] 4.2 补测试：`DependencyConventionsPluginTest` 与 `CatalogNotationsTest`（ProjectBuilder 父子夹具）覆盖版本回落两分支（注入值在位、缺位取 `DEFAULT_VERSION`）、组号派生红绿向与目录别名缺失的报红向；违例用例断言错误信息含判红对象与理由
- [x] 4.3 同提交三件：删除 `tny.dependency-management.gradle`、`buildSrc/build.gradle` 增注册行、根 `build.gradle` subprojects 段两行并一（原行位置注释登记入口名）；`ManagedVersionsCheck.java` 类 javadoc 悬空指针改指新类头；`tny.dependency-management.gradle:9-10` 若有随迁注释按"8.5 发现、8.14.5 复测复现"完整口径改写
- [x] 4.4 破坏探针（锁三点）：临时注释掉根脚本 dependency-conventions 应用行 → `./gradlew -q help` 配置期报红（module-checker 或 doc-gradle publishing 块，记录报错原文），还原 sha256 对账；再临时给 tny-game-net 加跳过供给验证不可行（无跳过通道即通过）；结果入 apply-notes
- [x] 4.5 验证：`./gradlew -p buildSrc test` 全绿；根 `help` 绿；描述符指向实现类（形态断言）；`baseline` 托管面样件（net 双依赖清单）逐字节一致

## 5. java-conventions 立口

- [x] 5.1 先写测试：源集与清单派生、tester 宿主排除谓词、`-tester` 夹具挂接面的可单测判定段拆为纯函数类并补红绿用例（依"检查逻辑必须携带单元测试"需求）；记录先失败后实现
- [x] 5.2 实现 `JavaConventionsPlugin` 入口与拆分类（JavaCompileConvention、JavadocSourcesConvention、ManifestJarConvention 等按 250 行单类计量拆）：sourcesJar 手写注册在前、`withSourcesJar` 命名复用在后的次序组合作为代码内注释＋断言锁定；`configurations.configureEach` 惰性排除面保持惰性形态；原脚本头注释与实测教训（51 项差异定罪记载）逐段随迁入目录页 javadoc
- [x] 5.3 同提交三件：删除 `tny.java-module.gradle`、注册行新增（id `tny.java-conventions`）、根 `build.gradle` javaProjects 段第 61-62 行（compile-baseline 与 java-module）并为一行 `apply plugin: 'tny.java-conventions'`（gate 行之后、integration-test 行之前的槽位）；`compile-baseline` 注册行从 gradlePlugin 块移除（实现类保留）
- [ ] 5.4 验证：`-p buildSrc test` 全绿；任务图对基线零差异；`tny-game-net` 双依赖清单与 java 线 POM 逐字节一致；编码选项与缓存策略样件一致（doc-gradle 面留组 6 一并验）；sourcesJar 组合断言（`tasks --all` 中 sourcesJar 任务属性与 assemble 挂接对基线）

## 6. plugin-conventions 立口

- [ ] 6.1 实现 `PluginConventionsPlugin`：内部先应用 CompileBaselinePlugin 语义再落插件线专属配置（原 plugin-module 脚本 40 行逐句等价＋头注释随迁）；目录页注释写明与 java-conventions 共享 CompileBaselinePlugin 的"按类复用非逐字复制"边界自证
- [ ] 6.2 同提交三件：删除 `tny.plugin-module.gradle`、注册行新增、根 `build.gradle` gradleProjects 段 compile-baseline 与 plugin-module 两行并为一行 `apply plugin: 'tny.plugin-conventions'`（publish、gate 两行保持原位）
- [ ] 6.3 验证：`-p buildSrc test` 全绿；`tny-game-doc-gradle` 的 publishing 块与编译编码样件、插件线 POM 对基线逐字节一致；gradleProjects 线任务图零差异

## 7. integration-test 同名 Java 化

- [ ] 7.1 先写测试：受控隔离撮合与应用蓝本核对的判定段拆为纯函数类补红绿用例；`findByName` 替代 `named` 判空后的报红文案可达路径写成用例（预期差异：原不可达文案现可达，措辞逐字对照原脚本）
- [ ] 7.2 实现三类拆分（入口接线、源集通道 `IntegrationLaneConvention`、隔离核对判定类）；头注释随迁；同提交删 `tny.integration-test.gradle` 加注册行（id 不变）；根 javaProjects 段 integration-test 行与 `tny-game-integration-test/build.gradle:9` 的 plugins 引用零改动
- [ ] 7.3 验证：`-p buildSrc test` 全绿；integration 系配置的 logback/log4j-to-slf4j 排除面样件对基线一致（`./gradlew :tny-game-integration-test:dependencies --configuration integrationRuntimeClasspath` 抓后比对）；integrationTest 任务面与 `tny-game-integration-test` 任务图样件零差异；描述符形态断言

## 8. benchmark-module 与 BenchmarkSuite 转 Java 及 jmh 供给迁移

- [ ] 8.1 先写测试：族属清单核对判定拆为纯函数类补红绿用例（jmhSuiteVerify 对账面）
- [ ] 8.2 实现 benchmark 入口与任务组类：两段 afterEvaluate 次序（族属清单段在前、benchParams 覆写段在后）代码注释锁定；`ext.listFile` 动态属性字段化替代；任务名与 finalizedBy 边逐字不变；`BenchmarkSuite` 支撑类同册转 Java
- [ ] 8.3 同提交原子四件：`buildSrc/build.gradle` 增 `gradlePluginPortal()` 与 jmh implementation 依赖（版本落点注释沿 dm 先例）、删除 `tny.benchmark-module.gradle` 加注册行（id 不变）、`tny-benchmark/build.gradle:3` 去 `version '0.7.3'`、`:48` 的 `jmhVersion = '1.37'` 改读 `gradle.properties` 新键 `jmhVersion=1.37`（gradle.properties 增键行含取用点注释）
- [ ] 8.4 验证：`-p buildSrc test` 全绿；`-PbenchParams` 覆写优先级回归（带/不带参数两跑记录 jmh 任务属性）；jmhList 产物文件路径与 jmhListVerify 读回一致；`tny-benchmark` 任务图样件零差异；`-Pjmh` 相关任务 dry-run 行集对基线

## 9. 次序契约、防蔓延与目录页复核

- [ ] 9.1 逐字序复核：三入口类 javadoc 目录页与原行序对照表逐一核对（根脚本终文十行的相对次序 == 原十四行去掉被吸收行的交错）；验证：对照表入 apply-notes
- [ ] 9.2 防蔓延 grep：`grep -n "tny.publish\|tny.publish.gate\|tny.publications\|tny.github-packages\|tny.central" buildSrc/src/main/java/tny/convention/*Conventions*.java` 与三入口实现类逐一核查零 id 字符串引用（注释中为交代来由的完整文档名除外）；验证：grep 输出入 apply-notes
- [ ] 9.3 形态断言总表：六枚描述符（三入口＋两同名＋compile-baseline 注销后残留检查）逐一 `implementation-class` 指向二进制实现类；`buildSrc/src/main/groovy/` 余九枚脚本清单核对（git、release、integrate、central、publish、publish.gate、publications、github-packages ＋ 无其余）

## 10. 零差异与全量回归

- [ ] 10.1 十一样件终态比对基线：常规七件＋组 2 四面（doc-gradle publishing/编码、两线 publish 任务名清单、benchmark/integration-test 任务图）逐字节一致；预期差异仅申报的 integration-test 失败文案可达性（非样件面）
- [ ] 10.2 configure-on-demand 单工程样件：`./gradlew :tny-game-net:tasks` 与带 `-Dorg.gradle.configureondemand=true` 场景的输出对基线一致（入口取根扩展通道在 CodOD 下的实证）
- [ ] 10.3 全量：`./gradlew check --continue` 绿、`-p buildSrc test` 绿、`./gradlew publish --dry-run` 任务图构建绿且 BOM 门禁节点保持在场、warm help 三连耗时对改造后前值劣化不超过 3 秒阈值；结果与差异行清单入 apply-notes
- [ ] 10.4 触碰文件按主规格 13 条需求逐条走查（重点：单类计量、目录页两跳、provenance 随迁、防蔓延、接线需求三场景自洽）；走查表入 apply-notes

## 11. 收口

- [ ] 11.1 `/opsx:verify consolidate-assembly-line` 无 CRITICAL；用户批准后执行归档，规格账本本册无差量入账（skip_specs），归档摘要注明十八枚 id 变十七枚（九二进制加八脚本）的净效果
- [ ] 11.2 归档后登记去向：探针四结论与限定语的发布族册引用指针、跨册修订条款（redesign 若改版本回落形态的落点条款）写入 redesign 册 apply-notes 的协调小节；验证：两处文字账可检索
