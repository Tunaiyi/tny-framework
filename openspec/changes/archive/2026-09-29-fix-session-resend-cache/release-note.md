# RELEASE NOTE（tny-game-net · fix-session-resend-cache）

## 会话重发缓存：特性从"不可用"变为"可用"

`MessageQueue.resize`（会话缓存动态调整）此前在任何"从禁用(0)启用(N>0)"路径必然 NPE，
"调整为 0"必然抛容量非法异常——即配置 `CommonSessionSetting.sendMessageCachedSize > 0`
的部署在玩家登录瞬间崩溃。本变更修复后：

- 0→N：正常启用，后续发送进入环形保留窗口（最近 N 条）
- N→0：禁用并释放全部已保留消息
- M→N：缩容保留最近 N 条（`CircularFifoQueue` 天然挤出语义，固化为规格）
- resize 与发送并发进行不再可能崩溃（字段 volatile + 锁内重读）

启用入口：会话配置 `CommonSessionSetting.setSendMessageCachedSize(n)`；
断线重连后的重发 API（`NetSession.resend` 三重载）自此真正可用，行为契约见
`openspec/specs/session-resend/`。

## 附带发现（移交，未在本变更修复）

测试桩 `MockNetTunnel`（tny-game-test 模块）close/disconnect 无幂等守护，
匿名会话场景无限递归 StackOverflow——真实链路 `BaseNetTunnel` 不受影响；
建议并入后续 `simplify-tunnel-unbound-rejection` 变更。
