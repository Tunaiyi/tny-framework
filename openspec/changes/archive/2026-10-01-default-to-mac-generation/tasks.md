# Tasks

## 1. 默认行为测试先行（红基线）

- [x] 1.1 写 `tny-game-net-netty4/src/test/java/com/tny/game/net/netty4/network/codec/MacGenerationDefaultTest.java`（JUnit 5，夹具先例 `MacGenerationPipelineTest`，但**不注入件**——专测默认值路径）：
  (a) `setVerifyEnable(true)+setEncryptEnable(true)` 不点名 unit：断言帧尾 8B 等于测试内独立 `SipHash24` 参考链（number‖body‖ak‖code 同构）重算值，且整帧与预存 legacy 金样帧不等；同代次往返还原原文；
  (b) 显式点名 `crc64CodecVerifier`+`xOrCodecCrypto`（经 prepareStart/UnitLoader 装配路径，夹具内以配置字符串驱动）：整帧与 legacy 金样逐字节一致（逃生舱）；
  (c) 双开关关闭：帧字节与金样一致且校验码区不存在（未启用零影响）；
  (d) `enable=true` 且 `securityKeys` 为空：启动校验 fail-fast（既有需求回归）。
- [x] 1.2 验证红基线：`./gradlew :tny-game-net-netty4:test --tests "*MacGenerationDefaultTest"` —— (a) 必红（默认仍 legacy），(b)(c)(d) 必绿；失败原因与预期一致后方可继续。

## 2. 默认翻转实现

- [x] 2.1 `tny-game-net-netty4/…/NetPacketCodecSetting.java:36,39`：`verifier` 默认 → `lowerCamelName(SipHash24CodecVerifier.class)`，`crypto` 默认 → `lowerCamelName(XorTileCodecCrypto.class)`；类 javadoc 补一行"默认装配键控认证代次；旧行为显式点名 crc64CodecVerifier/xOrCodecCrypto"。
- [x] 2.2 验证：`./gradlew :tny-game-common-digest:test :tny-game-net:test :tny-game-net-netty4:test` 全绿（(a) 转绿；既有管线测试因显式注入件不受波及；`NetPacketCodecConfigGuardTest`、`PacketGateTest` 若因默认变化失败，逐一核查其是否依赖默认值语义并按规格意图修正测试而非实现）。

## 3. 发布与文档闭环

- [x] 3.1 demo/模板核查：`grep -rn "verify-enable\|encrypt-enable" tny-game-net-demo/src/main/resources` 确认无"开启但未点名"的隐式用法；如有则显式钉 legacy 并在 PR 描述注明。
- [x] 3.2 `security-generations_readme.md`：代次表"legacy（默认）"标注改"显式点名可用；默认已翻至认证代次"，追加升级次序与 C# 钉选条款（引用 `archive/2026-09-30-add-mac-generation-siphash/peer-siphash-adoption.md`）。
- [x] 3.3 `tools/net-bench/crypto-bench-2026-09-30.md` §十二末尾追加一行"默认装配已翻至认证配对（本变更）"；`release-note.md`（本变更目录内新建）：BREAKING minor、逃生舱 yml 片段、混布症状= verify failed 断连（预期保护）。
- [x] 3.4 终验：三模块全量回归 + `openspec validate default-to-mac-generation` 通过。
