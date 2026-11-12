# Design

## Context

动机与差量清单见 proposal.md。此处沉淀裁决约束：

- **每项缺陷都有现成红基线**：去重轮（archive/2026-10-01-reduce-code-duplication）六组 verification 的"遗留登记"逐条给出钉桩名（RT_JSON/COUCH_ESCAPES、P_HTML_ESCAPES、RT_XML_ESCAPES、RT_PROPS_MSGSET、floatDefChannelMessageDriftPinned、nullCombinationsSharedAcrossOperators、shortByteBranchesNarrowToFirstOperand、unknownNumberTypeFallsToDoubleChannelThenExplicitFailure、demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed、ItemTypes 五分支用例、illegalKeyTextFailsUnderControl 等）。修复=翻转这些期望值，红→绿时序天然成立。
- **爆炸半径实测**（codegraph 索引未覆盖这些符号，沿用 grep 全仓调用面）：`ItemTypes.ofItemId` → starter-basics `GameExplorer` 两处（活跃且当前恒解析到 0 号位）；`RSAUtils` 吞异常两方法 → 仓内零消费；`NumberAide.sub/add` 系 → LocalNum 家族内部委托 + expr 脚本暴露面；protobuf 五类 → 唯一外部点 oplog.printFiles（日志文本面）。
- **ObjectMap.float 缺陷后果已中和**：转换链尾段自历史提交起走 `ObjectAide.convertTo` 的基本类型→包装归一分支，CCE 不再发生；在册残余仅"参数非装箱 + 失败消息尾段分叉"。本设计据此把该项降级为消息一致化+参数卫生（对应 object-access-conversion 差量措辞），防止按旧记忆夸大修复面。
- 既有主规格三条需求（直取/浮点正常返回/高精度原宽）不动，只 ADDED 增补——规避与已入账 Scenario 打架。

## Goals / Non-Goals

**Goals:** 翻转全部在册钉桩至规格承诺方向；BREAKING 项逐条进 release-note 公告表；消费面（GameExplorer/oplog 日志/脚本）以哨兵用例锁住变化边界。

**Non-Goals:** 不动报文/序列化协议字节面；不做比较族六路去重（结构非行为）；不引 Generator 抽象统一（五形态生成器语义各异，属去重否决面）；不清 AbstractFuture 死字段（行为中性，另可随单捎带但不承诺）。

## Decisions

### D1 null 操作数=零元语义（P4 不变量：运算通道内 null 无独立身份，视作缺席操作数即零）
五算术统一"null→该运算零元"：被减数空取 0 后取反（修方向性错误），减数空取 0（现状兼容），除/模的除数侧落零→显式失败（对齐 JDK `BigDecimal` 除零语义与 P6 守约——既有 divide 高精度路径本已受控失败，窄通道不得反而返伪值）。
**否决备选**：(a) 全 null 一律显式失败——破坏 add 现状兼容格（矩阵证据：`nullCombinationsSharedAcrossOperators` 钉 add(null,5)=5 被 LocalNum 委托面依赖）；(b) 保持"直接返回另一操作数"——减法方向错误即缺陷本体。
**验证（P13）**：翻转 `nullCombinationsSharedAcrossOperators` + 新矩阵（5 算子 × null 位 × 除零）。

### D2 窄类型提升照搬 JLS 二元数值提升（P6 与平台语义对齐：游戏脚本面最小心智负担）
byte/short/char 参与二元运算先提升至 int 通道，结果落型按提升后宽度（混合 long→long 通道）；`findClass` 优先级仅在"是否需要更高精度通道"上参与，不再以首操作数强行回折。修正 `add((byte)100,(short)300)=−112→400`（**BREAKING**）。
**否决备选**：(a) 一律升 BigDecimal 再落型——改变常规整型结果类型面，波及远大于收益；(b) 仅修乘加不修落型——数值对了类型错，LocalNum 家族继续吃错值。
**验证**：翻转 `shortByteBranchesNarrowToFirstOperand`；expr 模块回归（脚本暴露面）。

### D3 未知字段策略=全形态显式失败（P4 不变量：读回对象 MUST 完整表达输入；丢弃破坏该不变量）
五形态读回遇未知编号统一抛带位置与编号的失败。
**否决备选**：(a) 丢弃+告警——把数据丢失合法化，运营排障时"文本里有、对象里没有"最难查（P9 类信息隐藏反例）；(b) 保真保留未知字段——功能级改造（需内部未知域承载），超行为修复范畴；(c) 沿用"两种静默三种报错"——缺陷本体。
**验证**：`unknownNumberTypeFallsToDoubleChannelThenExplicitFailure` 类比新建五形态一致拒绝用例；RT_{XML,PROPS}_UNKNOWN/JSON 静默快照全部翻转。

### D4 Html 值转义对齐同族表（P6 实现必须守约：同族形态承诺同构）
不转义形态补转义（值内标记分隔符/换行以转义序列承载），还原公式与 octal/unicode 分支修正按 FormatTextSupport 单一事实源改，各形态装饰差异仍参数化承载——"族内统一策略"与"形态各自语法"不冲突。
**验证**：翻转 `P_HTML_ESCAPES` 与 `RT_JSON/COUCH_ESCAPES` 合并重打印快照；oplog printFiles 输出做前后样本比对入 release-note。

