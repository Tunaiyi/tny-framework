# 分配画像（measure-net-pipeline-allocation · 2026-09-29）

环境：Corretto 21.0.12 / macOS aarch64 / JMH 1.37（`-prof gc`、`-prof jfr`），`-f 2 -wi 3 -i 5 -w 500ms -r 1s`

## 方法论修正记录（本文件一半的价值在此）

1. **mockito 污染**：首轮 codec 基准 10560 B/op / 105k ops/s 系失真——JFR 栈归因显示分配大头
   与耗时大头均为 mockito 拦截（MockWeakReference/newMemberName/LocationImpl，~80%）。
   修正：bench 去 mockito（手写零分配 BenchTunnel + 真实 pipeline context；
   过程另捕获 netty `firstContext()` 在无 user handler 时返回 null 的陷阱，加占位 handler）。
2. **基线勘误**：`baseline-2026-09-29.md` 的 codec 行（104.9k/95.1k ops/s）为 mock 污染值，
   干净版：**verify=false 2.05M ops/s、verify=true 0.97M ops/s**（基线文件本体不改，此处为正式勘误）。
3. **D1 判据自身两处错误（数据面前认错）**：① "≥512B 即 humongous"错——G1 阈值实为
   regionSize/8（默认 ≥128KB），3KB 永非 humongous；② 2KB 绝对线未锚定 TPS，判据不完整。
   裁决按实际机制执行（见下）。
4. **人造环境差（R1 上界规律的第三次显形）**：EmbeddedChannel 用 UnpooledByteBufAllocator，
   生产 netty 默认 **Pooled**——3072 B/op 的大头（allocateUninitializedArray 5.7GB/s 归属）
   是 Unpooled 场景的缓冲背板分配，生产中大部分被池化吸收。微基准第三次给出"上界而非真值"。

## 最终数据（干净版）

| 基准 | 参数 | thrpt (ops/s) | alloc (B/op) | 结论 |
|---|---|---|---|---|
| codec.encodeThenDecode | verify=false | 2,049,277 ±631k | **3072 ±0** | 大头=Unpooled 背板；业务码分配占极小尾部 |
| codec.encodeThenDecode | verify=true | 968,705 ±16k | 3737 ±2 | **CRC64 吞吐税 -53%（CPU 性，非分配性）**，新发现 |
| queue.addMessage | cap=0 / cap=64 | 672M / 73M | ≈0 / ≈0 | 缓存本体零分配（volatile 短路+复用队列），P3 相关疑点关闭 |
| queue.filteredRead | 64 | 31.6M | 328 | 仅重连瞬间调用，非热路径 |
| respond.putAndPoll | 1k/10k | ≈18M | 112 / 112 | 在册规模不敏感（与基线一致）|

## P3 候选三态裁决（对应 design D1）

| 候选 | 裁决 | 依据 |
|---|---|---|
| WasteReader/Writer 每包 new | **不值得做** | 未进 JFR top 分配榜——JIT 标量替换已消化（P13 预言方向验证） |
| verifyCode byte[] 池化 | **不值得做** | 归属榜外，量级 <10B 级 |
| 每消息上下文三连 | **不值得做** | 榜外（<6MB/11GB 级） |
| bodyBuffer/Unpooled 背板 | **不值得做（生产口径）** | 大数来自 bench 的 Unpooled 人造环境；生产 Pooled 池化吸收。若未来有部署强制 `io.netty.allocator.type=unpooled` 或真实流量出现 young GC 高频（>1 次/秒 且分配率>1GB/s）→ 按"需流量复判"重开，本文件即对比基线 |
| **新议题（移交）**：CRC64 吞吐税 53% | 非分配问题，CPU 税 | 写入配置文档/告知部署方：开启 verify 需按此容量折减评估（97 万 ops/s 天花板下依然宽裕）；不构成代码变更 |

**L2 状态**：demo 真实管线 JFR——降级（设计 D2 判据）：本机无 redis 且 demo 配置默认连 redis
（application-client.yml `r2.redis.host`），bootRun 亦未配置；其验证目的（识别 bench 环境偏差）
已由上文修正 4 达成，实质完成。环境具备时可按 README jfr profiler 命令补采。
