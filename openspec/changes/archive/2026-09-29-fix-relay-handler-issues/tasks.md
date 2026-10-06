# Tasks

## 1. handler 行为测试先行（红灯基线）

- [x] 1.1 `tny-game-net-netty4/src/test/java/com/tny/game/net/netty4/relay/RelayHandlerBehaviorTest.java`（mock ctx 链 / mock processor、packet、arguments）四用例：
      ① channelActive 传播：日志级别 INFO 关闭时 mock ctx 验证 `fireChannelActive()` 恰一次且不调 `fireChannelRegistered()`（当前应红：既不传播又可能广播 registered）
      ② write 穿透：非 RelayPacket 对象 → 验证 `ctx.write(原对象, 原promise)` 被调用（当前应红：零调用）
      ③ 校验拒绝型异常 → 验证 `arguments.release()` 恰一次（processor 抛 ResultCodeRuntimeException 场景；当前应红：零释放）
      ④ 未知异常 → release 恰一次（现状 Throwable 分支已释放，防回归绿基线）
- [x] 1.2 运行记录红/绿矩阵到变更目录 `red-baseline.md`

## 2. 实现修复（NettyRelayPacketHandler 单文件）

- [x] 2.1 channelActive：日志与传播解耦（D2）——方法体末尾无条件 `super.channelActive(ctx)`
- [x] 2.2 write()：`packet == null` 时 `ctx.write(msg, promise)` 穿透（D3）
- [x] 2.3 catch ResultCodeRuntimeException 分支补 `release(packet)`（与 ERROR/非 ERROR 子分支顺序协调：release 先于 close 通知，D1）
- [x] 2.4 userEventTriggered：归属键改 `NettyRelayAttrKeys.RELAY_LINK`、switch 补 break（D4，行为不变）
- [x] 2.5 **R1 审计**：通读 `tny-game-net/.../relay/*Processor`（client/server/Base 与 TunnelRelay 转发链）确认"抛 ResultCodeRuntimeException 前无缓冲消费/释放"；结论记入 red-baseline.md，发现反例则暂停上报升级方案（arguments 原子释放标记）

## 3. 回归与收尾

- [x] 3.1 `./gradlew :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>` 全绿（既有 48 + 新 4）
- [x] 3.2 `./gradlew :tny-game-net:test --tests '*MessageQueueResizeTest' -Dorg.gradle.java.home=<corretto-21>` 守护不受影响
- [x] 3.3 release-note：非中继包写出行为（挂死→穿透）、校验异常释放纪律、移交登记（exceptionCaught 的 ResultCodable 缺失、pipeline.last() 死分支→executor-visibility 变更）
