# 报文校验/混淆 安全代次装配指南（net-protocol）

编解码防线的两个 SPI（`CodecVerifier`/`CodecCrypto`）按 unit 名装配代次实现，
`NetPacketCodecSetting` 的 `verifier`/`crypto` 字段（`security-keys` 同级）即开关。

## 现有代次

| 代次 | verifier unit 名 | crypto unit 名 | 校验码 | 能力 | 定位 |
|---|---|---|---|---|---|
| legacy（**需显式点名**，unit 真名 `cRC64CodecVerifier`/`xOrCodecCrypto`） | `cRC64CodecVerifier` | `xOrCodecCrypto` | 8B | 无键校验（样本可解元伪造）+ 静态键流混淆 | 保持至长尾走完 |
| **认证代次（默认，default-to-mac-generation 起）** | `sipHash24CodecVerifier` | `xorTileCodecCrypto` | 8B | **键控 MAC**（无密钥不可造码；number 绑定杀换皮重放）+ 键流逐字节等价的快速混淆 | 对外链路基线 |
| 快筛档 | `crc32CodecVerifier` | （正交任选） | **4B** | IEEE CRC32 硬件 intrinsic（~12ns），**无防伪**（无键可重算） | 内网/高扇出/可信任对端 |

## verifier × crypto 正交组合矩阵（终轮 2026-10-01 生产对实测，中位 Mops/s；k9|k16 键形）

| verifier \ crypto | `xOrCodecCrypto`(升级后) | `xorTileCodecCrypto` |
|---|---|---|
| `cRC64CodecVerifier`(链式，unit 真名首字母小写见上表) | **0.88 \| 0.86**（显式点名逃生舱，默认已翻认证代次，混淆级） | 0.87 \| 0.90（纯 crypto 升级**不提速**——税在算法本体） |
| `sipHash24CodecVerifier` | 1.32 \| 1.37（键形 8-对齐时最优） | **1.27 \| 1.37 ★默认认证推荐**（键形无条件、零 wire 变更） |
| `crc32CodecVerifier` | 1.49 \| 1.50 | **1.55 \| 1.69 ★快筛推荐**（4B 尾，两端同批） |

能力轴（防伪与否）优先于性能轴选择档位；表内数字仅同窗可比（96B 帧口径）。

**帧长限定（E2/尺寸维终裁，crypto-bench §十）**：上表为 ~96B 小帧口径。大帧形态（body≥512B）格局变化剧烈——
快筛对认证的领先扩至 ~1.9x；敏感档（sipHash24+真流加密）从"小帧 -6~8%"**反转**为大帧 2.4x 优于默认档；
而 legacy 档（显式点名逃生舱）1KB 总税 -85.5%，**大 body 链路一律禁用 legacy**，优先快筛（内网）或认证+流加密（对外）。
默认档升级实测幅度（v2/v1 同窗配对）：+6~9%（96B），非提案预期的 15~43%。

## 快筛档错配症状预告（4B 尾特有）

校验码 8B↔4B 代次错配时，接收端反推 bodyLength 的窗口错位 4 字节：症状多为**解码/长度类异常**
（`DECODE_ERROR` 断链）而非清晰的 `PACKET_VERIFY_FAILED`——两端必须严格同批切换，
灰度建议按服务对逐链路启用（不同于认证代次的"症状同为 verify failed"，运维需预告）。

## 启用示例（yml，`NettyChannelSetting` 编解码两段必须同代次）

```yaml
encoder:            # 认证代次已是默认，此段显式书写仅为自文档化；旧链路逃生舱写 cRC64CodecVerifier/xOrCodecCrypto
  security-keys: ["..."]
  verifier: "sipHash24CodecVerifier"
  crypto: "xorTileCodecCrypto"
decoder:
  security-keys: ["..."]
  verifier: "sipHash24CodecVerifier"
  crypto: "xorTileCodecCrypto"
```

快筛档（内网/高扇出；crypto 正交任选，示例配键流逐字节等价的 `xorTileCodecCrypto`）：

```yaml
encoder:
  security-keys: ["..."]
  verifier: "crc32CodecVerifier"   # 4B 尾——两端必须同批切换，错配症状见上「错配症状预告」
  crypto: "xorTileCodecCrypto"
decoder:
  security-keys: ["..."]
  verifier: "crc32CodecVerifier"
  crypto: "xorTileCodecCrypto"
```

## 切换约束（必读）

- **同一链路两端必须同批切换**：认证代次内（`cRC64CodecVerifier`↔`sipHash24CodecVerifier`）
  帧布局与校验码长度不变（8B）、仅校验码内容语义不同——单端切换会使该链路全部启用校验的
  报文被对端按既有"校验不通过终止连接处理"契约拒绝（表现为 `PACKET_VERIFY_FAILED` 断连，
  属预期保护，不是缺陷）；**快筛档不适用本条**：启用即帧尾 4B、帧长随之缩短，
  错配症状见上「快筛档错配症状预告」。
- `xorTileCodecCrypto` 可**单独**启用（键流与 `xOrCodecCrypto` 逐字节等价，零 wire 影响，
  等价性由 `XorTileCodecCryptoEquivalenceTest` 钉死）；`sipHash24CodecVerifier` 必须两端同批。
- 跨端（C#）：认证代次启用前，对端需移植 SipHash-2-4（公开规范 + 官方 64 向量表，
  本仓库 `SipHash24Test` 使用同一组向量，可直接做互测金样）。

## 安全边界声明（认证代次覆盖什么/不覆盖什么）

| 威胁 | 覆盖？ | 依据 |
|---|---|---|
| 改包/构造非法报文（无客户端密钥者） | ✅ 杀 | 键控 MAC，截断 8B，在线瞎猜率 2⁻⁶⁴/包 + 失败即断 |
| 同连接重放与"换皮重放"（旧载荷新包号） | ✅ 杀 | number 参与 MAC 输入 + 帧号严格单调 |
| 跨连接整帧重放 | ❌ 不覆盖 | 静态密钥下旧帧在新连接仍合法——待 session number 水位继承提案（另立） |
| 反射（下行帧回发） | ❌ 不覆盖 | 双向共密钥——待 R2 会话密钥方向分离（另立） |
| 密钥提取（设备沦陷/逆向客户端） | ❌ 不覆盖 | 任何端侧密码学的公共天花板——泄露半径收敛待 R2 |
| 被动流量分析 | △ 同 legacy | xorTile 键流等价（混淆定位不变）；更强保密待 chacha 流档（需先实测） |

性能锚（96B 全管线，JDK25 aarch64，`tny-benchmark/crypto-bench-2026-09-30.md（历史口径数据）`）：
legacy 0.909M → 认证代次 1.314M（+45%；内联原型上界 1.563M，差值=ThreadLocal+契约分配，可收回）。
