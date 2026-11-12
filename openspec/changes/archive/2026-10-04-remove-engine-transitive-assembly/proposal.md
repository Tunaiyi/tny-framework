# Proposal

## Why

前一册（fix-dependency-version-governance）把引擎实现模块从发布 POM 的 compile 域收缩出去后，仓内仍有两处把脚本引擎焊死在网络核心与领域模型库里：`tny-game-net` 的主源码在两处直接实例化 Groovy 引擎（`CommandPluginHolder.java:62` 的 null 兜底与 `DefaultMessageDispatcher.java:32` 的短构造器），`tny-game-basics` 则把 Groovy 引擎保留在 implementation 依赖里。这两处形态意味着：引擎选择权不在装配方手里，net 换引擎要改核心代码，basics 的外部消费者被 POM 的 runtime 域强制供给 Groovy。实测坐实修复前提已具备——`BaseMessageDispatcher` 的全参构造本来就接收 `ExprHolderFactory` 契约参数，net 模块已有六处 `@UnitInterface` 微框架注册先例可抄，仓内短构造调用点仅集成测试一处，且 oplog、net-demo、integration-test、benchmark 的主源码零处直接引用引擎类型。

## What Changes

- **BREAKING** `tny-game-net` 移除对 `tny-game-expr-groovy` 的依赖声明（含 `build.gradle` 的 implementation 边与两处源码实例化）：命令插件属性表达式与消息派发的引擎解析改为经框架既有的单元注册机制取得 `ExprHolderFactory` 契约实现；未装配引擎单元的应用在需要求值属性表达式时得到指明"未装配表达式引擎"的启动期或首次求值错误，替代当前静默回退 Groovy 的行为。使用短构造器的既有代码仍可编译，但运行时需要装配方提供引擎单元（仓内唯一调用点 `NetIntegrationHarness.java` 同步改为显式传入引擎工厂）。
- `tny-game-expr-groovy` 与 `tny-game-expr-mvel` 两个引擎实现模块为各自的 `ExprHolderFactory` 实现补单元注册声明（`@Unit` 形态，先例为 `MessageFactory`、`ContactFactory` 等六处），使装配方把引擎 jar 带上并完成扫描即注册生效，装配方向回归"实现依赖契约、装配组合实现"。
- **BREAKING** `tny-game-basics` 的 `tny-game-expr-groovy` 边从 implementation 降为 testImplementation：basics 主源码零处引用引擎类型（仅测试源码五处直接实例化，本册改为经单元解析取得或显式注入），其发布 POM 的 runtime 域不再携带 Groovy 引擎。外部业务工程若依赖此前经 basics 传递获得的 Groovy 运行时，需自行声明引擎依赖或改用 starter 装配。
- `tny-game-basics` 补 `api project(':tny-game-common-reflect')` 直达边：其公开类 `ItemModelProxyHandler` 实现 cglib 的 `MethodInterceptor` 接口、`LoadableModelManager` 使用 `com.tny.game.common.reflect.proxy` 类型，此前这些编译可见性全部靠引擎实现的传递链白拿（basics 到 expr-groovy 到 expr-jsr223 到 common-lifecycle 到 common-reflect）。前一册实施期实验证伪了"basics 改 testImplementation 即可编译"的假设，失败根因正是本条缺失的直声明，本册予以补全。
- `tny-game-starter-net-netty4` 确认并固化引擎装配责任：其自动配置直接引用 `GroovyExprHolderFactory`（`NetAutoConfiguration.java:18-19`），前一册已自声明 `api project(':tny-game-expr-groovy')`，本册在该既有装配点上补单元注册生效所需的扫描覆盖验证，不新增模块依赖。

## Capabilities

### New Capabilities

- `expression-engine-assembly`: 规定表达式引擎的装配与解析合同——网络核心与领域库只依赖 `ExprHolderFactory` 契约，引擎实现由装配方经单元注册机制提供；未装配引擎时的可观察错误行为；引擎 jar 进入运行时类路径后的注册生效条件。

### Modified Capabilities

（无——前一册的 `gradle-build-style` 与 `release-versioning` 需求不受本册影响；构建脚本形态变化属实现层，行为合同由新能力承载）

## Impact

- 源码改动集中于：`tny-game-net`（CommandPluginHolder.java、DefaultMessageDispatcher.java、build.gradle）、`tny-game-expr-groovy` 与 `tny-game-expr-mvel`（工厂实现的注册声明）、`tny-game-basics`（build.gradle 与五个测试文件的引擎取得方式）、`tny-game-integration-test`（NetIntegrationHarness.java 改显式注入）。
- 受影响下游模块与对应 starter：`tny-game-net` 的下游（rpc、doc、net-netty4、net-netty4-codec 系列、net-demo、benchmark、integration-test）经 `tny-game-starter-net-netty4` 装配路径使用引擎，需回归属性表达式求值链路；`tny-game-basics` 的下游（oplog、starter-basics、net-demo）失去经 basics 传递的 Groovy 运行时供给，starter-basics 消费者应用若使用 basics 的表达式求值须自带引擎声明。全仓主源码直接使用表达式引擎契约类型的模块为 net（六处签名文件）；经实测，oplog、net-demo、integration-test、benchmark、actor、loader 的主源码零处直接引用引擎类型，仓内承痛面收敛于 net、basics、integration-test 三处。
- 发布面变化：net 与 basics 的发布 POM 均不再任何域携带 expr-groovy；使用 net 或 basics 但不使用 starter 装配的外部业务工程属源码与运行时双重迁移对象（迁移路径：引入对应引擎模块依赖并完成框架扫描注册，或改用 starter）。
- 验证面：`./gradlew` 全仓编译、net/basics/integration-test 相关测试、属性表达式求值的集成冒烟（含未装配引擎时的错误形态用例）。
