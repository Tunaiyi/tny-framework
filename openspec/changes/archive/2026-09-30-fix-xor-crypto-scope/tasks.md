# Tasks

## 1. 契约测试先行（缺陷复现与钉死）

- [x] 1.1 新增 `tny-game-common-digest/src/test/java/com/tny/game/common/digest/binary/BytesAideXorWindowTest.java`（JUnit 5）——用页内偏移构造 `offset>0` 的字节窗口（`new byte[]{sec, code}` 双键流，复刻 `XOrCodecCrypto.java:27-30` 的调用形态），断言四条契约：
  (a) 全窗覆盖：`[offset, offset+length)` 内每个字节均被变换（对照全零参照窗口逐字节比对）；
  (b) 相位无关：同一消息在窗口起点 0 与非 0 的两种底层数组观下，变换结果逐字节相同；
  (c) 自逆往返：同参数执行两次还原原文；
  (d) 兼容锚：offset=0 金样本与修复前实现输出逐字节一致。
- [x] 1.2 新增 `tny-game-net-netty4/src/test/java/com/tny/game/net/netty4/network/codec/NetPacketEncryptScopeTest.java`（装配先例：同目录 `PacketGateTest.java`）——实现形态经红基线校准为确定性两条：(a) 生产调用形态对拍：同一消息在窗口起点 13 加密、在起点 0 解密必须还原且密文与起点 0 参照逐字节一致（修复前必红，不依赖池状态）；(b) ENCRYPT+CRC 全管线帧篡改中段 1 字节 → `causeVerify`（回归守卫）。
- [x] 1.3 验证（红基线）：`./gradlew :tny-game-common-digest:test :tny-game-net-netty4:test`——确认 1.1(a)(b) 与 1.2 往返用例在当前实现上失败，且失败原因与本缺陷一致（不得因测试自身问题而红）。

## 2. 修复实现

- [x] 2.1 `tny-game-common-digest/…/BytesAide.java:280-287`：循环上界 `i < length` 改为 `i < offset + length`；键流取位由绝对下标 `keys[i % keys.length]` 改为相对相位 `keys[(i - offset) % keys.length]`。`xor(byte[] data, byte[]... keyBytes)` 便捷重载（委托 offset=0）不动。
- [x] 2.2 `tny-game-net/…/codec/CodecCrypto.java` javadoc 成文窗口契约：参数 `(offset, length)` 表示全窗处理、键流相位相对窗口起点、加密=解密自逆；不改方法签名。
- [x] 2.3 验证：`./gradlew :tny-game-common-digest:test :tny-game-net-netty4:test` 全绿（1.x 用例转绿，含 1.1(d)/1.2 兼容锚不回退）。

## 3. 跨端审计与发布注记

- [ ] 3.1 **（已移交对端工单，等待回填——见 `peer-audit-ticket.md`，回填结论后方可勾选并归档）** 审计 C# 对端参考实现的加密窗口/相位语义（对拍通道先例：`tny-game-net-netty4/src/test/…/CoderTest.java` 的 `cshap.bin`）：若 C# 按"全窗+相对相位"规范实现 → 记录"本修复同时治愈 Java↔C# ENCRYPT 互操作"；若复刻缺陷 → 向对端提交同批修复工单并在 change 内记录跟踪链接。
- [x] 3.2 发布注记：`tny-game-common-digest`、`tny-game-net` 依 P11 标 BREAKING（建议 minor），发布说明附升级顺序约束（ENCRYPT 启用链路两端必须同批，混布期表现为 verify failed 断连——为预期保护行为）。
- [x] 3.3 更新 `tools/net-bench/crypto-bench-2026-09-30.md`：补注"legacy 锚点按 offset=0（EmbeddedChannel/Unpooled）计量，已包含真实 XOR 成本；修复使池化缓冲生产链路从 no-op 回到该锚点语义，ENCRYPT 链路 ~145ns/向成本属契约应有"。
- [x] 3.4 验证：`./gradlew :tny-game-common-digest:test :tny-game-net:test :tny-game-net-netty4:test` 全量回归通过；`openspec validate fix-xor-crypto-scope`（如可用）通过。
