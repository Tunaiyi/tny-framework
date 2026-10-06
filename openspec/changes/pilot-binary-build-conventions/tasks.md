# Tasks

## 1. 规则开道（规格差量之外唯一的生产规则文本改动）

- [x] 1.1 复核本变更 specs 差量八条修订与一条新增对 `openspec/specs/gradle-build-style/spec.md` 现文的逐条对号（MODIFIED 全文以归档同步生效，本步只确认无编号漂移、无场景丢失，`openspec validate pilot-binary-build-conventions --strict` 通过）
- [x] 1.2 改写仓库根 `CLAUDE.md`"Gradle 构建脚本规则"执行要点第二句：程序性行为容身之处扩为"任务动作块、预编译脚本插件载体、二进制实现类"三处；补一句"二进制约定插件之间允许相互 apply 并以 plugins.withType 反应式接线；过渡期内预编译脚本插件之间维持不互 apply"；其余两句与权威指针段不动
- [x] 1.3 验证：`openspec validate pilot-binary-build-conventions --strict` 零错误；CLAUDE.md diff 仅命中执行要点第二句与其补充句

## 2. 双探针（8.14.5 一次性沙箱，产物不入仓库）

- [x] 2.1 探针一：在沙箱目录建最小双脚本插件工程（当前 wrapper 同版本分发），复刻"预编译脚本插件嵌套 apply 另一个预编译脚本插件"形态，记录是否仍复现 Gradle 8.5 时代的类加载器报错
- [x] 2.2 探针二：沙箱 buildSrc 以 java-gradle-plugin 注册最小二进制插件，apply 方法内 `project.getPluginManager().apply("io.spring.dependency-management")` 与 `apply("com.gradleup.nmcp.aggregation")`（不声明对应 compileOnly 依赖），记录解析结果；若失败，改声明 compileOnly 后复测并记录成功形态，供后续册引用
- [x] 2.3 两项结论以完整句子回填 `openspec/changes/pilot-binary-build-conventions/design.md` 的 D2 小节（含探针日期、Gradle 版本、报错原文关键行）；验证：design.md 无"待回填"占位残留

## 3. buildSrc 测试基建

- [x] 3.1 `buildSrc/build.gradle` 增加 `id 'java-gradle-plugin'`、`gradlePlugin { plugins { moduleChecker { id = 'tny.module-checker'; implementationClass = 'tny.convention.checker.ModuleCheckerPlugin' }; projects { id = 'tny.projects'; implementationClass = 'tny.convention.ProjectsPlugin' } } }` 注册块、`testImplementation platform('org.junit:junit-bom:<现值>')` 与 junit-jupiter 依赖、`tasks.withType(Test).configureEach { useJUnitPlatform() }`；JUnit BOM 版本字面量按 D5 注释说明与主仓版本目录的关系
- [x] 3.2 新建 `buildSrc/src/test/java/tny/convention/BuildSrcSmokeTest.java`（ProjectBuilder 建一个空工程并断言 extensions 容器可用），作为基建打通锚；许可证头按项目规则
- [x] 3.3 `.github/workflows/build.yml` check 档主构建命令前增加 `./gradlew -p buildSrc test` 步骤
- [x] 3.4 验证：`./gradlew -p buildSrc test` 本地绿；`git push` 后该分支 build.yml check 作业绿且日志含 buildSrc 测试步骤执行记录

## 4. tny.projects 扩展（根 ext 五键收敛）

- [x] 4.1 新建 `buildSrc/src/main/java/tny/convention/ProjectsExtension.java`：projectGroup、pluginLegacyGroup 两个字符串属性与 moduleProjects()、javaProjects()、gradleProjects() 三个派生集合方法、isBom/isGradlePlugin/isIntegrationTest 三个谓词方法；派生逻辑逐字对照根 `build.gradle:25-39` 现有命名约定（含 javaProjects 排除三类），javadoc 记录"按名过滤不触发工程评估、configure-on-demand 语义与原 ext 派生等价"论断；`ProjectsPlugin.java` 创建名为 `projects` 的扩展并设组号默认值
- [x] 4.2 新建 `ProjectsExtensionTest`：ProjectBuilder 构造带三个后缀成员的假工程集合，断言三类谓词与派生集合的红绿两向（含 -tester 不落入任何排除类别谓词的反例——按 fix-dependency-version-governance D8 既有语义，-tester 留在 moduleProjects 与 javaProjects 两集合中）；先于消费方切换提交
- [x] 4.3 根 `build.gradle`：删除 L25-39 ext 块，改为 `apply plugin: 'tny.projects'` 一行加原注释随迁（组号单一事实源出处注释移入 ProjectsPlugin javadoc，脚本保留指针注释）；L55、L61 `configure(gradleProjects)`/`configure(javaProjects)` 改读 `extensions.getByType(ProjectsExtension).gradleProjects()` 等（L52-53"不互 apply"注释保留原文——本批不推翻其结论，探针一只服务后续册）
- [x] 4.4 六个消费脚本改读扩展（design D4 清单）：tny.bom-platform（parent.moduleProjects 与 endsWith 排除）、tny.java-module（rootProject.ext.moduleProjects 与 -tester 判定保持原样）、tny.dependency-management（rootProject.ext.projectGroup）、tny.central（javaProjects＋两处后缀判定）、tny.integration-test（endsWith 判定改 isIntegrationTest 谓词）、tny.module-checker 不在此列（其五键消费随 5.2 二进制化在类内直读扩展）；触碰行范围内按"触碰即改"处理违例形态
- [x] 4.5 验证：`grep -rn "ext\.\(projectGroup\|pluginLegacy\|moduleProjects\|javaProjects\|gradleProjects\)\|parent\.moduleProjects\|\.ext\.moduleProjects" --include='*.gradle' buildSrc build.gradle` 零命中（注释行逐条豁免判断）；`./gradlew -q :tny-game-common-lang:dependencies --configuration compileClasspath` 与 4.x 前 HEAD 输出零差异（同 daemon、UTF-8 locale 钉住，按抓样口径）

