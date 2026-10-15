# Tasks

## 1. 基线与解析来源留档

- [x] 1.1 运行 `./gradlew clean build` 确认全绿，并采集五份基线存入变更目录 baseline/：`tasks --all`、`:tny-game-net :tny-game-doc :tny-game-namnspace :tny-game-expr-graaljs` 四模块 dependencies 报表、publishToMavenLocal 后 m2 构件清单、tny-game-bom 与 tny-game-net 两份 POM（沿用 declarative 变更终态，若其归档目录基线可直接复制则注明链式引用）。
- [x] 1.2 运行 `./gradlew buildEnvironment :tny-game-net:buildEnvironment --console=plain` 将插件与工程解析来源输出存 baseline/build-environment.txt。

## 2. settings 统一解析面（设计 D1、D2、D3）

- [x] 2.1 settings.gradle 新增 dependencyResolutionManagement 块：repositoriesMode 终态设 FAIL_ON_PROJECT_REPOS（执行中先按 PREFER_SETTINGS 落地、经 2.3 探针证伪升级，见 design D1），exclusiveContent 独占组与 mavenLocal、三镜像、mavenCentral 按 tny.repositories 现顺序原样迁入，"镜像钉选陷阱"来由注释随迁块头；运行 `./gradlew projects -q` 确认评估通过。
- [x] 2.2 删除 tny.repositories.gradle、根 build.gradle 的 repositories 内联块与 tny.repositories 引入行、tny.subprojects-baseline 的引入行；两文件头注释中"接入仓库路由共享块/根工程内联最小集"表述同步改为"工程依赖解析仓由 settings 单点"；运行 `./gradlew projects -q` 通过后跑 `./gradlew clean build` 全绿，并以 1.1 基线做 tasks/deps/m2/POM 四比对加 1.2 buildEnvironment 比对，全零差异为完成判据。
- [x] 2.3 工程私设仓违例探针（模式经 2.3 实测修正为 FAIL_ON_PROJECT_REPOS，见 design D1）：临时给 tny-game-net/build.gradle 追加 `repositories { mavenCentral() }` 跑 `./gradlew :tny-game-net:dependencies --configuration compileClasspath -q`，应配置期报红；删除临时代码恢复绿，结果记 verification-notes.md。

## 3. 标量属性面（设计 D4）

- [x] 3.1 gradle.properties 追加 `centralValidationTimeoutMinutes=20` 与 `dockerSocketCandidates=`（含一行注释说明空值回退内置默认）；tny.central 的 validationTimeout 改 `providers.gradleProperty('centralValidationTimeoutMinutes').map{...}.getOrElse(20)` 分钟，tny.integration-test 的 socket 候选改读取 `dockerSocketCandidates` 逗号切分 trim、空回退 orbstack/docker 两默认路径；验证：正常键值跑 `./gradlew centralCheck --console=plain` 仍按分支形态报红、`./gradlew :tny-game-integration-test:integrationTest --rerun` 绿；再临时清空两键（-PcentralValidationTimeoutMinutes= -PdockerSocketCandidates= 模拟缺失）复跑同二题仍绿/仍红如语义，结果记 verification-notes.md。

## 4. doc-gradle 判据统一与终验（设计 D5、D6）

- [x] 4.1 tny-game-doc-gradle/build.gradle 发布仓判据改 `endsWith('-SNAPSHOT')` 并附一行同源防漂注释；跑 `./gradlew :tny-game-doc-gradle:publishToMavenLocal --rerun -q` 后其 m2 POM 与 1.1 基线逐字节一致为完成判据。
- [x] 4.2 终验复跑：clean build 全绿；四基线 + buildEnvironment 五比对零差异；centralCheck、`releaseCutAndTag -PdryRun -PreleaseVersion=9.9.9`、checkPublishPrerequisites 实断言、空凭据双向探针逐项通过；全部结论汇总记 verification-notes.md。
