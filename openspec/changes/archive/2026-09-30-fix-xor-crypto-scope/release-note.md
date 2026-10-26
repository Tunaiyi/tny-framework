# 发布注记（fix-xor-crypto-scope）

## 变更等级：BREAKING（行为合同修正）

- 受影响公共 API：`com.tny.game.common.digest.binary.BytesAide.xor(byte[], int, int, byte[]...)`
  ——签名不变；非零 `offset` 路径行为修正（全窗口 `[offset, offset+length)` + 相对键流相位）。
  offset=0 路径逐字节不变（含便捷重载 `xor(byte[], byte[]...)`）。
- 版本建议：`tny-game-common-digest` 与 `tny-game-net` 按 P11 走 **minor** 位（签名兼容但语义修正，patch 不足以承载），发布说明必须携带本注记。

## 对报文协议的影响

- 声明 ENCRYPT 位的报文：修复前在池化堆缓冲（`arrayOffset ≥ 载荷长度` 常态）下加密整段跳过——
  线上为**明文**却声明加密位；修复后全载荷必然变换。
- 帧字节布局不变；依赖"抓包见明文"的运维/调试工具在升级后看到的是密文（预期行为，非故障）。
- 未启用 ENCRYPT 的部署与链路：零影响。relay 链路（无加密字段）：零影响。

## 升级顺序约束

- **同一链路收发两端必须同批升级**：混布窗口内（新端全窗加密、旧端按缺陷窗口解密）表现为
  CRC `causeVerify` 断连——这是规格定义的保护行为（可观测拒绝优于静默明文），不是新缺陷。
- 客户端（含 C# 对端）先于或随服务端同版本发布；灰度期避免 ENCRYPT 链路新旧混跑。
- 跨端语义审计（tasks 3.1）已移交对端工单：`peer-audit-ticket.md`（含两个必答问题与逐字节对拍验收标准）。工单结论回填本节前，Java 修复与 C# 端**不得分别发布**至同一 ENCRYPT 链路。

## 性能影响

- ENCRYPT 链路新增真实 XOR 成本 ≈145ns/向（96B，修复前多数帧为 0——因为根本没执行）。
  数字与口径见 `tools/net-bench/crypto-bench-2026-09-30.md` 的 legacy 锚点注记；
  如需收回该成本属于后续"安全代次"提案（xorTile/siphash 档），不在本变更范围。
