# Proposal

## Why

框架目前只发布到自建仓库 `m2.tnydev.com`，外部使用者无法通过 Maven Central 获取任何构件，公开分发的通路缺失。接入 Maven Central 的命名空间验证要求申请人证明对组号对应域名的控制权：现用组号 `com.tny.game` 需要证明对 `tny.game` 域名的控制，而仓库内没有任何所有权证据；相反，`tnydev.com` 已被自有服务使用（`m2.tnydev.com` 即其子域），组号 `com.tnydev.game` 的域名验证可以完成。因此**组号必须迁移，这属于一次性、越晚越贵的决策**：Central 上的组号一经发布不可撤销，晚迁移意味着旧组号构件永久留存并被外部依赖。

同时，仓库现状距离 Central 的发布硬性要求存在两处缺口，必须在本变更内一并补齐：构建从未生成 sources 与 javadoc 附属构件（Central 对两者作强制要求）；仓库配置未接入 Central 发布端点。许可证方面：项目已于 2026-10-02 整体切换为 Apache License 2.0，源文件版权头、README 徽章与 LICENSE 文件全部随之更新，POM 的 Apache 声明自此与事实一致，原先两处声明不符的问题不复存在。

关于 Java 包名是否随组号改为 `com.tnydev.game`：经调研后决策为**不改**。全仓 2462 个源文件的包声明、43 个模块首行的组声明、以及以类全限定名为键的资源（Spring Boot 自动配置清单 `AutoConfiguration.imports`、框架自研工厂清单 `META-INF/tny-factory.properties`、日志配置中的 logger 名）都锚定在 `com.tny.game` 前缀上；Java 生态对组号与包名不一致有大量先例（Spring 系 `org.springframework.ai` 的组号与其子包结构、Netty 的组号与 `io.netty` 包名亦非逐段对应），Central 不校验包名。包名迁移的收益仅是组号与包名前缀的形式一致性，代价是全部下游工程的 import 语句改写与在网游戏热更配置的失效风险，收益成本严重不对称。Gradle 插件模块（`tny-game-doc-gradle`）的插件 id `com.tny.game` 同理保持不动，本次不将其发布到 Central。

## What Changes

- 全部发布构件的 Maven 组号由 `com.tny.game` 迁移为 `com.tnydev.game`；**BREAKING**——下游依赖坐标必须同步改写（`build.gradle:88` 与 43 个模块首行硬编码的组声明一并收敛，改由单一事实源派生）。
- 接入 Maven Central：新增 Central 发布通道（Portal 上传端点、签名、POM 完整字段），与现有内网 Nexus 发布并行双仓发布；**开发线快照同时发布到 Central 快照仓**（账号侧 Deploy Snapshots 已开启，2026-10-02 决策修订；快照在 Central 有自动过期清理，不作归档承诺，权威归档仍是内网仓）。
- POM 许可声明维持 Apache License 2.0（2026-10-02 全仓由 Mulan PSL v2 切换为 Apache License 2.0 后，声明与源文件头一致），补齐 Central 强制的 `name`、`description`、`url`、`licenses`、`developers`、`scm` 字段。
- 发布构建产物扩展为四件套：主 jar、sources jar、javadoc jar、POM（另附签名文件），javadoc 生成需通过构建工具链的 doclint 严格校验或按决定豁免并记录。
- `com.tny.game.gradle.plugin`（`tny-game-doc-gradle` 模块）本次不进入 Central；其插件 id 与组号不同步的既有形态不受影响。
- 发布门禁扩展一项前置校验：正式版本进入 Central 通道前核对命名空间验证状态与签名可用性，防止半成品坐标泄漏到不可撤销的公共仓库。
- 发布流程文档与 `/tny:release` 命令文本同步补充 Central 步骤；GitHub Actions 增加 Central 发布工作流（采用 Central Portal Gradle 接入方式，参考 central.sonatype.org 发布文档与 GitHub 官方 Java 包发布指南中 release 事件触发的工作流骨架）。

## Capabilities

### New Capabilities

- `central-publishing`：Maven Central 发布通道的对外契约——组号与命名空间验证、正式版准入条件（裸号加远端标签双门禁）、四件套加签名的产物完整性、Central 与内网仓库的并行发布语义（一侧失败不污染另一侧账本）。

### Modified Capabilities

- `release-versioning`：需求"发布标签是正式版发布的前置存证"的校验范围扩展——正式版发布任务现在面向两个目标仓库，标签存证校验 MUST 在推送至任一仓库前完成（原语义不变，仅覆盖新通道；版本号派生、快照稳定性、白名单、黑名单四条需求不受影响）。

## Impact

- 构建脚本：`build.gradle:88`（组号单一事实源改造，43 个模块首行的硬编码组声明随之删除或改派生）、`gradle/publications.gradle`（POM 字段、Central 仓库与凭据、签名配置、门禁前置校验）、`gradle/publish.gradle`（凭据属性扩展）、`gradle/release.gradle`（发布步骤说明联动）。
- 全部已发布模块（50 余个 `tny-game-*` 与 `tny-game-starter-*`）的制品坐标组号变更；jar 清单 `Implementation-Version` 不受影响（版本串形态已由上一变更固化为裸号加快照两态）。
- 下游业务工程：升级时依赖坐标前缀改写（一次性机械操作，无 API 变化）；`docs/site/index.html:174-177` 与 README 中的示例坐标同步更新。
- 外部依赖：新增 Central 发布插件（选型见 design D3）；新增 GitHub Actions secret（Central token、GPG 签名密钥）。
- CI：`.github/workflows/` 新增或扩展发布工作流；bench 双通道与测试工程不受影响。
- 不受影响：Java 包名、全部源码 import、SPI 资源文件中的类全限定名、Gradle 插件 id、`gradle/released-legacy.txt` 黑名单（其语义按版本段而非组号，无需改写）。
