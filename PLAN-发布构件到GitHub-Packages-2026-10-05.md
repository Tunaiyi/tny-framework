# TnyFramework 增设 GitHub Packages Maven 镜像发布通道：最终实施方案

## 一、背景与现状

本仓库的发布链路由五个 buildSrc 约定插件分工完成：`tny.publish` 承担双层拦截（对命中共享仓谓词的发布任务先做四项 Nexus 属性的非空断言，再挂载 `checkPublishPrerequisites` 任务，后者执行全量 check 与五项一致性核对）；`tny.java-module` 负责 java 线模块的构件与 mavenJava 发布接线；`tny.publications` 负责 POM 元数据、GPG 签名与两个发布目标仓的声明（按版本后缀三目路由的内网 Nexus 仓，以及按"快照版本且本机存在 Central 凭据属性"守卫声明的 centralSnapshots 仓）；`tny.central` 经 nmcp 插件承担正式版对 Maven Central 的聚合上传；`tny.release` 在根工程提供 releaseCut、releaseTag、releaseMergeBack 三个快通道任务。常规发布按 `docs/release-process.md` 流程六执行：切出 `<主>.<次>.<补丁>.release` 分支、推送附注标签 `v<版本>`、执行 `./gradlew publish` 使制品进入内网 Nexus、再执行 `./gradlew publishAggregationToCentralPortal` 完成 Central 聚合上传、最后合回开发线。GitHub Actions 侧由 `.github/workflows/publish.yml` 在 release.published 事件或人工 workflow_dispatch 触发，检出发布分支后分两步独立执行上述两个发布命令；该文件的 permissions 块现值只有 contents: read 一项。此外，BOM 模块 `tny-game-bom` 在自身 plugins 块接线发布，插件模块 `tny-game-doc-gradle` 不经 `tny.publications`，在模块文件第 20 至 32 行自声明一份 Nexus 仓，其构件不签名、POM 不含元数据组、组号为例外值 com.tny.game。目前不存在任何 GitHub Packages 通道。

## 二、GitHub Packages 注册表关键事实

以下事实按"结论、出处、确定程度"逐条列出。出处以官方文档页面名称与 URL 标注；凡本会话无法核实的，明确注明。

1. **认证模型只认 classic PAT 与 GITHUB_TOKEN。** fine-grained PAT 截至 2026 年 10 月 5 日仍不能访问 GitHub Packages，官方《Managing your personal access tokens》页把"Using fine-grained personal access token to access Packages"列在能力缺口清单内且注明缺口"并非永久"；官方《About permissions for GitHub Packages》页另有原文注记"GitHub Packages only supports authentication using a personal access token (classic)"。确定。
2. **Actions 内发布只需 packages: write 权限，且未点名的权限一律降为 none。** 官方《Workflow syntax for GitHub Actions》规定：一旦在 permissions 中点名任何权限，未被点名的权限全部设为 none；packages 权限的合法取值为 read、write、none 三种，只有 write 允许上传并发布包。官方《Publish Java packages with Gradle》示例即 contents: read 加 packages: write 两行并存。确定。
3. **内置 GITHUB_TOKEN 只能发布与运行工作流的那个仓库相关联的包。** 官方《About permissions for GitHub Packages》Access tokens 小节原文如此。这意味着选用内置令牌时，包必须挂在 Tunaiyi/tny-framework 本体。确定。
4. **公开包同样必须携带凭据才能下载，不存在匿名解析。** 官方《About permissions for GitHub Packages》写明多数注册表（Maven 在内）下载一律需认证，只有容器注册表允许匿名拉取；官方《Working with the Apache Maven registry》亦写明发布、安装、删除私有、内部及公开包都需要访问令牌。确定。
5. **Maven 与 Gradle 注册表只支持仓库作用域权限，包的可见性与权限完全继承所在仓库。** 官方《About permissions for GitHub Packages》把 Apache Maven registry 与 Gradle registry 列入"只支持仓库作用域权限"的清单；包设置页的 granular 权限与 GitHub Actions access 授权对 Maven 包无效；任何对仓库有 write 权限的人都能向该仓库发布包。确定。
6. **重复上传已存在的文件会被拒绝：HTTP 409，响应正文写着 overwriting is disabled。** 该规则从未见于官方文档，但多个真实项目的失败记录高度一致（ossuminc/sbt-ossuminc 的 NOTEBOOK.md、manosbatsis/corda5-testutils 的构建注释、apereo 官方博客、kmgowda/SBK issue #356），受约束的构件包括 `.asc` 签名文件、`.module` Gradle 模块元数据与校验和附属文件。实证确定级，官方条文缺失。
7. **Gradle 的 maven-publish 上传 `-SNAPSHOT` 构件时使用不带时间戳的原始文件名。** 官方 Gradle 文档没有逐字陈述此行为；证据形态是两方合围的高置信推定：本仓归档基线抓样目录（`openspec/changes/archive/2026-10-04-adopt-gradle-8-14-baseline/baseline/before/`）中的实际文件名 `tny-game-net-5.7.x-SNAPSHOT.module` 等，加上 kmgowda/SBK issue #356 记录的"重复部署同名文件触发 409"机制。这与 Maven 部署插件每次快照生成新时间戳文件名的行为不同。高置信推定（非官方明文）。
8. **maven-metadata.xml 由谁维护、同一快照坐标保留多少历史构建、旧构件何时回收，官方完全无记载。** 存疑，不得作为设计前提。
9. **URL 的 owner 段必须全小写。** 出处是官方《Working with the Gradle registry》页原文（即使 GitHub 账户或组织名含大写字母也必须使用小写）；构件名只允许小写字母、数字与连字符、含大写返回 422 的条文出自另一页《Working with the Apache Maven registry》——两条规则出处不同，引用时不可混页。确定。
10. **含点号的 artifactId 是否被 Maven 注册表接受没有实测记录。** 官方条文只列举小写字母、数字与连字符，点号不在列举之内；Gradle 插件 marker 构件的 artifactId 由机制固定生成为含点号形态（如 `com.tny.game.gradle.plugin`），422 的官方明示触发条件只有大写字母。存疑。这是插件线构件不纳入首批镜像的直接依据之一。
11. **删除版本的权限与限制。** 官方《Deleting and restoring a package》：删除仓库作用域包的单个版本要求对该仓库有 admin 权限；公共包的任一版本下载量超过 5000 次即无法删除；删除后 30 天内可恢复。GraphQL 的 deletePackageVersion 需要 classic PAT 同时带 read:packages、delete:packages、repo 三个 scope，且官方限定"适用于某些注册表"，Maven 注册表是否在内无记载。删除后同号能否立即重发，官方从未正面承诺。存疑。
12. **列举包的 REST 端点没有仓库路径形态。** 官方 Packages API 只有 `/orgs/{org}/packages`、`/user/packages`、`/users/{username}/packages` 三类；`repos/{owner}/{repo}/packages` 不存在，按此操作将得到 404。个人账户的包用 `GET https://api.github.com/users/Tunaiyi/packages?package_type=maven` 列举。确定。
13. **计费：公共仓库的包存储与流量免费，入站流量一律免费，Actions 内以 GITHUB_TOKEN 下载不计费。** 官方《Billing for GitHub Packages》。确定。本仓经匿名 REST 核实为公开仓库、默认分支 main（治理调查核实；本会话 gh CLI 未登录，组织级 Packages 策略与仓库 Actions 令牌权限基线无法核实，移交用户确认）。
14. **classic PAT 的 scope 矩阵。** 官方《About permissions for GitHub Packages》：读取包需 read:packages，上传需 write:packages，删除需 delete:packages（且需仓库 admin）；对只支持仓库作用域权限的注册表（Maven 在内），因包继承仓库权限，repo scope 亦被要求——该表述出自私有仓库权限继承语境，公开仓库是否必需 repo scope 官方未正面覆盖，勾上无害，属保守处置。scope 矩阵本身确定，公开仓库下 repo 的必要性存疑。

## 三、推荐方案总览

**骨架选择。** 两份方案验证得分同为七分，按"blockers 更少者为主骨架"取体系化方案（其 blockers 一项，多于其对照方案者两项）为主体形态：GitHub Packages 通道由新建的独立约定插件 `tny.github-packages` 承载，java 线装配线与 BOM 各以一行引入接入。体系化方案的唯一 blocker（插件线 marker 构件的 artifactId 含点号、能否被 Maven 注册表接受未经验证，而该方案默认把插件线纳入镜像）按其自述的替代出口解决：把根构建脚本对 gradleProjects 装配线的引入行撤出首批，插件线模块 `tny-game-doc-gradle` 暂不纳入——这一处置与最小改动方案的第三项决定（插件线不纳入）殊途同归，理由见第九节决策点一。

