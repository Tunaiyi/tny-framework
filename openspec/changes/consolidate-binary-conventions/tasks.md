# Tasks

## 1. 规则开道与归档账目更正

- [x] 1.1 复核本变更 specs 差量（两条 MODIFIED 加一条 ADDED）对 `openspec/specs/gradle-build-style/spec.md` 入账后现文的逐条对号：MODIFIED 全文以现文为底逐字扩写、既有场景零丢失、ADDED 三场景齐全；验证：`openspec validate consolidate-binary-conventions --strict` 零错误
- [x] 1.2 改写仓库根 `CLAUDE.md` 执行要点第二句末的归档路径引用（裸目录 pilot-binary-build-conventions 改为 openspec/changes/archive/2026-10-07-pilot-binary-build-conventions），过渡期不互 apply 句保留并追加"完整接线规则见 gradle-build-style 规格'约定插件接线规则按载体定'需求"指针；其余两句与权威指针段不动；验证：CLAUDE.md diff 仅命中该行
- [x] 1.3 新建 apply-notes.md 的"归档账目更正"小节，登记 pilot 卷宗四处失实（proposal 消费面"六脚本九行"枚举漏支撑类、五路复验"全部 PASS"未覆盖支撑类、任务 7.2 观察账登记缺位、谓词数量记载三与实际交付四）与 grep 范围教训改写句（扫描面必须覆盖根构建脚本、约定插件载体、模块构建文件、buildSrc 支撑类四类）；验证：小节四加一条目齐全
- [x] 1.4 验证：`openspec validate consolidate-binary-conventions --strict` 通过；更正小节与 CLAUDE.md 改动同提交前逐文件复核

## 2. 基线快照入库

- [x] 2.1 在同一 Gradle daemon、UTF-8 locale 钉住条件下抓取改造前五样本并转存 `openspec/changes/consolidate-binary-conventions/baseline/`：全量任务图（`./gradlew tasks --all`）、`tny-game-net` compile 与 runtime 依赖清单、java 线与插件线双 POM（先运行 `generatePomFileForMavenJavaPublication` 与插件线同名任务再转存，防空文件）；验证：五样件非空且 README 记录锚定 `git rev-parse HEAD` 哈希与抓样环境（Corretto 21、无 JAVA_TOOL_OPTIONS、剔噪口径按 context 权威条目）
- [x] 2.2 留存缺陷证据与耗时基线：`./gradlew :tny-game-bom:publish --dry-run` 的 HEAD 报红输出全文转存 baseline（Task not found 证据对）；warm daemon 下 `./gradlew help` 连续三次耗时记录为"改造前"值；验证：两项读数入 apply-notes 组 2 小节

## 3. 探针三（依赖类路径类型化访问，沙箱验证，产物不入仓库）

- [x] 3.1 在 /tmp 沙箱（当前 wrapper 同版本分发）建最小工程：buildSrc 以 implementation 声明 `io.spring.gradle:dependency-management-plugin:1.1.7`，注册二进制插件在其 apply 内对子工程按 id 应用该第三方插件、经 `getByType` 取回 `DependencyManagementExtension` 并调用 `getManagedVersions()`；判据三条逐一记录：编译通过、按 id 应用成功、取回实例的类加载器与 buildSrc 判定同一
- [x] 3.2 对照变体与顺带记录：保留根 plugins 块带版本声明行的双声明并存变体结果（谁生效、报错形态）；同法顺带记录 `com.gradleup.nmcp` all-in-one 构件（Central 可解析、描述符齐）与 `me.champeau.jmh`（不在 Central，需 gradlePluginPortal）两枚的供给形态供后续册引用；结论以完整句子回填 design.md"探针结论"小节
- [x] 3.3 验证：design.md 探针小节无"待回填"占位残留、三判据逐条有实测句子；沙箱目录删除、`git status` 对仓库零污染

## 4. 零发布合同自检修复（测试先行）

