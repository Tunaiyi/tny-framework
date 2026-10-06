# Proposal

## Why

消息派发前的三个防护插件全部处于"存在但失效/危险"状态：超时检查器的耗时条件写反（启用后每条消息都被判超时，插件实际不可用）；序号防重放检查器在水位推进时写入旧值（防重放永不生效，形同虚设）；插件链执行器吞掉插件异常后继续执行业务（校验插件一旦抛异常，效果等同校验通过——安全能力反向失效）。这三个插件是框架对游戏外挂场景（重放、伪造超时容忍）的既有防线，必须恢复其正确语义。

## What Changes

- 修复超时检查：以"当前时刻 − 消息请求时间 > 阈值"判定超时，超期消息拦截并返回超时错误码。
- 修复序号防重放：新消息放行后水位推进为该消息 ID；不大于水位的消息按"已处理"拦截；未认证连接保持现状豁免。
- **BREAKING** 插件链异常语义由 fail-open 改为 fail-closed：插件执行中抛出未处理异常时，消息被拦截并返回系统错误码、记录错误日志，业务方法不再执行。影响面：所有经 `@BeforePlugin/@AfterPlugin` 装配的控制器（含 demo 的 `SpringBootParamFilterPlugin`——其非声明异常此前穿透执行业务，修复后被拦截）。
- 清理序号检查器中不可达的 `instanceof` 分支（`Tunnel` 接口已有 `getSession()` 契约方法）。

## Capabilities

### New Capabilities

- `message-checking`: 消息派发前防护检查的行为契约——请求超时判定、重放消息判定、插件链失败语义。

### Modified Capabilities

（无——既有账本 `net-tunnel`/`net-session`/`relay-link` 的合同不受影响；`RpcMonitor` 处理器自带逐项 catch，不在插件链语义变更范围内，不受影响。）

## Impact

- **代码**：`tny-game-net/.../command/plugins/MessageTimeoutCheckerPlugin.java`、`MessageSequenceCheckerPlugin.java`、`command/dispatcher/PluginChain.java`（异常分支）；插件装配与链结构（`MethodControllerHolder`）不动。
- **行为（BREAKING 面）**：插件抛异常时的外部可观察结果从"业务照常执行并应答"变为"拦截并返回系统错误码"。错误码从 `NetResultCode` 既有枚举中选取，不新增枚举值（避免公共 API 增量）。
- **下游/示例**：demo 工程四个控制器使用参数过滤插件；参数解析类异常（消息体畸形）此前穿透到业务方法触发不可控异常，修复后为受控拦截——属正确性改善，RELEASE NOTE 声明。
- **时钟假设**：超时判定依赖客户端消息时间戳，客户端时钟超前可能误拦（design R2 记录）。
- **测试**：新增三个插件/链行为测试类（`tny-game-net/src/test/.../command/plugins`、`command/dispatcher`）；回归面 `:tny-game-net:test` 全量 + netty4 36 例。