**两处路线修正（嫁接另一方案并说明改选理由）。** 其一，CI 启用形态弃用体系化方案的"聚合 publish 扇出自动启用"，改采最小改动方案的"独立第三步骤、逐仓聚合任务名"：体系化方案自己的验证结论指出，镜像任务与 Nexus 任务同处一条 `./gradlew publish` 命令时，Gradle 默认首个失败即中止，镜像半途失败会连带撤停同一次命令中尚未执行的 Nexus 上传，这与在途变更 central-publish-tnydev-group 的 central-publishing 增量规格"双仓并行发布相互独立"条文的通道隔离语义直接冲突；独立步骤既满足该隔离原则，又不会把已成功的 Nexus 正式仓重推一遍。其二，快照策略弃用体系化方案的"快照同样镜像"，改采最小改动方案的"仅裸号正式版"：体系化方案依据的"每次快照部署生成新时间戳文件名、彼此不冲突"是 Maven 部署插件的语义，而本仓发布端是 Gradle，maven-publish 上传快照构件使用不带时间戳的同名文件（见第二节事实 7），同一快照版本二次发布必然触发 409（见事实 6）；该路线前提不成立，故改选守卫排除快照的路线。

**吸收的修正。** 最小改动方案的两项 blocker 全部修复：新增仓的守卫条件同时断言 githubPackagesToken 与 githubPackagesUsername 两个属性（凭据闭包在配置期求值，缺任一属性都必须让仓整体不声明，否则半配置机器上每次 Gradle 调用在配置期报红），并据此改写失败时机的表述；验证命令不再在开发线上观测本通道任务面，改为"开发线断言任务不存在（设计使然）加临时发布形态分支演练任务存在"两段式。两份方案的全部非空 corrections（重发阶梯命令改正、校验和描述改正、门禁推演自相矛盾句删改、消费方片段空凭据回退改正、publish.yml 头注与作业名同步、"同构"表述缩窄为"守卫手法同构"、大小写规则出处分页、owner 小写的完整句表述、CI 链未实证改为显式前置条件、组织基线核对落入清单、fine-grained 结论附核实日期、快照文件名结论标注证据形态）均已并入下文各节正文。

**总改动面。** 新建一个约定插件文件；根 `build.gradle` 的 javaProjects 装配线加一行引入；`tny-game-bom/build.gradle` plugins 块加一行；`.github/workflows/publish.yml` 增开 packages: write 权限、改写头注与作业名、追加第三个发布步骤；`docs/release-process.md` 三处联动并新增镜像通道一节；`README.md` 消费约定补第四条并同步计数词；`openspec/config.yaml` 上下文发布句更新。`tny.publish.gradle`、`tny.publications.gradle`、`tny.central.gradle` 与门禁谓词零改动。需要用户拍板的五项决定（快照不镜像、包挂本仓库、插件线暂不纳入、CI 用内置 GITHUB_TOKEN、openspec 立项时序）及推荐值见第九节。

## 四、逐项改动

### 4.1 新建 `buildSrc/src/main/groovy/tny.github-packages.gradle`

新文件，整体内容如下（约四十行，满足 gradle-build-style 的插件文件头注释要求与长度界线）：

```groovy
// GitHub Packages 镜像通道约定插件（capability central-publishing 的第三目的地声明点；
// 规格文本见 openspec change add-github-packages-channel 的 central-publishing 增量）。
// 本插件负责：为应用它的工程在 publishing.repositories 增加名为 githubPackages 的仓库声明，
// 把同一套发布构件镜像分发到本仓库的 GitHub Packages Apache Maven 注册表。
// 边界：发布门禁（凭据属性断言与五重一致性校验）在 tny.publish，POM 元数据与签名在 tny.publications，
// Central 聚合上传在 tny.central，发布快通道 git 编排在 tny.release；本插件只增仓库声明，不触碰门禁。
// 通道语义：仅裸号正式版的尽力镜像——GitHub Packages 对已存在的构件文件拒绝覆盖上传
// （重复 PUT 同名文件返回 HTTP 409，多个真实项目的一致实测记录），而 Gradle 的 maven-publish
// 上传 -SNAPSHOT 构件使用不带时间戳的文件名，同一快照版本二次发布必然重复 PUT 同名文件；
// 快照历史保留与 maven-metadata.xml 维护语义官方无记载，不作为可用前提。
// 运维预案与消费方接入说明成文于 docs/release-process.md 的 GitHub Packages 镜像通道一节。
plugins {
    id 'maven-publish'
}

publishing {
    repositories {
        // 守卫条件为三条子句的合取，任何一条不满足即整体不声明本仓：
        // 其一，版本不是快照形态（来由见文件头通道语义段）。
        // 其二与其三，两个凭据属性同时在位。凭据闭包里的 providers.gradleProperty(...).get()
        // 在配置阶段（maven-publish 仓库容器添加 maven 声明之时）即被求值，属性缺失会在那一刻
        // 抛 MissingValueException 而使该机器全部 Gradle 调用报红；因此守卫必须同时断言两个
        // 属性，不给"只配了口令没配用户名"的半配置状态留窗口。缺凭据的机器不声明本仓，
        // 本地裸 publish 的行为与本变更前逐任务一致（守卫手法与 tny.publications 的
        // centralSnapshots 先例同源；覆盖面不同——先例只收快照，本仓只收正式版）。
        if (!project.version.toString().endsWith('-SNAPSHOT')
                && project.hasProperty('githubPackagesToken')
                && project.hasProperty('githubPackagesUsername')) {
            maven {
                // 必须显式命名：tny.publications 的首个 Nexus 仓未命名，其默认名派生的任务
                // publishMavenJavaPublicationToMavenRepository 已被硬编码写进 tny.publish 的
                // 门禁谓词，第二个未命名仓会与之冲突。本仓名派生的发布任务按名字规则自动命中
                // 门禁谓词的通配条件，继承双层拦截，本插件不豁免。
                name = 'githubPackages'
                // URL 的 owner 段按官方《Working with the Gradle registry》文档要求取全小写
                // （账户名含大写亦须折小写，否则 422）；与 POM scm 段的页面地址
                // https://github.com/Tunaiyi/tny-framework 的大小写形态不同属有意为之。
                url = 'https://maven.pkg.github.com/tunaiyi/tny-framework'
                credentials {
                    // 属性键名不得含点号：Gradle 环境桥 ORG_GRADLE_PROJECT_* 拒绝含点属性名
                    // （实测教训成文于 tny.publications 第 96 至 97 行签名段注记）。
                    username = providers.gradleProperty('githubPackagesUsername').get()
                    password = providers.gradleProperty('githubPackagesToken').get()
                }
            }
        }
    }
}
```

### 4.2 `build.gradle`（根构建脚本）

位置：第 58 至 65 行的 `configure(javaProjects)` 块内、`apply plugin: 'tny.publications'`（第 64 行）之后追加一行。改动后该块全文：

```groovy
configure(javaProjects) {
    apply plugin: 'com.gradleup.nmcp'
    apply plugin: 'tny.publish'
    apply plugin: 'tny.compile-baseline'
    apply plugin: 'tny.java-module'
    apply plugin: 'tny.integration-test'
    apply plugin: 'tny.publications'
    apply plugin: 'tny.github-packages'
}
```

说明三点：其一，不向 `configure(gradleProjects)` 块加引入行——这是唯一 blocker 的处置（marker 构件含点号 artifactId 未经验证，且插件线构件无签名、POM 无元数据，面貌与主通道不一致），日后裁决纳入时只需在 gradleProjects 块补同样的一行，这正是独立插件形态的扩展性所在。其二，放在 `tny.publications` 之后引入，保证求值该守卫时版本已由 `tny.git` 派生就位（先例：`tny.publications` 自身的守卫也在同序位读 version）。其三，根脚本由现值 65 行增至 66 行，仍在 eighty 行界线之内。

### 4.3 `tny-game-bom/build.gradle`

位置：plugins 块内 `id 'tny.publications'`（第 6 行）之后新增一行：

```groovy
    id 'tny.github-packages'
```

理由：BOM 因 `-bom` 后缀不在根 javaProjects 集合内，其发布接线一向由模块文件 plugins 块自报（`tny.publications` 即如此接入），镜像通道按同一形态加一行。BOM 必须纳入：下游 import 平台约束依赖该 POM，镜像缺它则纯 GitHub 生态的消费者不可用。

