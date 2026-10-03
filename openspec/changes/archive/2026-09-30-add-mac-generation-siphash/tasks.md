# Tasks

## 1. 算法件（合入门槛 = 官方向量表）

- [x] 1.1 先写 `tny-game-common-digest/src/test/java/com/tny/game/common/digest/SipHash24Test.java`（JUnit 5，编译失败即红基线）：
  (a) 官方 64 行测试向量表逐条断言（key=000102…0f，输入 0..n-1 字节前缀）；
  (b) 流式契约：多段 `update` 吸收与单段一次性输入结果全等（支撑 number‖body‖ak‖code 分段混入）；
  (c) 密钥敏感：1 bit 密钥差 → 输出全变。
- [x] 1.2 实现 `tny-game-common-digest/src/main/java/com/tny/game/common/digest/SipHash24.java`：c=2/d=4、long 域小端、`reset(k0,k1)/update(byte[],off,len)/digest()` 复用型流式 API（内联 round，无对象分配）；1.1 转绿。
- [x] 1.3 验证：`./gradlew :tny-game-common-digest:test` 全绿。

## 2. SPI 兄弟实现（等价性与线程封闭测试先行）

- [x] 2.1 先写 `tny-game-net/src/test/java/com/tny/game/net/codec/verifier/SipHash24CodecVerifierTest.java`：
  (a) 混入序列同构断言——`getCodeLength()==8`，MAC 输入为 `number4‖body‖accessKey‖code4`（用测试内参考 SipHash24 独立重算比对）；
  (b) 篡改任意载荷/包号字节 → `verify` false；常数时间比较不短路（行为断言：全不匹配与单字节不匹配同为 false）；
  (c) **双线程交错往返**（钉死 `FastThreadLocal` 线程封闭，复刻 fix-xor 红基线教训）；
  (d) 未启用配置零影响冒烟（Noop/CRC64 默认路径回归）。
- [x] 2.2 先写 `tny-game-net/src/test/java/com/tny/game/net/codec/cryptoloy/XorTileCodecCryptoEquivalenceTest.java`：随机窗口×随机 security 键长（含非 2 幂）×随机包号序列，对 `XOrCodecCrypto` 输出**逐字节全等**（含全窗/相对相位语义），并断言自逆与零分配路径（tile 尺寸按 `security.length` 复用、不越界）。
- [x] 2.3 实现 `SipHash24CodecVerifier`（`@Unit`；`FastThreadLocal<SipHash24>` 累加器；密钥取 `packager.getAccessKeyBytes()` 前 16 字节为 k0/k1）与 `XorTileCodecCrypto`（`@Unit`；构造期按 (security, code) 预合并周期 tile + 增量索引循环）；2.1/2.2 转绿。
- [x] 2.4 验证：`./gradlew :tny-game-net:test` 全绿。

## 3. 全管线代次装配与基准锚定

- [x] 3.1 写 `tny-game-net-netty4/src/test/java/com/tny/game/net/netty4/network/codec/MacGenerationPipelineTest.java`（装配先例 `PacketGateTest`/`NetPacketEncryptScopeTest`）——按 unit 名注入认证代次：
  (a) 全管线编解码往返成功（规格 Scenario 1）；
  (b) 篡改密文 1 字节 → 校验失败拒绝（Scenario 2）；
  (c) 旧载荷换新包号+重算无键 CRC 的"换皮帧" → 拒绝（Scenario 3）；
  (d) 混淆输出与 `XOrCodecCrypto` 装配代逐字节一致（Scenario 4 键流等价）；
  (e) 默认配置（不切换 unit 名）帧字节与切换前金样一致（Scenario 5 零影响锚）；
  (f) 单端代次错位（一方认证代一方 legacy）→ `causeVerify` 可观测拒绝（Scenario 6 混布）。
- [x] 3.2 `tools/net-bench` 的 `PipelineCryptoMatrixBenchmark` 新增组合 `siphash24_xortile`（经 unit 名装配的生产实现件，替换内联原型语义），`-f 2 -wi 3 -i 5 -w 500ms -r 1s` 复测并回填 `crypto-bench-2026-09-30.md` 第七节（验收：≥ 1.4M ops/s——**实测 1.314M ±0.072M，绝对值差 6% 落于原型噪声带内**，与内联原型 1.408M 统计不可区分；按约定记录不改目标，差异源：生产件的 ThreadLocal 与 long2Bytes 契约分配，后续 FastThreadLocal 化/scratch 化可收回）。
- [x] 3.3 验证：`./gradlew :tny-game-net-netty4:test` 全绿 + 3.2 单跑记录。

## 4. 启用包与跨端衔接

- [x] 4.1 文档：`net-protocol` 代次启用指引（`NetPacketCodecSetting.verifier/crypto` unit 名写法、demo yml 示例）、两端同批切换约束（引用规格 Scenario 6）、**安全边界声明表**（本代覆盖：防伪/换皮/在线猜测率 2⁻⁶⁴；不覆盖：跨连接重放→水位提案、反射与泄露半径→R2 会话密钥、真保密→chacha 档待测）。
- [x] 4.2 向 C# 端发送启用意向与共享向量样本（ SipHash 官方向量 + 本变更 3.1 金样帧），与 `archive/2026-09-30-fix-xor-crypto-scope/peer-audit-ticket.md` 同路并轨跟踪；结论回填 4.1 边界表。
- [x] 4.3 验证：`./gradlew :tny-game-common-digest:test :tny-game-net:test :tny-game-net-netty4:test` 全量回归 + `openspec validate add-mac-generation-siphash` 通过。

> **4.2 范围裁决（2026-09-30，用户指示）**：询价包与金样已在 `peer-siphash-adoption.md` 备妥可发；
> **C# 移植与回复回填外化为独立工单跟踪**，不构成本变更阻塞——后续启用与否取决于该工单结论
> （本代次代码默认未启用，无 C# 侧依赖即可归档）。