## 5. tny.module-checker 二进制化（首个可单测样板）

- [x] 5.1 新建三个纯逻辑检查类 `tny.convention.checker.GroupAlignmentCheck`、`UnpublishedContractCheck`、`ManagedVersionsCheck`（`buildSrc/src/main/java/` 下）：输入为工程描述记录（名字、组、构建文件存在性、角色声明映射）与事实值、输出问题清单字符串列表；判定文本与原脚本逐字对应；每类随附可执行单测（纯判定类以事实夹具构造输入、装配面以 ProjectBuilder）覆盖一个通过用例与一个违例用例，违例用例断言错误信息含判红对象与理由（新增需求第一场景）
- [x] 5.2 新建 `ModuleCheckerPlugin`：组号对账与托管版本面对账在 `gradle.projectsEvaluated` 回调调用检查类并聚合抛 GradleException，零发布合同逐成员工程在 `afterEvaluate` 收尾调用检查类抛首条违例（时机与原脚本一致），工程描述记录的采集方式逐字对照原脚本（含"未评估工程跳过"分支、沿依赖边强制评估读 ModuleSetting 的零发布合同段、`getDependencyProject` 弃用替代法）；类头 javadoc 承载原脚本头注释全部职责边界与 provenance（三项对账的规格出处、adopt-gradle-official-dsl 沿革改写为完整句子），javadoc 写明单测覆盖与端到端语义的分工（design D3）
- [x] 5.3 删除 `buildSrc/src/main/groovy/tny.module-checker.gradle`；确认注册块（3.1）的 id 生效、根 `build.gradle:47` 应用行不改（ext 块删除后现文为 39 行）；四处按名注释提及（爆炸半径摘要所列）复核语义成立无需改动
- [x] 5.4 验证：`./gradlew -p buildSrc test` 全绿；`./gradlew -q help` 配置期对账照常通过；破坏探针两例（design D7）：临时给某发布线模块加错误组号声明使组号对账报红（改根事实源设值不会报红——派生组号随之自洽）、临时让某发布线成员加一条指向不发布模块 :tny-game-integration-test 的 implementation 依赖边使零发布合同报红（给该模块自身加 publishing 块不会报红——它按命名约定不属于 javaProjects，判红由依赖边构成），各自捕获报错文案后恢复原文件并用 sha256 比对确认逐字节还原

## 6. 零差异与全量回归

- [x] 6.1 基线零差异三件套（改造前后同 daemon 抓取，前缀过滤口径按 design D7）：`tasks --all` 任务图、代表性模块（tny-game-net、一个 starter）`dependencies` 清单、双 POM（java 线与插件线各一）生成物逐行比对
- [x] 6.2 验证：`./gradlew check` 全量绿；`./gradlew publish -PdryRun` 形态不可用则以 `./gradlew generatePomFileForMavenPublicationPublication` 代替干跑面，确认发布坐标零变化
- [x] 6.3 触碰文件按 gradle-build-style 全部 11 条需求过一遍评审（重点：注释随迁完整性、区块顺序、无新增点名）；apply-notes.md 记录探针结论、破坏探针文案、零差异比对摘要与 buildSrc 配置耗时前后值

## 7. 收口

- [ ] 7.1 `openspec verify pilot-binary-build-conventions`（或按项目 verify 流程）无 CRITICAL；用户批准后 `/opsx:archive` 归档，规格差量同步进账本
- [ ] 7.2 归档后观察账登记两项挂后续册：托管版本面对账的端到端破坏探针形态（D7 挂账）、buildSrc 耗时劣化阈值复核（Risks 第四条）
