# RELEASE NOTE（tny-game-net-netty4 · fix-packet-verifier-gate）

## CRC64 报文校验自此首次可用

校验路径（`verifyEnable=true`）此前**端到端双重失效**：
1. 编码器 bodyBuffer 误用 direct 缓冲，`array()` 访问即抛 `UnsupportedOperationException`；
2. 解码器校验判定取反（true=通过 被当成 失败）。
不存在依赖旧行为的部署（启用即全量崩溃）。修复后：合法包正常往返，篡改/超限包被拒并断链
（接收侧字节流不可信，维持关闭语义）。

## 行为变更：超限报文本地拒绝

发送编码后超过 `maxPayloadLength` 的消息：
- 旧：照常写出 → 对端解码超限抛异常 → **对端关闭连接**（可被用作远程断连攻击面）；
- 新：本地抛 `NetPacketEncodeException`，写回执失败可感知，双端连接保持健康。

`NettyMessageHandler.exceptionCaught` 新增编码方向识别分支（仅识别新子类型，
其余异常传播语义零改动）。既有 `this == ctx.pipeline().last()` 判定恒假
（last() 返回 ChannelHandler，类型不匹配）——本次未顺手修，登记为后续卫生项。

## 遗留登记

`CoderTest` 仍是 main 方法（本次以 PacketGateTest 正规化其能力，CoderTest 本体待卫生变更处理）。
