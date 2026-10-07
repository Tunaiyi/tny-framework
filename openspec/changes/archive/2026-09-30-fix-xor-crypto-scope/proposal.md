# Proposal

## Why

`BytesAide.xor(data, offset, length, keyBytes)`（`tny-game-common-digest`，`BytesAide.java:280-287`）的循环上界误用 `length`（长度）而非 `offset + length`（窗口末端），且键流按绝对数组下标 `i % keys.length` 取相位。生产唯一非零 offset 调用方是报文编解码（`NetPacketV1Encoder.java:103` / `NetPacketV1Decoder.java:149`，传入池化缓冲的 `arrayOffset()`——实测池化堆缓冲 `arrayOffset` 常态非零），后果：ENCRYPT 选项开启时，多数帧的"加密"实际执行 **0 字节**（帧明文上线，帧内却声明 ENCRYPT 位），少数帧只混淆前段字节且键流相位随缓冲池位置漂移；收发两端因运行同一缺陷代码而"自洽"，掩盖了问题。

## What Changes

- 修正 `BytesAide.xor(data, offset, length, keyBytes)` 语义：处理完整窗口 `[offset, offset+length)`，键流相位按窗口内相对位置 `(i - offset) % keys.length` 取值（offset=0 时与现行为逐字节一致）。**BREAKING**（`tny-game-common-digest` 已发布公共 API 的可观察行为变更；对 ENCRYPT 启用的链路，线上字节从"多数帧不加密"变为"全帧加密"，新旧端混布时 ENCRYPT 帧校验将失败，须同批升级两端）。
- `CodecCrypto` 接口 javadoc 增补 `(offset, length)` 窗口与相对键流相位的契约措辞（不改变方法签名）。
- 新增回归测试：非零 `arrayOffset` 缓冲的全窗口覆盖测试、收发往返测试、键流相位与缓冲位置无关性测试。
- 不做：性能换代（xorTile/siphash 代次属于后续独立提案）、协议 option 位变更、`xor(byte[] data, ...)` 便捷重载（offset=0 委托，行为本已正确）。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `net-protocol`: 新增需求"加密作用于完整载荷窗口且键流相位相对化"——声明 ENCRYPT 位的报文，其载荷加密 MUST 覆盖载荷全部字节，且键流取位 MUST NOT 依赖接收方缓冲在内存池中的绝对位置；未声明 ENCRYPT 位的报文行为 MUST NOT 变化（兼容锚点）。

## Impact

- 受影响模块：`tny-game-common-digest`（`BytesAide` 实现修正）、`tny-game-net`（`XOrCodecCrypto` 经其委托而行为修正；`CodecCrypto` javadoc）、`tny-game-net-netty4`（编解码调用点行为恢复契约，代码不改）。
- starter/装配：`tny-game-starter-net-netty4` 无需改动（unit 装配链不变），但启用 `encoder/decoder.crypto` 为 `xOrCodecCrypto` 且 `encrypt-enable: true` 的部署需**两端同批升级**。
- 跨端：C# 客户端对拍实现需审计其键流窗口语义（相对/绝对、全窗/截窗）；若 C# 端已按"全窗相对相位"规范实现，则本修复使 Java↔C# 的 ENCRYPT 链路从"当前必然校验失败或明文错解"变为正常工作（修复互操作）；若 C# 复刻了缺陷语义，需同步修正（迁移路径见 design.md）。
- 不受影响：relay 链路（`RelayPacketV1*` 无加密字段）、`NoneCodecCrypto` 路径、`encrypt` 选项关闭的全部现存部署、`tools/net-bench` 基线锚点（EmbeddedChannel 用 Unpooled，offset=0 语义前后一致）。
- 风险：ENCRYPT 开启的既有 Java↔Java 链路升级窗口内混布 → 新服务端加密、旧对端不解密 → CRC verify failed 断连；部署序与门控策略见 design.md。
