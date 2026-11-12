# Design

## Context

见 proposal.md 的 Why。机制现状经实测：`UnitLoader.getLoader(契约.class).getAllUnits()` 与具名解析 `checkUnit(name)` 是框架既有装配 API（`DefaultMessageDispatcher.java:47-49` 同文件已在用，`NetBootstrap.java:56` 有具名先例）；契约接口的注册形态为 `@UnitInterface` 标注（`MessageFactory.java:25` 等六处先例）；引擎实现是继承骨架（`GroovyExprHolderFactory` 继承 expr-jsr223 的 `ScriptExprHolderFactory`）。前一册留下的两处焊点：`CommandPluginHolder.java:62` 的 null 兜底与 `DefaultMessageDispatcher.java:32` 短构造器直接实例化 Groovy 工厂；basics 的引擎边降 testImplementation 被实验证伪——其公开类 `ItemModelProxyHandler`（public，实现 cglib `MethodInterceptor`）与 `LoadableModelManager.java:24` 的 reflect 包引用此前全靠引擎传递链白拿。仓内短构造调用点实测仅一处（integration-test 的 `NetIntegrationHarness.java`）；net 中 `ExprHolderFactory` 契约类型出现在六个文件的签名上（ControllerHolder、Method/ClassControllerHolder、CommandPluginHolder、Base/DefaultMessageDispatcher），这些签名不动。爆炸半径核对记录：codegraph 索引对上述 net 源文件无符号覆盖（查询返回空），影响面以全仓 grep 实证替代。

约束：`docs/design/object-design-principles.md` 与 `docs/design/patterns-in-tny.md` 按册要求通读；模式卷决策表"新增一个实现模块 → 策略 + SPI 注册（expr 家族先例）""抽象层 MUST NOT 改动（P3）"直接适用。

## Goals / Non-Goals

**Goals:**
- 兑现规格差量 `expression-engine-assembly` 三条需求：引擎由装配方供给、随装配自注册、发布面零引擎携带。
- net 与 basics 的源码与 POM 彻底摆脱具体引擎引用；仓内唯一短构造调用点与五个 basics 测试文件按新装配合同就位。

**Non-Goals:**
- 不改 `ExprHolderFactory` 契约接口本身的方法（`ExprHolderFactory.java:22` 起）——P3 面向扩展开放：新增能力靠注册，不靠改契约。
- 不做引擎求值语义的任何变化（求值结果逐值等值是本册验收红线之一）。
- 不触碰 starter 线已有的引擎 api 声明形态（前一册刚固化，本册只补"注册生效"验证）。
- 不在本册处理 mvel 引擎在 starter-basics 上的既有装配（前一册修正，保持）。

## Decisions

**D1 引擎解析改走既有单元装配机制：`ExprHolderFactory` 契约标注 `@UnitInterface`，引擎实现类标注单元注册，求值位置经 `UnitLoader` 解析取得工厂。（P2 依赖倒置、模式卷 M1 先例优先——`MessageFactory`/`ContactFactory` 六处同形先例；P3 契约接口方法零改动）**
显式传入工厂的既有构造参数保留为最高优先（现状即如此）；未显式传入时（CommandPluginHolder 的 null 兜底位与 DefaultMessageDispatcher 短构造器）不再实例化具体引擎，改经 `UnitLoader.getLoader(ExprHolderFactory.class)` 解析：恰有一个注册引擎则用之，零个在首次求值时报"未装配表达式引擎"并附配置位置，多个报冲突错误指明已注册清单。备选一并否决：删除短构造器或 null 语义——直接打断外部业务工程既有构造代码，迁移代价大于收益且无必要（参数位本来就是契约形态）。备选二否决：引入 ServiceLoader 式默认工厂注册点——框架已有 `@UnitInterface` 微框架先例且 `NetBootstrap.java:56` 的具名解析正是"starter 决定用哪个引擎"的现成通道，另起第二套装配机制违反 M1 与 P10。
时序细节：解析推迟到首次求值（lazy），构造器不做引擎解析——避免不使用表达式的应用因未装配引擎而启动失败，错误只落在真正求值的路径上（P11 最小化合同破坏面）。

