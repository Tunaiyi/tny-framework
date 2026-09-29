# 回归结果摘要（任务 3.3–3.5）

2026-09-29，`./gradlew :tny-game-common-lang:test :tny-game-net:test :tny-game-net-netty4:test`

| 模块 | 结果 |
|---|---|
| tny-game-common-lang | **全绿**（含 StampedLockAideTest 6/6） |
| tny-game-net | 新增测试全绿（TunnelSessionSnapshotTest 1/1、VisibilityContractTest 3/3）；**预存失败** `CommonMessageHeadTest` 8 例——Mockito 与本机 JDK 25 不兼容（`MockitoException: cannot mock interface Message`）。已用 `git stash` 移除本变更全部源码改动后复跑证实同样失败 → 与变更无关，不扩大范围修复 |
| tny-game-net-netty4 | **36/36 全绿**（NettyClientTunnelTest / NettyServerTunnelTest，隧道 bind/ping/pong/send 落地侧回归——直接覆盖 BaseNetTunnel 改动影响面） |
