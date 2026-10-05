# central-publishing Specification

## Purpose

定义 TnyFramework 向 Maven Central 发布构件的行为契约：组号命名空间的事实源与迁移纪律、正式版进入公共仓库的准入完整性、Central 与内网仓库并行发布时的独立性，以及快照与 Gradle 插件构件的排除边界。供所有涉及发布通道、制品坐标或下游依赖文档的变更引用。

## Requirements

### Requirement: 制品组号以单一事实源派生
全部发布构件的 Maven 组号 SHALL 由构建系统的单一声明点派生，其值为 `com.tnydev.game`；任何模块 MUST NOT 独立硬编码组号，声明点之外的组号出现即构建期校验判红。已迁移到本组号的版本号 MUST NOT 再以旧组号 `com.tny.game` 发布，防止同一版本的双组号构件并存导致下游依赖解析歧义。

#### Scenario: 全仓构件组号一致
- **WHEN** 在任一合法发布分支执行构建并检查所有待发布构件的坐标
- **THEN** 每个构件的组号均为 `com.tnydev.game`，且构建输出中不存在第二处组号声明来源

#### Scenario: 模块硬编码组号被判红
- **WHEN** 某模块的构建脚本自行声明了与单一事实源不同的组号并执行校验
- **THEN** 构建失败，错误信息指出违例模块与两处组号的具体值，发布被拒绝

### Requirement: Central 正式版准入完整性
构件发布至 Maven Central 前，构建系统 MUST 校验发布件完整性：POM 具备项目名、描述、主页、与事实一致的许可证、开发者、源码仓库地址六类必填字段，且附随主 jar、sources jar、javadoc jar 与有效数字签名；任一缺失时 Central 推送 MUST 被拒绝。POM 许可证 MUST 与源文件版权头所示许可证一致。

#### Scenario: 完整发布件推送成功
- **WHEN** 正式版本的构件四件套与签名齐备且 POM 必填字段完整时执行 Central 发布
- **THEN** 推送被接受，构件可在 Central 仓库按该坐标检索到

#### Scenario: 缺签名或缺附属构件被前置拒绝
- **WHEN** javadoc 构件未生成或签名缺失时执行 Central 发布
- **THEN** 发布任务在进入网络传输前即失败，错误信息逐项列出缺失的必需件，内网仓库不受本次失败影响

### Requirement: 双仓并行发布相互独立
正式版本 SHALL 同时发布至 Maven Central 与内网发布仓；两个通道的推送 MUST 独立提交与独立报告结果——一个通道成功不依赖另一通道，一个通道失败 MUST NOT 撤回或污染已成功通道的已发布内容，失败通道的错误信息 MUST 指明是哪个通道以及可否安全重试。

#### Scenario: 一侧失败另一侧不受累
- **WHEN** Central 通道因网络故障失败而内网通道已成功
- **THEN** 构建报告内网成功与 Central 失败两项事实，内网构件保持已发布状态，重试仅针对 Central 通道

#### Scenario: 双通道同坐标一致性
- **WHEN** 一次正式版发布在两通道均成功
- **THEN** 两通道中同一模块的版本号、组号、构件清单完全一致，消费方任选其一解析结果等价

### Requirement: 快照构件发布到 Central 快照仓
开发线产生的快照构件 SHALL 同时发布到内网快照仓与 Maven Central 快照仓（双通道独立，语义与正式版双仓一致）；Central 快照通道仅在开发线形态（`<主>.<次>.x`）参与，发布分支形态执行快照上传时 MUST 被拒绝。正式版（裸号）MUST NOT 走快照通道。

#### Scenario: 快照双通道分发
- **WHEN** 开发线执行快照发布（含 Central 快照上传任务）且 Central 凭据有效
- **THEN** 同名快照坐标同时可在内网快照仓与 Central 快照仓检索到，一侧失败不影响另一侧已发布内容

#### Scenario: 发布分支误走快照通道被拒绝
- **WHEN** 在发布分支形态（`<主>.<次>.<补丁>.release`）执行 Central 快照上传
- **THEN** 请求被拒绝，错误信息说明快照通道仅接受开发线形态，正式版必须走 release 发布通道

### Requirement: Gradle 插件构件本次不入 Central
`tny-game-doc-gradle` 模块的构件 MUST NOT 进入 Maven Central 发布；其既有发布通道（内网仓库与 Gradle 插件 id `com.tny.game` 的坐标体系）MUST 保持原组号不变，与框架主组号迁移互不牵连。

#### Scenario: 插件构件走原通道
- **WHEN** 全仓执行正式版发布
- **THEN** 该插件模块仅按原有内网通道以原组号发布，不出现在 Central 待发布清单中

#### Scenario: 插件 id 不受组号迁移牵连
- **WHEN** 使用者以既有的 `com.tny.game` 插件 id 在构建脚本中应用该插件
- **THEN** 插件解析与本次组号迁移前行为一致，不因主组号变更而报错
