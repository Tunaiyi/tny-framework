# Design

## Context

动机与原则见 proposal.md - Why；验收基准成文于规格差量（新增一条需求三场景）。六处现场违例与对应改法一一对应见下 D1-D5。现场事实补充：settings.gradle 第 115 行注释已声明"基准设施刻意不以 tny-game- 开头，不落入发布装配线"（optimize-net-hot-path D1）——即"靠命名约定表达成员归属"本就是既有设计，本变更只是把共享脚本从"名字点杀"收回"约定判定"，与该先例同向。

爆炸半径核查（按设计规则记录）：codegraph 不覆盖构建脚本，无适用符号；消费面五处逐一列明并以零差异套件锁死——发布线成员（tasks --all 与 POM 面）、BOM 约束清单（bom POM 逐字节）、Central 聚合集与完整性检查（centralCheck 探针与产物面）、组号对账（违例探针红/绿对称）、测试类路径（namnspace 等模块 dependencies 报表 + testCompileClasspath 抽看）。

两卷检索（M1 条款要求）：自报登记的形状在框架运行时早有先例——`@UnitInterface` 声明即注册、装配由扫描撮合（模式卷第一节"微框架"行），tny.demo-app/publishTagCheckMemo 是其构建期同族；命名约定判定则是需求五"清单分组即架构目录"的镜像使用（名字本身携带分类信息是仓库明文惯例）。本案不引入任何第三种机制。

## Goals / Non-Goals

**Goals:**

- 六处精确名清零，共享脚本进入"新模块零改动"形态；前瞻探针各证一次（第二个集成测试模块自动归类、不发布合同对新登记成员生效）。
- 现状行为逐字节不变（零差异红线全套）。

**Non-Goals:**

- settings.gradle include 清单不动（用户拍板：注册行为，需求五地位不变）。
- 模块自有构建文件中的兄弟工程引用不动（声明面，非共享脚本）。
- 不重命名任何模块、不改命名约定本身（tny-game- 前缀与四类后缀沿用现状）。
- 不追求注释去名（文字规则要求来由给全名）。

## Decisions

**D1 成员谓词全部后缀化，约定锚点入 settings 注释。** 根 javaProjects 排除条件改 `!name.endsWith('-bom') && !name.endsWith('-integration-test')`（`-gradle` 已是后缀）；tny.bom-platform 与 tny.central 的对应判断同改（聚合成员 = javaProjects ∪ moduleProjects 中 `-bom` 结尾者；完整性检查对 `-bom` 成员跳过构件清单要求）。当前成员下谓词逐条等值：仓内恰有一个 -bom 与一个 -integration-test。settings.gradle 在模块清单区补一段注释：五类命名约定（`tny-game-` 前缀 = 框架模块、`-bom`、`-gradle`、`-integration-test`、`-tester`、刻意不带前缀 = 不发布设施）的定义与"共享脚本据此判定成员"的指认，成为约定的权威文本。依据：P3（新增成员是数据不是修改）、需求五（清单与命名同源，此处是其逻辑延伸）。被否决备选：成员清单集中到一个 ext 白名单——否决：名字只是搬家，仍是枚举。

**D2 组号例外分支并入既有线。** tny.project-checks 的例外判断由 `m == project(':tny-game-doc-gradle')` 改 `m.name.endsWith('-gradle')`——与 gradleProjects 线定义同一个模式，两处天然一致；doc-gradle 模块文件内自报组号的注释本就引用该 spec 出处，无需动。

