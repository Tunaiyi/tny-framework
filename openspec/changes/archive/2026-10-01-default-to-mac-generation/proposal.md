# Proposal

## Why

`verify/encrypt` 开启但**未点名算法 unit** 的部署，今天默认落到 legacy 配对（CRC64+XOr）：实测它同时是**最慢**（96B P50 0.92µs，比认证档慢 47%；1KB 慢 3-6 倍）和**最弱**（可样本消元伪造）的档位——"不配置"这个最普遍的选择恰好命中双输格。三个已归档变更（认证代次、快筛代次、optimize 收尾）已把替代件全部生产化并用 120+ 基准臂裁决：`sipHash24CodecVerifier + xorTileCodecCrypto` 是"键形无条件、全帧长无悬崖、零帧布局变更"的默认最优。默认值应当指向它。

## What Changes

- **BREAKING**：`NetPacketCodecSetting` 的 `verifier`/`crypto` 字段默认值由 `crc64CodecVerifier`/`xOrCodecCrypto` 翻转为 `sipHash24CodecVerifier`/`xorTileCodecCrypto`——对"开启校验/加密但未显式点名 unit"的链路，帧的校验码与混淆字节内容改变（布局与 8B 长度不变），与未升级对端互拒（预期保护行为）。
- 逃生舱：显式配置旧 unit 名即可完整保持 legacy 行为（含对 C# 长尾链路的过渡——其 SipHash 移植经外派工单跟踪中）。
- `verifyEnable`/`encryptEnable` 默认值**保持 false 不变**（本变更只翻"开启后用什么"，不决定"是否开启"——enable 默认翻转属 secure-by-default 独立议题，用户决策排除）。
- 文档：代次 readme 默认列更新 + 升级次序与 C# 依赖显式声明。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `net-protocol`: 新增需求"算法代次默认值"——启用校验/加密而未指定算法时，系统 MUST 装配键控认证代次（8B 键控 MAC + 逐字节等价的快速混淆流）；显式指定 MUST 覆盖默认；未启用校验/加密的链路 MUST 逐字节零影响；对按旧默认装配的端，MUST 以可观察校验失败拒绝而非静默误解密。

## Impact

- 模块：`tny-game-net-netty4`（`NetPacketCodecSetting.java:36,39` 两行默认值）；`tny-game-net` 抽象层与两实现件**零改动**（已在库，unit 名解析走现成 `UnitLoader`）。
- starter/下游：`tny-game-starter-net-netty4` 无代码改动但 yml 语义变——**所有"enable=true 且未点名 unit"的外部部署在升级后帧内容翻转**，发布说明标注 BREAKING（建议 minor）并给出逃生舱配置示例；仓内 demo/测试核查无隐式依赖（三处管线测试均显式注入件）。
- 跨端：Java↔C# 且 C# 未合入 SipHash 移植的链路，升级前必须显式钉 `verifier: "crc64CodecVerifier"`（与外派采纳工单联动，design 有次序条款）。
- relay/内网无 MAC 字段链路：零影响。性能方向：翻转即免费获得 +45~48% 吞吐与更真实的安全，无回退风险格。