### 4.4 `.github/workflows/publish.yml`

**改动一（第 20 至 21 行）**，permissions 块改为：

```yaml
permissions:
  contents: read
  packages: write
```

`contents: read` 必须原样保留：官方《Workflow syntax for GitHub Actions》规定点名任何权限后未点名的权限一律降为 none，删掉它会令 actions/checkout 失败。

**改动二（第 3 至 5 行头注与第 25 行作业名同步改写，避免留下过期陈述）**。头注改为：

```yaml
# Central 发布工作流（openspec capability central-publishing；design D3/D4）：三通道分步独立执行——
# 先内网 Nexus（五重门禁），再 Central 聚合上传（centralCheck 前置），最后 GitHub Packages 镜像
# （逐仓聚合任务，五重门禁在任务内照常先行）。
# 一个通道失败不掩盖、不撤回另一个：三个 run 步骤各自独立报错，已成功通道的产物不回退；
# 镜像通道的同号重发处置预案见 docs/release-process.md 的 GitHub Packages 镜像通道一节。
```

作业名行改为：

```yaml
    name: Publish release to Nexus, Maven Central and GitHub Packages
```

**改动三（原第 59 至 66 行 Central 步骤之后、文件末尾追加新步骤）**：

```yaml
      # GitHub Packages 镜像通道（第三个分发目标，包归属本仓库：内置 GITHUB_TOKEN 按官方
      # 《About permissions for GitHub Packages》只能发布与运行本工作流的仓库相关联的包，
      # 并依赖上方 packages: write 权限）。选用逐仓聚合任务名而非复用聚合 publish：
      # 已成功的 Nexus 与 Central 通道不因本步骤被重推，镜像失败也不波及二者（通道隔离）。
      # tny.github-packages 的守卫要求裸号版本且两个凭据属性在位才声明本仓；两个属性经
      # Gradle 环境桥注入，键名不含点号。NEXUS_USERNAME 与 NEXUS_PASSWORD 必须一并注入：
      # tny.publish 的属性断言不区分目标仓库，命中谓词的发布任务在四项 Nexus 属性任一为空时
      # fail-fast，即便本步骤只向 GitHub Packages 上传。SIGNING_* 三件与上方两个步骤同源：
      # 发布分支形态必签，.asc 签名产物是本发布任务的依赖构件。
      # env 注入不进命令行与日志（runner 对 secrets 脱敏），与上方步骤既有注记同一手法。
      - name: Publish to GitHub Packages
        env:
          SIGNING_KEY: ${{ secrets.SIGNING_KEY }}
          SIGNING_KEY_ID: ${{ secrets.SIGNING_KEY_ID }}
          SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
          ORG_GRADLE_PROJECT_NEXUS_USERNAME: ${{ secrets.NEXUS_USERNAME }}
          ORG_GRADLE_PROJECT_NEXUS_PASSWORD: ${{ secrets.NEXUS_PASSWORD }}
          ORG_GRADLE_PROJECT_githubPackagesUsername: ${{ github.actor }}
          ORG_GRADLE_PROJECT_githubPackagesToken: ${{ secrets.GITHUB_TOKEN }}
        run: ./gradlew publishAllPublicationsToGithubPackagesRepository
```

任务名大小写说明：仓名 `githubPackages` 按 maven-publish 的首字母大写规则派生为 `ToGithubPackagesRepository` 后缀。零新增 secret：`secrets.GITHUB_TOKEN` 是内置令牌，`${{ github.actor }}` 是上下文变量。

### 4.5 `docs/release-process.md`（三处联动加一个新节）

**其一，流程六第 4 步（现文第 105 至 106 行）句末补一句**：

> 同一命令在发布分支形态、且本机已按「GitHub Packages 镜像通道」一节配置两个镜像凭据属性时，还会扇出镜像至 GitHub Packages（仅裸号正式版）；GitHub Actions 上该镜像由工作流第三步骤独立执行，不经此扇出。通道清单见下文各节。

**其二，"制品库运维前置条件"一节（现文第 179 至 184 行）追加第 3 条**：

> 3. GitHub Packages 镜像通道对同一版本的任何已上传文件拒绝覆盖（重复 PUT 返回 HTTP 409，响应正文为 overwriting is disabled），进入该通道的正式版发布因此是一次成型动作；失败处置按「GitHub Packages 镜像通道」一节的阶梯执行，不得以重跑全量发布作为默认补救。

**其三，在"Central 发布（正式版第二通道）"一节之后、"快速通道（Gradle 任务）"一节之前，新增以下整节**：

```markdown
## GitHub Packages 镜像通道

自 add-github-packages-channel 变更起，裸号正式版构件在镜像凭据在位时同步分发到
https://maven.pkg.github.com/tunaiyi/tny-framework（URL 的 owner 段按官方 Gradle 注册表
文档要求全小写）。触发形态有二：GitHub Actions 的 publish.yml 第三个步骤在每次发布时独立
执行逐仓聚合任务；本机在发布分支形态执行 `./gradlew publish` 时按守卫条件自动扇出。
快照版本不经本通道（Gradle 的 maven-publish 上传快照构件使用不带时间戳的同名文件，
重复发布会触发 409；守卫按版本后缀从根上排除）。覆盖范围：java 线模块与 BOM；
插件模块 tny-game-doc-gradle 暂不纳入（其 marker 构件的 artifactId 含点号，Maven 注册表
是否接受未经实测，且其构件无签名、POM 无元数据）。

镜像不减免任何门禁：五项一致性校验与 Nexus 属性断言照常先行，缺 `vN.M.K` 远端附注标签时
镜像任务同样被拒绝，本通道不构成发布纪律的旁路。构件面貌：java 线模块为主 jar、sources jar、
javadoc jar、POM、`.module` 元数据与逐构件 `.asc`（正式版必签，签名产物随构件分发）；
BOM 为 POM 加 `.module` 加 `.asc`。校验和方面：Gradle 对每个构件生成 md5、sha1、sha256、
sha512 四种校验和并全部上传，GitHub Packages 不收 SHA-512 校验和时 Gradle 仅警告不失败；
本仓 `gradle.properties` 第 26 行的 insecure checksums 全局属性作用于消费方解析时的
算法选择放宽，与本仓上传形态无因果关系，本通道无需新增任何校验和配置。

**消费方接入**：GitHub Packages 对公开包同样要求凭据下载，不存在匿名解析；免凭据的公开
分发渠道仍是 Maven Central。fine-grained PAT 截至 2026-10-05 仍不能访问 Packages，必须
创建 personal access tokens (classic) 并勾选 read:packages 与 repo 两个 scope。Gradle 下游
在其工程 settings.gradle 的 dependencyResolutionManagement 唯一声明点内追加：

    maven {
        url = 'https://maven.pkg.github.com/tunaiyi/tny-framework'
        credentials {
            username = providers.gradleProperty('gprUser').get()
            password = providers.gradleProperty('gprToken').get()
        }
    }

（gprUser 与 gprToken 写在用户级 gradle.properties，或由 CI 经 ORG_GRADLE_PROJECT_* 注入；
键名不得含点号。两个属性任一缺失时 Gradle 在配置期即报缺属性、错误可读，不会静默地
带着空凭据到解析期才收 401。）Maven 下游在 pom.xml 的 repositories 条目声明同一 URL，
并在 ~/.m2/settings.xml 配置同 id 的 server 凭据；本通道不收快照，故 release 坐标的
仓库条目无需 snapshots 开关。

**同号重发处置阶梯**（镜像半途失败、已上传文件不可覆盖时按序选择）：
1. 先查存证再决策（只读）：Packages 页面查看 Recent Versions，或以
   `gh api users/Tunaiyi/packages?package_type=maven` 列举本账户的包，确定受影响构件集合
   （注意：Packages REST 没有 repos 路径的列举端点，勿用）。
2. 首选提高补丁号重发：走 releaseCut 至 releaseMergeBack 的正常全流程，与"发布容器与标签
   一经创建不可移动"的既有纪律完全一致，不依赖任何注册表未承诺的行为。
3. 必须保留同号时：先由持有仓库 admin 权限者在包页面删除受影响模块的已发布版本（公共包
   任一版本下载量超 5000 次即无法删除；官方从未承诺删除后同号可立即重发，采用本步前
   必须以一次性测试版本实测），再重跑镜像发布。
4. 仅部分模块已镜像时，可对未上传的模块逐个补跑
   `./gradlew :模块名:publishAllPublicationsToGithubPackagesRepository`，接受镜像短暂不全。
   两点必须言明：该任务命中共享仓门禁谓词，会连带执行 checkPublishPrerequisites
   （含全量 check 测试与发布分支上的标签存证核对），并非轻量操作；未配置镜像凭据的机器上
   该任务根本不存在，Gradle 直接报 Cannot locate tasks，补跑仅适用于已配置镜像凭据的机器。
   无待补构件时该命令不产生任何外发。

**本机首次配置**：创建 personal access tokens (classic)，发布需勾选 write:packages 与 repo
两个 scope（repo 要求出自官方"仓库作用域注册表的包继承仓库权限"的说明；公开仓库情形官方
未正面覆盖是否必需，一并勾选属保守），删除版本预案另加 read:packages 与 delete:packages。
把下列两行写入用户级 `~/.gradle/gradle.properties`：

    githubPackagesUsername=<你的 GitHub 登录名>
    githubPackagesToken=<classic PAT>

两个属性必须成对在位，镜像仓才会被声明（守卫同时断言两者，理由见本仓
tny.github-packages 插件注释）；只配其一的机器与未配的机器行为同改动前逐任务一致。
既有门禁要求的 NEXUS_USERNAME 与 NEXUS_PASSWORD 本机发布流程本来就必须配置，无需新增。
凭据永不写入仓库内任何文件。
```

