# Proposal

## Why

通道收发热路径上的"乐观读锁"保护是假的：工具类 `StampedLockAide` 的校验被写在业务执行**之前**、执行后从不复验，等价于无锁执行；同时会话认证凭证与中继链路心跳时间戳以普通字段跨线程读写，无任何可见性保证。这三处都靠"看起来有保护"的假象存活，断线重连场景下存在真实数据竞争隐患，且工具类的错误示范会误导后续调用者。

## What Changes

- 移除 `tny-game-net` 通道接收/发送热路径上的无效乐观读锁包装，显式以易失引用快照语义接收与发送消息（对无副作用调用方行为等价，语义诚实化）。
- 修正 `tny-game-common-lang` 公共工具类 `StampedLockAide` 四个乐观读方法的实现顺序（校验移到执行之后，失败时降级为悲观读锁重跑），并在 javadoc 首次写明"业务必须无副作用/幂等"的使用契约。**BREAKING**：公共 API 实现语义变更——对有副作用的供应商，执行次数可能从恰好一次变为至多两次；仓库内现存调用方将全部迁离，剩余调用方须满足新契约。
- 会话认证凭证字段、中继链路最近心跳时间字段收敛为易失可见性保证。纯正确性修复，外部行为不变。

## Capabilities

### New Capabilities

- `net-tunnel`: 通道消息接收/发送与会话切换（重连）并发时的行为承诺——单一会话快照、接收不被切换阻塞。
- `net-session`: 会话状态关键字段（认证凭证）的跨线程可见性契约。
- `relay-link`: 中继链路心跳新鲜度的跨线程可见性契约（空闲判定不误杀）。

### Modified Capabilities

（无——项目规格账本尚无既有能力主题，本变更为 `openspec/specs/` 引入首批三个主题。）

## Impact

- **代码**：`tny-game-common-lang/.../StampedLockAide.java`；`tny-game-net/.../transport/BaseNetTunnel.java`（receive/send 两处热路径）、`BaseNetSession.java`、`relay/link/BaseRelayLink.java`；写侧 `bind()`/`resetSession()` 保留（`ServerTransportTunnel`/`ClientTransportTunnel` 实现已核实为原子引用赋值，审计任务覆盖其余子类）。
- **公共 API（BREAKING 面）**：`StampedLockAide` 为 `tny-game-common-lang` 公开工具类。仓库内调用方经 codegraph 核实仅 `BaseNetTunnel.receive/send` 两处（本变更迁离），`runInOptimisticReadLock` 等其余乐观读方法零调用方；对外部依赖者，无副作用用法修复后结果不变，有副作用用法属误用。
- **下游 starter/业务工程**：无需改动；重连与心跳行为对外不可见变化。
- **测试**：`tny-game-net` 新增并发回归测试 3 个、`tny-game-common-lang` 工具类单测 1 个；`obsolete/` 不受影响。
