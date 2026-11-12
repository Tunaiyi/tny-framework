# Proposal

## Why

用户定下的构建原则：新增一个项目模块时，共享构建脚本（根 build.gradle 与 buildSrc 全部插件）不应需要任何改动；共享脚本里不应出现具体项目名字。现场盘点六处违例：根脚本成员谓词以精确名排除 tny-game-bom 与 tny-game-integration-test、tny.bom-platform 以精确名排除集成测试模块、tny.central 两处精确引用 tny-game-bom、tny.project-checks 以精确名处理 tny-game-doc-gradle 的组号例外并以精确名钉死 tny-bench 的零发布合同、tny.java-module 以精确名引用 tny-game-tester。这些位置每一个都对应一个真实的将来断点：加第二个 BOM、第二个集成测试模块、第二个插件模块或第二个不发布设施时，都得回头改共享脚本，且改动点分散在多个文件、无编译期提示——漏改一处就是静默的错误分类（例如第二个 BOM 被当成普通发布模块挂进 java 线）。命名后缀模式（-bom、-gradle、-integration-test、-tester、tny-game- 前缀）与自报登记（tny.demo-app 的蓝图清单、发布门禁的根工程缓存已开先例）两条既有通道，恰好覆盖全部六处，无需新机制。

## What Changes

- 根脚本成员谓词改后缀模式：javaProjects 的排除条件由两个精确名改为 `-bom` 与 `-integration-test` 后缀判定（当前成员下结果逐条相同）；命名约定的定义锚点写进 settings.gradle 分组注释（那里本来就是模块清单的权威面）。
- tny.bom-platform 排除、tny.central 的 BOM 聚合成员与完整性检查跳过，同步改 `-bom`/`-integration-test` 后缀判定。
- tny.project-checks 组号例外分支改 `-gradle` 后缀判定（与 gradleProjects 线同一规则，两处天然同步）；tny-bench 零发布合同改自报登记：新增标记插件 tny.unpublished，语义为"本工程不发布"——应用即登记进根工程不发布清单（模式与 tny.demo-app 同构），并禁用本工程的 publish 任务；tny-bench 与 tny-game-integration-test 各自应用（后者的禁用段从此前的 tny.it-demo-isolation 中迁出，该文件因此改名 tny.demo-isolation、只保留演示应用子进程隔离接线，名实相符）；对账改为"任何工程不得依赖登记为不发布的工程；登记工程不得命中发布线谓词"——现状下清单只有 tny-bench，合同覆盖面等价（集成测试模块本就被装配线谓词排除在"工程"判定之外，登记不改变任何现有判定结果），报错文案通用化并列出违例双方路径。
- tny.java-module 的公共测试夹具引用改 `-tester` 后缀模式检索（当前唯一命中即 tny-game-tester，声明面等价）。
- 注释与文档中的模块名不受禁（文字规则要求来由引用必须给完整名称），禁令针对的是代码里的成员判定与跨工程引用。
- settings.gradle 的 include 清单保持显式（用户拍板：那是模块注册行为，且需求五"清单即架构目录"的地位不动）；模块自己的构建文件引用自己或邻居不在禁令内（禁令只针对共享脚本）。
- 行为零差异：任务图、依赖解析、发布物、POM 与现状逐字节一致；同时新增两个前瞻探针证明原则成立（模拟第二个集成测试模块自动归类、加非法依赖触发登记合同报红）。

## Capabilities

### New Capabilities

无新增能力：本变更全部落在 gradle-build-style 的管辖主题（构建脚本书写形态）内。

### Modified Capabilities

- `gradle-build-style`：新增一条需求"共享构建脚本禁止枚举具体工程名，成员判定用命名约定或自报登记"，含正反三个场景（新增类别成员零改动自动归类；共享脚本写死精确名被判违例；自报登记通道用于无法模式化的个体属性）。这是把用户口头原则升格为可验收的规格条文，不是对既有八条的修改。

## Impact

- **受影响文件**：build.gradle（谓词）、buildSrc 下 tny.bom-platform / tny.central / tny.project-checks / tny.java-module 四个插件、新增 tny.unpublished.gradle、tny.it-demo-isolation.gradle 改名 tny.demo-isolation.gradle（发布禁用段迁出）、tny-bench/build.gradle 与 tny-game-integration-test/build.gradle 的引入行调整、settings.gradle 仅注释补约定锚点。
- **消费面**：发布链、门禁、聚合、对账、BOM 约束条目、测试类路径——全部以"现状成员不变"为硬约束，零差异套件逐面验收。
- **下游**：不触构件坐标与内容，无 BREAKING；CI 不变。
- **规格账本**：归档时 gradle-build-style 主账本由八条需求变为九条。