### 4.6 `README.md`

第 695 行的引导句"下游消费请遵守三条约定"改为"下游消费请遵守四条约定"（计数词必须同步，否则读者按字面只找三条），并在第 699 行第 3 条之后追加：

> 4. **GitHub Packages 镜像坐标与内网仓同版本等价，但下载必须携带凭据。** 镜像地址 `https://maven.pkg.github.com/tunaiyi/tny-framework` 仅收录正式版本；GitHub Packages 对公开包同样要求凭据下载（fine-grained PAT 不可用，需 classic PAT 勾选 read:packages 与 repo scope），免凭据匿名解析只适用于 Maven Central。接入配置详见 [docs/release-process.md](docs/release-process.md) 的 GitHub Packages 镜像通道一节。

### 4.7 `openspec/config.yaml`

第 34 行的发布描述改为：

> 发布：maven-publish 推送到 Nexus 与 Maven Central，正式版本按凭据在位镜像到 GitHub Packages（tny.publish、tny.central 与 tny.github-packages 约定插件），组号由根 build.gradle 单一事实源派生为 com.tnydev.game，……（后半句照旧）

## 五、认证配置

两条路径，凭据值一律不进仓库文件。

**CI 发布路径（推荐，零新增 secret）。** 所需权限：`publish.yml` 工作流级 permissions 新增 `packages: write`，并保留 `contents: read`（官方规则：点名任何权限后未点名的权限一律降为 none，缺 contents: read 会使 checkout 失败）。所需凭据：内置 `GITHUB_TOKEN`（无需创建）与作用户名的上下文变量 `github.actor`。注入方式：新步骤 env 以 `ORG_GRADLE_PROJECT_githubPackagesToken: ${{ secrets.GITHUB_TOKEN }}` 与 `ORG_GRADLE_PROJECT_githubPackagesUsername: ${{ github.actor }}` 桥为 Gradle 属性。同时必须注入 `ORG_GRADLE_PROJECT_NEXUS_USERNAME`、`ORG_GRADLE_PROJECT_NEXUS_PASSWORD`（tny.publish 的属性断言不区分目标仓）与 `SIGNING_KEY`、`SIGNING_KEY_ID`、`SIGNING_PASSWORD`（发布分支形态必签，`.asc` 是发布任务的依赖构件）。固有限制：官方规定 GITHUB_TOKEN 只能发布与运行本工作流的仓库相关联的包，因此包归属与 URL 锁死在 Tunaiyi/tny-framework 本体（owner 段小写形态）；若日后要把包挂到独立构件仓库，必须弃用内置令牌、改建对目标仓库有写权限的 classic PAT secret 并重新评审，这属于另一设计决策。

**本机发布路径。** 所需凭据：personal access tokens (classic)，勾选 write:packages 与 repo（公开仓库下 repo 的必要性官方未正面覆盖，一并勾选属保守；如实告知用户）；若要演练删除版本预案再加 read:packages 与 delete:packages（删除要求对所在仓库有 admin 权限）。必须明确告知用户：fine-grained PAT 截至 2026-10-05 不能用于 GitHub Packages（官方缺口清单，核实日期见 docs），按主流建议创建 fine-grained token 的人会在发布与解析时得到 401。注入方式：把 `githubPackagesUsername` 与 `githubPackagesToken` 两行写入用户级 `~/.gradle/gradle.properties`（临时试验可用 ORG_GRADLE_PROJECT_* 环境变量）。键名硬约束：不得含点号——Gradle 环境桥 ORG_GRADLE_PROJECT_* 拒绝含点属性名，该实测教训成文于 `tny.publications.gradle` 第 96 至 97 行签名段注记。两个属性必须成对在位：守卫同时断言两者，只配其一的机器不声明该仓、构建不报红（这是对已验证方案 blocker 的修复——若只断言 token，半配置机器会在配置期因 username 取值失败而使每一次 Gradle 调用报红）。

## 六、快照版本策略

**明确结论：首批不向 GitHub Packages 发布快照版本；githubPackages 仓按"版本不以 -SNAPSHOT 结尾"守卫声明，只有裸号正式版进入该通道。**

依据逐条交代。第一，官方 Maven 注册表文档明确支持 SNAPSHOT 版本的发布与解析（确定），所以不发快照不是注册表能力限制，而是本工程的纪律选择。第二，不可覆盖规则：GitHub Packages 对已存在的构件文件拒绝覆盖上传，重复 PUT 同名文件返回 HTTP 409（第三方实测记录高度一致，官方条文未记载）。第三，Gradle 的 maven-publish 上传 `-SNAPSHOT` 构件使用不带时间戳的原始文件名——本仓归档基线目录中 `tny-game-net-5.7.x-SNAPSHOT.module` 等真实抓样文件名与 kmgowda/SBK issue #356 的失败机制共同支持该结论，虽然官方无逐字陈述，属高置信推定；同一快照版本每次发布都推同名文件，二次快照发布必然 409，快照通道在本仓的语义就是"注定失败的定时炸弹"。第四，maven-metadata.xml 由谁维护、同一快照坐标保留多少历史构建、旧构件何时回收，官方全无记载（存疑），快照在本通道的可追溯性无法作为承诺。第五，三路调查中此处存在一处事实矛盾："构建链路"一路把"GitHub Packages 允许同名快照覆盖"当作事实陈述，"注册表事实"一路核实为"覆盖规则从未被官方记载、所有 409 记录都发生在正式版与附属文件上"——本方案按更保守一方处置，不把未经证实的覆盖宽容当作设计前提。

快照的历史可追溯与滚动分发继续由内网 Nexus 快照仓与 Central 快照通道（centralSnapshots 条件仓）承担，不受本方案影响。若用户日后拍板开放快照镜像，改动只有把守卫条件里的版本子句反写这一处，但必须同时成文同号快照重发的处置纪律（失败即停、不自动重试，重发只有升版本或在包页面删除旧版本两条路，删除要求操作者对所在仓库有 admin 权限），并接受快照历史不可承诺的事实。

## 七、门禁与规格合规性

### 7.1 新通道任务在双层拦截下的行为

任务集合：仓名 `githubPackages` 使 maven-publish 在每个应用本插件的工程（java 线全部模块与 `tny-game-bom`；插件线未应用、无对应任务）各派生 `publishMavenJavaPublicationToGithubPackagesRepository` 与 `publishAllPublicationsToGithubPackagesRepository` 两个任务。

第一层属性断言：`tny.publish.gradle` 第 24 至 33 行的谓词 `publishesToSharedRepository` 的第二条规则（任务名以 publish 开头且不以 ToMavenLocal 结尾）逐字命中两类新任务名（它们不等于第 26 行硬编码的三个裸名，走通配分支）；谓词消费者 `tasks.matching` 是活视图，后加入的任务自动被挂载，门禁文件零改动。doFirst 断言要求 RELEASE_REPOSITORY_URL、SNAPSHOT_REPOSITORY_URL、NEXUS_USERNAME、NEXUS_PASSWORD 四项非空——前两项已入库 `gradle.properties` 第 17 至 18 行恒非空，后两项因此在 CI 新步骤照注入，本机发布复用发布者既有的 Nexus 配置。准确表述失败时机（修正两份方案的相互矛盾处）：守卫把声明仓库所需的版本与凭据条件全部前移到配置期，缺任一条件的机器根本不声明该仓、不生成任务，对不存在的逐仓任务名执行会得 Cannot locate tasks；声明了该仓但缺 Nexus 凭据的机器执行镜像任务，会在执行期 doFirst 被 Nexus 属性断言拒绝——报错文案指 Nexus 不指 GitHub，是已配置的既有摩擦。GitHub Packages 自身凭据不在断言清单内，其友好性由守卫形态代偿（仓被声明时两属性必在）。把断言清单按仓归属拆分属于门禁语义变更，本方案不动，移交独立变更裁决。

