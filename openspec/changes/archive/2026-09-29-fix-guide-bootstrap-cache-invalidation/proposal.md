# Proposal

## Why

`net-guide-lifecycle` 账本已立约"任一实例关闭后重新开启 MUST 能恢复完整的监听与处理能力"（scenario"关闭后可重新开启"），但 `fix-server-guide-lifecycle` 的修复只重建了线程组层：两个服务端 guide 的 `ServerBootstrap` 经 DCL 缓存，**close 未失效该缓存**——旧构建器内固化着已 shutdown 的旧组引用，重开时 DCL 命中缓存直接复用，端口恢复监听必然失败。该缺陷由交叉验证外部审计变更（fix-net-audit-findings）时逐行读码坐实：其"缓存的引导器随组一并重建"断言属实。**本变更不新增任何合同，是代码追上已立台账的履行性修复。**

## What Changes

- `NettyServerGuide.close()` 与 `NettyRelayServerGuide.close()`：置空组字段处同步 `this.bootstrap = null`（close 在各自 statusLock/幂等闸内，无并发窗口新增）。
- 范围裁定（防扩大）：client 两 guide（NettyClientGuide/NettyRelayClientGuide）具备 `closed` 单向终态闸，close 后本不可重开——账本未对其承诺重开能力，其 bootstrap 缓存**不动**；共享组注入架构调整属已确认的后续独立变更（share-guide-event-loop-groups），不混入本单。

## Capabilities

### New Capabilities

（无——skip_specs。）

### Modified Capabilities

（无——`net-guide-lifecycle` 既有条款原样兑现，不改动措辞。）

## Impact

- 代码：两个 server guide 的 close 各 +1 行；行为变化仅在"close→reopen"路径（此前该路径死透，无兼容负担——违约状态下不存在依赖旧行为的正确用户）。
- 测试：close 销毁 bootstrap 缓存的断言（反射预置哨兵 bootstrap 实例→close→断言置 null），绕开完整装配依赖（reopen E2E 的装配成本教训沿用结构断言策略，链路上"bootstrap null → DCL 必新建 → init 绑定 ensure 重建的新组"由结构闭合）。
- 验证后 `net-guide-lifecycle` scenario"关闭后可重新开启"从违约转兑现。
