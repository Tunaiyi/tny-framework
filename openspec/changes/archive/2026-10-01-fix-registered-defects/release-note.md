# Release Note（fix-registered-defects）

**性质：行为 BREAKING 变更**（公共签名零变更；报文/协议字节面零变更）。全部条目为"在册钉桩翻正"——每处均有红基线留痕（`red-baseline.md`）。升级前请按下表核对使用面。

## BREAKING 公告表

### tny-game-protobuf（文本格式族，5 项）

| # | 变更 | 前 → 后 | 下游 |
|---|---|---|---|
| P1 | Unicode 转义还原权展开修正 | 高位字节值读回有损（`ﾯ` 类错值）→ 逐字节还原、二次打印逐字一致 | 使用 merge/读回面者 |
| P2 | Html 值渲染转义生效 | 标记/换行裸出（结构破坏+首段丢失）→ 统一转义表+尖括号 `\<` `\>` | Html 打印消费方（若有贴前端展示将见转义序列） |
| P3 | Xml 打印产物读回闭合 | 特殊字符集值 merge 必抛 `Expected ">"` → 读回成功且值等 | 依赖"报错=跳过"旁路逻辑者 |
| P4 | **未知字段四可读回形态统一显式拒绝**（Html 无读回面） | Json/Couch 静默丢弃、Xml/Props 词法错位 → `行:列: Unknown field number: N.`（格式不承诺对外稳定） | 以文本回填含演化字段消息者：原"成功但缺字段"改为显式失败 |
| P5 | Props 消息集扩展往返对称 | 打印短名/读回要求全名+点号必抛 → 原样读回闭合（打印侧字节零改动） | Props merge 面 |

**oplog 影响实测：零**——`Protobuf2JsonFormat.printFiles` 前后 37 面逐字节一致，JSON 日志文本无变化。

### tny-game-common-lang（数值工具+转换消息，3 项）

| # | 变更 | 前 → 后 |
|---|---|---|
| L1 | null 操作数零元语义 | `sub(null,5)`：`5` → **`−5`**；`multiply(null,5)`：`5` → **`0`**；`divide/mod` 零除数（含 null 视零）→ 当场 `ArithmeticException("/ by zero")`，不再返回 Infinity/NaN/对侧值；add 外显结果不变 |
| L2 | 窄类型 JLS 数值提升 | `add((byte)100,(short)300)`：`Byte(−112)` → **`Integer(400)`**；`add((byte)100,300L)`：`Byte(−112)` → **`Long(400)`**；同宽超界亦 Integer。**连带**：静态类型按窄装箱推断的调用点会遇 checkcast CCE（升级=加宽见证或 `intValue()`）；LocalNum 存储家族外显值零变化（回折自担，实证 18+ 格） |
| L3 | 转换失败消息装箱归一 | 同输入两实现尾段 `float` vs `java.lang.Float` 分叉 → 逐字一致（措辞向） |

### tny-game-basics（枚举门面，3 项·行为级最重）

| # | 变更 | 前 → 后 |
|---|---|---|
| B1 | `DemandTypes.check` 严格化 | 未注册身份：`null` 静默放行 → 显式失败（消息含身份；仓内零既有调用方） |
| B2 | `ItemTypes.ofAlias` 空输入受控 | `""`/`"$"`：`ArrayIndexOutOfBoundsException` → `IllegalArgumentException`（消息含输入；GameExplorer.getItemByAlias 空别名输入方可见） |
| B3 | **`ItemTypes.ofItemId` 归位语义** | 位宽阶梯与段截断互相抵消，任意输入恒落段基址 0（道具解析长期带病）→ "取商至首位×段基址"单一规则：`2000001→模型 2000000 条目`、`itemIdOf(500000999)` 同归位；未注册号段按宽松 of 返 null。**运营侧请核对道具表**：过去"解析失败被跳过"的路径可能首次真实命中类型对象（GameExplorer×2 哨兵在册） |

### tny-game-common-digest（RSA，1 项）

| # | 变更 | 前 → 后 |
|---|---|---|
| D1 | `getPublicKey/getPrivateKey` 非法输入统一显式失败 | 吞异常返回 `null`（与 toKey 系方向相反）→ `IllegalArgumentException`（指明失败环节）；仓内零消费点，外部调用方升级=以受控异常处理替换判空 |

## 无 BREAKING 面声明

公共类名/方法/参数/返回类型零变更；网络报文、ProtoEx/JSON 序列化字节面、`oplog` 日志文本、LocalNum 外显值——均不变。
