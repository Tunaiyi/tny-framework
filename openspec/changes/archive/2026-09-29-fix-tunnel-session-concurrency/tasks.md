# Tasks

## 1. 并发回归测试（先行编写，修复前应能观测到失败或不稳定）

- [x] 1.1 `tny-game-net/src/test/java/.../transport/TunnelSessionSnapshotTest.java`：双线程 + latch 构造"消息处理进行中 × 会话切换"，断言消息以进入时刻的会话快照完成处理（对应 net-tunnel 规格两个 Scenario）；另断言高频接收路径无锁阻塞（可用处理耗时上界粗验）
- [x] 1.2 `tny-game-net/src/test/java/.../session/SessionCertificateVisibilityTest.java`：登录线程写凭证、经 happens-before 边（latch）后业务线程读，断言观察到完整新凭证；并发读写迭代 ≥50 次
- [x] 1.3 `tny-game-net/src/test/java/.../relay/link/RelayLinkHeartbeatVisibilityTest.java`：模拟 IO 线程写心跳时刻 → 调度线程读，断言心跳处理完成后的下一次检测可见
- [x] 1.4 `tny-game-common-lang/src/test/java/.../concurrent/utils/StampedLockAideTest.java`：无副作用供应商下验证标准乐观读语义——无并发写时恰好执行一次且无悲观锁介入；写锁持有时 validate 失败降级重跑且结果正确
- [x] 1.5 运行上述测试确认当前实现下 1.1/1.4 可捕获缺陷（红灯基线），记录结果摘要到本变更目录

## 2. 实现修复

- [x] 2.1 `tny-game-net/.../transport/BaseNetTunnel.java`：`receive`/`send` 移除 `StampedLockAide.supplyInOptimisticReadLock` 包装，直接调用 `doReceive`/`doSend`（内部已持单次 volatile 快照读）；`accessId` 字段加 volatile
- [x] 2.2 `tny-game-common-lang/.../concurrent/utils/StampedLockAide.java`：四个 `*InOptimisticReadLock` 方法改为标准模式（执行业务 → validate → 失败则 readLock 下重跑）；**扩展**：三个 `*InWriteLock` 方法同族缺陷一并改为真写锁（兑现 design D1/R1 写侧互斥前提，披露见 red-baseline.md 追加节）；类与方法 javadoc 写明供应商契约
- [x] 2.3 `tny-game-net/.../session/BaseNetSession.java`：`certificate` 字段加 volatile
- [x] 2.4 `tny-game-net/.../relay/link/BaseRelayLink.java`：`latelyHeartbeatTime` 字段加 volatile

## 3. 约束审计与收尾

- [x] 3.1 审计 `BaseNetTunnel.resetSession` 全部实现（`ServerTransportTunnel`、`ClientTransportTunnel` 及仓库内其余子类），确认均为引用整体替换形态；结论记入本变更目录（R1 缓解项）
- [x] 3.2 全仓 `grep` 确认 `StampedLockAide` 乐观读方法除已迁离的两处外无残余调用，将结论写入 RELEASE NOTE 段落（兼容性声明）
- [x] 3.3 运行 `./gradlew :tny-game-common-lang:test` 并确认通过
- [x] 3.4 运行 `./gradlew :tny-game-net:test` 并确认通过
- [x] 3.5 运行 `./gradlew :tny-game-net-netty4:test` 并确认通过（Tunnel 子类落地侧回归）
