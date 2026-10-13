# Design

## Context

动机与 BREAKING 判定见 proposal.md 的 Why。此处记录约束技术方案的事实。

**组号声明现状**（grep 取证）：`build.gradle:88` 以 `group 'com.tny.game'` 声明根组，另有 43 个模块的 `build.gradle` 首行重复硬编码 `group "com.tny.game"`——两处并存已是双事实源，本次迁移必须先收敛为单一声明点，否则半改状态会让下游拿到组号混杂的构件集。本仓库 codegraph 索引只覆盖 Java 源码，Gradle 脚本符号不在图内，爆炸半径以上述 grep 与下游文档检索（`docs/site/index.html:174-177` 示例坐标、README 许可证徽章）取证。

**包名耦合面**（决定"换组不换包"的成本依据）：全仓 2462 个源文件的包声明、`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 系列清单、`META-INF/tny-factory.properties` 以类全限定名为键与值、`log4j2.json` logger 名——包名若随组迁移，除 import 改写外还要动这些资源字符串与全部下游工程；Maven 组号与 Java 包名在规范上解耦（Netty 组号 `io.netty` 对应包名 `io.netty` 是巧合一致，Spring AI 的组号层级与其包层级并不逐段对应），Central 只校验组号对应的域名所有权，从不校验包名。

**Central 接入约束**：Maven Central 经 Central Portal 接入，命名空间验证要求对组号首两级域名段（`tnydev.com`）的 DNS 控制权——`m2.tnydev.com` 在用即为控制权证据；`com.tny.game` 需验证 `tny.game` 域名，无证据，此为组号迁移的硬驱动。Central 要求 POM 具备 name/description/url/licenses/developers/scm 六类字段并要求主 jar、sources jar、javadoc jar、POM 四件套外加数字签名；现构建从未生成 sources 与 javadoc 构件，为上线前必修；POM 许可证字段（Apache 2.0）与源文件头（当时为 Mulan PSL v2）曾有的声明不符问题，已于 2026-10-02 全仓切换为 Apache License 2.0 时消除（`gradle/publications.gradle` 许可证块保持 Apache 声明）。Central 提供官方快照仓（`central.sonatype.com/repository/maven-snapshots/`，标准 maven-deploy 端点、Portal token 认证、无验证管线、90 天自动清理）。2026-10-02 决策修订（用户在命名空间开启 Enable SNAPSHOTs 并改写 spec R4）：开发线快照同步分发 Central 快照仓，内网快照仓保留为权威归档，Central 侧不作归档承诺。`tny-game-doc-gradle` 的 Gradle 插件 id `com.tny.game` 是其既有外部契约，插件 marker 坐标由 id 而非项目组派生，本次将其排除出 Central 发布以避免牵动插件解析链。

**发布门禁衔接**（既有能力约束）：`release-versioning` 的标签存证校验在任一发布任务执行前拒绝缺标签的正式版；新增 Central 通道后该门禁保持为两个目标仓库共用的单一前置检查，不因通道变多而出现"标签没建但 Central 已推"的窗口。

**签名与触发方式**：采用 GitHub Actions 的 release 事件触发发布工作流（GitHub 官方 Java 包发布指南的骨架），签名走工作流环境注入的 GPG 私钥（secrets 管理，私钥不入库）；同一签名覆盖双仓产物。

## Goals / Non-Goals

**Goals:**
- 组号迁移一次到位：全量发布构件以 `com.tnydev.game` 进 Central，旧组号从未进入公共仓库，外部无断档窗口。
- 组号声明收敛为单一事实源，并由构建期对账保证 43 个残留点全部清除。
- Central 准入完整性（POM 六字段、四件套、签名）在推送前本地可验证，半成品不出网。
- 双仓独立发布：一侧失败不污染另一侧账本，重试幂等安全。

**Non-Goals:**
- 不改 Java 包名与 Gradle 插件 id（proposal 已决策，收益成本不对称）。
- 不迁移内网 Nexus 既有构件（历史坐标保持原样供在网工程解析）。
- 不引入 CI 全量自动发布（发布触发仍经人工 release 事件，防误发公共仓库不可撤销内容）。
- 不为 `tny-game-doc-gradle` 设计 Central 发布路径。

## Decisions

### D1 组号改为 `com.tnydev.game`，包名 `com.tny.game` 保持不动（依据 P11：坐标即合同——组号变更已标 BREAKING 并给迁移说明；包名不变使合同主体面零破坏）
被否决备选：同步改包名为 `com.tnydev.game`——否决理由：改动面 2462 源文件加资源字符串加全部下游 import，而收益仅为组号与包名前缀形式一致；Java 生态组号包名解耦有大量先例，Central 不校验包名，形式收益无外部兑现点。被否决备选：保持旧组号申请 Central——否决理由：命名空间验证要求 `tny.game` 域名控制权，无证据，物理不可行。

### D2 组号收敛到根构建脚本单一声明点，模块级硬编码全删（依据先例 `centralize-java-version-property` 归档变更——同一"多源收敛单源"手法，M1 先例优先）
`build.gradle` 的组声明改为 `com.tnydev.game`，43 个模块首行 `group "com.tny.game"` 删除后自动继承根值；增加配置期对账断言（发布任务的模块清单中任一构件组不等于声明点即判红），兑现 spec"制品组号以单一事实源派生"的错误路径。被否决备选：仅全局替换 43 处文字保留双事实源——否决理由：下一轮加模块时旧组号会经复制粘贴回潮，正是本次要消灭的病根；被否决备选：改 settings.gradle 集中注入——否决理由：模块首行声明删除后根继承已是最小机制，无需再造注入层。

### D3 Central 接入走官方推荐的 Gradle 发布插件加 GitHub Actions release 触发，GPG 私钥经 secrets 注入（依据 P13：发布准入完整性必须可被构建任务验证）
POM 六字段补齐与 sources/javadoc/signing 由发布插件统一生成挂接；签名采用工作流环境注入私钥，内网与 Central 双仓共用同一签名账本。被否决备选：Central Portal 托管签名（web 上传流程）——否决理由：与"GitHub release 事件触发"的自动化目标冲突，且 Portal 托管路径不利于内网仓库共用签名产物；被否决备选：本地手工命令发布——否决理由：重复劳动不可复现，私钥落个人机扩大泄漏面；被否决备选：沿用 GitHub 指南示例的 OSSRH 旧端点——否决理由：Central 官方文档明示新接入不走旧 JIRA 工作流，照抄即错。

实施定案补记（apply 阶段，含两次路线修正的完整账目）：

- 插件终选 **nmcp**（`com.gradleup.nmcp` + `com.gradleup.nmcp.aggregation` 1.6.2，Gradle 团队维护）：其模块插件"不创建 publication、不 apply maven-publish、直接消费既有 mavenJava"，与本仓手工发布模型、五重门禁零冲突。**vanniktech 系（CSDN 教程路线）被否**：接管型插件自建 publication 并与 POM/签名/附属件全面接管，与既有 `mavenJava` 同坐标双 publication 会向仓库重复上传、门禁模型需整体迁移，收益不抵重构面。
- 原决策中"POM 六字段与 sources/javadoc/signing 由发布插件统一生成"与实际不符，修正为：**POM 六字段、sources/javadoc 构件、signing 全部由仓库自有构建配置承担**（publications.gradle/build.gradle，已完成并实测），nmcp 仅承担聚合上传；聚合任务 `publishAggregationToCentralPortal` 的 `publishingType=AUTOMATIC`。
- 快照通道定案（spec R4 修订后）：进 Central 的快照唯一经 publications.gradle 的 centralSnapshots 原生仓（与内网同经 `publish` 分发、参与条件=快照版本形态+Central 凭据存在，与 Nexus 路由同文件同源判定）；nmcp 自带的快照聚合/直传任务保持禁用——两条通道并跑会让同一构件产生双时间戳部署竞争。
- 集成实测暴露两处环境问题并修复：其一，模块级任务禁用必须写在模块作用域脚本（根工程 configureEach 覆盖不到模块任务）；其二，nmcp 插件传递依赖 `xmlutil 1.0.0-rc3` 在三家镜像均缺包（Central 权威源存在），项目仓库列表追加 `mavenCentral()` 兜底，且 Gradle 负面解析缓存需 `--refresh-dependencies` 清一次——此坑对全部依赖解析通用，不只 nmcp。

### D4 双仓独立发布，失败互不回滚、重试幂等（依据 P4 精神：把"已发布"当作不可变事实保护，而非事务中间态）
Central 与内网各自的发布任务独立执行并分别报告；重试语义靠 Maven 坐标系天然保证——已推成功的一侧再推同版本被拒按等价成功处理（对 Central 侧以"已存在则视为完成"的判定吸收该拒绝码），绝不撤回或跳过失败侧。被否决备选：先内网成功后再推 Central、任一失败即人工回滚内网——否决理由：Maven 仓库部署不可逆，"回滚内网"本身就是制造账实分裂。

### D5 Central 发布通道与既有发布门禁同源判定，doc-gradle 模块排除（依据 P12：发布资格判定只有一处事实源，新通道复用不另立）
正式版 Central 通道参与条件即 `release-versioning` 白名单的发布分支形态加裸号版本（centralCheck 守卫），`tny-game-doc-gradle` 因不应用 publications.gradle 而自然落选；快照的 Central 分发条件复用 publications.gradle 内既有的版本后缀路由判定（快照形态+凭据存在），与 Nexus 仓库声明同文件同表达式，不另立第二套规则。spec 的"Central 通道共用同一前置校验"与"快照双通道分发"两个场景同由此兑现。被否决备选：给 Central 单独一套触发判断——否决理由：两套判断必然漂移，孤儿制品风险回归。

### D6 javadoc 与 sources 构件用 Gradle 标准 `withJavadocJar()/withSourcesJar()` 生成，doclint 严格度先测再定
实施任务先在代表性模块实测生成，若既有注释在严格 doclint 下大面积报错，采取"javadoc 任务产出但 doclint 降级并记录豁免"策略——Central 只强制 javadoc jar 存在，不强制注释质量。被否决备选：手写 assembly 拼 javadoc jar——否决理由：标准能力存在却另起炉灶，违反 P3 精神且多维护一份构建逻辑。

实施补记（apply 阶段实测记录）：代表性模块 `tny-game-common-lang` 严格 doclint 实测 100 错误，主体为全仓 920 文件在用的 `@date` 自定义标签（64 处"未知标记"）与块标签次序告警（28 处），属团队标签约定而非注释内容缺陷；据此按本决策走降级豁免——`build.gradle` 的 javaProjects 内对 Javadoc 任务设 `-Xdoclint:none` 并注册 `@date` 标签（日期在 HTML 中正常渲染）。sources 通道实施时发现 javaProjects 块尾部既有手写 `sourcesJar` 任务与 `java { withSourcesJar() }` 组合（与根 toolchain 块的重复声明冲突），处理为仅新增 `withJavadocJar()`、不动既有 sources 声明，避免重复注册。豁免范围仅 javaProjects 的 javadoc 任务；若后续要恢复严格 doclint，属注释治理专项，不影响 Central 准入。

## Risks / Trade-offs

- Central 命名空间验证需操作 `tnydev.com` 的 DNS，若管理员流程受阻则发布通道整体阻塞。→ 列为实施首个任务（验证组号可用即解锁后续全部工作），受阻时变更降级为"仅内网发布 + 设计存档"。
- doclint 与 POM 字段补齐会暴露存量注释债，修复面未知可能拖长变更。→ D6 的先测再定策略加 Central 只验存在性不验质量，最坏路径为降级豁免。
- 43 个模块首行的删除与仍活跃的变更（bench、CI 稳定性）存在构建脚本冲突窗口。→ 删除类改动集中在一个任务组内一次完成，合入前 rebase 活跃分支。
- 下游按旧组号文档操作找不到新构件。→ 旧组号从未进公共仓库，外部无此问题；内网与示例文档（`docs/site`、README）同步改写并在 release 说明中标注迁移。
- GPG 私钥进入 CI secrets 即新增泄漏面。→ 密钥仓库级隔离、设过期时间、季度轮换，日志侧由 GitHub 对 secrets 自动脱敏兜底。
- Central 发布不可撤销，任何误发（错误版本、缺签名件）都永久留账。→ 准入完整性本地前置校验加 release 事件人工触发闸口双保险；本设计无法消除的残余风险如实声明：若本地校验全过但内容本身有缺陷，仍依赖发布前评审拦截。

## Migration Plan

1. 前置验证：在 Central Portal 完成 `com.tnydev.game` 命名空间 DNS 验证；验证不通过则停止本变更后续实施并回 proposal 重新定组号。
2. 组号收敛：根声明改 `com.tnydev.game`、删 43 处模块首行、加构建期组号对账断言；跑全仓编译与代表性模块测试确认无回归。
3. 准入补齐：POM 六字段补齐，许可证声明保持 Apache License 2.0（与全仓源文件头一致）；挂 sources/javadoc 生成并实测 doclint 影响面；配置签名（本地验证可签可验）。
4. 通道接入：发布插件与双仓声明配置、门禁同源判定接入、GitHub Actions release 工作流与 secrets；在测试分支以 dry-run 工作流走通"门禁拒绝缺标签 / 准入拒绝缺签名 / 双仓独立报告"三条路径。
5. 文档联动：README 与 `docs/site` 示例坐标改新组号、发布流程文档补 Central 步骤与失败重试说明。
6. 首件发布：经 `/tny:release` 流程产出首个正式版并按新通道发布，验收后本变更归档。

回滚策略：步骤 1 至 3 全部可 revert；首件发布前 Central 仓库零足迹、回滚无外部痕迹；步骤 4 起以工作流停用为界（不删 secrets 只停触发），历史内网通道始终独立可用。

## Open Questions

- 发布插件选型细节（官方推荐清单内多候选）在步骤 4 实施时按当前版本兼容矩阵定夺，不影响本设计结构与任务拆分。
