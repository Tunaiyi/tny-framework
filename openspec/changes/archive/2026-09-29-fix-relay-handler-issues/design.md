# Design

## Context

动机见 proposal.md - Why。事实（核实）：

- `channelActive:59-69`：`super.channelRegistered(ctx)` 误调且包在 `isInfoEnabled` + `isActive()` 分支内（else 分支连误调都没有——日志关时零传播）。
- `write:109-127`：packet==null 时整个 if 落空——未 write 未 promise 完成。
- catch 分支：`ResultCodeRuntimeException`（含链路校验拒绝，ERROR 级 close 通道/非 ERROR 仅 warn）均不 release；`Throwable` release(packet)。release 链 `RelayPacket.release→arguments→TunnelRelayArguments:39→OctetMessageBody.release`（接口，各实现幂等性未保证）。
- `userEventTriggered:174-194`：读 `NettyNetAttrKeys.TUNNEL`（中继通道生命周期挂的是 RELAY_LINK）→ 日志恒缺上下文；`WRITER_IDLE` case 缺 break（fallthrough 到空 default，无实际损害）。
- 消费点：`RelayMessage.release()` 由转发/处理链在成功路径调用（163-170）。

## Goals / Non-Goals

**Goals:** 四处修正 + 释放纪律入规格；行为变化最小面。
**Non-Goals:** 不改 processor 消费逻辑；不动 `exceptionCaught` 的既有分类（其 ResultCodable 分支缺失与 `pipeline.last()` 死分支问题登记移交，不并入——前者属应答语义主题、后者已在 executor-visibility 候选池）。

## Decisions

**D1：释放纪律定为"handler 终结释放"**——所有异常分支统一 `release(packet)`。
[P4 资源不变量的持有者终结：包引用由接收循环持有直至移交成功（handle 正常返回）或确认失败（异常），异常=未移交，handler 是唯一确定终点。]
- **R1 假设与验证**：不存在"处理器已消费缓冲后抛异常"路径——apply 审计 `RelayPacketProcessor` 各实现（checkLink/TunnelConnect 等校验型抛出均在消费前；转发成功后不再抛）；若发现反例则本决策改为"arguments 侧加原子释放标记"（升级方案已备案）。测试钉死"异常路径 release 恰一次"（mock 计数验证）。
- 否决备选：让各 processor 自释放——被否：N 个实现各管各的泄漏面，规则不可审计。

**D2：channelActive 拆"日志"与"传播"两职责**——`super.channelActive(ctx)` 方法末无条件调用；日志保持 INFO 守卫。[P5 职责分离；P6 通道契约按语义广播。]

**D3：write 对未知对象穿透** `ctx.write(msg, promise)`——handler 不做类型裁决之外的吞弃（与 MessageToByteEncoder 生态协作惯例一致，否决：抛异常——非中继包可能合法来自上游其它编码层）。

**D4：userEventTriggered 归属键改 RELAY_LINK + 补 break**——仅日志上下文修复与卫生，行为（close）不变。

## Risks / Trade-offs

- **R1**（上）双释放窗口——测试+审计双保险。
- **R2**：非中继包穿透后由 netty 编码层报错（原静默吞）——发送方从"挂死"变"显式失败"，方向正确；RELEASE NOTE 声明。
- **R3**：ERROR 级异常 release 后通道 close 顺序——release 在 close 日志前执行，无相互依赖。

## Open Questions

无。