- [ ] 4.1 新建 `buildSrc/src/test/java/tny/convention/ModuleSettingTest.java` 三用例（ProjectBuilder 父子夹具，根先应用 tny.projects）：线内命名（如 tny-game-core）应用 module-setting 并调 `enableUnpublished()` 断言抛错且文案含工程路径与 javaProjects 字样；线外命名（-integration-test 后缀、无前缀两形态）断言不抛；根未应用 tny.projects 时断言"扩展不在位"报错；在 HEAD 现文上先跑一次并记录违例用例确实失败（证明用例打中缺陷本体）
- [ ] 4.2 `ModuleSetting` 转 Java（包名 tny.convention 与全部方法外形不变：`enum Mode`、`has(Mode)`、`static enabled(Project, Mode)`、`void enableApp()`、`void enableUnpublished()`），`enableUnpublished` 成员判定改 `getRootProject().extensions.getByType(ProjectsExtension.class).javaProjects()`，删除任何 has 兜底；类头 javadoc 承接原 Groovy 类头全部注记并登记"自检曾随根 ext 退役静默失效"的实测教训完整句；删除 `ModuleSetting.groovy`；验证：4.1 三用例全绿、`./gradlew -p buildSrc test` 全绿
- [ ] 4.3 端到端破坏探针：临时给 `tny-game-net/build.gradle` 加 `id 'tny.module-setting'` 应用行与 `moduleSetting { enableUnpublished() }` 声明块，运行 `./gradlew :tny-game-net:help` 记录配置期报红文案，还原并用 sha256 对账逐字节一致；探针记录入 apply-notes
- [ ] 4.4 验证：`./gradlew -q help` 配置期对账照常通过（三个既有声明者不报红——线外语义保持）；组 4 全部提交后 `./gradlew -p buildSrc test` 绿

## 5. tny.module-setting 薄壳同名替换

- [ ] 5.1 新建 `tny.convention.setting` 无关的既有包形态——`ModuleSettingPlugin.java`（`buildSrc/src/main/java/tny/convention/`）一行创建 `moduleSetting` 扩展；同一提交内删除 `buildSrc/src/main/groovy/tny.module-setting.gradle` 并在 `buildSrc/build.gradle` gradlePlugin 块加注册行（id 不变）；类 javadoc 承接原脚本头注释（职责、取代史、边界句）；验证：`build/pluginDescriptors/tny.module-setting.properties` 的 implementationClass 指向二进制实现类，`./gradlew -q help` 绿
- [ ] 5.2 验证：三个模块的 `moduleSetting {}` 声明块与两处 `enabled` 沿边查询零改动照常工作（`./gradlew -p buildSrc test` 绿加 `./gradlew :tny-game-integration-test:help` 配置期绿）

## 6. tny.compile-baseline 与 tny.bom-platform 同名替换

- [ ] 6.1 `CompileBaselinePlugin.java` 等价迁移（`configurations.configureEach` 双缓存策略、`tasks.withType(AbstractCompile/Javadoc).configureEach` 经 `providers` 读 encoding 属性）；同一提交内删脚本加注册；验证：与 baseline 任务图比对零漂移、`./gradlew -q help` 绿
- [ ] 6.2 `BomPlatformPlugin.java` 等价迁移（经 `project.getParent()` 按类型取根扩展、constraints 派生循环与 `isIntegrationTest` 排除与按名排序逐句等价，类 javadoc 承接原头注释）；同一提交内删脚本加注册；验证：`./gradlew :tny-game-bom:generatePomFileForMavenJavaPublication` 后 POM 与 baseline 逐字节一致
- [ ] 6.3 验证：`./gradlew -p buildSrc test` 全绿；两枚描述符形态断言逐条记录入 apply-notes

## 7. 接线类 Java 化与依赖声明点迁移

