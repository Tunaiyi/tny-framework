# Proposal

## Why

`MessageQueue` 使用 StampedLock 但全部以悲观模式调用（readLock/writeLock），乐观读零使用——付成本无收益；且其写锁不可重入、stamp 解锁仪式冗长、无竞争读锁开销高于普通监视器。该队列处于"每会话串行"模型内（addMessage 高频无竞争、resize/get 偶发），锁选型准则应为最简可重入原语。

## What Changes

- `MessageQueue`：`StampedLock` 替换为 `synchronized` 监视器对象；保留"锁外 volatile 读短路禁用态 + 锁内重读"结构（addMessage 高频路径依旧零锁开销）。行为完全不变，声明 skip_specs。
- 配套：模式卷决策表已增补"synchronized vs StampedLock"与"volatile 快照"两行选型规则。

## Capabilities

### New Capabilities

（无——行为不变重构。）

### Modified Capabilities

（无——session-resend 规格描述行为，与锁原语无关。）

## Impact

- 仅 `tny-game-net/.../transport/MessageQueue.java`（约 4 处锁原语替换 + import 清理）。
- 防回归网现成：`MessageQueueResizeTest` 6 用例（含并发 smoke）为上一变更刚建立的红灯测试，即本重构的行为基线。
