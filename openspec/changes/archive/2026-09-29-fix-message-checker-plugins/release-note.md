# RELEASE NOTE（tny-game-net）

## BREAKING：插件链异常语义 fail-open → fail-closed

- 此前：`@BeforePlugin/@AfterPlugin` 检查插件抛出未处理异常时，仅记录日志，**消息照常进入业务方法**。
- 现在：插件异常 → 消息被拦截（前置）或应答被系统错误码取代（后置），并记录含插件类名的错误日志。
- 影响识别方法：若你的插件依赖"抛异常但业务继续"的行为，那是在依赖缺陷；正确做法是插件内部
  自行 catch 并决定是否 `doneAndIntercept`。

## 检查器插件修复（此前不可用，修复后可启用）

- `MessageTimeoutCheckerPlugin`：超时判定修正（此前条件写反，启用即恒拦截）。语义为端到端超时
  （含网络在途），依赖客户端消息时间戳——客户端时钟异常可能导致误拦/放松。
- `MessageSequenceCheckerPlugin`：防重放水位推进修正（此前永不生效）。仅对已认证会话启用，
  按连接维护消息编号水位，乱序/重放消息将以"已处理"错误码拦截。
