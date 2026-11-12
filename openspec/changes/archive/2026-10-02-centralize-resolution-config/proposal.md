# Proposal

## Why

全量脚本配置审计（用户发起，随 declarative-it-demo-isolation 一并完成）查明：坐标/版本已有版本目录统一面、身份与成员已有根 ext 单点、插件域结构数据正由扩展面收口，唯独"工程依赖从哪些仓库解析"没有统一面，散成四处且互相重复。exclusiveContent 独占路由组（com.gradleup.nmcp 与 io.github.pdvrieze.xmlutil）在根 build.gradle 与 tny.repositories 插件逐字双写——这正是修订后 gradle-build-style 需求一"共享配置块禁止逐字复制"点名禁止的形态；镜像仓清单三份（settings.gradle 的 pluginManagement 五 URL、tny.repositories 四 URL、根内联一份），语义确有不同（插件解析与依赖解析是两个集合）但 URL 字符串重复维护；docker socket 候选路径与 Central 校验超时 20 分钟等机器环境标量烙在插件机制里，换机器调整要改脚本；tny-game-doc-gradle 的发布仓接线与 tny.publications 近乎复制且判据形态漂移（`endsWith('SNAPSHOT')` 对 `endsWith('-SNAPSHOT')`），是未来版本字符串变化时的静默错路由隐患。Gradle 为工程依赖解析提供的官方统一面是 settings.gradle 的 dependencyResolutionManagement，本变更把这一空格补上。

## What Changes

- settings.gradle 新增 `dependencyResolutionManagement` 块：全部工程依赖解析仓（exclusiveContent 独占组、mavenLocal、tencent 镜像、tnydev 私服、aliyun 镜像、mavenCentral）以唯一一份声明于此，`repositoriesMode` 设 FAIL_ON_PROJECT_REPOS——任何工程私设 repositories 即配置期报红，杜绝第二书写点复发（原拟 PREFER_SETTINGS 经任务 2.3 探针实测证伪后升级，见 design D1）。
- tny.repositories 约定插件退役（配置面整体迁往 settings，"镜像钉选陷阱"来由注释随行迁移）；根 build.gradle 与 tny.subprojects-baseline 中对它的引入行随之删除；根工程自身不再内联 repositories 块（统一受 settings 面覆盖，独占组钉 Central 的既有解析行为不变）。
- gradle.properties 新增机器环境标量键并给默认值回退：`centralValidationTimeoutMinutes=20`（tny.central 经 providers 读取，缺失取 20）与 `dockerSocketCandidates=`（逗号分隔候选 socket 路径，tny.integration-test 读取，空值回退现两路径内置默认——CI 无此键时行为不变）。仓 URL 保持 settings 字面量：它们不是逐机变化的值，property 化只增加解折成本，统一面已是 settings 单点。
- tny-game-doc-gradle 的发布仓接线判据由 `endsWith('SNAPSHOT')` 统一为与 tny.publications 同源的 `endsWith('-SNAPSHOT')` 形态并附一行来由注释（当前全部版本串下两判据选择一致，属消除静默错路由隐患的形态收敛，不改发布行为）。
- 全程零差异红线：解析结果、任务清单、构件与 POM 对基线零差异；行为面唯一敏感点"根工程解析可见面从最小集扩至统一集"由独占路由组保证 nmcp/xmlutil 仍钉 Central，验收含 `./gradlew buildEnvironment` 与依赖报表佐证。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。本变更是解析配置在构建脚本间的位置归一（settings 统一面），不改任何外部可观察行为，gradle-build-style 各需求的文字与判定标准不因之变化（其需求一禁止的"逐字复制"恰由本变更清除），故以 skip_specs 声明零规格差量。

## Impact

- **受影响文件**：settings.gradle（新增 dependencyResolutionManagement）、build.gradle（删内联 repositories 与 tny.repositories 引入行）、buildSrc/src/main/groovy/tny.subprojects-baseline.gradle（删引入行）与 tny.repositories.gradle（退役）、buildSrc/src/main/groovy/tny.central.gradle 与 tny.integration-test.gradle（标量改 providers 读取带回退）、gradle.properties（两个新键）、tny-game-doc-gradle/build.gradle（判据形态统一）。
- **消费面**：全部工程解析走同一 settings 面；pluginManagement 块不动（插件解析与依赖解析保持两个语义集合）；CI 与发布通道无感知。
- **验证手段**：四份基线（tasks --all、四模块 dependencies 报表、publishToMavenLocal 构件清单、两份 POM）逐项零差异；`./gradlew buildEnvironment` 与 `:tny-game-net:buildEnvironment` 记录解析来源；centralCheck 与 releaseCutAndTag dryRun 探针复跑；工程私设仓违例探针（临时给一模块加 repositories 应报红）。
- **下游**：不动发布坐标与 POM 结构，不触公共 API，无 BREAKING。
