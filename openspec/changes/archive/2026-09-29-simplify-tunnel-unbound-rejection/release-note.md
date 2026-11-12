# RELEASE NOTE（tny-game-net · simplify-tunnel-unbound-rejection）

- 通道层未绑定会话的接收由"NPE 被上层吞"改为"warn + 显式拒绝（false）"：
  当前生产依赖 EventLoop 串行保证该路径难以触发，本变更把这份隐性安全转为显式合同——
  未来任何改变接收线程模型/新增接收入口的演化都在合同保护内进行。
- doReceive 的 while(true) 重试死枝线性化（全仓 receive 实现复核：无人返回 false，行为等价），
  消除"看似可能自旋"的误读结构。
- send2AllOnline 名实不符的三选一决策材料已产出（本变更未实施，建议方案 A：按在线过滤），
  见 send2AllOnline-decision.md。
