# Design

## Context

动机见 proposal。已知事实：`tools/net-bench` 三基准在位（PacketCodec verify 参数化 / MessageQueue / RespondFuture），gc profiler 为 JMH 内建（命令行 `-prof gc`，无构建改动）；demo 三 Boot 应用与 `.run` 配置在位，GameServerApp 的启动依赖面（是否需外部存储）待任务 1.3 现场确认。

## Goals / Non-Goals

**Goals:** per-op 分配确定性数字；真实管线 top 热点（或合规降级记录）；三态裁决与判据。
**Non-Goals:** 不做任何分配优化（裁决产物交后续变更）；不引入新的基准主题（现有三基准覆盖疑点面）。

## Decisions

**D1 判据公式（先立规则后看数，防事后合理化）**：以 `每消息分配字节 × 目标TPS` 对比 young 代分配吸收能力（G1 默认 region 与 512B 对象阈值：≥512B 直接进 old 有额外代价——bodyBuffer 尺寸恰好敏感）。裁决线：
- 每消息分配 < 2KB 且无 ≥512B 高频对象 → **不值得做**（P3 关闭）；
- 存在 ≥512B/条高频对象（如大 bodyBuffer） → **值得做**（对象复用/pool 议题，后续变更按热点排序）；
- 介于其间或 L1/L2 结论矛盾 → **需真实流量复判**（挂起，记明所需数据）。
判定规则先于数据入档，是本单对"事后找理由"的防御。

**D2 L2 条件执行的操作化判据**：`bootRun GameServerApp` 三分钟内出现"监听成功"日志视为可启动（demo 用内存态配置的可能性高，`.run` 配置的存在暗示可本机跑）；超时/报错则走预声明降级路径，不投入修复 demo 环境的时间（那是另一件事）。

**D3 数据落档格式**：profile 文档头部固定记录环境（JDK/OS/JMH 参数），数字表含 ±error——与 baseline 文件同构，保证未来可复测可比。

## Risks / Trade-offs

- **R1**：`-prof gc` 与 fork/JVM 参数耦合（TLAB 随机性），单 fork 波动大 → `-f 2` 起步取均值，误差列必须入表。
- **R2**：L2 demo 流量小样本可能淹没分配热点在 JIT 噪音里 → 只做 top-20 排序核对（不看绝对值），定性用途。

## Open Questions

无。
