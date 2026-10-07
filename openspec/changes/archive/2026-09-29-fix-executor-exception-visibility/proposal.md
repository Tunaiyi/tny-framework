# Proposal

## Why

网络层的四类"失败静默"互相掩盖，构成运维黑洞：① 命令执行器的异步失败仅挂 debug 级日志回调——生产级别下业务命令的异步异常**无任何消费者**，静默蒸发；② 通道收到消息但会话尚未就绪时**无日志丢弃**（代码内 TODO 注释明示未实现）；③（修正：见 red-baseline）前期审查怀疑通道异常分类链因 `pipeline.last()` 判断恒假成为死代码——红灯实验推翻该结论：netty `last()` 返回业务尾 handler，分类链与结果码应答在生产管线（handler 为业务尾）中已正常工作；本项从变更范围撤销，仅以回归测试固化该真实行为；④ 测试桩 `MockNetTunnel` 的 close/disconnect 无幂等守护，匿名会话场景触发递归栈溢出（前变更已实证），用它做验收的测试会误导排查。

## What Changes

- `SerialCommandExecutor`：future 消费回调改为无条件挂载——失败必记 error（命令名+异常），成功值仅 debug。
- `NettyMessageHandler.channelRead`：无会话丢弃路径补可诊断 warn（通道标识+消息标识），终结 TODO。
- `MockNetTunnel`：close/disconnect 加状态置前与幂等守卫（对齐真实 `BaseNetTunnel` 的关序）。

## Capabilities

### New Capabilities

- `command-execution`: 命令异步执行失败的日志可见性契约。

### Modified Capabilities

- `net-tunnel`：ADDED 一条——未就绪丢弃必须可观察。（结果码应答经证实为既有正常行为，改为回归测试固化，不新增规格。）

## Impact

- 代码：`tny-game-net`（SerialCommandExecutor、net-test 的 MockNetTunnel）+ `tny-game-net-netty4`（两个 handler）。
- 行为变化：结果码应答行为已存在，本变更不改变之（仅测试固化）。
- 高频丢弃日志：窗口仅存在于绑定前（通道生命周期极短），不加限流（design D2 论证）。
- MockNetTunnel 修复解锁后续用它编写的会话级测试（含队列中 unbound-rejection 变更）。