### D5 ofItemId 单一商式：`ofModelId((int) id)` 语义即"段基址还原"，删除位宽阶梯（P5 按变更原因划责：阶梯与段截断是同一职责写两遍且互相抵消）
阶梯是历史过度设计：每级先缩位再交 `ofModelId` 段内截断，商恒小于段基址→恒 0。正确不变量="同模型序列号恒归位模型条目"，`ofModelId` 的 `/段大小×段大小` 本已蕴含剥尾。
**否决备选**：(a) 修阶梯每级除数——两段规则仍互为抵消源，且新增位宽分支维护面；(b) 维持现状仅告警——GameExplorer 拿到的仍是错值。
**验证（先手）**：实施首步从 `ItemTypes` 注册表取真实模型号段，建 GameExplorer 消费面哨兵（差量第三 Requirement 的回归 Scenario），修复前恒 0 命中=红基线。

### D6 DemandTypes.check 严格化 + ofAlias 空输入受控失败（P6：一族门面同契约，单类打折即 LSP 违例）
校验通道改走既有严格查找件（兄弟门面形态在位，零新抽象）；别名空串在拆分前判非法。
**否决备选**：保留宽松以兼容"依赖 null 的调用方"——grep 消费点清单先行，若有依赖即为静默放行缺陷的受害者而非受益者（release-note 公告）。

### D7 RSA 吞异常入口改显式抛（P6 守约 + P11 三问：编译不变、行为=失败显式化、无报文面）
`getPublicKey/getPrivateKey` 文本/数字非法路径不再 catch-null，透传既有 `InvalidKeySpecException`/`IllegalArgumentException` 方向（与 `toPrivateKey` 侧对表——同能力同方向）。仓内零消费者，外部消费者升级路径=把 null 判空改为受控异常处理，release-note 记。
**否决备选**：保留返 null 并加告警——方向分裂存续，钉桩翻转目标落空。

### D8 ObjectMap 参数装箱化+消息归一（P4：引擎单一事实源上消灭"参数形态双轨"）
共享引擎侧把原始类参数统一装箱规范化（一处归一、两侧同得消息一致），`float.class` 字面参数改 `Float.class`；行为后果零变化（convertTo 历史已中和）——本项是把"依赖上游宽容"的隐性正确转成显式正确。
**验证**：翻转 `floatDefChannelMessageDriftPinned` 为逐字一致断言；`getFloatChannelMatrix` 保持不变绿。

## Risks / Trade-offs

- **[GameExplorer 行为变化] →** 修复前恒解析 0 号位（多数情形查无→上层既有 null 分支容忍？）；修复后真实归位可能首次命中类型对象、打开过去"因失败而静默跳过"的代码路径。缓解：哨兵用例先行+变更说明点名运营侧核对道具表。
- **[oplog 日志文本形态变化（转义修正）] →** 外部日志解析方可能依赖旧字节形态。缓解：release-note 给前后样本 diff；日志面为文本非协议，解析器按标准转义语义本就应兼容。
- **[脚本面数值语义位移（D1/D2）] →** MVEL/GraalJS 配置脚本此前可能有人"顺着 −112/方向错误"写了补偿逻辑。缓解：全仓表达式资源 grep 常数对（0 命中则公告即可）。
- **[钉桩翻转误伤既有绿格] →** 每处翻转单列 diff、其余格保持不动，组级测试全绿才进下一组（沿用两轮验证节奏）。
- **[Trade-off] 未知字段选"显式失败"牺牲了旧 Json/Couch 静默通道的可用性**——换取读回完整性不变量；运营若有依赖可在发布说明预告逃生窗（配置开关不做：P10 单一场景）。

## Migration Plan

组序（文件面互斥、可逐组回滚）：1 前置哨兵与红基线 → 2 protobuf 转义保真（D4+公式）→ 3 protobuf 闭合与未知策略与消息集对称（D3/D5p）→ 4 lang 数值语义（D1/D2）→ 5 lang 消息一致（D8）→ 6 basics 门面三修（D5/D6）→ 7 digest RSA（D7）→ 8 门禁+release-note+账目。回滚=按组 revert；翻转后的测试即新契约基线，revert 代码会红=护栏仍在。

## Compatibility Impact

| 面 | 变更 | 下游 | 公告要点 |
|---|---|---|---|
| protobuf 五形态文本输出/读回 | 转义生效、还原保真、未知显式失败、消息集对称（D3/D4） | oplog 日志文本（唯一外部消费点） | BREAKING(修复向)×4，给样本 diff |
| NumberAide 五算术 | null 语义、窄类型提升（D1/D2） | LocalNum 家族、expr 脚本面 | BREAKING×2，脚本作者公告 |
| 转换失败消息 | 尾段归一装箱形态（D8） | net 两消费点（无消息解析依赖） | BREAKING(措辞向) |
| 枚举门面 | check 严格化、ofAlias 受控、ofItemId 归位（D5/D6） | **GameExplorer×2**、basics 校验链 | BREAKING×3，行为级最重 |
| RSA 文本入口 | null→显式抛（D7） | 仓内零消费 | BREAKING，外部按公告迁移 |
| 报文/序列化字节面、网络协议 | **零变更** | — | — |

公共签名（方法名/参数/返回类型）全部不动——本变更为行为 BREAKING，非签名 BREAKING。

## Open Questions

- `ofItemId` 的段基址大小是否需支持多档配置（若策划号段非百万制）——实施期查注册表实际号段再裁，默认单一常量。
- 未知字段失败消息格式（编号+位置的最小组合）——五形态归一实施时定样，不承诺对外格式稳定。
