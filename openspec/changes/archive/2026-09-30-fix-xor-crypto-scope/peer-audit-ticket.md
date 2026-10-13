# 对端审计工单（fix-xor-crypto-scope · tasks 3.1 移交件）

## 背景（给 C# 端的一页说明）

Java 框架侧 `BytesAide.xor(data, offset, length, keys...)` 存在缺陷：循环上界误写 `i < length`
（应为 `i < offset + length`），且键流相位用绝对下标 `i % keys.length`（应为窗口相对 `(i-offset) % keys.length`）。
生产池化缓冲下（`arrayOffset` 常态非零）后果为：声明 ENCRYPT 位的帧**多数整段跳过加密（明文上线）**，
少数部分加密且两端窗口起点独立漂移时密文对不上。Java 修复已在 change `fix-xor-crypto-scope` 落地
（全窗 + 相对相位；offset=0 时行为与历史逐字节一致）。

## 需要 C# 端回答的两个问题

1. **窗口边界**：加密实现对载荷窗口 `[offset, offset+length)` 是否全量变换？
   （若 C# 侧缓冲恒为独立数组、offset 永远为 0，则历史行为与规范语义重合，问题 1 直接安全通过。）
2. **键流相位**：键流取位是绝对下标还是窗口相对下标？跨包 `packetCode` 派生序列
   （种子 = `accessId + securityKeys[floorMod(accessId)]` 的 djb32 哈希，`java.util.Random` 按包号空转推进）
   是否与 Java 参考实现逐包一致？

## 验收标准（对拍）

用同一 `(accessId, securityKeys, packetNumber)` 三元组：
- C# 对基准明文（含 `0x00` 字节与 13 字节页内偏移窗口两种形态）产出的密文，
  与 Java `NetPacketEncryptScopeTest.encryptDecryptAcrossDifferentWindowOffsetsRoundTrips`
  的期望输出逐字节一致 → 审计通过；
- 任一字节不一致 → C# 复刻了缺陷语义，需按同一规则修正并**与 Java 同批发布**
  （混布期 ENCRYPT 帧会触发 `causeVerify` 断连，属预期保护）。

## 状态

- [ ] 已指派至 C# 客户端工程
- [ ] 对拍结论回填至 `release-note.md` "跨端审计" 一节
- [ ] 回填后可勾选 tasks.md 3.1 并允许本变更进入归档
