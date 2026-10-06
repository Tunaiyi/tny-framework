# Tasks

## 1. 声明泛化

- [x] 1.1 `MessageQueue.java`：字段与 resize/getMessages/getAllMessages/addMessage 中的局部变量声明改为 `Queue<Message>`（构造点 `new CircularFifoQueue<>` 不变）；字段注释补一行"容量挤出为实现特性，契约见 session-resend 规格"

## 2. 验证

- [x] 2.1 `./gradlew :tny-game-net:test --tests '*MessageQueueResizeTest' --tests '*SessionResendSafetyTest' -Dorg.gradle.java.home=<corretto-21>` 7/7 全绿
