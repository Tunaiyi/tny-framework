# send2AllOnline 三选一决策材料（交后续变更决策，本变更不实施）

现状：`AbstractSessionKeeper.send2AllOnline:120-123` 遍历 sessionMap.values() 全量 send，
不判在线；向 OFFLINE/CLOSE 会话发送时 `BaseNetSession.send` 仅拦 CLOSE（isClosed），
OFFLINE 会话的 tunnel 已关闭 → 写出走 transport 失败路径（异常被 write 层吞或 promise 悬置），
行为不可控。

| 方案 | 行为 | 兼容性 | 评估 |
|---|---|---|---|
| A 按 isOnline() 过滤 | 离线会话静默跳过 | 依赖"发离线"者实际从未成功收到（写出即失败），从不可控→可预测，无真实破坏 | **倾向**：名实一致 + 可规格化（"广播仅达在线会话"） |
| B 改名 send2All | 行为不变名实一致 | 抽象模块 public API BREAKING（P11） | 成本高于收益 |
| C 维持+文档 | 全不变 | 无 | 债务后移 |

建议后续变更：方案 A + `session-broadcast`（或 net-session 追加）规格一条："定向全体广播仅投递给在线会话"。
影响面提示：游戏服务器全服公告场景离线玩家本就收不到（连接不在），A 与现网可观察行为差异≈0，
差异仅在内部失败的静默形态。
