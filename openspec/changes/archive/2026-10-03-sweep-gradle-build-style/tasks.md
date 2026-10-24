# Tasks

## 1. 基线快照

- [x] 1.1 在当前主干形态上运行 `./gradlew clean build` 确认全量构建成功，失败即停止清扫并先修基线，把构建结论记入变更目录 verification-notes.md。
- [x] 1.2 采集三份基线存进变更目录：`./gradlew tasks --all` 的任务清单输出、`./gradlew :tny-game-net:dependencies :tny-game-doc:dependencies` 的依赖解析报表、`./gradlew publishToMavenLocal` 后本地 Maven 仓库中本组构件（jar、sources jar、javadoc jar、POM）的文件清单。

## 2. 全仓文本纪律与注释残留清零（设计 D8、D9 的机械部分）

- [x] 2.1 全仓 `.gradle` 文件中不含插值的字符串改为单引号（覆盖模块文件 `project(":xxx")` 与 `apply plugin: "..."` 形态约一百九十八处、根文件与 gradle/ 脚本内约四十处；含 `${}` 的串保留双引号），settings.gradle 的 pluginManagement 仓地址同批处理；改后 grep 复核不存在"字符串体内无 `$` 仍用双引号"的残留，运行 `./gradlew clean build` 确认构建成功。
- [x] 2.2 修正 gradle/git.gradle 的五处分号结尾与三处无大括号 if，gradle/tny-module.gradle 中 `Automatic-Module-Name` 行的复述性行尾注释并入语义完整的说明；运行 `./gradlew :tny-game-net:compileJava` 确认脚本仍可求值。
- [x] 2.3 删除全部注释掉的配置残留：settings.gradle 九行注释 include、根 build.gradle 一行注释 ext 配置、gradle/publish-plugin.gradle 尾部注释掉的 configureDeploymentRepository 方法整体、tny-game-codec 五行、tny-game-expr-graaljs 九行、tny-game-starter-basics 十四行、tny-game-net 三行、tny-game-doc-gradle 四处、tny-game-common-lang 两行、tny-game-doc 三行、tny-game-net-demo 两行、tny-game-mongodb 两行、tny-game-loader 一行、tny-game-common-lifecycle 一行、tny-game-starter-redisson 一行、tny-game-starter-net-netty4 一行、tny-game-net-netty4-codec-jprotobuf 处确认无残留；运行 `./gradlew clean build` 确认全量装配不受影响。
- [x] 2.4 把已是字符串的属性的多余插值包裹改为直接赋引用（根文件 `version "${projectVersion}"`、`group projectGroup` 核对、编码赋值等），运行 `./gradlew :tny-game-basics:jar` 并对产物的 MANIFEST 与 POM 中的版本字段确认仍为 `5.7.x-SNAPSHOT` 形态原值。

## 3. 根构建文件重排与单一事实源收敛（设计 D1、D2、D4、D6、D12）

- [x] 3.1 新建 gradle/project-checks.gradle（含文件头块注释），把根文件"组号对账"与"零发布合同对账"两个 gradle.projectsEvaluated 块逐行迁入，根文件原位置改为一行 apply；把任意一个模块的组号临时改为错误值运行 `./gradlew :tny-game-net:compileJava` 确认对账报红并列出违例，恢复后确认构建通过。
- [x] 3.2 把根文件 dependencyManagement 中十七条携带版本字面量的 dependency 声明的版本段移入 gradle/dependency.gradle 的 vers 表并改为具名引用（设计 D4 清单），运行 `./gradlew :tny-game-net:dependencies --configuration runtimeClasspath` 与基线报表 diff 确认零差异。
- [x] 3.3 消灭 spread 与点名求值：根文件两处 spread 编码行、compileJava 点名块（含 `"UTF-8"` 硬编码统一为 encoding 属性引用）、`compileJava.dependsOn(processResources)`、idea 块 outputDir 的 `compileJava.destinationDir` 求值，全部改写为 `tasks.withType(JavaCompile).configureEach` / `tasks.withType(Javadoc).configureEach` / 约定路径或 `tasks.named` 惰性形态（设计 D6、根 idea 按 D6 附带项处理），运行 `./gradlew :tny-game-net:test` 确认编译与测试通过且 `-parameters` 仍生效（用 `javap -p -c` 抽查测试产物参数名保留）。
- [x] 3.4 `javaProjects` 与 `gradleProjects` 派生合并为单次赋值合取谓词形态，谓词各条件保留原来由注释（设计 D12），运行 `./gradlew projects` 确认工程分类集合成员与迁移前逐一相同。
- [x] 3.5 根文件按规格需求五的固定区块顺序重排（插件、身份、编译与 toolchain、仓库与依赖管理、依赖、任务、发布），configure(subprojects)、configure(gradleProjects)、configure(javaProjects) 三个公共配置块分别迁往新脚本 gradle/subprojects-baseline.gradle、gradle/plugin-module.gradle、gradle/java-module.gradle（各带文件头块注释），根文件只留一行引入，回到八十行内（设计 D1）；运行 `./gradlew clean build` 确认全量构建成功，并把 `./gradlew tasks --all` 输出与基线 diff 确认任务清单零差异。

