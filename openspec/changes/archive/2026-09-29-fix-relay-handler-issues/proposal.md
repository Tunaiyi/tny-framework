# Proposal

## Why

中继包处理器 `NettyRelayPacketHandler` 存在四处缺陷：① `channelActive` 内误调 `super.channelRegistered(ctx)`——向管道广播错误事件（已注册事件二次触发），且整段传播逻辑嵌在日志开关与 `isActive` 分支内，日志关闭时**激活事件被吞**（下游 handler 收不到 channelActive）；② `write()` 对非中继包对象（`packet == null`）静默吞掉：既不穿透下游也不完成 promise——调用方 future 悬挂；③ 异常处理不对称：`Throwable` 分支释放包体引用而 `ResultCodeRuntimeException` 分支不释放——该校验异常恰是中继链路最常见的失败路径（链路未建立/状态校验），异常一次泄漏一份缓冲；④ `userEventTriggered` 读错归属键（中继通道上读会话通道 attr，恒 null）且 switch 缺 break。

## What Changes

- `channelActive`：日志保留，事件传播改为无条件 `super.channelActive(ctx)`（P6 守约：通道激活按契约广播）。
- `write()`：非中继包对象原样穿透 `ctx.write(msg, promise)`（不识别不吞）。
- 异常释放纪律统一为 **"handler 终结释放"**：所有 catch 分支（含 `ResultCodeRuntimeException`）统一 `release(packet)`；成功路径归处理器管辖——`OctetMessageBody.release` 各实现无幂等保证，故以"异常必达 handler 终结、处理器不得在抛异常前消费后释放"为约束（design R1 记录假设与验证）。
- `userEventTriggered`：空闲事件归属改读中继链路键（RELAY_LINK），switch 补 break；行为（关通道）不变。

## Capabilities

### New Capabilities

（无新主题——归入既有 `relay-link`。）

### Modified Capabilities

- `relay-link`：ADDED 两条新需求（事件传播正确性 / 中继包失败释放责任与写穿透），不修改既有"心跳可见性"需求。

## Impact

- 仅 `tny-game-net-netty4/.../relay/NettyRelayPacketHandler.java`（四处局部修改）。
- 行为变更：①非中继包写出不再悬挂 future（此前死等，现到下游/编码层自然处理）；②校验型异常后包体引用被释放（此前泄漏累积）；③日志降级不再吞激活事件。
- 下游 `RelayPacketProcessor` 实现（net 模块内建 client/server processor）经审无"抛异常前已释放"路径（design R1），统一纪律成立。
- 测试：handler 单测 3 例（事件传播/写穿透/异常释放），EmbeddedChannel + mock arguments 验证 release 恰一次。
