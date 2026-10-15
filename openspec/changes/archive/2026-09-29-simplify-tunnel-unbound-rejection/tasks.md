# Tasks

## 1. 行为测试先行（红灯基线）

- [x] 1.1 `tny-game-net/src/test/java/com/tny/game/net/transport/TunnelUnboundRejectionTest.java`（TestTunnel 复用 TunnelSessionSnapshotTest 模式，会话不绑定）三用例：
      ① 未绑定通道 receive 消息 → 返回 false 且不抛异常（当前 NPE 逃逸出 receive——注意 channelRead 层 catch 会吞，测试直接调 `tunnel.receive()` 断言异常形态，应红）
      ② 拒绝后绑定会话再 receive → 正常受理返回 true（场景二"不损伤通道"）
      ③ 既有绑定路径回归锚：复用/对照 TunnelSessionSnapshotTest 快照用例（应绿）
- [x] 1.2 运行记录红/绿矩阵到 `red-baseline.md`（预期 ① 红、②③ 绿）

## 2. 实现

- [x] 2.1 `BaseNetTunnel.doReceive`：快照 null → warn（通道 id + 消息标识）+ return false（置于 createEnter 之前，D1）
- [x] 2.2 `while(true)` 线性化（D2）：closed→false，否则返回单次 `session.receive` 结果
- [x] 2.3 全仓 `implements NetSession`/`receive` 覆写实现清单复核（design R1），结论记入 red-baseline

## 3. 决策材料与回归

- [x] 3.1 `send2AllOnline-decision.md`：三选一分析（design D3）+ 倾向与理由，留后续变更决策
- [x] 3.2 `./gradlew :tny-game-net:test :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>` 全量绿（新 3 例 + TunnelSessionSnapshotTest + netty4 全部）
- [x] 3.3 release-note：合同补全说明（可达性诚实表述：当前依赖 EventLoop 串行的隐性安全转为显式合同）
