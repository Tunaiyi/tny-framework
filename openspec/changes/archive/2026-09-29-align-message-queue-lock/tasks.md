# Tasks

## 1. 锁替换

- [x] 1.1 `MessageQueue.java`：字段 `sentMessageLock` 由 StampedLock 改为 `private final Object sentMessageLock = new Object();`；`resize/getMessages/getAllMessages/addMessage` 改用 `synchronized (sentMessageLock)`；保留 addMessage 锁外 volatile 短路 + 锁内重读结构；移除 `java.util.concurrent.locks.StampedLock` import

## 2. 验证

- [x] 2.1 `./gradlew :tny-game-net:test --tests '*MessageQueueResizeTest' --tests '*SessionResendSafetyTest' -Dorg.gradle.java.home=<corretto-21>` 7/7 全绿（含并发 smoke——行为等价性证据）
- [x] 2.2 `./gradlew :tny-game-net:test :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>` 全量回归零失败