**D2 basics 先补 `api project(':tny-game-common-reflect')` 直达边，再把引擎边降为 testImplementation。（P9 迪米特与信息隐藏的镜像原则：用了什么就声明什么；P13 每条边可验证）**
reflect 边取 api 的依据是公开的：`ItemModelProxyHandler` 是 public 类且实现 cglib 接口，cglib 类型在 basics 公开签名上；此前该可见性经 basics → expr-groovy → expr-jsr223（`tny-game-expr-jsr223/build.gradle:7`）→ common-lifecycle（`tny-game-common-lifecycle/build.gradle:7` 的 api common-reflect）→ common-reflect（api cglib-nodep）四级传递白拿。五个测试文件的 `new GroovyExprHolderFactory()` 不改——测试源集自己就是装配方，testImplementation 类路径含引擎，直 new 合法且比经单元解析更直白。备选否决：测试文件改经 UnitLoader 取工厂——为一处测试可见性把装配机制拖进单测，超出需要（P10）。

**D3 引擎注册标注打在具体工厂类上（2026-10-04 实施前修订：原案"契约标注 @UnitInterface"被事实证伪——`tny-game-expr` 契约模块零依赖，标注契约需为注解新增 expr 到 common-lifecycle 的依赖边并给 expr 的发布 POM 增依赖，且与"契约文件不动"的 Non-Goal 最严读法冲突）。**
`@UnitInterface` 与 `@Unit(unitInterfaces = ExprHolderFactory.class)` 同打在具体引擎工厂类上（expr-groovy 的 `GroovyExprHolderFactory`；expr-mvel 的 `MvelExpressionHolderFactory`——`MvelExprHolderFactory` 是抽象骨架不打，避免依赖注解经继承传递的不确定语义；`MvelTemplateHolderFactory` 经实施核查确认只是同引擎的模板渲染变体（覆写 preProcess 的子类），不是独立装配选项，打标注会使装配 mvel 即产生两个契约单元自我冲突，故不打，需要模板变体的调用方显式构造）。机制依据实测：`UnitLoadInitiator:39` 的 `getBeansWithAnnotation(@UnitInterface)` 经 bean 具体类上的直标注解即可发现；`UnitLoader.forEachLoader:72-81` 原生支持按 `@Unit.unitInterfaces` 参数把实例注册进契约键 loader，注解 javadoc"这些 Class 必须是 Class 继承或实现的"表明该通道即为契约不带注解的场景预备。两引擎模块对注解包的可见性零成本：expr-mvel 已有 `api common-lifecycle`（build.gradle:6），expr-groovy 经 expr-jsr223 的 api 链（expr-jsr223/build.gradle:7）。（P1 装配方向"实现 → 抽象"；P9 依赖最小面）
引擎 jar 进入运行时且被框架扫描覆盖即注册生效；装配方（starter 或业务工程）通过带哪个引擎 jar 决定生效引擎，与既有 codec 家族装配惯例同形。具名多引擎场景的优先级由 `NetBootstrap` 式配置指定承接（本册不新增配置键，冲突时显式报错即可——规格 Requirement 2 的确定性条款）。

**D4 integration-test 的 harness 改为显式传入 Groovy 工厂。（装配者显式声明——与前一册三处连带修正同一原则）**
`NetIntegrationHarness.java` 是全仓唯一短构造调用点；集成测试栈本就是装配位，显式注入消除对单元解析时序的依赖，IT 模块需补 `integrationImplementation project(':tny-game-expr-groovy')`（net 收缩后运行类路径不再含引擎，IT 求值必须自带——前一册实测 IT 堆栈经 net 传递获得引擎的现状在本册终结）。

