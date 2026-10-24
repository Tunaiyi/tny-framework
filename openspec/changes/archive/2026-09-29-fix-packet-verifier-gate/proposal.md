# Proposal

## Why

报文编解码路径的三道防线全部失效或反效：① 解码器的校验判定整体写反——`CodecVerifier.verify` 契约是 true=校验通过（CRC64 实现以 `Arrays.equals` 返回 true），解码器却在返回 true 时抛"verify failed"，**启用 CRC64 校验后合法包全部被毁、篡改包反而放行**；② 发送超限报文仅记 warn 继续写出——接收端解码超限抛异常并**关闭连接**，等价于"任何对端可发一条超限报文远程掐断链路"的攻击面，且发送方对失败毫无感知；③ 解码载荷路径对隧道引用无防护，绑定失败窗口内到达的包以 NPE 崩掉（异常被吞、静默丢包）。

## What Changes

- 解码器校验判定修正为"校验不通过才拦截"（条件取反），CRC64 校验自此真正可用。
- **行为变更**：发送侧超限报文改为编码阶段显式拒绝——写 future 以编码异常失败通知发送方，连接保持健康（现状：发出→对端断连）。为此新增内部异常 `NetPacketEncodeException`（netty4 codec 包，继承 `NetCodecException`），处理器 `exceptionCaught` 识别编码方向异常仅记录不再关闭通道（接收方向解码异常维持关闭语义——流已不可信）。
- 解码载荷入口补隧道空引用防护，绑定失败窗口以可诊断的解码异常拒绝（接收端关闭连接，语义与现有一致）。

## Capabilities

### New Capabilities

- `net-protocol`: 报文编解码防线行为契约——完整性校验判定、发送超限拒绝、解码上下文未就绪的处置。

### Modified Capabilities

（无——`net-tunnel`/`message-checking` 现有合同不涉及编解码层。）

## Impact

- 代码：`NetPacketV1Decoder`（校验条件 + 隧道防护）、`NetPacketV1Encoder`（超限抛出，替换现仅 warn）、`NettyMessageHandler.exceptionCaught`（编码异常识别分支）、新增 `NetPacketEncodeException`。全部位于 `tny-game-net-netty4`。
- 兼容性：CRC64 校验启用即全量崩溃（现状），不存在依赖旧行为的下游——修复属"从无到有"；超限发送从"发出并毒死对端"变"本地失败"是**可观察行为变更**（对使用方：以前静默丢+断连，现在发送失败可感知）；默认 `verifyEnable=false` 的配置行为零变化。
- 既有 `CoderTest` 是 main 方法（从未进 CI），其装配模式（EmbeddedChannel + mockAs）转用为正规 JUnit 测试。
- 测试：新增编解码防线 JUnit 测试（校验正反例/超限/防护），回归 netty4 36 例 + net 全量。
