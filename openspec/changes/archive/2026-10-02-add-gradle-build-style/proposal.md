# Proposal

## Why

仓库的构建脚本目前把声明式配置与程序性逻辑混写：根 `build.gradle` 里既有跨项目对账的循环分支，又有旧式任务声明与 spread 批量赋值；`settings.gradle` 里留存着注释掉的模块条目；模块依赖面存在手写坐标与拼接版本号。这类形态让构建脚本读起来像程序而不像配置说明书，未参与变更的读者必须跟随控制流才能还原意图。本会话已把整改要求写入根 `CLAUDE.md`，但规则要长期生效并经得起评审，其正式文本必须纳入 openspec 规格账本，与本仓"外部可观察的行为契约进 spec"的既有纪律一致。

## What Changes

- 新增能力 `gradle-build-style`：以需求加场景的规格形态定义 Gradle 构建脚本的可读性与 DSL 风格契约，覆盖配置阶段与行为分离、惰性 API 形态、版本与坐标单一事实源、Groovy 语言纪律、区块顺序与版面组织、注释来由说明、扫读验收与长度界线、存量违例的触碰即改机制。
- 根 `CLAUDE.md` 的"Gradle 构建脚本规则"一节收缩为指针与执行要点，规则正文的唯一权威出处改为 `openspec/specs/gradle-build-style/spec.md`，避免两处正文随时间漂移。
- 本变更只立规矩，不批量清扫存量违例：存量脚本按"触碰即改"渐进修正，全仓清扫需要另行立项。

## Capabilities

### New Capabilities
- `gradle-build-style`: 约束仓库全部 `.gradle` 文件的书写形态——配置阶段只允许声明式语句，程序性行为限定在任务动作块与 `gradle/` 编排脚本，版本坐标由单一事实源管理，并给出可验收的扫读测试与长度界线及存量违例的渐进修正机制。

### Modified Capabilities

（无。既有能力的规格均不涉及构建脚本书写形态；`release-versioning` 与 `integration-testing` 约束的是发布与验证的行为结果，不约束脚本的文本形态，本变更不改动它们的任何需求。）

## Impact

- 受影响文件：根 `CLAUDE.md`（"Gradle 构建脚本规则"一节改为指针）；全部 `.gradle` 文件成为本能力的约束对象（根 `build.gradle`、`settings.gradle`、`gradle/` 下 10 个辅助脚本、各模块 `build.gradle`），但本变更不重写任何脚本正文，存量违例按触碰即改处理。
- 公共 API 与制品：无影响。变更对象是构建脚本的文本形态，不改动依赖坐标、版本派生逻辑与发布行为；`gradle.properties`、`gradle/dependency.gradle` 的既有事实源结构保持原样并被本能力确立为规范形态。
- 下游消费：无影响。本变更不触及任何模块的公开接口与报文协议，按仓库规则逐类核对后确认无受影响的下游模块与对应 starter 模块。
- 进行中变更的共存：`central-publish-tnydev-group` 与 `migrate-git-calls-to-grgit` 都在改 `gradle/` 脚本，触碰即改只适用于各变更自己改动的区域，本能力不与并行变更做全文件重写竞争。
- 验收方式：以人工评审加静态检索清单执行（grep 级检查旧式 `task ` 声明、spread 赋值、分号结尾行、注释掉的 `include` 行等），不随本变更建设自动 lint 任务（取舍理由见 design.md 决策 D3）。