**D5 发布面核对判据固化为任务：`tny-game-net` 与 `tny-game-basics` 的 `publishToMavenLocal` 产物 POM 与 Gradle 模块元数据中，指向 expr-groovy/expr-mvel 的条目在任何域都不存在。（P13）**
以改造前后元数据对照为验收件，按 config 零差异口径的抓取规范留存变更目录。

## Compatibility Impact

本册是 P11 三问的"下游能否无感知升级"答案为否的对外合同变化（proposal 已标 BREAKING）：
- 源码兼容：net 与 basics 的全部公开签名不变（构造器参数、类型、方法零改动）；纯 API 使用者无感知。
- 行为兼容（运行时）：(1) 此前"classpath 有 Groovy 就静默能用 Groovy"的应用，升级后仍装配 starter-net-netty4 路径的（其 api 边携带引擎）行为不变——但生效前提从"类路径命中"变为"类路径命中且经框架扫描注册"，扫描覆盖是实施验证重点（见 Risks 第一条）；不经框架扫描的裸用场景（库式引用、手工 new 短构造器、不跑生命周期）属性表达式求值将从"能用 Groovy"变为"报未装配引擎"，属本册成文的行为收紧，迁移口径：显式传入工厂（一行改动）或补装配声明。(2) 此前经 basics 传递获得 Groovy 运行时但不做表达式求值的工程零感知；求值者按需求 3 场景的迁移口径处理。(3) 求值结果语义逐值不变（Non-Goal 红线）。
- 报文兼容：不涉及报文与序列化格式（引擎只作用于配置期属性表达式），规格差量以迁移兼容场景覆盖升级路径。
- 发布 POM：net 与 basics 的全依赖域删除引擎条目（compile 已在前一册删除，本册删除 runtime 域残余）。

## Risks / Trade-offs

- [框架扫描是否覆盖引擎实现包是本册最大不确定性：若不覆盖，"引入 jar 即注册生效"不成立，所有求值点报未装配] → 任务序列第一位做最小集成探针实测（带 starter 的既有 IT/测试栈直接验证），探针不过则回到本差量修订装配条款，不带病推进。
- [多引擎注册的冲突面比现状（永远 Groovy 兜底）更严] → 规格已把"确定且可诊断"写成需求；实施冲突报错含已注册清单，用户可见即迁就显式配置；仓内实测 codec/net 装配面不存在双引擎共存工程。
- [basics 测试改 testImplementation 后 `:tny-game-basics:test` 依赖引擎直 new 的五个文件仍绿，但 oplog 等下游测试若隐式依赖 basics 运行域引擎会报红] → 全仓测试面回归（含 integration-test 求值冒烟）排在收口组，报红即停并回报，不静默扩大边集。
- [懒解析（D1 时序细节）使错误从构造期推迟到首次求值，排障距离变长] → 报错文案带配置位置与"已注册的引擎清单"（空清单即未装配），可诊断性由规格错误路径场景背书。

## Migration Plan

顺序：1 装配探针（验证扫描覆盖与解析通道，失败即停回规格）→ 2 契约与引擎注册标注（expr 三模块小改，行为暂不变——net 仍兜底，可独立提交）→ 3 net 两焊点改单元解析并删引擎边（含 CommandPluginHolder/BaseMessageDispatcher 兜底路径替换）→ 4 basics 补 reflect 边降引擎边 → 5 IT harness 显式注入与 IT 模块引擎边 → 6 收口回归（全仓编译、net/basics/rpc/doc/starter 测试、IT 求值冒烟、发布元数据零引擎判据）。回滚：3、4 各自独立 revert（标注先行不破坏行为，删除边与焊点同组原子）；外部迁移指引随发布说明出稿。

## Open Questions

- 冲突引擎报错文案是否附"用哪个配置键指定引擎"的前瞻指引——待装配探针实测具名解析键位后在实施中定稿，不影响规格与任务分解。
