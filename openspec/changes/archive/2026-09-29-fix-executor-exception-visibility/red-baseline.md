# 红灯基线与过程修正（任务 1.6）

## 终态矩阵（daemon=Corretto 21）

| 测试 | 修复前 | 修复后 |
|---|---|---|
| MockNetTunnelCloseTest（递归溢出复现） | **红**（StackOverflowError） | 绿 |
| SerialCommandExecutorQueueLivenessTest（场景三） | 绿（队列活性本就成立） | 绿（防回归） |
| MessageHandlerDiscardTest（丢弃无副作用） | 绿（行为面现状正确） | 绿 |
| HandlerResultCodeReplyTest ×2 | **意外绿**（见修正①） | 绿（转型为回归固化） |
| 丢弃/失败的"日志文本"断言 | **未编写**（见修正②） | 人工验收清单（本文件末） |

## 修正①：`pipeline.last()` "死分支"论断被红灯实验推翻

提案声称 `this == ctx.pipeline().last()` 恒假、结果码应答为死代码。实验直接命中
`handleResultCodeException`（应答真实发生，堆栈为证）——netty `last()` 返回**业务尾
handler**（head/tail 哨兵不计入），判断在真实管线形态下为真。
处置：该需求从 spec 撤销、实现任务 2.3 撤销、测试转为回归固化。
**教训固化：审查期"类型不匹配必然恒假"式的静态推断，必须经执行证据才能进规格。**
登记的真实脆弱性：若未来在业务 handler 之后再挂业务 handler，应答静默失效（移交观测主题）。

## 修正②：日志断言按 design R3 预声明路径降级

testRuntimeClasspath 为 **slf4j-simple**（无内存 Appender、输出流初始化后不可重定向）→
"丢弃 warn 留痕 / 失败 error 留痕"两条文本断言无法在 CI 建立。按预声明降级为人工验收：

- 人工验收 A：以 demo 服务器在会话绑定前发一条报文 → 日志出现"尚未绑定会话，丢弃消息 id/protocol/mode"；
- 人工验收 B：业务方法抛异步失败 → EXECUTOR logger 出现 "execute [...] command failed"（生产级别下）。

## 附带修复（测试自身）

`HandlerResultCodeReplyTest` 首版对 mock 返回链 stub 不足：`RpcMessageAide.send` 消费
`tunnel.send(...)→receipt.written().thenRun(...)`，mock 默认返回 null 致 NPE——按真实
返回形态补全（MessageSent→CompletionStageFuture 链）。该 NPE 恰属"测试发现生产契约
隐含非空要求"的记录（write/send 返回非空回执是隐式契约，未被 javadoc 声明——登记卫生项）。

## 实现落地（2.1/2.2/2.4 摘要）

- SerialCommandExecutor：whenComplete 无条件（cause→error 日志；value→debug 守卫）
- NettyMessageHandler.channelRead：无 tunnel 丢弃补 warn + TODO 注释改指观测后续项
- MockNetTunnel：close/disconnect 状态前置 + 幂等守卫（对齐 BaseNetTunnel 关序）
