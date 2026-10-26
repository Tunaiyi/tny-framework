# C# 对端：认证代次采纳询价（待发送，草稿）

关联：`archive/2026-09-30-fix-xor-crypto-scope/peer-audit-ticket.md`（同路发出，一并回答）。

## 意向

Java 框架已合入报文"认证代次"（键控 MAC 替换无键 CRC，帧布局/校验码长度不变）。
**默认未启用**；启用需两端同批切换配置。请评估 C# 端移植 SipHash-2-4 的排期。

## 需要确认的三件事

1. **算法移植**：SipHash-2-4（c=2/d=4，64-bit 输出取小端 8 字节）——公开规范
   https://github.com/veorq/SipHash （CC0），金样即其 `vectors.h` 官方 64 向量表
   （key=`000102...0f`，输入=`00..(n-1)` 前缀；Java 侧 `SipHash24Test` 使用同一张表）。
2. **MAC 输入构造**（与现 CRC 混入序列同构，逐字段沿用现有值）：
   `SipHash24( k = accessKeyBytes[0..8] ‖ [8..16],  msg = number4(LE) ‖ 载荷明文 ‖ accessKeyBytes ‖ packetCode4(LE) )`，
   其中 accessKeyBytes = MD5(accessId 十进制字符串 + securityKeys[accessId mod 数量])（与现 Java 派生一致）。
3. **同批切换约束**：任一侧先行启用会造成该链路校验失败断连（预期保护行为）。
   请确认发布窗口能否与服务端对齐；不能则本代次对 C# 链路保持关闭（Java 侧支持按 bootstrap 配置共存）。

## 共享金样（Java 侧已导出，由 `tny-game-common-digest` 的 SipHash24 直算，与官方表同源互验）

| 用例（K 为 16 字节密钥，msg 为输入字节串） | 期望 8B MAC（hex） |
|---|---|
| K=`000102...0f`; msg=空 | `310e0edd47db6f72`（= 官方向量表 vectors[0]，交叉印证） |
| K 同上; msg=`616263`（"abc"） | `a50720aa53fabc5d` |
| K 同上; msg=8B `00..07`（整字边界） | `6224939a79f5f593` |
| K 同上; msg=9B `00..08`（跨字残块） | `b0e4a90bdf82009e` |
| K 同上; msg=17B `00..10` | `9447be2cf5e99a69` |
| K 同上; msg=137B `i*7+3` | `8d2b9ccff605aa36` |
| K'=同 K 仅 bit0 翻转; msg=137B 同上 | `37d37ba4e7dc00b6` |

MAC 输出为 64 位小端字节序（与官方表一致）。C# 侧自测通过本表 + 官方 64 向量即视为算法移植完成；
帧级混入序列见上文第 2 条配方（number4 与 code4 为 `int2Bytes` 小端 4 字节）。
Java 侧复导方式：对 `com.tny.game.common.digest.SipHash24`（无外部依赖）用上述用例直算即可，一分钟内可重生成。

## 状态

> 2026-09-30 决策：本询价随独立工单跟踪发送与回复（用户指示），不阻塞 `add-mac-generation-siphash`
> 的归档；C# 回复到达后仍须回填代次 readme（`security-generations_readme.md`）与本文档再启用。

- [ ] 已发送（由维护者经独立工单执行）
- [ ] 回复已收到，结论回填 `security-generations_readme.md` 与本文档