第二层一致性门禁：第 271 至 273 行使两类新任务都 `dependsOn checkPublishPrerequisites`（全量 check 加 validatePublishConsistency 五项核对）。

### 7.2 五重门禁逐条推演（CI 发布路径）

1. 分支形态白名单：工作流由 release.published 触发后检出 `<主>.<次>.<补丁>.release` 分支，命中发布分支白名单且裸号形态匹配，放行。
2. 版本形态匹配：版本由分支派生，与目的地无关，放行。
3. 同号黑名单：只依赖 `gradle/released-legacy.txt` 与版本号，与新通道无关，行为不变。
4. 仓库路由一致（第 78 至 92 行）：遍历全部 http 开头仓 URL 做小写关键字判定；`https://maven.pkg.github.com/tunaiyi/tny-framework` 既不含 releases 与 -release/ 也不含 snapshots，开发线形态与发布分支形态都不报 problem 放行。注意该校验不替本通道把关"版本形态与仓的匹配"，"仅正式版进本仓"的约束由守卫声明自身表达——这符合门禁"只断言、仓库路由在 publishing.repositories 决定"的既有边界（该文件第 15 行注释成文）。
5. 发布标签远端存证：release.published 事件时附注标签已由 `tny.release` 的 releaseTag 任务先行推送；`git ls-remote` 按上游优先加单远端兜底解析远端名，结果按"分支+提交+标签"在根工程记忆化，新步骤的独立 Gradle 调用会再查一次远端且结果相同。显式前置条件：CI 检出后分支名与远端解析这条链在真实 Runner 上尚无实证绿（在途变更 central-publish-tnydev-group 的任务清单中，5.1 工作流演练与 5.2 失败路径演练两项均未勾选，本会话已核实）；首发前须先在测试标签上走通 publish.yml 现有两步。

### 7.3 扇出与既有机制的互扰

CI 的聚合扇出不含本通道：第一步骤 `./gradlew publish` 不注入镜像属性，守卫不声明该仓，扇出集合与改动前逐字一致；镜像只由第三步骤显式执行。本机发布分支形态且两属性在位时，`./gradlew publish` 会自动扇出镜像（与 centralSnapshots 的"守卫手法"同构——注意表述边界：覆盖面不同，先例只收快照、本仓只收正式版，且先例参与聚合扇出而 CI 上本通道走独立步骤）。第 114 至 119 行禁用名字含 CentralPortalSnapshots 任务的逻辑不匹配新任务名；tny.central 的 nmcp staging 仓（publishMavenJavaPublicationToNmcpRepository）本就命中谓词，属存量行为；validateRepositoryRouting 每次遍历的仓集合多一个 URL 但判定结果不变。CI 时长代价：新步骤是独立 Gradle 调用，checkPublishPrerequisites 令全量 check 重跑一遍，发布流水线时长约增一倍——这是换取通道隔离与不重推 Nexus 的明示代价（豁免需改门禁语义，另行裁决）。

### 7.4 gradle-build-style 逐条对照

对照 `openspec/specs/gradle-build-style/spec.md` 现文逐条核验（括号内为该文件行号）：

- 需求一"工程配置写声明式语句"（第 9 至 10 行）：新插件是"布尔条件守卫单条声明语句"形态（一个合取守卫加块内唯一一条 maven 声明），附交代各子句来由的注释，先例即 `tny.publications` 第 76 至 85 行的 centralSnapshots；"多个装配线共用的同一段配置 MUST 收编为可组合的共享约定插件"在本案成立——java 线经根脚本一行引入、BOM 经 plugins 块一行引入，声明点唯一，无一字复制。根脚本对插件的引用只有一行引入语句。
- 需求二"惰性形态与 providers"（第 25 行）：凭据经 `providers.gradleProperty` 读取；守卫的存在性检查沿用 `project.hasProperty` 先例（第 76 行同款），不构成系统属性直读违例；评审如从严可统一为 `providers.gradleProperty(...).isPresent()`，形态等义。
- 需求三、需求四（版本单一事实源与托管对账，第 36、59 行）：本案零触碰。
- 需求五"Groovy 语言纪律"（第 70 行）：新增文本全部单引号字符串、if 带大括号、无分号结尾、无 spread；多行合取条件是合法链式续行。
- 需求六"区块顺序与版面组织"（第 81 行）：新文件只含 publishing 仓库配置，属发布类目；`tny.publications` 零改动，其区块顺序不受扰动。
- 需求七"注释解释配置来由、插件文件头注释"（第 92 行）：新文件头写明职责与四插件边界；每条注释给出来由与出处（409 实测记录、Gradle 注册表小写规则、环境桥含点教训的成文位置）；注释文字遵守 CLAUDE.md 第一节。
- 需求八"扫读测试与长度界线"（第 107 行）：新插件约四十行，远低于约定插件的 250 行界线；根 `build.gradle` 现值 65 行（wc -l 实测）加一行为 66，低于 80 行界线；`tny-game-bom/build.gradle` 加一行为 20 行；`tny.publish.gradle` 现值 273 行已越界，本案刻意零改动该文件，不触发触碰即改。
- 需求九"存量违例触碰即改"（第 118 行）：改动区域（新文件、javaProjects 块尾一行、BOM plugins 块尾一行、publish.yml 非 .gradle 文件）内无旧式任务声明、spread 批量赋值、分号结尾、注释掉的配置残留四类违例形态；`tny-game-doc-gradle/build.gradle` 第 33 至 35 行的注释残留因本案不改该文件而不产生顺手修正义务。
- 需求十"共享脚本禁点名具体工程"（第 129 行）：URL 中的 tunaiyi 与 tny-framework 是 GitHub 仓库标识而非 Gradle 工程名字面量，同文件先例见 `tny.publications` 第 33 行与 scm 段第 46 至 50 行；成员判定走 javaProjects 命名约定，零点名。
- 需求十一（工具链基线成册，第 144 行）：不涉。
- `publish.yml` 不受本规格管辖，其合规依据是 GitHub Actions 文档与 CLAUDE.md 文字规则（头注与作业名同步改写即后者要求）。

## 八、验证步骤

以下命令按顺序执行，前十步全部为本地或只读操作、零真实外发上传；第十一步真实发布单列、需用户明确批准后执行。