**D3 零发布合同改自报登记，并顺带完成两处职责归位（用户定稿）。** tny.unpublished 定义为"本工程不发布"的完整语义：登记根工程不发布清单（键名与消费侧注释互指，模式同 tnyDemoBlueprints）+ 禁用本工程 publish 任务（原 tny.it-demo-isolation 首段整体迁入）；tny-bench 与 tny-game-integration-test 各引入该插件——前者无发布插件挂身、禁用遍历为空集天然无副作用，后者行为逐字不变；余下的隔离接线文件改名 tny.demo-isolation，单一职责名实相符。；tny.project-checks 合同改为两条遍历：登记工程不得命中 moduleProjects 谓词（现文案"命中装配线谓词"通用化含违例路径）、任何 moduleProjects 成员不得声明对登记工程的 project 依赖（现"不得依赖 bench"的泛化）。现状等价：登记清单 = {tny-bench}，覆盖面与旧合同逐条相同；报错信息从"点名句"变为"列出双方路径"，违例判定集合不变。依据：P2（合同依赖登记属性不依赖个体名）、@UnitInterface 先例。被否决备选：合同保留点名并加 TODO——否决：用户原则明文禁名。
- 语义注意（写入设计即文档）：未来若再加第二个不发布设施，它必须贴 tny.unpublished 才受合同保护——"忘记贴标记"的新设施等同普通工具工程（不在 tny-game- 前缀内、不被发布，依赖检查天然不触发），风险面与现状一致，无静默错误分类。

**D4 公共夹具改模式检索。** tny.java-module 的 testImplementation 由点名改为 `rootProject.moduleProjects` 中 `-tester` 后缀的全部命中（当前唯一）。等价性：同工程同 notation 对象；未来多夹具自动同挂（这正是原则想要的方向）。评估时机：java 线工程配置期读 root ext 集合按 name 过滤，不依赖被检索工程完成评估（name 在 include 即知）。

**D5 验收以"前瞻模拟"补"现状零差异"。** 零差异套件之外，加两个原则性探针：其一，临时创建最小 `tny-game-probe-integration-test`（只有 include + 空构建文件），验证 `tasks --all` 中其出现在集成测试类通道、不进发布 publication、BOM 约束不含它，随后整体删除；其二，临时给某发布模块加对 tny-bench 的依赖，零发布合同执行前报红并列出双方路径，删除恢复。两探针结果入 verification-notes。

## Risks / Trade-offs

- [后缀约定被破坏（有人建 `tny-game-bomX`）] → 需求文本已把约定成文于 settings 注释与规格；对账与聚合按后缀判定，误名模块会被当普通发布线成员——错误可见（POM/聚合清单当场多一项），不是静默。
- [moduleProjects 在 root ext 定义、其谓词先于子工程评估可用] → name 判定只依赖 include 结果，成立。
- [tester 模式检索把 -tester 自己包含] → 现状点名同样如此（tny-game-tester 在 javaProjects 内，自挂 testImplementation 自我依赖为既有可运行行为），改法不引入差异；若 Gradle 对自我 project 依赖在 testImplementation 报环，现状早已报（实际不报，端到端全绿为证）。
- [unpublished 登记表在 checks 遍历时机] → 与 blueprint 清单同法：合同执行在 gradle.projectsEvaluated（该处本就是评估完再查的既有语义），登记发生在更早的 apply 时点，读取必完整。
- [模拟探针模块误残留] → 探针后立即删除并复跑 `projects` 绿 + `git status` 干净截图入 notes。

## Migration Plan

单原子批次：五文件谓词/登记改造 + tny.unpublished 新增 + bench 标记引入 + settings 注释锚点 → 零差异套件（tasks --all、四报表、BOM/net POM、全量构建、centralCheck、组号违例探针、零发布探针新旧文案对照）→ D5 两枚前瞻探针 → 记 verification-notes。回滚为七文件整体 revert。

## 实施修正注记

- D3 合同第一条断言的谓词由 moduleProjects 修正为 javaProjects：实施探针即证伪原案——tny-game-integration-test 按命名合法属于 moduleProjects（"登记不发布"与"属于框架模块集合"并不矛盾），其不发布的准确判据是不进入发布线 javaProjects；对 tny-bench 场景（旧合同目标）两判据同样触发（冒充前缀者必然落入发布线，除非再冒充排除后缀，而排除后缀各有专义），覆盖面等价。proposal"集成测试模块本就被装配线谓词排除在判定之外"一句按此理解成立（被排除的是发布线）。

## Open Questions

无。