## 4. gradle/ 脚本分域拆分与界线达标（设计 D3、D10）

- [x] 4.1 新建 gradle/publish-gate.gradle（含文件头块注释），把 publications.gradle 的共享仓判定谓词、发布一致性校验闭包、checkPublishPrerequisites 任务（改为 `tasks.register` 形态）与门禁挂接整体迁入（校验语义逐行不变），publications.gradle 一行 apply 引入；确认两个文件都回到二百五十行内（`wc -l`），并在开发线跑一次 `./gradlew publish` 确认门禁对版本形态的断言行为与迁移前一致。
- [x] 4.2 为 git.gradle、dependency.gradle、publications.gradle、publish.gradle、publish-plugin.gradle、tny-module.gradle 补文件头块注释（本脚本负责什么、与哪些机制相邻、边界在哪，形状以 gradle/integration-test.gradle 为先例；设计 D10），`head -15` 逐文件抽查确认注释与脚本实际职责相符。
- [x] 4.3 gradle/dependency.gradle 压回二百五十行界内：删除第 192 行注释残留、连续空行收敛、graalvm_tools 两条并入 graalvm 分组位置（条目不删不减），`wc -l` 确认达标且 `./gradlew :tny-game-expr-graaljs:dependencies` 与基线 diff 零差异。
- [x] 4.4 gradle/central.gradle 中 `"AUTOMATIC"`、`"verification"` 等无插值串改单引号并核查该文件对需求六 dryRun 条款的适用性（结论：上传由 nmcp 插件任务承担、本脚本自定义动作仅断言与校验，不构成不可逆编排，无需补预览通道——把该判定记入变更目录 verification-notes.md），运行 `./gradlew centralCheck` 确认开发线上按既有语义报红（仅发布分支放行）。

## 5. 模块文件修复与超线拆分（设计 D5、D7、D11 及坐标收敛）

