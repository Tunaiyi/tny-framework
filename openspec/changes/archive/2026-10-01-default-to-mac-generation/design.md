# Design

## Context

用户决策（2026-10-01）：默认翻转只翻"开启后用什么算法"，**不动 enable 开关**（secure-by-default 另议）。默认值的现居处：`NetPacketCodecSetting.java:36,39`（netty4 实现模块的字段初始化 `lowerCamelName(CRC64CodecVerifier.class)` / `XOrCodecCrypto`）。替代件均已在库并带重等价证明：`SipHash24CodecVerifier`（官方向量锚定 + 66k 穷举等价链）、`XorTileCodecCrypto`（oracle 全等 + 全输入域护栏）；装配解析走现成 `NetPacketV1Codec.prepareStart` 的 UnitLoader 路径。仓内 grep 证实无隐式依赖旧默认的 demo/测试（三处管线测试均显式注入件）。

## Goals / Non-Goals

**Goals:** 让"开启但未点名"落到唯一无双输象限的档位（更快且真安全）；逃生舱一等公民；混布行为可观察。
**Non-Goals:** 不动 `verifyEnable/encryptEnable` 默认；不改任何实现类；不引入新 unit 名/别名；不处理 C# 移植本身（外派工单）。

## Decisions

1. **翻转载体 = netty4 `NetPacketCodecSetting` 两字段初始化值**（P1：抽象层无 unit 名字符串概念，下沉默认值反而污染抽象；该模块为已发布实现模块，默认值属其公开合同 → 按 P11 标 BREAKING，建议 minor）。
   否决：在抽象层 `DataPackCodecOptions` 放默认 unit 名——violates P1/P2（抽象层不应知道具体实现类的名字）；否决：连 enable 一起翻——用户显式排除。

2. **零新代码引用既有生产件**：默认值即两个已归档变更交付的 unit 名（`sipHash24CodecVerifier`/`xorTileCodecCrypto`）。
   否决：新增 "default" 别名 unit——多一层无信息的间接（M2 模式克制）；否决：等待 R2 会话密钥一起翻——默认与密钥来源正交，R2 落地后同两名不变、仅密钥派生升级（前轮 securityState 设计的直接红利）。

3. **跨端次序条款（发布检查单）**：与未完成 SipHash 移植的 C# 对端互通的链路，升级本版本前必须显式钉 `verifier: "crc64CodecVerifier"` + `crypto: "xOrCodecCrypto"`（逃生舱），对端合入并经 `peer-siphash-adoption.md` 金样互测后摘除。release 注记引用该工单编号形成闭环追踪。

4. **测试先行策略**（P13，逐 Scenario 认领）：netty4 新增 `MacGenerationDefaultTest`——
   (a) 默认装配断言：enable 但未点名 → 帧尾 8B 与测试内独立 SipHash 参考链重算全等，且与预存的 legacy 金样帧**不等**（证明翻转生效）；
   (b) 逃生舱：显式点名旧两件套 → 与金样帧逐字节一致；
   (c) 未 enable 零影响：默认关闭时帧字节与金样一致（不触达新件）；
   (d) fail-fast 保持：enable 且无密钥 → 启动校验异常（既有测试面回归）。
   (a) 在翻转前必红、翻转后必绿；(b)(c)(d) 全程绿（回归护栏）。

## Risks / Trade-offs

- **外部下游不可枚举**：所有"enable=true 未点名 unit"的下游升级即翻语义——缓解=逃生舱一行配置 + BREAKING minor 注记 + 升级序（先钉后摘）；这正是 P11 三问中"报文格式向后兼容？否"的显式代价。
- **性能**：默认档即更快档（认证配对 vs 旧默认：+45~48% 吞吐、P50 0.71 vs 0.92µs）——无回退风险格；SipHash 件 24→64B/包的额外分配为零（ThreadLocal scratch）。
- **可观测性**：混布症状为 `PACKET_VERIFY_FAILED` 断连（清晰），优于快筛档曾记录的 4B/8B 错配"解码类异常"症状——本翻转无此问题（8B 等长）。

## Compatibility Impact

- 公开合同：`tny-game-net-netty4` 配置类默认值（Spring yml 绑定面）行为变更 → **BREAKING**，minor 版本；`tny-game-net` 抽象模块与两实现类零 diff。
- 未启用校验/加密的绝大多数现存部署：零影响（enable 默认 false 是最大缓冲垫）。
- 帧布局不变（8B 校验码等长）⇒ 抓包工具、长度预算、废字节位全部兼容；仅校验码/密文**内容**换代。
- relay 内网帧、心跳、超限防线、序号机制：零触碰。
