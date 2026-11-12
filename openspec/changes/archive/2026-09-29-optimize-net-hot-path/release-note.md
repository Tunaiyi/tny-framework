# RELEASE NOTE（optimize-net-hot-path）

- 新增 `tools/net-bench`（JMH 微基准模块，零发布、零生产代码影响；bench 不在 tny-game-* 命名空间，不受根装配线管理）。
- 基线数据入库：`tools/net-bench/baseline-2026-09-29.md`（环境/参数/原始数据/候选清单）。
- **两个"性能疑点"被基线证伪**（RespondFuture 全量扫描、MessageQueue 锁）——判定不优化，防止了两次看似合理的过度工程。
- 一项确定修复：CRC64 校验失败路径日志加 `isDebugEnabled` 守卫（消除坏包/攻击流量的整包 hex 放大，行为不变）。
- 遗留移交：`send2AllOnline` 语义决策（行为变更，独立提案）；编解码逐包分配延后至真实管线（JFR/GC）证据出现（design R1 上界原则）。
