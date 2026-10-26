# Design

## Context

审计事实与动机见 proposal.md - Why。现状四个 repositories 书写点：`settings.gradle:3-11`（pluginManagement，5 URL）、根 `build.gradle:42-53`（内联最小集：exclusiveContent + mavenCentral）、`tny.repositories.gradle`（子工程全集：exclusiveContent + mavenLocal + 三镜像 + central）、`tny-game-doc-gradle/build.gradle:22`（publishing 的发布仓，非解析仓，与本次归一面正交）。exclusiveContent 组在根与 tny.repositories 逐字相同——"形同义异"辩护在复查中撤销：组名与过滤器逐字复制即违例。

爆炸半径核查（与前两变更同格式记录）：codegraph 不索引构建脚本，无适用符号；真实消费面为一切触发解析的通道——依赖解析（四报表覆盖代表面）、buildSrc 与 pluginManagement（独立不受影响）、发布上传（publishing.repositories 在 publications 内，属"发布到哪里"不属"从哪里解析"，本变更不动）、nmcp 聚合的 :nmcpTasks 解析（依赖根工程可见仓，风险主点见 D2）。

## Goals / Non-Goals

**Goals:**

- "从哪里解析"收敛为 settings 唯一书写点，FAIL_ON_PROJECT_REPOS 让第二书写点在配置期即不可能出现。
- 机器环境标量（超时、socket 候选）进 gradle.properties 属性面，插件读键带回退，换机调参不改脚本。
- 零差异红线全项保持（新增 buildEnvironment 佐证面）。

**Non-Goals:**

- 不动 pluginManagement 块（插件解析集合语义不同：jcenter/spring-plugin/gradle-plugin 镜像专供插件）。
- 不动 publishing.repositories 的仓 URL 与凭据接线（"发布到哪里"面，D6 只统一其判据形态）。
- 不做发布仓接线的结构统一（doc-gradle 与 publications 合并成共享块——两线 POM 组件形态不同、字段近重复非逐字复制，收编属另一主题）。
- 不仓 URL property 化（逐机不变值，settings 字面量即单点；见 D4）。

## Decisions

**D1 统一面选 settings.gradle 的 dependencyResolutionManagement，模式 FAIL_ON_PROJECT_REPOS。** exclusiveContent 独占组、mavenLocal、三镜像、mavenCentral 按现 tny.repositories 顺序原样迁入（顺序即解析优先级，保持等值）；`repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS`。依据：这是 Gradle 官方为"工程依赖解析仓"提供的唯一集中声明设施（机制自带，非自造 DSL，与版本目录同族）；FAIL_ON_PROJECT_REPOS 的报红语义把"禁止第二书写点"从评审纪律升级为机器强制。实施修正（任务 2.3 探针实测）：原选 PREFER_SETTINGS 被证伪——该模式对工程私设仓仅忽略加告警，不报红，达不到 D1 的机器强制意图，遂升级 FAIL_ON_PROJECT_REPOS；被否决备选：PREFER_PROJECT——否决理由：项目级块仍合法，双写隐患只靠人审守；PREFER_SETTINGS 保留——否决理由：静默忽略使违例可长存，探针实证后不采；tny.repositories 保留为"settings 面的转写插件"——否决理由：多一层间接且逐字复制换了形态没消失。

**D2 根工程内联 repositories 块退役，等价性由独占组钉死。** 迁移后根工程解析可见面=统一全集。风险评估：根工程自身触发解析的只有 nmcp 聚合的 :nmcpTasks 配置（com.gradleup.nmcp 与 xmlutil 组）——exclusiveContent 对该两组仍直连 Central（镜像不劫持），其余 URL 对根无消费方；`mavenLocal()` 进根可见面的影响：根无本地解析需求，行为等值。验收以 buildEnvironment 与全量解析报表证实。依据：根脚本终态是"配置说明书目录页"（spec 需求一根脚本形态），仓库块属机制配置应离页。

**D3 tny.repositories 退役；"镜像钉选陷阱"来由注释随迁 settings。** 该插件唯一内容是配置数据非机制，保留即"空壳间接层"；tencent 镜像缺 xmlutil jar 的两次首探实测注释整体搬到 settings 块头（需求六：注释解释来由，随数据走）。tny.subprojects-baseline 与根脚本的引入行删除，头部"接入仓库路由共享块"表述同步改"解析仓由 settings 单点"。

**D4 标量属性面：两个新键，providers + getOrElse 双保险。** `centralValidationTimeoutMinutes=20` 由 tny.central 经 `providers.gradleProperty(...).map{Integer}.getOrElse(20)` 读取；`dockerSocketCandidates=`（逗号分隔）由 tny.integration-test 读取，空/缺失回退内置 orbstack/docker 两默认路径。理由：实测 settings/providers 对嵌套 build（buildSrc）的键不可见，插件读取一律带回退，CI 无键环境行为不变。仓 URL 不入 properties：值不随机器变，settings 字面量已是单点，property 化徒增解折层。依据：需求三"两处事实源"精神在环境标量上的类比推广；P13（回退路径可用无键构建验证）。

**D5 doc-gradle 发布仓判据统一为 `endsWith('-SNAPSHOT')`。** 与 tny.publications 同源形态。当前派生值只有 `X.Y.x-SNAPSHOT` 与裸号两形态，两判据选择一致，改动零行为；不统一则未来任何以 SNAPSHOT 结尾的非 `-SNAPSHOT` 版本形态（如 -RC-SNAPSHOT 类）会在两线分叉路由。附注一行来由（判据与 publications 同源，防形态漂移）。

**D6 验收基线沿用四件套并新增解析来源面。** tasks --all、四模块 dependencies 报表、m2 清单、两份 POM 零差异之外，新增：`./gradlew buildEnvironment :tny-game-net:buildEnvironment` 前后留档对照（证明插件与工程解析来源面未漂）；工程私设仓违例探针（FAIL_ON_PROJECT_REPOS 下临时给 tny-game-net 加一行 `repositories { mavenCentral() }` 应配置期报红）；centralCheck、releaseCutAndTag -PdryRun、checkPublishPrerequisites、空凭据双向探针复跑。

## Risks / Trade-offs

【FAIL_ON_PROJECT_REPOS 下 buildSrc 与主构建解析隔离】→ buildSrc 有独立 settings，dependencyResolutionManagement 不约束它；buildSrc 编译期解析仍走默认 portal——现状即如此（未声明 repositories），本变更不触碰，风险不变。
- 【根工程解析面扩大引入镜像优先劫持】→ exclusiveContent 的 filter 精确钉组：nmcp/xmlutil 两组仅 Central 可见，镜像对它们不可达；其余组根无消费。验证：buildEnvironment 全量输出比对 + 全量构建。
- 【dockerSocketCandidates 键值格式退化（用户写空格/分号）】→ 解析按逗号切并 trim；空段忽略；内置默认兜底保证最坏=现状。
- 【settings 块头注释变长】→ 来由注释随迁入是需求六的明确归属；行数界不适用 settings（需求七未点名 settings 界），无违例。

## Migration Plan

三批次，各自可独立回退：批次一 settings 统一面落地 + tny.repositories 退役 + 根/基线引入行删（核心迁移，全量四基线 + buildEnvironment 验收）；批次二标量属性面（central/integration-test 两插件 + gradle.properties，无键回退探针）；批次三 doc-gradle 判据统一（发布仓形态探针：doc-gradle publishToMavenLocal 的 POM 与 m2 清单零差异）。终验复跑全部行为探针并记 verification-notes。

## Open Questions

无。