- [x] 5.1 删除根 java-module 线的手写 sourcesJar 声明（保留 `java { withSourcesJar() }`），gradleProjects 线的 sourcesJar 与 createProject 改 `tasks.register` 惰性形态且 dependsOn 移入注册闭包（设计 D5），跑 `./gradlew publishToMavenLocal` 比对基线构件清单：sources jar 与 javadoc jar 逐模块仍在、内容抽查 `unzip -l` 差异仅为条目顺序。
- [x] 5.2 tny-game-namnspace 的 commons-codec、hutool-core、zero-allocation-hashing 与 tny-game-doc-gradle 的 tools-template 四处手写带版本坐标改为具名引用（commons-codec 走既有 libs 条目加根 BOM 派生版本，其余两条与 tools-template 在 gradle/dependency.gradle 增/用带版本条目），运行 `./gradlew :tny-game-namnspace:dependencies :tny-game-doc-gradle:dependencies` 与基线 diff 确认版本解析零差异，并跑 `./gradlew :tny-game-namnspace:test` 确认通过。
- [x] 5.3 新建 gradle/bench-suite.gradle（含文件头块注释），迁入 tny-bench 的族清单 ext 与 jmhList/jmhListVerify/jmhSuiteVerify/benchRoutineExport 四个任务注册（模块归属保持 `:tny-bench`），模块文件一行 apply 后 `wc -l` 回到八十行内（设计 D11）；运行 CI 等价命令 `./gradlew :tny-bench:jmhCompileGeneratedClasses :tny-bench:jmhList :tny-bench:jmhSuiteVerify -q` 确认与基线行为一致。
- [x] 5.4 新建 gradle/it-demo-isolation.gradle（含文件头块注释），迁入 tny-game-integration-test 的 integrationTest 子进程隔离配置块，spread 编码行与 compileJava/compileIntegrationJava 点名块改为按类型惰性形态（设计 D6、D11），模块文件 `wc -l` 回到八十行内；运行 `./gradlew :tny-game-integration-test:integrationTest -PincludeDocker=false` 确认非容器档用例通过（或按既有 skip 语义跳过容器用例且无失败）。
- [x] 5.5 gradle/publish.gradle 的本地发布豁免谓词由 `startsWith("publishToMavenLocal")` 同步为 `endsWith("ToMavenLocal")` 语义（设计 D7，本变更声明的唯一行为修正）：临时移除发布属性注入后 `./gradlew :tny-game-net:publishToMavenLocal` 应成功（修复前会误挂断言而失败），恢复属性后 `./gradlew :tny-game-net:publish` 在空凭据环境仍按既有 fail-fast 文案报红。
- [x] 5.6 settings.gradle 版面收尾：删除残留连续空行使其余分组块之间最多一个空行（注释 include 已在 2.3 清除），运行 `./gradlew projects` 确认模块清单与工程装配不受影响。
- [x] 5.7 按设计 D8 把系统属性与环境变量直读改为 providers 形态：gradle/tny-module.gradle 的 Created-By 属性改用 `providers.systemProperty`，gradle/integration-test.gradle 的 DOCKER_HOST 探测与 user.home 读取改用 `providers.environmentVariable` 与 `providers.systemProperty`（读取时机与回退语义不变）；运行 `./gradlew :tny-game-net:jar` 并 `unzip -p` 抽查产物 MANIFEST 的 Created-By 字段值与基线形态一致。

## 6. 终验与规格自检

- [x] 6.4 tny-game-bom 构建文件的配置阶段循环（约束面条目 findAll 排序逐个 api）迁往新脚本 gradle/bom-platform.gradle（含文件头块注释），模块文件一行引入（设计需求一"程序性行为限定容身之处"，实施中发现的漏项补登）；运行 `./gradlew :tny-game-bom:generatePomFileForMavenJavaPublication` 并比对 POM constraints 依赖清单与基线发布产物一致。
- [x] 6.5 gradle/dependency-sources.gradle 的旧式 `task downloadDependencySources` 声明改 `tasks.register` 形态；gradle/tny-module.gradle 的 `jar { manifest... }` 点名求值改 `tasks.named('jar').configure` 惰性形态（同属实施中发现的漏项补登）；运行 `./gradlew :tny-game-net:jar :tny-game-net:downloadDependencySources -q` 确认任务仍可寻址执行。

- [x] 6.1 运行 `./gradlew clean build` 全量构建，`./gradlew tasks --all`、两份 dependencies 报表、publishToMavenLocal 构件清单三项与第 1 组基线 diff，确认零差异后把对比结论写入变更目录 verification-notes.md。
- [x] 6.2 对照 gradle-build-style 八条需求逐条做扫读自检：每个 `.gradle` 文件用三十秒扫读回答"依赖什么、用什么编译、发布到哪里"三问，逐条在 verification-notes.md 记录通过结论与存疑文件清单；对存疑文件回到对应需求条文复核并修正。
- [x] 6.3 运行 `./gradlew :tny-game-net:test :tny-game-codec:test :tny-game-common-lifecycle:test` 抽样回归核心模块测试，确认通过并记入 verification-notes.md。
