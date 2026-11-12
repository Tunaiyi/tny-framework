# Tasks

## 1. 实施

- [x] 1.1 `MessageQueue.java`：`sentMessageLock` 改 `ReentrantLock`；resize/getMessages/getAllMessages/addMessage 改 lock()/finally unlock()；更新字段注释（虚拟线程兼容理由+冷路径 synchronized 豁免边界）

## 2. 验证与收尾

- [x] 2.1 `./gradlew :tny-game-net:test --tests '*MessageQueueResizeTest' --tests '*SessionResendSafetyTest' -Dorg.gradle.java.home=<corretto-21>` 7/7 全绿
- [x] 2.2 `docs/design/patterns-in-tny.md` 选型行更新：热路径 → ReentrantLock（虚拟线程兼容）；synchronized 限冷路径；登记全项目其余 10 处 synchronized 冷路径豁免清单（一行注释于决策表内）