1. 配置期编译与守卫防报红回归（开发线 `5.7.x` 上）：`./gradlew help` 应通过；随后半配置注入 `ORG_GRADLE_PROJECT_githubPackagesToken=dummy ./gradlew help` 也应通过——守卫同时断言两属性，缺 username 只导致不声明，配置期不得抛 MissingValueException（这是对已验证方案第二项 blocker 修复的直接回归测试，开发线上即可观测）。
2. 开发线任务面断言（注意语义，避免误判）：`./gradlew :tny-game-net:tasks --all | grep -i githubpackages` 预期为空——开发线版本是 `5.7.x-SNAPSHOT`，守卫的版本子句使本通道任务在开发线根本不存在，"为空"是设计意图而非验收失败；有凭据无凭据在开发线观察不到差异，凭据子句的验证只能放在第 4 步的发布形态上进行。
3. 临时发布形态演练分支（只建本地分支，绝不推送、绝不真实发布）：`git switch -c 5.7.99.release`（取一个远未使用的补丁号避免撞号）。
4. 任务面与守卫开关验证（该分支上）：`ORG_GRADLE_PROJECT_githubPackagesToken=dummy ORG_GRADLE_PROJECT_githubPackagesUsername=dummy ./gradlew :tny-game-net:tasks --all` 应出现 `publishMavenJavaPublicationToGithubPackagesRepository` 与 `publishAllPublicationsToGithubPackagesRepository`；同一注入下 `./gradlew publishAllPublicationsToGithubPackagesRepository --dry-run` 的任务图应含 `checkPublishPrerequisites` 且排在前（--dry-run 只列任务不执行动作，dummy 值不会外发）；清空两注入后重跑 `tasks --all`，任务应消失（证明凭据子句独立生效）。
5. 标签存证不旁路的真实执行证据（该分支、保持 dummy 注入）：`./gradlew :tny-game-net:publishAllPublicationsToGithubPackagesRepository` 应因 `checkPublishPrerequisites` 的标签存证核对失败而被拒（远端无标签 `v5.7.99`，且 `gradle.properties` 已含两项 Nexus URL、本机需已配置 NEXUS 两项，否则先撞属性断言——两者都是拒绝，均证明镜像不旁路门禁；发布动作在依赖校验失败后根本不会执行，无任何外发）。本步会跑一次全量 check 测试，耗时较长，属可接受的取证成本。演练完毕立即 `git switch 5.7.x && git branch -D 5.7.99.release`。
6. 构件名只读探测：`./gradlew :tny-game-net:generateMetadataFileForMavenJavaPublication :tny-game-bom:generatePomFileForMavenJavaPublication` 后核对生成的 groupId 与 artifactId 全为小写字母、数字与连字符形态（com.tnydev.game 与 tny-game-* 天然满足；本步是给未来新增大写命名模块留的兜底提醒，门禁暂无对应核对项）。
7. 工作流文件静态校验：`python3 -c "import yaml; yaml.safe_load(open('.github/workflows/publish.yml'))"`（语法）；本机装有 actionlint 时执行 `actionlint .github/workflows/publish.yml`（语义，含 permissions 与步骤形态）。YAML 语法绿不等于键值语义对，第 4 步的 dummy 注入落属性验证已一并覆盖 ORG_GRADLE_PROJECT 键名大小写拼写正确性。
8. 改动面收口核对：`git diff --stat` 应只触及第 4.1 至 4.7 小节所列文件；`git diff buildSrc/src/main/groovy/tny.publish.gradle buildSrc/src/main/groovy/tny.publications.gradle` 应为空。
9. 用户侧 GitHub 界面核对（本会话 gh CLI 未登录，无法代查）：仓库 Settings 中 Packages 未被组织策略禁用；Settings → Actions → General 的 workflow token 权限基线没有把 packages 限制到只读（组织级策略可覆盖工作流显式声明，这一项必须由用户在界面确认）；目标命名空间 `tunaiyi/tny-framework` 下尚无同名占号包。
10. CI 链前置实证（首发闸门）：按在途变更 central-publish-tnydev-group 的任务 5.1，先以仓库内测试标签走通 publish.yml 现有两个步骤的 dry-run 演练（分支名解析、远端名解析、标签存证核对通过、不外发真实制品）。在途任务 5.1、5.2 现均未勾选，这条链尚无实证绿；新步骤继承同一前提，未走通前不进入第十一步。
11. **真实首发（需用户明确批准，不在本方案自动执行范围）**：首选随下一个正常发布周期在 release.published 触发时自然发生；或经用户批准后在 `<主>.<次>.<补丁>.release` 分支本机执行 `./gradlew publishAllPublicationsToGithubPackagesRepository` 做一次单通道试点。失败即停、绝不自动重跑同号（409 不可覆盖规则使同号重跑必败），处置按 docs 的阶梯走升版本或删版本重发两条路。发布后由用户以只读方式核对包页面版本清单与 `gh api users/Tunaiyi/packages?package_type=maven` 列表。

## 九、未决问题与决策点

1. **插件线 `tny-game-doc-gradle` 是否纳入镜像——推荐：首批不纳入。** 理由：其 java-gradle-plugin marker 构件的 artifactId 由 Gradle 机制固定生成为含点号形态（如 `com.tny.game.gradle.plugin`），官方条文只列举小写字母、数字与连字符，点号是否被 Maven 注册表拒绝无任何实测记录（第二节事实 10），纳入则首次真实发布可能半途 422 并触发不可覆盖处置阶梯；且该线构件无签名、POM 无元数据组，镜像面貌与主通道不一致。若日后裁决纳入：先以一次性测试版本实测 marker 上传（属真实上传，需另行批准），通过后在根脚本 gradleProjects 块补一行引入即可，改动面恰为一行——独立插件形态正是为此保留的。
2. **快照是否镜像——推荐：不镜像。** 依据见第六节（Gradle 快照上传同名文件加 409 不可覆盖加元数据行为存疑的三重理由；两条调查路线矛盾处按保守方处置）。
3. **包归属仓库——推荐：挂 Tunaiyi/tny-framework 本体。** 这是选用内置 GITHUB_TOKEN 时官方权限规则强制的唯一选项；若用户想把包挂到独立构件仓库，必须弃用内置令牌改建 classic PAT secret，属另一设计决策，本方案不预支。
4. **CI 凭据来源——推荐：内置 GITHUB_TOKEN。** 零新增 secret、零轮换负担；替代路线（PAT secret）仅在改挂仓库或想让 CI 与仓库解耦时才必要。
5. **openspec 立项时序——推荐：等在途变更 central-publish-tnydev-group 归档、central-publishing 能力进入主账本之后，再立本变更。** 理由：central-publishing 的权威文本目前只是该在途变更的 ADDED 增量（其任务清单第 4.4、5.1、5.2 项未勾，本会话已核实），叠加立项会让两份增量互相覆盖、归档时基线漂移；若业务要求并行，则本变更的 proposal.md 必须写明以该在途增量现文为基线并钉住两册归档先后约束。
6. **属性断言按仓归属拆分——推荐：本变更不动。** 镜像任务继续为 Nexus 四属性付断言代价；拆分改变"共享仓统一资格"的门禁语义，须另立门禁设计变更评审（`tny.publish.gradle` 已达 273 行、越 250 行界线，改动前还需先按其自身界线要求做拆分，一并留给那个变更）。
7. **CI 时长增倍是否可接受——推荐：接受。** 新步骤独立 Gradle 调用令全量 check 重跑，发布流水线时长约增一倍；豁免镜像步骤的测试依赖等于对门禁开后门，与"不旁路"的设计目标冲突。若不可接受，归入决策 6 的门禁变更一并处置。
8. **本机扇出的半途失败面——推荐：接受并以文档封堵。** 本机在配齐凭据的发布分支上执行聚合 publish 时，镜像任务与 Nexus 同命令扇出，镜像失败会中止同一次调用中尚未执行的 Nexus 任务；处置仍是阶梯（先查存证、升号优先）。不做本机扇出顺序改造，因其等价于门禁语义变更（决策 6 范畴）。
9. **fine-grained PAT 缺口的时效——推荐：文档一律附核实日期。** 官方声明缺口"并非永久"，关闭后 docs 的"必须 classic"字样需回访更新；本方案所有相关表述均已带 2026-10-05 核实日期。

## 十、实施路径建议

**openspec 立项形态：新增独立 change 目录 `add-github-packages-channel`**（产出 proposal.md、design.md、tasks.md 与 central-publishing 规格增量），规格增量为对 central-publishing 能力的两条 ADDED 需求，**不修改 release-versioning**（其条文以"与版本形态匹配的目标仓库"泛称表述，天然容纳新目的地，改动它反而动摇"不得破坏双层拦截与五重门禁"的约束前提），**不修改 gradle-build-style**（第七条 4 节已证新增形态逐条合规）。ADDED 需求建议文本：

