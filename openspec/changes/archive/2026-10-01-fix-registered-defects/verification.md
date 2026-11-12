# Verification Summary（fix-registered-defects）

## 门禁（8.1）

`./gradlew test -x :tny-game-namnspace-etcd:test` → **BUILD SUCCESSFUL**；全仓测试 XML 汇总 **20 模块 / 937 用例 / 0 failure / 0 error / 0 skipped**（上轮基线 929 → +8：GameExplorer 号段哨兵、全形态转义矩阵、未知字段一致拒绝、非法输入闭合用例）。排除项沿用 etcd 环境债。

## 规格符合性审计（末位门禁，PASS）

- 实测工件口径：**11 Requirement / 24 Scenario**（任务书 12/26 为撰写期估算误差，审计员以工件为准逐条判定——本文件如实改账）。
- 逐 Requirement 判定全 PASS：每格翻转断言存在且方向=承诺；点名格组外零触碰；四域 red-baseline 红摘录在案。
- 审计小注记（LOW，不阻断，登记备查）：① BigInteger 显式零除数格未钉（JDK 通道本已抛出，风险极低）；② 转义矩阵含 é/😀 高位与代理对，CJK 显式样本缺注记（矩阵已有高位字节覆盖）；③ **Html 形态无 merge 读回面**——"五形态一致拒绝"差量措辞落位为**四可读回形态**一致拒绝 + 打印侧转义生效（Html 不新增读回 API，签名冻结授权；红基线+javadoc 双处注明，非翻转不彻底）。
- 越界触碰清单：审计所列均为兄弟支线工作面（bench 改名、集成测试通道、relay 握手等），非本变更产物——与本线无关，未代其做任何决定。

## 关键实施偏离与裁定（在册）

1. **ofItemId 字面修正**：design D5 的字面 `(int) id` 在 Scenario 样本 2500000999（超 int 位宽）下自相矛盾；实施按 1.1 号段证据（`itemIdOf` 十进制拼接、`getIdHead=id/1e6`）裁定为"反复÷10 取商至首位 × 段基址还原"，属 Open Question 授权面。主规格 sync 时 numeric/enumeration 措辞按此实态入账（建议 proposal 组同步一句裁定结果）。
2. **窄类型提升的调用面连带效应**（lang breakingSamples 实证）：以窄装箱静态类型推断结果的调用点（如 `mod((byte)7,(byte)2)` 推断 N=Byte）在 checkcast 处会出现运行期 CCE"Integer cannot be cast to Byte"——外显数值全对、静态推断面收窄；仓内 LocalNum 家族 18+ 既有绿格实证外显值零变化（外层 `as(x,存储原宽)` 回折自担承诺），外部消费者升级路径=加宽 `<Number>` 见证或 `intValue()` 取值，release-note 点名。
3. **oplog 影响面实测归零**：修复前后 37 个打印面 diff，`printFiles` 六面**逐字节一致**——BREAKING 全在读回/merge 面与 Html 打印面，既有 JSON 日志文本无需下游解析器变更。

## 过程账

- 首轮四域工作流 protobuf 代理曾被误判 fail-fast（journal 滞后），我补派拆段链后原代理结果回流——链已即时叫停，protobuf 测试面经全仓门禁复验完好（937 全绿含 protobuf 17/17）。
- 红基线总账：`red-baseline.md`（四域分册合并全文，按 task 归位）。
- 消费面账目：`consumer-scan.md`（DemandTypes.check 零调用方；GameExplorer×2 清单；表达式资源 −112 扫描结论）。

## validate（8.2 后半）

`openspec validate fix-registered-defects --strict` → valid（见任务勾记）。

## 翻转对照表（8.3：原钉桩名 → 新断言）

| 原在册钉桩（去重轮遗留登记） | 翻正后断言位置 | 方向变化 |
|---|---|---|
| RT_JSON/COUCH_ESCAPES 合并重打印有损快照 | FormatEscapeFidelityTest Unicode 位权格＋二次打印逐字等 | 有损 `ﾯ` → 逐字节还原 |
| P_HTML_ESCAPES 值裸出/首段丢失 | 同文件 Html 转义格（短表+`\<` `\>`） | 裸出 → 转义承载 |
| RT_XML_ESCAPES 读回必抛 `Expected ">"` | FormatMergeRoundTripTest XML 格＋四形态闭合矩阵 | 抛错 → 成功且值等 |
| unknown 三形态各异报错/两形态静默丢弃快照 | FormatUnknownFieldRejectionTest（四可读回形态统一样例）＋UNKNOWN 四格翻转 | 各异/静默 → 统一显式拒绝 |
| RT_PROPS_MSGSET 读回必抛 `Expected "."` | 同点翻正＋非扩展路径点号强制零波及 | 分裂 → 往返对称 |
| nullCombinationsSharedAcrossOperators | NumericHashIntegrityContractTest 零元矩阵（sub 取反/除零 ArithmeticException/add 兼容保留） | 返对侧 → 零元语义 |
| shortByteBranchesNarrowToFirstOperand（−112） | 同文件 JLS 提升格（400/Integer、Long 混合、同宽超界） | 首操作数回折 → 提升通道 |
| floatDefChannelMessageDriftPinned | MapConvertAccessContractTest 两侧消息逐字等＋装箱尾段（引擎 boxed 归一＋门面 Float.class 双保险） | 尾段分叉 → 归一 |
| demandTypesCheckIsLenient…_DivergencePinnedNotFixed | EnumRegistryFamilyContractTest 两格 assertThrows(NPE 含身份) | null 放行 → 显式失败 |
| ItemTypes.ofAlias("") IOOBE | 翻正 IllegalArgumentException 含输入描述（补纯分隔符格） | 越界栈 → 受控失败 |
| ofItemId 五分支恒 0 现状 | 五分支全翻＋GameExplorerItemIdSentinelTest 真实号段归位 | 恒 0 号位 → 归位模型条目 |
| RSAUtilsTest.illegalKeyTextFailsUnderControl null 半段 | 翻正 IllegalArgumentException×4（公/私×非数文本/非法编码），toKey 系三格零改动对表 | 吞返 null → 显式抛 |
