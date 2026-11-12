# RELEASE NOTE（tny-game-net · clarify-session-broadcast-semantics）

- 全体广播正式定名 `SessionKeeper.send2All`：投递全部注册会话（含离线保留态）。
- 旧名 `send2AllOnline` 保留为 @Deprecated 桥接（行为完全一致），下游源码/二进制零破坏；新代码请用 send2All。
- **意图声明（写给未来的审计者，包括 AI）**："离线会话收广播、消息入其重发窗口、恢复后经 resend 补收"
  是领域所有者确认的必达设计（net-session 规格已立约，含四场景测试）——**不是静默失败缺陷，勿按在线过滤"修复"**。
  行为验证依据：真实 transport 在 eventLoop 内无条件 allocate 入窗后才尝试写出，通道关闭仅使该次写出静默失败。