```markdown
## ADDED Requirements

### Requirement: GitHub Packages 镜像通道按凭据与版本形态守卫声明
构建系统 SHALL 以独立约定插件声明名为 githubPackages 的发布目标仓库，地址为
https://maven.pkg.github.com/tunaiyi/tny-framework（owner 段按官方注册表文档全小写）；
该仓库 MUST 仅在版本为裸号正式版形态且 githubPackagesUsername 与 githubPackagesToken
两项工程属性同时在位时声明。凭据在位时，命中共享仓门禁谓词的发布任务 SHALL 把同一套
构件镜像分发至该仓，且 MUST NOT 对本通道减免属性断言与一致性校验中的任何一项。
本通道为「双仓并行发布相互独立」需求所述两个目的地之外的第三目的地；该条文的通道
枚举随本需求扩写为「各目的地独立提交与独立报告结果，一个目的地失败不得撤回或污染
已成功目的地」，GitHub Actions 上本通道以独立步骤执行以满足该语义，本机扇出的
半途失败处置成文于 docs/release-process.md。

#### Scenario: 缺凭据机器发布行为不变
- **WHEN** 未成对配置 githubPackagesUsername 与 githubPackagesToken 的开发机执行任意构建与 ./gradlew publish
- **THEN** 任务图中不存在任何 ToGithubPackagesRepository 任务，发布结果与本变更实施前逐任务一致，配置阶段不因半配置凭据报错

#### Scenario: 快照形态不声明镜像仓
- **WHEN** 在开发线形态（<主>.<次>.x）且凭据成对在位时执行构建
- **THEN** githubPackages 仓不因本变更被声明，快照构件不进入该通道

#### Scenario: 镜像通道不旁路标签存证
- **WHEN** 在发布分支形态未推送 v<N>.<M>.<K> 附注标签而执行逐仓镜像发布任务
- **THEN** 发布被一致性门禁以标签存证缺失拒绝，GitHub Packages 上不出现该版本构件

### Requirement: 镜像通道正式版一次成型并成文重发处置
GitHub Packages 对已存在的构件文件拒绝覆盖上传（重复 PUT 返回 HTTP 409，官方条文未记载、
第三方实测记录一致）；正式版本构件进入该通道 MUST 视为一次成型。镜像半途失败时，处置
MUST 按 docs/release-process.md 的阶梯执行（先只读查存证、优先提高补丁号重发、同号重发
仅在删除版本实测可行后采用），MUST NOT 以重跑全量发布作为默认补救。

#### Scenario: 半途失败后重跑被拒且指引可达
- **WHEN** 一次正式版发布中部分构件已镜像成功、其余失败，发布者重跑同一镜像发布命令
- **THEN** 已上传构件以 409 拒绝，错误输出与文档阶梯使发布者能够定位提高补丁号或删版本重发的处置路径
```

**同一变更内必须联动的文档与账本文件**（列举式条文与新事实同步，防过期陈述）：`docs/release-process.md`（流程六第 4 步补句、运维前置第 3 条、新节）、`README.md`（第 695 行计数词与第 4 条）、`openspec/config.yaml` 第 34 行发布句、`publish.yml` 头注与作业名；以及等 central-publish-tnydev-group 归档后，把其 central-publishing 增量中"正式版本同时发布至 Maven Central 与内网发布仓"与"快照同时发布到内网快照仓与 Maven Central 快照仓"两条的通道枚举按上文 Requirement 的扩写口径核对为并存不冲突（本通道不收快照，快照条文不需改动）。

**tasks.md 建议分组（顺序即实施顺序）**：第一组，插件与装配（新建插件文件、根脚本一行、BOM 一行）；第二组，CI（permissions、头注与作业名、第三步骤）；第三组，文档四处联动；第四组，第八节验证步骤 1 至 8 的安全演练与留痕；第五组，用户侧核对（第八节步骤 9、10，其中步骤 10 依赖在途变更任务 5.1 完成）；第六组，真实首发（第八节步骤 11，条目本身写明"需用户明确批准后方执行"）。收口以 openspec verify 对照三条 Scenario 逐条取证（缺凭据不变用步骤 4 清空注入的证据、快照不声明用步骤 2 的证据、不旁路标签用步骤 5 的拒绝输出），任务清单全勾后归档；归档时序若与 central-publish-tnydev-group 并行，则 proposal.md 按第九节决策 5 的口径钉住两册先后约束。

---

## 附录 A：两份候选方案的对抗验证结论

### 视角：最小改动（可行度 7/10）

验证者认定的阻断问题（综合阶段已全部修复或改道，修复方式见正文第三节）：
- rolloutAndVerify 第 5 步的验证命令按方案字面在开发线 5.7.x 上执行必然失效：新增守卫是「版本非 -SNAPSHOT 且 token 在位」的合取，开发线版本形态就是 5.7.x-SNAPSHOT（tny.git 按分支名派生），第一个子句即把 githubPackages 仓整体排除——因此「本机凭据配置完成后执行 ./gradlew -q tasks --all | grep -i githubPackages 确认逐仓任务集合就位」永远 grep 为空，「./gradlew publishAllPublicationsToGithubPackagesRepository --dry-run 观察任务图」会直接报 Task not found，「临时清空凭据重跑 publish --dry-run 确认列表不含 GithubPackages 任务」更是恒真（无凭据与有凭据在开发线观察不到任何差异，无法证明凭据子句生效）。这些自检要么全部改到临时发布分支形态（切一个 <V>.release 形态分支）执行，要么在方案里明示「开发线上本通道任务面不可观测，验证随第 8 步发布形态进行」。以方案现文照做会把『改动未生效』误判为验收失败，或把恒真比较误判为守卫获证。
- 凭据缺失的失败时机被方案说反，掩盖了一个真实的构建全灭场景：maven-publish 的 repositories 容器在配置阶段添加 maven{} 时立即执行仓库体与 credentials 闭包，providers.gradleProperty('githubPackagesUsername').get() 在属性缺失时于配置期抛 MissingValueException（先例 centralSnapshots 的守卫注释之所以存在，正因为 .get() 是配置期行为），而方案 gateInteraction 写「缺 githubPackagesUsername 会在 credentials 闭包求值时以属性缺失异常暴露于执行期」、rationale 写「password 用 .get() 而缺属性不炸配置期」。按此认知，守卫只查 githubPackagesToken 而不查 githubPackagesUsername 的半配置机器（照第 4 步指引漏配 username 的用户完全可能触发）在发布分支形态下会让每一次 Gradle 调用——包括 ./gradlew help 与所有构建——在配置期报红，且方案自己的第 5 步「./gradlew help 确认配置阶段编译通过」在开发线上永远绿，发现不了它。修复：守卫条件同时断言两个属性（hasProperty 两项都判），并改正两处时机表述。

### 视角：体系化（可行度 7/10）

验证者认定的阻断问题（综合阶段已全部修复或改道，修复方式见正文第三节）：
- 方案对插件线构件的小写合规断言不成立且验证步骤未覆盖：根 build.gradle 的 gradleProjects 引入行默认把 tny-game-doc-gradle 的 java-gradle-plugin marker 发布纳入镜像，而 marker 构件的 artifactId 由 Gradle 机制固定生成为 'com.tny.game.gradle.plugin'（含点号）。官方 Apache Maven 注册表文档对 artifactId 的表述是 should only contain lowercase letters, digits, or hyphens（本次已核实原句），点号不在列举字符集内；方案断言的现坐标面（com.tnydev.game、tny-game-*、com.tny.game）只覆盖组号与项目名推导的构件，不含 marker 构件，且验证步骤（d）只对 :tny-game-net 生成模块元数据核对，从不触及 marker。若 GitHub Maven 注册表按文档字符集拒绝点号，首次含插件线镜像的真实发布必然半途 422 失败，已上传构件随即进入 409 不可覆盖处置阶梯。合入前必须先以一次性版本实测 marker 上传，或把 gradleProjects 那行引入从首批撤下（方案自己已声明该行可单独删除），否则该断言属未经核验的事实声明。

---

## 附录 B：完整性批评者的缺口清单

### 缺口 1（重要度：高）

本机扇出与 CI 第三步骤的通道归属没有裁决，正常发布必然同号双发触发 409。方案的「本机首次配置」一节指导发布者把 githubPackagesUsername 与 githubPackagesToken 写入 ~/.gradle/gradle.properties，而守卫条件（发布分支形态加凭据成对）使本机 `./gradlew publish` 自动扇出镜像；随后 GitHub Actions 在 release.published 事件上执行第三步骤 publishAllPublicationsToGithubPackagesRepository，对同一批已上传文件再次 PUT，按事实 6 的不可覆盖规则全部返回 409，第三次步骤在每一次正常发布上都会红。决策点八只处理了同一条命令内镜像与 Nexus 的半途失败面，没有处理本机与 CI 的重叠；同理，对已成功镜像的版本人工重新触发 workflow_dispatch 或重跑工作流，第三步骤也必然 409，方案没有给出「重跑即失败属预期、如何判读」的说明。

建议补写内容：

在第九节增设「决策点十：镜像由谁执行」并推荐「仅 CI 执行」，把结论原文写入 docs 的 GitHub Packages 镜像通道一节：「本通道的常规执行者是 GitHub Actions publish.yml 的第三步骤；发布者本机不要在 ~/.gradle/gradle.properties 中长期配置 githubPackagesUsername 与 githubPackagesToken 成对属性，否则本机在发布分支执行 ./gradlew publish 会先行把构件送入镜像，随后 release.published 触发的 CI 第三步骤对同名文件重传必遭 HTTP 409 而失败。第八节步骤 11 的单通道试点属例外：试点时临时以 ORG_GRADLE_PROJECT_* 环境变量注入两个属性、试点结束立即确认标签尚未推送（release.published 未发生、CI 不会再传），或试点后接受同号不再经 CI 镜像。人工重新触发 publish.yml 重跑已镜像成功的发布时，第三步骤的 409 属『构件已在镜像中』的证据而非新故障，判读方法：按阶梯第一步只读查存证确认版本清单完整后，视该步骤失败为可忽略，不得为消除 409 而删除版本重发。」

