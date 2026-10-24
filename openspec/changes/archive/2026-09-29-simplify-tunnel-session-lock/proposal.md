# Proposal

## Why

前序变更 `fix-tunnel-session-concurrency` 将通道收发热路径改为纯 volatile 快照读（零锁零冲突）后，`BaseNetTunnel.sessionLock`（StampedLock）的写锁已完全嵌套在 `statusLock`（ReentrantLock）临界区内获取，而读侧不再参与该锁的任何协议——它成了永远无人竞争的双重保险，只增加一次无谓的加锁开销与"两个锁保护同一件事"的误导。StampedLock 还非重入，未来若有人在 `resetSession` 实现内误调其方法会直接死锁。

## What Changes

- 移除 `BaseNetTunnel` 的 `sessionLock` 字段：`bind()` 的会话切换互斥完全由既有 `statusLock` 承担（bind 是 `resetSession` 唯一入口，已由前次变更审计确认）。
- 行为不变：纯内部结构简化，无 BREAKING，无规格改动（`skip_specs`）。

## Capabilities

### New Capabilities

（无——不改变外部行为，声明 skip_specs。）

### Modified Capabilities

（无——`net-tunnel` 规格中"消息以单一会话快照处理"与"消息处理不被会话切换阻塞"两条合同均不受影响：切换互斥域由 statusLock 完整覆盖，读侧锁协议本就未参与。R1 约束的承接说明见 Impact。）

## Impact

- `tny-game-net/.../transport/BaseNetTunnel.java`：删 `sessionLock` 字段与注释；`bind()` 内 `StampedLockAide.supplyInWriteLock(this.sessionLock, () -> resetSession(session))` 改为直接 `resetSession(session)`；`StampedLockAide` 若无其它使用则移除 import。
- **R1 约束迁移**（design 前次变更遗留）：未来子类若把 `resetSession` 演变为多步操作，其互斥由 `bind()` 的 `statusLock` 提供（bind 仍是唯一切换入口）；约束语义不变，载体从 sessionLock 换成 statusLock。此事实以 `bind()` 处注释固化。
- 回归面：`TunnelSessionSnapshotTest`、`VisibilityContractTest`、netty4 `NettyClient/ServerTunnelTest`（36 例覆盖 bind/ping/pong/send）、`:tny-game-common-lang` 的 `StampedLockAideTest`（工具类本次不动，防误伤）。
