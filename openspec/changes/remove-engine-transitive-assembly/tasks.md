# Tasks

## 1. 装配探针（设计 Risks 第一条前置验证）

- [x] 1.1 写最小集成探针验证"引擎 jar 进入运行时且被框架扫描覆盖即注册生效"在真实装配路径成立：在既有测试栈（net 测试或 integration-test）构造带 expr-groovy 依赖的扫描装配，经 `UnitLoader.getLoader(ExprHolderFactory.class)` 解析取得工厂。验证方式：探针取得非空工厂并求值一条样例表达式成功；探针不成立即停止后续任务并回报修订规格差量装配条款（P12）。

## 2. 测试先行：引擎装配合同测试（涉及公共行为，按账本规则排在实现任务之前）

- [x] 2.1 在 `tny-game-net/src/test/java/com/tny/game/net/command/dispatcher/` 新增 JUnit 5 测试类（类名以 Test 结尾，包路径与被测类一致），覆盖规格三条路径：显式传入工厂优先于单元解析；未装配引擎时属性表达式求值报"未装配表达式引擎"且文案含配置位置；装配单一引擎后求值结果与既有 Groovy 行为逐值一致。验证方式：本组测试在改造前允许红（装配路径断言尚未实现），但编译必须过；改造后（第 4 组）全绿。
- [x] 2.2 运行 `./gradlew :tny-game-net:compileTestJava` 确认测试编译通过并把当前红绿状态记入变更目录。

## 3. 注册标注（行为不变的一批，可独立提交）

- [x] 3.1 `tny-game-expr/src/main/java/com/tny/game/expr/ExprHolderFactory.java` 契约接口标注 `@UnitInterface`（先例 `MessageFactory.java:25`），import 经简单名。验证方式：`./gradlew :tny-game-expr:build` 通过，全仓编译无新增告警。
- [x] 3.2 `tny-game-expr-groovy` 的 `GroovyExprHolderFactory`（第 29 行类）与 `tny-game-expr-mvel` 的 `MvelExprHolderFactory`、`MvelTemplateHolderFactory` 按框架单元实现先例补注册声明。验证方式：`./gradlew :tny-game-expr-groovy:test :tny-game-expr-mvel:test` 通过，且 1.1 探针在标注后仍绿。

## 4. 拆除 net 焊点并删引擎边（BREAKING 主体）

- [x] 4.1 替换两处直接实例化：`CommandPluginHolder.java:62` 的 null 兜底与 `DefaultMessageDispatcher.java:32` 短构造器改为经 `UnitLoader` 解析（恰一个注册用之、零个在首次求值报"未装配表达式引擎"附配置位置与空清单、多个报冲突附清单），显式传入参数路径不动；删除 `tny-game-net/build.gradle` 的 `implementation project(':tny-game-expr-groovy')` 与两处 `com.tny.game.expr.groovy` import。验证方式：2.1 的装配合同测试全绿；`./gradlew :tny-game-net:test :tny-game-rpc:test :tny-game-doc:test` 通过；net 发布 POM 与模块元数据任何域无 expr-groovy 条目（`publishToMavenLocal` 后核对并留档）。
- [x] 4.2 运行 `./gradlew :tny-game-net:test :tny-game-net-netty4:test :tny-game-starter-net-netty4:test` 并确认通过（属性表达式经 starter 装配路径的下游回归），结果摘要记入变更目录。

## 5. basics 声明补全与引擎边降级

- [x] 5.1 `tny-game-basics/build.gradle` 补 `api project(':tny-game-common-reflect')` 直达边（注释登记 `ItemModelProxyHandler` 公开签名实现 cglib 接口的依据），引擎边降为 `testImplementation`；五个测试文件的直接实例化不改（测试源集自装配）。验证方式：`./gradlew :tny-game-basics:test :tny-game-oplog:test :tny-game-starter-basics:test` 通过；basics 发布 POM 全依赖域无 expr-groovy 条目。

## 6. 集成测试栈显式装配

- [x] 6.1 `tny-game-integration-test` 的 `NetIntegrationHarness.java` 改为显式传入 `GroovyExprHolderFactory`（或经单元解析断言），`tny-game-integration-test/build.gradle` 补 `integrationImplementation project(':tny-game-expr-groovy')`（net 不再传递供给的运行域补偿）。验证方式：`./gradlew :tny-game-integration-test:integrationTest --rerun-tasks` 中含表达式求值路径的用例通过（按 docker 标签隔离口径执行）。

## 7. 收口回归与判据留档

- [x] 7.1 全仓 `./gradlew clean build` 通过（守卫与既有门禁全程在位），无工程因引擎拆除报红。
- [x] 7.2 发布面零引擎总核对：net、basics 的 `publishToMavenLocal` 产物 POM 与模块元数据全依赖域不含 expr-groovy/expr-mvel，改造前后对照件存变更目录 baseline/（按 config 零差异口径抓取）。
- [x] 7.3 汇总各组测试摘要与探针留痕成 verification-notes.md 存入变更目录，对外迁移口径（显式注入或装配声明两行指引）写入注记供发布说明引用。
