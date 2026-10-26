# Tasks

## 1. 行为测试先行（红灯基线）

- [x] 1.1 `tny-game-net/src/test/java/com/tny/game/net/command/plugins/MessageTimeoutCheckerPluginTest.java`：用 `TestMessages`/`RpcInvokeContext` 最小桩构造三场景——请求时间距今超阈值→拦截且返回超时码；未超→放行不拦截；阈值≤0→跳过判定。当前实现下"超期拦截"场景应红（现恒拦截致"未超期"场景也红），记录基线
- [x] 1.2 `tny-game-net/src/test/java/com/tny/game/net/command/plugins/MessageSequenceCheckerPluginTest.java`：已认证会话（`MockNetSession` 持 `AttributeHolder` 属性面）三场景——编号 10 且水位 5→放行且水位推进到 10；再次编号 10→拦截且水位不变；未认证→豁免。当前实现"放行不推进"与"重放不拦截"应红，记录基线
- [x] 1.3 `tny-game-net/src/test/java/com/tny/game/net/command/dispatcher/PluginChainFailClosedTest.java`：桩插件链三场景——前置插件抛运行时异常→`isIntercept` 为真且下一环与业务未触达；后置插件抛异常→响应拦截标志；全部正常→依次执行无拦截。当前 fail-open 下前两场景应红，记录基线
- [x] 1.4 运行三个新测试类，将红/绿矩阵记入本变更目录 `red-baseline.md`（区分"应红"与"碰巧绿"）

## 2. 实现修复

- [x] 2.1 `MessageTimeoutCheckerPlugin`：判定改为 `System.currentTimeMillis() - head.getTime() > attribute`，javadoc 注明客户端时钟假设（design R2）
- [x] 2.2 `MessageSequenceCheckerPlugin`：放行分支 `setAttribute(CHECK_MESSAGE_ID, head.getId())`；会话获取简化为 `tunnel.getSession()`（D4，移除不可达 instanceof 分支与可能为 null 的会话防御——无会话时按现状豁免路径处理并注释）
- [x] 2.3 `PluginChain.execute`：catch 分支改为记录错误日志 + `context.doneAndIntercept(<既有系统错误码>)`（在 `NetResultCode` 现有枚举内选定并注释依据，不新增枚举值）
- [x] 2.4 运行 `./gradlew :tny-game-net:test --tests '*MessageTimeoutCheckerPluginTest' --tests '*MessageSequenceCheckerPluginTest' --tests '*PluginChainFailClosedTest'` 确认全绿

## 3. 回归与收尾

- [x] 3.1 运行 `./gradlew :tny-game-net:test` 全量，确认除已知 `CommonMessageHeadTest`（JDK/Mockito 预存环境问题）外无新增失败；摘要记入变更目录
- [x] 3.2 运行 `./gradlew :tny-game-net-netty4:test` 确认 36 例全绿
- [x] 3.3 `grep` 全仓确认无其它代码依赖"插件异常穿透"行为（搜索 PluginChain 使用点与 try-catch 环绕调用方的组合），结论写入 audit 记录
- [x] 3.4 在变更目录撰写 RELEASE NOTE 段：fail-open→fail-closed 的 BREAKING 声明 + 两检查器"修复前不可用"的兼容性说明