### 缺口 2（重要度：中）

两处「存疑事实必须先实测」的动作（事实 10 含点号 artifactId、事实 11 删除后同号可否立即重发与 GraphQL 删除对 Maven 注册表是否适用，以及阶梯第 3 步要求的一次性测试版本实测）没有交代安全且可执行的实测场所。实测要真实上传，就必须构造发布分支形态并通过五重门禁（镜像任务命中门禁谓词，缺远端附注标签会被拒），意味着要在生产命名空间 tunaiyi/tny-framework 打出测试标签、生成需要再删除的垃圾包，而删除本身正是待实测的不确定项——循环依赖，一旦实施就会卡住。

建议补写内容：

在第九节决策一与决策三之间补一段「实测场地」：「上述四项存疑事实的实测一律在专用沙箱仓库（建议 tuner 名下新建私有仓库 gpr-sandbox，并开启 Packages）进行，不在 tny-framework 生产命名空间打测试标签。做法：在沙箱仓库复制一份最小工程（应用同款守卫形态的临时副本，URL 的 owner 段指向沙箱），依次实测四件事并把结论回填第二节事实——其一，java-gradle-plugin 生成的含点号 marker 构件上传 Maven 注册表是被接受还是返回 422；其二，删除某版本的单个版本后，同一版本号立即重传能否成功；其三，GraphQL 的 deletePackageVersion 对 Maven 注册表是否生效（不可用则删除只有包页面一条路，阶梯第 3 步需按『六十多个包逐个手工删除』重估成本）；其四，GitHub Packages 拒收 SHA-512 校验和时 Gradle 仅警告不失败的表述能否复现。沙箱为私有仓库时其包存储计费按第二节事实 13 的私有档核对额度，测试构件体量小，预期不触顶。」

### 缺口 3（重要度：中）

第二节事实 13 只论证了「公共仓库免费」这一个计费维度，没有核 GitHub Packages 对 Maven 注册表的使用限制维度。本仓每次全量发布要向镜像 PUT 约六十多个模块乘以（主 jar、sources jar、javadoc jar、POM、.module、.asc 再各配 md5、sha1、sha256、sha512 四种校验和）的上千个请求，其中 javadoc jar 是本仓体量最大的单体构件；官方对 Maven 注册表设有单文件大小与包容量上限、对 Actions runner 短时间批量上传有速率限制，方案未引用这些条文、未与本仓实量比对，而上传超限产生的失败与 409 在现象上难以区分，会被误送进同号重发阶梯。

建议补写内容：

在第二节事实 13 之后增补一条「使用限制」事实并挂验证动作：「GitHub Packages 对 Maven 注册表存在官方记载的使用限制（单个构件文件大小上限、包容量软上限、短时批量请求的速率限制，具体数值以官方 usage limits 页面现文为准，立项时抓取全文并注明核实日期）。本仓发布形态下每版本约上传六十余个构件族、上千次 PUT，且 javadoc jar 中体积最大者需与该上限比对。实施前的核对动作：以 `find . -path '*/build/docs/javadoc*' -name '*.jar' -exec du -h {} + | sort -h | tail` 取现仓 javadoc 产物最大体积，与官方上限逐项比对；比对结论写入方案；首次全量上传同时充当速率限制观察点，若出现 429 或超时类失败（区别于 409 的 overwriting is disabled 正文），处置不是升号或删版本，而是向注册表提额或改分批发射，此路径在 docs 阶梯外另立一句说明。」

### 缺口 4（重要度：中）

消费方接入说明对 FAIL_ON_PROJECT_REPOS 只交代了一半。片段要求下游「在其工程 settings.gradle 的 dependencyResolutionManagement 唯一声明点内追加」，这只对已经采用该模式的消费方成立：未启用 dependencyResolutionManagement 的传统工程把 maven 块写在 settings.gradle 里不会生效（该文件没有这个容器时 Gradle 直接报 DSL 错误），写在 build.gradle 的 repositories 块才是其正确落点，方案没有给这部分读者指路。另外 classic PAT 的 repo scope 意味着下游用户交出的令牌可读写该账户全部有权限的仓库，本仓自身的 FAIL_ON_PROJECT_REPOS 是仓库构建纪律、对消费方没有任何约束力，这两点都需要在向消费方成文时说清，否则外部开发者接入即困惑。

建议补写内容：

把 docs 的「消费方接入」段改写为覆盖两种形态并补一句令牌风险提示：「Gradle 下游的追加位置取决于其工程自身的仓库声明模式：若工程已在 settings.gradle 启用 dependencyResolutionManagement 并把 repositoriesMode 设为 FAIL_ON_PROJECT_REPOS（该模式禁止任何子工程在 build.gradle 里私设 repositories，本仓自身即此形态，但这只是本仓纪律、对下游工程没有约束力），则必须把上述 maven 块加进 settings.gradle 的 dependencyResolutionManagement.repositories；若工程未启用该模式，则把同一个 maven 块加进消费方 build.gradle 的 repositories（或 subprojects.repositories）块即可，两种形态的块内容逐字相同。另需向消费方言明：GitHub Packages 的 Maven 注册表权限完全继承所在仓库，classic PAT 勾选 repo scope 后该令牌即可作用于令牌持有者的全部仓库权限，建议下游为接入本镜像单独创建最小权限的 classic PAT，不要复用日常令牌。」

### 缺口 5（重要度：中）

镜像通道的常态触发条件是「GitHub Release 被 publish」，方案没有把这个触发前提写成发布纪律，也没有常态化的完整性核对。流程六与 tny:release 快速通道都只强制推送附注标签并本机发布 Nexus；若某次发布只推了标签而没有创建并 publish GitHub Release，release.published 事件不会发生，CI 第三步骤从不执行，该版本在镜像里静默缺失，而纯 GitHub 生态的消费方只会遭遇解析失败，没有任何对账动作能暴露缺号。第八节步骤 11 的「发布后核对包页面」只覆盖首次发布，后续版本无例行核对。

建议补写内容：

在 docs 的 GitHub Packages 镜像通道一节触发形态段之后补一句纪律与一句对账：「本通道的常态触发前提是该版本对应的 GitHub Release 已被 publish（publish.yml 由 release.published 事件触发）；只推标签不发布 Release 时，Nexus 与 Central 照常、镜像静默不发生，因此发布流程在 releaseTag 之后必须完成 GitHub Release 的创建与发布，方视为该版本全通道发布完毕。每次发布收口后的例行对账（只读）：以 `git tag -l 'v*'` 的版本清单与 `gh api users/Tunaiyi/packages?package_type=maven --jq '.[].name'` 及代表性包的版本清单比对，发现缺号即按缺失模块补跑逐仓镜像任务（该补跑仅在缺号版本构件确未上传时安全，已上传文件重传将遭 409）。」

### 缺口 6（重要度：低）

文档联动的位置锚点用了现文行号，而决策五自己排除了时序并行风险的前提是行号稳定——在途变更 central-publish-tnydev-group 归档合入后，docs/release-process.md、README.md、openspec/config.yaml、publish.yml 的行号大概率漂移，按行号执行第三组任务会改错位置；同时 tny.release.gradle 内四处引导发布者执行 ./gradlew publish 的输出话术（现文第 108、116、144、155、196 行）在镜像通道归属裁决后属于需要同步的面向人文本，但未列入第十节联动文件清单。

建议补写内容：

两处修订：其一，把方案内所有以行号表述的改动位置（如「现文第 105 至 106 行」「第 695 行」）改为「以节标题与引导句原文为锚，行号仅作当前快照参考；若归档在途变更后行号漂移，以锚定的标题与句子重新定位」；其二，在第十节「同一变更内必须联动的文档与账本文件」清单中补一项：「tny.release 的引导话术——buildSrc/src/main/groovy/tny.release.gradle 中 releaseCut 与 releaseTag 完成提示、releaseMergeBack 前提确认句里指路 ./gradlew publish 的文字，按镜像通道归属裁决同步改写（若裁定镜像仅由 CI 执行，则话术无需改动，但须在该文件对应位置加一句注释说明本机发布不含镜像属设计使然，防止后来者误以为遗漏）；此项改动触碰 tny.release.gradle，注意其现文行数与 gradle-build-style 长度界线的关系，如已越界按触碰即改规则评估。」

