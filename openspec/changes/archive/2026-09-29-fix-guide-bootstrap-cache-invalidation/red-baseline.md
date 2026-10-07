# 红灯基线（2026-09-29）

④ closeInvalidatesBootstrapCache FAIL（NettyServerGuide.close 未清 bootstrap 缓存）
⑤ relayServerGuideCloseInvalidatesBootstrap FAIL（同型）
既有 ①②③ pass（组隔离/重建/isBound 不回归）。
修复后 5/5 全绿（见任务 3.1 记录）。
