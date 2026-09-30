# Proposal

## Why

现网默认"校验+加密"防线（CRC64 逐字节 + 取模 XOR）经 `tools/net-bench` 实测：全管线往返 0.909M ops/s（校验+混淆合计税 −60%），且 keyed-CRC 的 GF(2) 线性结构使攻击者可通过样本消元重算任意改包的校验码——**花了最贵的钱，没买到防伪能力**。基准档位矩阵（`crypto-bench-2026-09-30.md` 第七节）实测出平衡替代：**SipHash-2-4 键控 MAC（60ns/向）+ tile 合并键流 XOR（77ns/向）组合达 1.563M ops/s（+72%），校验从"可重算"升级为"无密钥不可造"**。本变更引入该"认证代"作为可装配的算法代次（默认仍为 legacy，按 bootstrap 配置启用）。

## What Changes

- 新增 `tny-game-common-digest`：`SipHash24` 流式键控 MAC 算法件（c=2/d=4，多段吸收；**官方向量表逐条核验**为合入前置）。非 BREAKING（新增公共类型）。
- 新增 `tny-game-net` verifier 家族实现：`SipHash24CodecVerifier`（`@Unit`，`getCodeLength()=8` 与现 CRC64 等长、混入序列与 `CRC64CodecVerifier` 完全同构：number‖body‖accessKey‖code；实例态经 `FastThreadLocal` 隔离，遵守共享单实例现状）。
- 新增 `tny-game-net` cryptoloy 家族实现：`XorTileCodecCrypto`（`@Unit`，键流输出与 `XOrCodecCrypto` **逐字节等价**——含 fix-xor-crypto-scope 修正后的全窗/相对相位语义——仅实现形态为周期预合并+计数器循环）。
- 装配面：`NetPacketCodecSetting.verifier/crypto` 现有 unit 名配置切至新实现即启用本代次；**不引入 option 新位、不引入每连接策略、默认行为零变化**。跨连接重放的 session 级 number 水位继承、代次共存位（bit5）**明确不在本变更范围**（各自后续独立提案）。
- 无公共 API 签名变更、无帧布局变更（8B 校验码槽位不变）。

## Capabilities

### New Capabilities

（无——两实现均为既有 `CodecVerifier`/`CodecCrypto` `@UnitInterface` SPI 的兄弟实现，遵循 `ObjectCodecFactory`/`MessageBodyCodec` 家族先例，抽象契约零改动，P3/P10 合规。）

### Modified Capabilities

- `net-protocol`: 新增需求——"键控消息认证代次"（配置启用后：校验码 MUST 为键控 MAC，无密钥方 MUST NOT 能为改包构造合法校验码；混入序列 MUST 绑定包号使换皮重放失效；混淆键流 MUST 与现有键流在相同密钥材料下逐字节等价，保证密文语义连续；校验码长度与帧布局 MUST 不变）；同时新增"默认代次不受影响"兼容 Scenario（未启用配置的部署行为逐字节不变）。

## Impact

- 模块：`tny-game-common-digest`（+1 类）、`tny-game-net`（+2 类，`verifier`/`cryptoloy` 包按先例放置）；`tny-game-net-netty4` 编解码**零改动**（SPI 消费方）；`tny-game-starter-net-netty4` 零改动（unit 名装配链现成）。
- 下游：启用代次需**链路两端同批切换配置**（混布期新代次帧的 8B MAC 在 legacy 对端上按 CRC64 重算必不匹配 → 按现有"校验不通过终止该连接处理"契约拒绝——既有防线自然承接，无需新行为）；未启用的全部现有部署零影响。
- C# 对端：启用代次需移植 SipHash-2-4（有公开规范与向量表）；审计问询可搭 `2026-09-30-fix-xor-crypto-scope/peer-audit-ticket.md` 同批发出。
- 基准与验证：`tools/net-bench` 管线矩阵新增生产装配组合 `siphash24_xortile`（unit 名注入，替代基准内联原型），复测锚定 1.563M 量级；SipHash 官方向量测试为合入门槛。
- 性能预期：ENCRYPT/VERIFY 双开链路往返 1095ns→约 639ns（+72%）；本代不改变混淆强度定位（抗分析层），防伪能力为新增项；反射/跨连接重放防护属 R2/水位后续提案，本代文档如实声明边界。
