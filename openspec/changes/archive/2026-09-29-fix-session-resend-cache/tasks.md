# Tasks

## 1. 行为测试先行（红灯基线）

- [x] 1.1 `tny-game-net/src/test/java/com/tny/game/net/transport/MessageQueueResizeTest.java`：resize 三态——0→N 首次启用不抛异常且后续 add 可查（当前 NPE 应红）；N→0 禁用后查询为空；M→N 缩容保留最近 N；消息 stub 可复用 `RpcContextFixture.StubMessage`
- [x] 1.2 同文件补环形挤出用例：容量 N 连续 add N+k 条后仅最近 N 条可见（当前实现该行为已正确，作为防回归绿基线）
- [x] 1.3 并发 smoke：一线程循环 resize、一线程循环 addMessage/getMessages，断言无异常抛出（可见性缺口的粗粒度捕获，红绿均记录）
- [x] 1.4 `tny-game-net/src/test/java/com/tny/game/net/session/SessionResendSafetyTest.java`：以最小 SessionContext 桩构造 CommonSession（缓存启用），关闭会话后 resend 三重载均安全无效、getSentMessages 不抛异常（对应规格第三个 Requirement）
- [x] 1.5 运行新测试，红/绿矩阵记入变更目录 `red-baseline.md`

## 2. 实现修复

- [x] 2.1 `MessageQueue.resize`：messageSize≤0 时置队列 null 并返回（D1 禁用语义）；旧队列非 null 时 new 后 addAll 迁移（D2）；旧队列 null 时直接 new（修 NPE）
- [x] 2.2 `MessageQueue.sentMessageQueue` 字段加 volatile；`addMessage/getMessages/getAllMessages` 锁内改为重读局部变量（D3）
- [x] 2.3 `BaseNetSession` 构造冗余 if/else 合并为单行 `new MessageQueue(sendMessageCachedSize)`（D4，行为不变）

## 3. 回归与收尾

- [x] 3.1 `./gradlew :tny-game-net:test -Dorg.gradle.java.home=<corretto-21>` 全绿（含既有 35 例与新增）
- [x] 3.2 `./gradlew :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>` 36 例全绿（会话工厂/登录链路回归）
- [x] 3.3 变更记录目录补 release-note：重发缓存特性"从不可用到可用"的启用说明（配置入口 `CommonSessionSetting.sendMessageCachedSize`）
