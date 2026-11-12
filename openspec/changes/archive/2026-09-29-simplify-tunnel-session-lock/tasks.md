# Tasks

## 1. 实现简化

- [x] 1.1 `BaseNetTunnel.java`：`bind()` 中 `StampedLockAide.supplyInWriteLock(this.sessionLock, () -> resetSession(session))` 改为直接调用 `resetSession(session)`，并在该行上方加注释固化互斥约束："会话切换互斥由本方法的 statusLock 提供；resetSession 唯一切换入口，未来多步实现须保持经 bind 进入"
- [x] 1.2 `BaseNetTunnel.java`：删除 `sessionLock` 字段及其注释；若 `StampedLockAide` 在该文件无其它引用则移除 import

## 2. 回归验证

- [x] 2.1 运行 `./gradlew :tny-game-net:test --tests '*TunnelSessionSnapshotTest' --tests '*VisibilityContractTest'` 确认通过
- [x] 2.2 运行 `./gradlew :tny-game-net-netty4:test` 确认 36 例全绿（bind/重连落地侧）
- [x] 2.3 运行 `./gradlew :tny-game-common-lang:test --tests '*StampedLockAideTest'` 确认工具类未受影响
- [x] 2.4 grep 确认 `sessionLock` 标识符在 tny-game-net 与 tny-game-net-netty4 中无残余引用
