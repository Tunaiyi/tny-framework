# Tasks

## 1. 防线测试先行（红灯基线）

- [x] 1.1 新建 `tny-game-net-netty4/src/test/java/com/tny/game/net/netty4/network/codec/PacketGateTest.java`（JUnit5，EmbeddedChannel + `mockAs` 装配复用 CoderTest 模式；参考 `use-reentrant-lock` 后 MessageQueue 测试的独立桩习惯，mockito 仅用于 ctx/tunnel）：
      校验对拍——真实 `CRC64CodecVerifier` + 加密关闭的 config：encode 出合法字节流 → decode 回来必须产出原消息（当前 verify 反效应红）；篡改一个 body 字节 → decode 必须抛 causeVerify 异常（当前会放行，应红）
- [x] 1.2 同文件超限发送：构造编码后 payload 超 `maxPayloadLength` 的消息调 `encodeObject`，断言抛 `NetCodecException` 子类且 channel `isOpen()`（当前仅 warn 不抛，应红——实现前以 `assertThrows` 定义期望）
- [x] 1.3 同文件隧道防护：`TUNNEL` attr 置 null 的 channel 上调 `decodeObject`（喂合法帧头），断言抛携带 "tunnel" 字样的 `NetCodecException` 而非 NullPointerException（当前 NPE，应红）
- [x] 1.4 `NettyMessageHandler` 行为测试：pipeline 中写编码异常（1.2 场景经 EmbeddedChannel.writeOne 触发）→ 断言写 promise 失败且 `channel.isActive()` 仍真（编码异常不关闭通道）；同时反例：喂畸形字节流触发解码异常 → 通道被关闭（D3 语义保持）。当前正例应红（exceptionCaught→super 关闭）
- [x] 1.5 运行新测试，红/绿矩阵记入变更目录 `red-baseline.md`（预期 1.1-1.4 至少各一红）

## 2. 实现修复

- [x] 2.1 `NetPacketV1Decoder:140` 校验条件取反为 `if (!verify(...)) throw`，注释写明 verify 契约（true=通过）
- [x] 2.2 新建 `NetPacketEncodeException extends NetCodecException`（netty4 codec 包）；`NetPacketV1Encoder` 超限分支由 warn 改为抛出（消息含实际/上限值）；**实施扩展**：bodyBuffer 分配 buffer()→heapBuffer()（发现校验路径第二层缺陷：direct buffer 上 array() 必崩，红灯矩阵已披露）；logger.debug 系列调用包 `isDebugEnabled` 守卫（顺手清 C1 半项，注明）
- [x] 2.3 `NettyMessageHandler.exceptionCaught` 增分支：`cause instanceof NetPacketEncodeException` → warn 记录后 return（不透传，不关闭）；其余类型零改动
- [x] 2.4 `NetPacketV1Decoder.readPayload` 入口 `tunnel == null` → 抛带因 `NetCodecException`

## 3. 回归与收尾

- [x] 3.1 `./gradlew :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>`：新测试全绿 + 既有 36 例零回归
- [x] 3.2 `./gradlew :tny-game-net:test --tests '*MessageQueueResizeTest'`（守护测试不受影响）
- [x] 3.3 变更目录写 release-note：CRC64 校验"自此可用"、超限发送行为变更（本地失败取代对端断连）、CoderTest main 遗留问题登记（转 JUnit 建议后续卫生变更）
