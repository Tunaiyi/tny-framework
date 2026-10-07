# Design

## Context

认证代次 = 既有 `CodecVerifier`/`CodecCrypto` 两个 `@UnitInterface` SPI 的兄弟实现 + 装配切换，不新增抽象、不动编解码调用序。爆炸半径核查（codegraph 接口边查询无出边——Java 接口调用边不入索引，以 grep 为准）：两个 SPI 的生产消费点仅 `NetPacketV1Encoder.java:94/:103` 与 `NetPacketV1Decoder.java:149/:160`，经 `NetPacketV1Codec.prepareStart:51-65` 的 `UnitLoader` 按配置字符串解析——新增实现类对任何现存路径零影响，仅在被 unit 名显式配置时生效。

实测预算（`tools/net-bench/crypto-bench-2026-09-30.md`）：目标组合全管线 1.563M ops/s（vs legacy 0.909M，+72%）；SipHash 60ns/向 vs 生产 CRC64 440ns；XOr 取模形态 145ns/向（fix-xor-crypto-scope 后为真实付费）。

## Goals / Non-Goals

**Goals:**
- 提供"配置即启用"的键控认证代次：防伪（无钥不可造码）、换皮重放拦截（包号绑定）、帧布局零变化、键流与既有实现逐字节等价。
- 算法正确性以公开规范向量表为合入门槛。

**Non-Goals:**
- 跨连接重放防护（session number 水位继承）、反射拦截与泄露半径收敛（会话密钥 R2）、代次共存位（bit5）——均明确另立提案（用户决策 2026-09-30）。
- 真加密档位（siphash_chacha 属后续，需先补持久密钥流形态实测）。
- 每连接策略装配改造（本代次为 bootstrap 全局切换）。

## Decisions

1. **MAC 混入序列与生产 CRC64 完全同构：`number4 ‖ body ‖ accessKey ‖ code4`**。
   依据 P4（新代次继承同一不变量：校验码绑定包号与密钥材料）、P1（跨端移植面最小——C# 只需替换算法本体，输入构造逐字节沿用）。
   否决：基准探针用的"number 折入密钥 tweak"（域分离弱、非正式构造，仅作计时探针）；否决：仅 body 参与 MAC（旧载荷换新包号即可换皮重放，规格 Scenario 3 直接失败）。

2. **算法件 `SipHash24` 手写入 `tny-game-common-digest`，以官方 64 向量表为合入门槛；禁走 JCE**。
   依据 P13（每个安全断言有向量测试认领；"实现正确性"的判据是公开测试向量而非自洽往返）。SipHash 不在 JCE Provider 中，本无第二选择；但决策显式记录"未来亦不以 JCE 包装替换"——本会话对 JCE 的两次实测（GCM 1824 B/op、ChaChaPoly 1357ns）证明 **JCE 每调用机器税是大头，密码学件的性能正确形态是"密钥装载一次、状态复用/流式吸收"**（`AeadRfc7539` 0 B/op 为同一结论的对照证据）。
   否决：HMAC-SHA256 截断（实测双端 421ns，7× 于 SipHash，仅换"强度档位"无必要）；否决：引入 BouncyCastle 依赖（P10 三次法则：当前仅一个场景需要，且向量表核验不依赖库）。

3. **实现态隔离：verifier 内流式 SipHash 累加器实例经 `FastThreadLocal` 持有**。
   依据 P13 热点条款与 `NetPacketV1Encoder.java:27,57-63` 的 `MemoryAllotCounter` 同款先例——verifier 为全连接共享单实例（`DatagramChannelMaker.java:36-41`），任何每帧可变状态都必须线程封闭。实现按 P1 修正落点：抽象层 `tny-game-net` 无 netty 依赖（实证 `build.gradle`），采用 JDK `ThreadLocal`（语义同型）；netty4 装配层的 `FastThreadLocal` 化列为后续可选项，不属本变更。
   否决：把累加器挂 `DataPackageContext`（那是 R2 的 `securityState` 槽位职责；本代次密钥静态、无每连接状态可挂，P5）；否决：每次 generate 新建对象（无必要的每帧分配）。

4. **`XorTileCodecCrypto` 采用"周期预合并 tile + 增量计数器"字节循环，不采用字级 long 展开**。
   依据实测：字级版含每帧 tile/longs 数组分配（96 B/op）；计数器版零分配且逐字节成本相近（取模已由增量索引消灭）。P13 的分配账优先于 20ns 的 CPU 账。等价性以随机窗口×随机键长的全等断言钉死（对 `XOrCodecCrypto` 输出逐字节比对，含 fix-xor 后的全窗/相对相位语义）。
   否决：tile+long 版（基准已证分配税）；否决：顺手并入字级 SIMD 式优化（优化不属本变更，等价性验证面翻倍）。

5. **启用方式 = bootstrap 配置切换，不加帧内代次位（用户决策）**。
   混布保护复用既有契约："校验不通过 MUST 终止该连接处理"（`net-protocol` 现存需求）与 `NetPacketV1Decoder` 的 option/配置双向硬拒——新代次帧的 8B MAC 在旧端按无键算法重算必然不匹配、被既有路径拒绝，**无需为共存新增机制**。
   否决：本变更加 bit5+每连接策略（把 R2 的前置工程吃进止血件，体积与回滚粒度劣化）。

6. **默认不启用、文档先行**：新实现合入后默认代次仍为 CRC64/XOr；启用清单、两端同批约束、跨端向量对齐流程写入 `net-protocol` 文档与 demo 配置示例。
   依据 P11（发布即合同：默认行为零变化使"新增能力"本身不触发兼容性三问）。

## Risks / Trade-offs

- **防伪强度的密钥前提**：本代次密钥仍是全局静态派生——防的是"不持有客户端的人"；设备沦陷提取密钥后与 legacy 同罪。文档必须明示（这是 R2 的存在理由，不是本变更的缺陷）。
- **8B 截断 MAC**：在线瞎猜率 2⁻⁶⁴/包 + 既有"校验失败终止连接"截断尝试次数，充分；不采用 16B（帧预算与现槽位不符）。
- **双端不同批**：单边启用 = 该链路全拒（可观测、不静默）——由规格 Scenario 6 认领，发布文档给升级次序。
- **C# 移植周期**：不阻塞合入（默认关）；向量表与 Java 侧共用同一 JSON 样本，互测通过后再启用。

## Compatibility Impact

- 公共 API：仅新增类型（`SipHash24`、两个 `@Unit` 实现），无签名/行为变更于既有类型——非 BREAKING。
- 报文格式：帧布局、校验码长度、混淆字节序（与既有键流全等）均不变；**内容语义**仅在被配置启用时改变（新代次帧与旧代次帧互不互解，按既有拒绝契约处置）。
- relay/内网链路：中继帧无校验字段（`RelayPacketV1` 系）不受影响；网关终结客户端帧，切换时与客户端同批（对端账本与 `peer-audit-ticket.md` 同路发出）。
- 下游 starter：无代码改动，`NettyChannelSetting`/yml 以 unit 名启用。
