# 发布注记（default-to-mac-generation）

## 变更等级：BREAKING（公开配置默认值语义翻转）

`tny-game-net-netty4` `NetPacketCodecSetting.verifier/crypto` 默认值：
`cRC64CodecVerifier`+`xOrCodecCrypto` → **`sipHash24CodecVerifier`+`xorTileCodecCrypto`**（键控认证代次）。
`verifyEnable/encryptEnable` 默认仍 false——**未开启校验/加密的部署完全无感**。

## 影响与升级次序

1. 仅"enable=true 且未点名 unit"的链路翻转帧内容（布局与 8B 长度不变，内容换代）；与未升级对端相遇为
   `PACKET_VERIFY_FAILED` 断连（清晰可观测，预期保护）。
2. 逃生舱（过渡期钉回旧语义）：
   ```yaml
   encoder: { verifier: "cRC64CodecVerifier", crypto: "xOrCodecCrypto" }
   decoder: { verifier: "cRC64CodecVerifier", crypto: "xOrCodecCrypto" }
   ```
   （注意 unit 真名为 `cRC64CodecVerifier`——首字母小写推导；历史文档中 `crc64...` 写法为笔误已订正）
3. Java↔C# 链路：C# SipHash 移植（peer-siphash-adoption.md 工单）完成前，先按 2 钉回旧对，工单闭环后摘除。
4. 收益：默认档吞吐 +45~48%、P50 0.92→0.71µs（96B），且首次具备真实防伪/防换皮重放语义——默认值翻转对
   多数开启方是纯升级。
5. 版本建议：`tny-game-net-netty4` minor（P11）。
