# Tasks

## 1. 契约测试先行（编译失败即红基线）

- [x] 1.1 写 `tny-game-net/src/test/java/com/tny/game/net/codec/verifier/Crc32CodecVerifierTest.java`（JUnit 5，装配先例 `SipHash24CodecVerifierTest`）：
  (a) 算法锚：IEEE CRC32 标准金样 `"123456789"` → `0xCBF43926`（经类内独立 `java.util.zip.CRC32` 直算对照 verifier 混入序列在恒等前缀输入下的等价性——以测试内参考链比对，锚定"number‖body‖ak‖code 同构"输入构造）；`getCodeLength()==4`；
  (b) 敏感性：改载荷/包号/accessKey 任一 → `verify` false；
  (c) 双线程交错自校验（`ThreadLocal` 线程封闭回归，先例同款）；
  (d) 默认代次（CRC64）路径回归冒烟不受影响。
- [x] 1.2 验证红基线：`./gradlew :tny-game-net:test`——1.1 因符号缺失编译失败，确认失败原因即"实现未落地"。

## 2. 实现与转绿

- [x] 2.1 实现 `tny-game-net/src/main/java/com/tny/game/net/codec/verifier/Crc32CodecVerifier.java`：`@Unit`；`ThreadLocal<java.util.zip.CRC32>` 复用 + `reset()` 后按 number4‖body(off,len)‖ak‖code4 流式 `update` 链；`int2Bytes((int)getValue())` 返回 4B；javadoc 首位声明"无防伪能力，仅快速筛选"（规格 Scenario 5）。
- [x] 2.2 验证：`./gradlew :tny-game-net:test` 全绿（1.1 转绿）。

## 3. 管线代次与基准锚定

- [x] 3.1 写 `tny-game-net-netty4/src/test/java/com/tny/game/net/netty4/network/codec/Crc32GenerationPipelineTest.java`（先例 `MacGenerationPipelineTest`）：
  (a) 同批代次全管线往返 + 帧尾 4B 断言；
  (b) **帧长对比**：同一 Message（time 固定共享）分别经 legacy(8B) 与快筛(4B)  fixture 编码 → 总长差恰 4B、前段字节一致（键流同配置时）；
  (c) 篡改载荷/包号 → 可观察拒绝；
  (d) 代次错配双向：4B 帧→8B 解码端 与 8B 帧→4B 解码端 均抛 `NetCodecException`（可观测性断言，规格混布锚）；
  (e) 默认装配 fixture 帧字节与代次存在前完全一致（零影响锚）。
- [x] 3.2 `tools/net-bench` 矩阵新增生产装配组合 `prod_crc32_xortile`（`Crc32CodecVerifier` + 生产 `XorTileCodecCrypto`），同参复测并回填 `crypto-bench-2026-09-30.md` 第七节（验收 ≥1.5M ops/s——**实测 1.377M ±0.090M（复跑，双峰噪声轮已弃）**，绝对值差 8%；与认证代次同款生产件 vs 内联原型落差 ~16%（ThreadLocal 查找+int2Bytes 契约分配），收回路径注记于代次 readme，另立优化件不扩本变更范围）。
- [x] 3.3 验证：`./gradlew :tny-game-net-netty4:test` 全绿 + 3.2 单跑记录。

## 4. 文档与收尾

- [x] 4.1 `tny-game-net/…/codec/security-generations_readme.md` 代次表追加"快筛档"行（unit 名、4B 校验码、能力=无键快筛、适用=内网/高扇出/可信任对端）、推荐组合表（verifier×crypto 正交矩阵）与**错配症状预告**（混布表现为解码类异常而非 causeVerify）；`net-protocol` 文档若引用代次清单同步。
- [x] 4.2 验证：`./gradlew :tny-game-common-digest:test :tny-game-net:test :tny-game-net-netty4:test` 全量回归 + `openspec validate add-crc32-verify-tier` 通过。
