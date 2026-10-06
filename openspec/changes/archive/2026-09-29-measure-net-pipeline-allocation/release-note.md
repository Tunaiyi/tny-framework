# RELEASE NOTE（measure-net-pipeline-allocation）

**零生产代码改动**（本声明履行）。交付：
- `tools/net-bench/allocation-profile-2026-09-29.md`：P3 候选全部裁决为"按生产口径不值得做"，
  附完整方法论修正链（mockito 污染、Unpooled/Pooled 环境差、D1 判据两处自身错误）；
- 基线勘误：codec 吞吐 105k→2.05M ops/s（真实值提高 20 倍——mock 拦截曾同时污染吞吐与分配）；
- 新议题移交：CRC64 verify 的 CPU 吞吐税 -53%（非分配税），建议写入部署配置文档告知容量折减；
- bench 资产修正：PacketCodecBenchmark 去 mockito（BenchTunnel 手写桩 + firstContext 占位 handler 陷阱记录）。
若未来出现 io allocator=pooled 失效场景或真实流量 young GC>1/s，本 profile 即重开裁决的对比基线。
