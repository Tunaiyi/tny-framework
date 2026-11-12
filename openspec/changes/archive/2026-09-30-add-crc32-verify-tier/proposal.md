# Proposal

## Why

档位矩阵（`tools/net-bench/crypto-bench-2026-09-30.md`）测得：现默认 CRC64 校验在 96B 报文上花 440ns/向，而 JDK 内置的 `java.util.zip.CRC32`（IEEE，硬件 intrinsic）实测 **11.7ns/向且零分配**——同为本框架"无防伪意图、只做快速过滤"的混淆定位（keyed-CRC 家族），成本却差 37 倍。对**不需要键控防伪**的链路（内网服务互连、高扇出推送、可信任对端），这 428ns/向的税买的是用不上的线性可解结构。框架的 verifier SPI 本就是为此准备的插槽（认证代次变更 `add-mac-generation-siphash` 已示范兄弟装配），本变更补上快筛一档。

## What Changes

- 新增 `tny-game-net/…/codec/verifier/Crc32CodecVerifier`：`@Unit` SPI 兄弟实现，基于 JDK `java.util.zip.CRC32`（x86 SSE4.2/新 aarch64 intrinsic）；混入序列与 `CRC64CodecVerifier`/`SipHash24CodecVerifier` 完全同构（number‖body‖accessKey‖code 流式 `update` 链，零每包分配）；校验码 4 字节（**短于现 8B，帧长随之缩短 4B**）。
- 不引入 `CRC32C`：IEEE 多项式与 .NET `System.IO.Hashing.Crc32` 官方实现直接对表（跨端零自研），实测速度与 32C 无差（11.7 vs 11.4ns）——选型理由入 design。
- 装配面与边界同认证代次模式：unit 名配置切换、**不进任何默认配置**、不动抽象层与编解码调用序、crypto 侧正交可任意组合（含与 `xorTileCodecCrypto` 搭配）。
- 明确不承诺防伪：本代次安全定位 = 现 CRC64 同级（无键可重算、线性），仅面向不需要防伪的链路；对外链路仍推荐认证代次。

## Capabilities

### New Capabilities

（无——`CodecVerifier` SPI 既有契约的又一个实现，P3 合规。）

### Modified Capabilities

- `net-protocol`: 新增需求"快速筛选代次的校验码与帧预算"（启用后校验码 MUST 为 4 字节且帧总长相应缩短；混入序列 MUST 与既有代次同构以保证代次间密钥材料语义连续；MUST NOT 承诺防伪能力——对外链路文档明示；默认配置 MUST 不启用本代次）。

## Impact

- 模块：仅 `tny-game-net` 新增一个 verifier 实现类；`tny-game-net-netty4` 零改动（解码端 `verifyLength = verifier.getCodeLength()` 机制现成对称，`NetPacketV1Decoder.java:138` 已证）。
- **混布代价高于认证代次（须在启用文档声明）**：校验码 8B→4B 改变帧总长——旧代次对端按 8B 切尾会把新代次帧的解析窗口错位 4 字节，触发的是**长度/解析类拒绝**而非清晰的可观测 `causeVerify`；两端必须严格同批，且建议按 tunnel/服务对逐链路启用。
- C# 对端：无需移植工作（IEEE CRC32 为 `System.IO.Hashing` 内置）——认证代次询价件（`peer-siphash-adoption.md`）可附带本档说明，跨端成本几乎为零。
- 基准：矩阵将新增生产装配组合 `prod_crc32`（原型 `crc32c_xorTile` 1.657M 的 IEEE 同速版本，验收锚 ≥1.5M）。
- 既有部署零影响（默认不启用）；与 `add-mac-generation-siphash` 无文件冲突（同为 SPI 兄弟 + 文档代次表追加行）。
