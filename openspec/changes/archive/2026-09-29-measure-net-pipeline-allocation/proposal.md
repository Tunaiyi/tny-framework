# Proposal

## Why

基线报告（baseline-2026-09-29）将"编解码逐包分配"（NettyWasteReader/Writer 每包 new、verifyCode byte[]、bodyBuffer、每消息 RpcTransactionContext/RpcInvokeContext/RpcInvokeCommand 对象组）列为 P3 候选并附 R1 警示：微基准数字是上界，JIT 逃逸分析在真实调用链可能已消化大部分分配——**优化与否缺两层证据：per-op 分配字节数的确定性量化，以及真实管线（EventLoop+完整派发链）下的分配热点构成**。凭现状决定，要么错杀真瓶颈候选，要么为微基准幻觉动工。

## What Changes

纯测量变更，生产代码零改动：

- **L1（确定性，必做）**：对现有 bench 三基准挂 JMH `-prof gc` 复跑，量化每操作分配（bytes/op、GC 计数），verify 开/关对照——结果入库 `allocation-profile-2026-09-29.md`。
- **L2（真实管线采样，条件执行）**：本地起 demo `GameServerApp` + `GameClientApp` 压入稳定流量，`-XX:StartFlightRecording` 采集 JFR，用 `jdk.ObjectAllocationInNewTLAB`/`jdk.ObjectAllocationOutsideTLAB` 聚合 top 分配点，与 L1 交叉印证。**预声明降级**：若 demo 在本机无法启动（依赖 mongo/redis 等），L2 记录降级为"待有环境时补采"，L1 结论照常交付——不重演临时降级。
- **裁决输出**：P3 候选三态判定（值得做 / 不值得 / 需真实流量复判），附判定公式（per-op 分配 × 目标 TPS 对 young 分配预算）与数字依据。

## Capabilities

### New Capabilities

（无——skip_specs，纯测量与决策文档。）

### Modified Capabilities

（无——仅在基线文件追加引用注记，不改其内容。）

## Impact

- 仅 `tools/net-bench/`（新增 profile 文档与运行说明一行）与变更目录决策文档；生产代码、测试、账本零触碰。
- 若裁决为"值得做"，产出物直接成为后续优化变更的输入（含基线数字与热点排序）。
