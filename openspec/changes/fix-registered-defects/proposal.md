# Proposal

## Why

去重变更（reduce-code-duplication，已归档）在"零行为变更"铁律下把 **15 项已证实的行为缺陷逐字钉桩入册**（六组 verification 的"遗留登记"节），另有 `ofItemId` 这类经实现复核坐实的**必然坍缩缺陷**——它被活跃消费面（道具类型解析链路）每天以错误结果使用。钉桩即红基线：翻转断言就是修复的验收标准，素材完备度是三轮里最高的一次，现在收口成本最低。

## What Changes

- **protobuf 文本格式保真**（新能力 `protobuf-text-fidelity`）：Unicode 转义还原公式修正（高位字节往返无损）；文本值渲染统一转义（消除标记注入面与族内策略分裂）；打印→读回对特殊字符集闭合；未知字段处置五形态统一为显式失败（**BREAKING**：原两形态静默丢弃、三形态各异报错）；消息集扩展打印/读回对称化。
- **数值工具 null 与窄类型语义定规**（`numeric-hash-integrity` 增补需求）：null 操作数按零元语义统一参与（修正减法方向）；窄类型（byte/short/char）混合运算按二进制数值提升进 int 通道（修正溢出折叠，**BREAKING**：`add((byte)100,(short)300)` 由 −112 变 400 且结果类型 Integer）；除/模零元显式失败。
- **转换失败消息一致化**（`object-access-conversion` 增补需求）：包装与视图两实现的错型失败消息恒以装箱形态描述目标类型（消除 `float` vs `java.lang.Float` 尾段分叉；后果面已由历史演进中和，本项为参数卫生+消息收敛）。
- **枚举门面语义收口**（新能力 `enumeration-facade-semantics`）：严格校验通道不得降级为宽松空返（**BREAKING**：未命中由 null/空返改显式失败）；空标识符查找显式失败不越界；**道具位宽解析改单一商式语义**（**BREAKING**：任意输入现恒解析到 0 号段，修复后按模型号正确分位——道具类型解析链路的行为将变化）。
- **RSA 密钥文本入口失败方向统一**（`digest-codec` 增补需求）：非法密钥/数字文本一律显式失败，废除吞异常返回 null 的静默通道（**BREAKING**：仓内零消费点，外部按发布合同公告）。

明确不做：AbstractFuture 死字段清理（行为中性，属 hygiene 随单捎带但零承诺）、`consumeIdentifier` 引号剥离分叉（无消费者受害，维持现状登记）、比较族六路克隆（结构非行为，留下轮）、去重否决清单 8 族（单 3，另议）。

## Capabilities

### New Capabilities
- `protobuf-text-fidelity`：protobuf 五文本形态的转义保真、往返闭合、未知字段处置策略的外部行为契约。
- `enumeration-facade-semantics`：枚举注册门面的严格/宽松通道、空输入、位宽解析的外部行为契约。

### Modified Capabilities
- `numeric-hash-integrity`：增补 null 操作数与窄类型提升的确定性语义需求（现文未覆盖两格）。
- `object-access-conversion`：增补两实现错型失败消息一致化需求（现文已承诺"正常返回"，本项收敛消息面）。
- `digest-codec`：增补密钥文本入口失败方向统一需求（现文只覆盖"错误密钥解密显式失败"，未覆盖"文本非法返 null"）。

## Impact

- **受影响模块**：`tny-game-protobuf`（核心）、`tny-game-common-lang`（number/map-access）、`tny-game-basics`（ItemTypes/DemandTypes）、`tny-game-common-digest`（rsa）。
- **下游消费面（实测）**：
  - `ItemTypes.ofItemId`：**`tny-game-starter-basics/GameExplorer` 两处活跃调用**（道具类型解析）——修复即行为变化，须先以真实号段样本建消费面哨兵用例。
  - protobuf 打印面：`tny-game-oplog`（printFiles→JSON 文本落日志）——转义修正影响日志字节形态，运营解析侧需公告。
  - `NumberAide` 窄类型通道：LocalNum 家族（lang 内委托）与表达式引擎暴露面（`tny-game-expr` 回归）。
  - `RSAUtils` 吞异常两方法：**全仓 grep 零消费者**——行为变更风险面最小。
  - `DemandTypes.check`：basics 需求校验链——严格化可能把既有静默 null 路径变显式失败，需消费点清单先行。
- **对应 starter**：`tny-game-starter-basics`（GameExplorer）、`tny-game-starter-data`/`starter-net-netty4` 无涉。
- **报文/序列化格式**：无协议字节面变更（protobuf 文本形态为日志/工具面，非传输报文；digest-codec 既有小端/密文面不动）。
- **验收**：既有钉桩翻转 + 新契约用例 + 全仓 `./gradlew test -x :tny-game-namnspace-etcd:test` 门禁；release-note 按模块出 BREAKING 表。
