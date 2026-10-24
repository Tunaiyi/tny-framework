# Proposal

## Why

gradle-build-style 能力规格（openspec/specs/gradle-build-style/spec.md）的生效方式是"触碰即改"渐进修正，并明确规定需要全仓清扫时必须另立独立变更。近期连续几个变更（迁移 git 调用到 GrGit、集中发布组号等）已经逐个触碰了核心构建脚本，但全仓仍有大量存量违例散落未清：违例最集中的是根 build.gradle（其中两个配置阶段的过程式对账块正是规格 Scenario"配置阶段写入过程式对账被判违例"点名的形态），另有三处 spread 批量赋值、四处旧式 task 声明、二十余处绕过单一事实源的硬编码版本与手写坐标、十余处注释掉的配置残留、两个超过 250 行界线的辅助脚本与两个超过 80 行界线的模块脚本。这些存量不一次清掉，此后每个触碰构建脚本的变更都要在改动区域里反复顺手修同一批形态，评审成本持续累积。因此按规格自身的立项目，现在开设这个独立的全仓清扫变更。

## What Changes

本次变更只改 `.gradle` 文件的书写形态，不改变任何外部可观察行为：任务清单与任务名、解析出的依赖图、构件内容与发布坐标全部保持原样。按规格八条需求逐类清扫以下违例：

- **程序性行为迁出配置阶段**：根 build.gradle 中"组号对账"与"基准模块零发布合同对账"两个 gradle.projectsEvaluated 过程式校验块，迁入 `gradle/` 目录下的专用对账编排脚本，根脚本只保留一行引入语句；根脚本 ext 块中 javaProjects 的三步中间变量累加派生改写为声明式形态。
- **旧式任务与点名求值**：根 build.gradle 中四处 `task <名>(type: ...)` 旧式声明改为 `tasks.register`；`compileJava.dependsOn(processResources)` 与两处 `createProject` 的点名求值改为按任务类型惰性配置或注册闭包内声明依赖；tny-game-integration-test 的编码 spread 赋值并入 gradle/integration-test.gradle 的按类型惰性配置。
- **spread 批量赋值**：根 build.gradle 两处 `[compileJava, ...]*.options*.encoding = "${encoding}"` 改写为 `tasks.withType(JavaCompile).configureEach` 与 `tasks.withType(Javadoc).configureEach` 形态。
- **版本与坐标回归单一事实源**：根 build.gradle dependencyManagement 块中约二十条携带版本字面量的 `dependency` 声明，把版本收进 `gradle/dependency.gradle` 具名表后以引用表达；tny-game-namnspace 的三条手写坐标（commons-codec、hutool-core、zero-allocation-hashing）与 tny-game-doc-gradle 的一条手写坐标（tools-template）收入具名引用表，模块文件只留 `api libs.xxx`。
- **注释掉的配置残留清除**：settings.gradle 中九条 `////include` 与 `//include` 残留行（对应 tny-game-cache 系列、asyndb、zookeeper、suite、test-lua，这些模块的源码均不在本仓库内）、根 build.gradle 一行注释掉的 ext 配置、tny-game-net-demo 两行注释掉的 mavenBom、tny-game-mongodb 一行注释掉的依赖，一律直接删除，历史形态由版本库承担。
- **Groovy 文本纪律**：全仓 `.gradle` 文件中不含插值的字符串改用单引号；把已经是字符串的属性值再包一层插值模板的写法（如 `version "${projectVersion}"`）改为直接赋引用；`gradle/tny-module.gradle` 中 `System.getProperty("java.version")` 直读改为 Gradle providers 配置模型读取；`options.encoding = "UTF-8"` 的硬编码值统一回 encoding 属性。
- **区块顺序与版面**：根 build.gradle 与 gradle/ 各脚本内的配置区块按"插件、身份、编译与 toolchain、仓库与依赖管理、依赖、任务、发布"固定顺序重排；settings.gradle 模块清单的连续空行收敛为一个。
- **长度界线回到界内**：gradle/dependency.gradle（255 行）与 gradle/publications.gradle（257 行）压回 250 行界线之内（压缩手段为合并同源声明与清理注释复述，不改变内容语义）；tny-game-integration-test/build.gradle（107 行）与 tny-bench/build.gradle（189 行）压回 80 行界线之内（把程序性逻辑移入 gradle/ 分域脚本或任务动作块）。
- **文件头职责注释补齐**：gradle/ 下缺少文件头块注释的辅助脚本（git.gradle、publish.gradle、publish-plugin.gradle、tny-module.gradle 等）按规格补写"本脚本负责什么、与其他机制的边界是什么"。

## Capabilities

### New Capabilities

无。本变更不引入新能力。

### Modified Capabilities

无。本变更不修改任何能力的需求：清扫所依据的八条需求已完整存在于 gradle-build-style 主规格中，本次是把该规格既有要求的存量落地从"触碰即改"推进到"全仓清零"，规格文本本身不变。因此本变更在 .openspec.yaml 中声明 skip_specs，不产出规格差量。

## Impact

- **受影响文件**：根 build.gradle、settings.gradle、gradle/ 目录下全部十个 .gradle 辅助脚本，以及下列模块构建文件——tny-game-namnspace、tny-game-doc-gradle、tny-game-mongodb、tny-game-net-demo、tny-game-integration-test、tny-bench，其余模块构建文件扫查后如无违例则不动。
- **公共 API 与下游**：不触及任何 Java 源码与公共接口，下游业务工程依赖的已发布 API、报文协议、构件坐标与版本号均不变，各 starter 模块不受影响，故不标注 BREAKING。
- **构建与 CI**：CI 工作流以任务名调用构建（如 `:tny-bench:jmhList`、`publish`、发布快通道任务），清扫保持全部任务名与任务语义不变；gradle/release.gradle 的 `-PdryRun` 预览通道已存在，本次不动其语义。
- **验收方式**：每个任务组完成后运行全量构建（`./gradlew clean build`）与依赖报表对账（`./gradlew dependencies` 前后抽样比对），确认任务图与解析结果不因形态改写而变化；组号对账与零发布合同两条校验迁出后必须仍然生效（用故意改坏一个模块组号的方式验证其会报红）。
