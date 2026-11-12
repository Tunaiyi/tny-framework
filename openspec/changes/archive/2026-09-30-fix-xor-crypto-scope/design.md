# Design

## Context

`BytesAide.xor(data, offset, length, keyBytes...)`（`tny-game-common-digest/…/BytesAide.java:280-287`）循环为 `for (i = offset; i < length; i++)` 且键流取位用绝对下标 `i % keys.length`。便捷重载 `xor(data, keys)` 以 `(0, data.length)` 委托，恰因 offset=0 而语义正确——缺陷只在非零 offset 路径。

爆炸半径核查（grep 直读 + codegraph 符号检索双重确认）：
- 生产调用方唯一：`XOrCodecCrypto.java:30`（`tny-game-net`），其入口再上溯为 `NetPacketV1Encoder.java:103` 与 `NetPacketV1Decoder.java:149`，两处传入池化堆缓冲的 `arrayOffset()`；
- `tools/net-bench` 两处调用均为 offset=0（语义修复前后不变，基线锚点安全）；
- starter 仅注册 bean（`NetAutoConfiguration.java:179`），无逻辑依赖；
- 除 codec 路径外无任何模块引用该方法。

池化缓冲实证（本机 netty 4.1.104）：`heapBuffer` 在分配复用后 `arrayOffset` 常态为大于载荷长度的页内偏移——即生产 ENCRYPT 帧多数"循环体执行 0 次"（明文上线但帧声明加密位），少数部分覆盖且相位随池位置漂移。

## Goals / Non-Goals

**Goals:**
- ENCRYPT 声明与线上字节一致：全载荷必然被变换，结果与缓冲池位置无关。
- 修复保持 offset=0 的历史行为逐字节不变（该场景新旧语义重合）。
- 缺陷不可复发：契约测试钉住"全窗 + 相对相位"。

**Non-Goals:**
- 不改变混淆算法强度（Vigenère 仍是 Vigenère——会话密钥/防伪代次属于后续独立提案）。
- 不做性能换代（xorTile/字级循环归后续"安全代次"提案）。
- 不动方法签名、不加协议位、不改 relay 链路。

## Decisions

1. **规范语义 = 完整窗口 `[offset, offset+length)` + 相对键流相位 `(i-offset) % keys.length`；offset=0 时为历史行为**。
   依据 P11（合同变更三问：编译兼容=是；报文格式=字段布局不变、字节内容变为契约本意）与 P4（`BytesAide.xor` 的不变量本就是"对窗口做变换"，现实现违背自身不变量）。
   否决方案 a：保留绝对相位仅修边界——否决：修复后两端 `arrayOffset` 独立漂移，密文不可复现，比现状更糟。
   否决方案 b：新增 option 位做新旧加密语义协商——否决：P10（"不加密却声明加密"不是任何场景需要的语义，无需为缺陷保留共存代次），且引入双向拒绝模板扩展成本。
   否决方案 c：在 `XOrCodecCrypto` 调用侧规避（改传参凑窗口）——否决：P5 契约根修在工具函数，规避会让其他未来调用者继续踩雷（判据参照模式卷"策略契约修复先位于抽象，而非实现侧绕路"）。

2. **修复落点：`BytesAide.xor` 实现体**。P3 合规（新增修复不改抽象签名；`tny-game-net` 侧零改动）。热路径成本：每字节增加一次减法与边界比较——量级 ~0（P13：相对 XOR 本体可忽略；相对现状"多数帧根本不执行"则是新增 ~145ns/向的真实成本，这是修复的应有之义，性能优化归后续代次）。

3. **`CodecCrypto` javadoc 契约收紧**（`(offset,length)` 窗口含义 + 相位规则成文）。P11（已发布接口的行为契约必须以文字钉死，供第三实现者对齐）；P7 合规（不加方法）。`XOrCodecCrypto` 实现无需改动，但 `NettyByteBufferAllocator` 生态文档不涉及。

4. **测试策略**：P13 逐 Scenario 认领——
   - 确定性复现夹具：`Unpooled.wrappedBuffer(page, off, len)` 构造非零 `arrayOffset`（不依赖池状态，测试可重复）；
   - 单测（digest 模块）：全窗覆盖（每个字节都被变换，off=0 参照断言）、双缓冲位置同结果断言、自逆往返断言；
   - 集成（`tny-game-net-netty4` codec 测试目录，先例 `PacketGateTest`）：ENCRYPT 开启 + 池化 allocator 的编解码往返 + 篡改载荷导致 verify failed 的负路径；
   - 回归锚：offset=0 输出与修复前金样本逐字节一致。

5. **部署兼容策略**：ENCRYPT 启用的链路两端必须同批升级（混布窗口内新端全窗加密、旧端按缺陷解窗 → verify failed 断连，属预期保护行为而非静默降级——错误可观测优于明文假安全）。ENCRYPT 未启用的部署零影响。跨端审计任务：核对 C# 对端实现的窗口/相位语义（若 C# 已按规范实现，本修复同时治好 Java↔C# 的 ENCRYPT 互操作；若 C# 复刻缺陷，需同批发版）。

## Risks / Trade-offs

- **混布断连**：见决策 5——风险集中在"ENCRYPT 开启且双端版本错位"的组合，需在发布说明中显式标注；缓解手段是升级顺序编排（两端同制品版本发布），协议层不加协商位（决策 1 已否决并记录理由）。
- **修复后性能回落**：此前"免费"的 no-op 加密变成真实 XOR 成本（~145ns/向），ENCRYPT 链路吞吐预计回落约 12-15%（对照 `crypto-bench-2026-09-30.md` 矩阵 legacy 与去加密档差值）。接受：这是声明语义的应有成本；后续代次（xorTile/siphash 档）在独立提案中偿还。
- **`xor(byte[], byte[]...)` 便捷重载**：委托 offset=0，行为不变，无需迁移。
- **下游已发布版本**：`tny-game-common-digest` 按 Nexus 合同规则标注 BREAKING 版本号（patch 不足以承载语义变化，建议 minor），提案通过时在 tasks 中落发布注记。

## Compatibility Impact

- 公共 API：`BytesAide.xor(byte[], int, int, byte[]...)`——签名不变，非零 offset 路径行为变更（BREAKING，按 P11 标注入发布说明）。
- 报文格式：帧字节布局不变；帧的载荷字节内容在"ENCRYPT 位 + 非零窗口起点"组合下由"多数不加密"变为"必然加密"——依赖旧缺陷行为（即实际不加密）的流量在升级后变为密文，任何按明文解读线上帧的工具（抓包解析脚本等）需感知。
- 互操作：C# 端语义待审计（tasks 2.x）；修复后 Java↔Java、Java↔规范 C# 在 ENCRYPT 上首次真正互通。
- 不受影响面：`NoneCodecCrypto`、未启用 ENCRYPT 的全部链路、relay 帧（无加密字段）、bench 基线锚点。
