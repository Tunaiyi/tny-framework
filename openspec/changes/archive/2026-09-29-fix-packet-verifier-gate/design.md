# Design

## Context

动机见 proposal.md - Why。关键现状事实（均核实）：

- `CodecVerifier.verify` 语义 true=通过（`CRC64CodecVerifier:50-58` equals→true；Noop→true）。
- 解码器 `NetPacketV1Decoder:140` `if (verify(...)) throw` —— 与契约相反。
- 编码器 `NetPacketV1Encoder:109-114` 超限仅 `logger.warn` 继续写出。
- 写出链路：`NettyChannelMessageTransport.write:57` 把 `channel.writeAndFlush` 投递 eventLoop；`MessageToByteEncoder` 内 encode 抛异常 → `promise.tryFailure` + `ctx.fireExceptionCaught` → `NettyMessageHandler.exceptionCaught`（140-157 无此类型分支 → `super.exceptionCaught` → **默认关闭通道**）。即现行为=本地不炸、对端收到超限包后关闭；修复后目标=本地失败、双端连接健康。
- 解码载荷 `NetPacketV1Decoder:91-98` 直接使用 `TUNNEL` attr 引用无防护。
- 既有 `CoderTest` 为 main 方法（EmbeddedChannel + mockito mockAs 装配），从未进 JUnit 执行。

## Goals / Non-Goals

**Goals:** 三道防线转正；超限发送本地化失败；测试正规化（main→JUnit）。
**Non-Goals:** 接收侧超限包处理不改（关连接是正确语义——对端违约、字节流无法安全重同步）；不改协议线格式与 `NetResultCode`；不动 `DataPackageContext` 的包序推进时机（校验失败前已 `goToAndCheck`，序号语义与现状一致）。

## Decisions

**D1：解码器条件取反 `if (!verifier.verify(...)) throw`**
[依据接口 javadoc 语义与两实现（true=通过）；P6 守约。] 修复方向唯一，无备选——现状必错。

**D2（实施修正版）：编码器超限抛 `NetPacketEncodeException`（netty4 codec 包新增；**extends NetException**——原定 NetCodecException 因其构造器 private 不可继承，打开 net 抽象模块公共面被否，实现模块自建类型更符合 P1/P11，错误码复用既有 `ENCODE_ERROR`）；`NettyMessageHandler.exceptionCaught` 增识别分支：编码方向异常记录日志后 `return`（不透传 super → 不关连接），其余异常维持原语义（写 promise 失败由 netty 自动完成，发送方可感知）**
[为什么需要新异常型：`NetCodecException` 同时是接收侧解码异常型，解码侧必须保持"关闭"（D3），仅凭类型无法区分方向——子类型是最小充分区分（P7 反向应用：不污染既有契约）。] 放 netty4 包不动 net 抽象模块公共 API（P1：抽象层零变化）。
- 否决备选①：编码器返回"占位空包"——被否：协议无此概念，对端解出空载荷是新故障。
- 否决备选②：超限继续发出靠对端处理——被否：即现状，远程断连攻击面。
- 风险与验证：netty `MessageToByteEncoder` 在 fireExceptionCaught 前已 `tryFailure(promise)`——写回执失败传播链不依赖 handler 行为；apply 用 EmbeddedChannel 测试钉死"本地失败 + channel 仍 open"两断言。

**D3：接收侧超限/校验失败仍关连接（明示不改）**
字节流一旦有包超限或校验失败，后续帧边界不可信，保留"拒绝并断链"是协议安全常识；规格 Scenario 2/3 已固化。

**D4：解码载荷入口 `tunnel == null → throw NetCodecException("... no session tunnel ...")`**
[进入解码器的包理应有 tunnel（initChannel 先于读事件设置 attr），null 属异常窗口——显式拒绝转为既有解码异常流（关连接），杜绝 NPE 伪装成"未知异常"（P13）。]

## Risks / Trade-offs

- **R1**：`exceptionCaught` 识别分支若误吞非编码类异常会掩盖真故障——分支判定只认新子类型，默认路径零改动；测试含"解码异常仍关闭"反例。
- **R2**：现网若有依赖"超限被静默发出"者（对端恰好宽容），修复后其发送开始显式失败——RELEASE NOTE 声明；该依赖建立在缺陷上。
- **R3**：mockito 在 JDK 25 daemon 下不可用（既有环境约束）——测试在 daemon=21 跑（toolchain 已钉定，CI 同）。

## Open Questions

无。