- [ ] 7.1 `buildSrc/build.gradle` 增 `implementation 'io.spring.gradle:dependency-management-plugin:1.1.7'`，版本字面量按 pilot D5 先例以注释写明与主仓版本目录及 gradle.properties 的关系（单一落点登记义务）
- [ ] 7.2 新建 `ModuleCheckerPlugin.java` 逐段等价替换 `.groovy`（三段时机、configure-on-demand 已评估过滤、自依赖边双保险、`evaluationDependsOn` 与 `rootProject.project(d.path)` 弃用替代原样；托管快照改 `DependencyManagementExtension.getManagedVersions()` 类型化读取；类头 javadoc 承接原文件头全部职责边界与分工注记，语言例外句改写为"随本册类型化路线落地而撤销"的完整沿革）；同一提交内删除 `ModuleCheckerPlugin.groovy`
- [ ] 7.3 根 `build.gradle` 退役 `io.spring.dependency-management` 带版本 `apply false` 行（按探针三结论），`configure(subprojects)` 应用行保留，原行位置注释改指 buildSrc 依赖声明；第 16 行旧注释"buildSrc 作用域无法解析父构建类路径插件"按运行期与编译期适用面差异改写为完整句（pilot design 注记边界段的落实）。探针三变体 B 实证双声明并存为硬失败，本条与 7.1 的依赖声明必须落在同一提交（探针结论小节原子条款）
- [ ] 7.4 验证：`./gradlew -p buildSrc test` 全绿；`./gradlew -q help` 配置期托管对账照常静默通过；破坏探针复跑一例（临时扰动组号事实源设值→组号对账报红、还原 sha256），堆栈判红帧落在 `ModuleCheckerPlugin.java` 且无脚本桥接帧，记录入 apply-notes
- [ ] 7.5 验证：`tny-game-net` 双依赖清单与插件线 POM 对 baseline 零差异（`./gradlew :tny-game-net:dependencies --configuration compileClasspath` 等按抓样口径）

## 8. BOM 门禁半配对缺陷修复

- [ ] 8.1 `tny-game-bom/build.gradle` plugins 块在 `id 'tny.publish'` 行后补 `id 'tny.publish.gate'` 一行，注释写明来由（拆分提交 0faf6edb 只补根装配线两条集合之遗漏；变更册名全文登记）；验证：`./gradlew :tny-game-bom:publish --dry-run` 任务图含 `:tny-game-bom:checkPublishPrerequisites` 且挂在各发布任务依赖边上，与 baseline 的报红记录构成前后对
- [ ] 8.2 验证：根级 `./gradlew publish --dry-run` 任务图构建通过（缺陷复现路径转绿）；`tny-game-bom` 的 POM 与 Gradle 模块元数据经 `generatePomFileForMavenJavaPublication` 与 `publishToMavenLocal` 两条路径对 baseline 逐字节零差异（门禁节点不产物化，预期零漂移）；该任务图差异登记为全册唯一申报的预期差异

## 9. 观察账两项在册执行

- [ ] 9.1 托管版本面端到端破坏探针（pilot D7 挂账承接）：临时把 `gradle.properties` 一个守卫族版本键（如 log4j 族）扰动一个补丁位，运行 `./gradlew -q help` 记录 `ManagedVersionsCheck` 报红全文（逐项列出坐标、声明值、生效值），还原并 sha256 对账；结果入 apply-notes 并标记该观察账项闭环
- [ ] 9.2 buildSrc 耗时阈值复核（pilot Risks 第四条承接）：全部替换完成后 warm daemon `./gradlew help` 三次取数，与 2.2 改造前值对照；劣化未超 3 秒记"闭环"，超阈按"移交必须指名承接册"登记到后续合并册并在 apply-notes 写明指名

## 10. 零差异与全量回归

- [ ] 10.1 改造后五样本重跑并与 baseline 逐字节比对：任务图唯一差异为 8.2 申报的 BOM 门禁子图，其余四样件零差异；比对摘要与差异行清单入 apply-notes
- [ ] 10.2 全量 `./gradlew check --continue` 绿、`./gradlew -p buildSrc test` 绿（与 CI build.yml 的 buildSrc 测试步骤同命令）；`./gradlew :tny-game-bom:build` 单模块绿
- [ ] 10.3 触碰文件按 gradle-build-style 现文全部 13 条需求逐条走查（重点：新 ADDED 接线需求与本册各引入行的自洽、载体三形态判据、单类计量与目录页义务、provenance 注释逐段随迁、触碰即改），走查表入 apply-notes

## 11. 收口

- [ ] 11.1 `openspec verify consolidate-binary-conventions` 无 CRITICAL；用户批准后执行归档，规格差量同步入账本
- [ ] 11.2 归档后登记去向：9.2 若超阈其移交指名的合并册、探针三对 nmcp/jmh 的形态结论被后续册引用的出处句，写入下一册（装配线册）立项时的引用清单；验证：`openspec list --json` 本册不再出现于活动目录，主规格需求数由 12 变为 13
