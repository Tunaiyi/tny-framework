# 红基线总账（fix-registered-defects）

> 四域分册全文见 red-baseline-basics/protobuf/lang/digest.md；本文件为索引+合并全文。按 task 号归位。


---
## basics+前置账目域（tasks 1.1/1.2/6.1-6.4）

# Red baseline — basics 域（tasks 1.1/1.2/6.1-6.4，design D5/D6）

日期：2026-10-01。分支 5.7.x。铁律自查：本域测试翻转只动差量点名格（对照表见文末"翻转面清单"），
其余既有绿格期望值与断言零改动（`git diff -- tny-game-basics/src/test` 仅 2 方法 + 新文件，见下）。

## 一、11 在册钉桩核对表（tasks 1.2；六组归档 verification 遗留登记 → 现测试代码）

| # | 钉桩名（design Context 名录） | 现测试代码位置（在位确认） | 对应差量 Scenario | 本域归属 |
|---|---|---|---|---|
| 1 | RT_JSON/COUCH_ESCAPES | `tny-game-protobuf/.../FormatMergeRoundTripTest.java` `jsonMergeRoundTripSnapshots`（RT_JSON_ESCAPES_PRINT L140）/ `couchMergeRoundTripSnapshots`（RT_COUCH_ESCAPES_PRINT L144） | protobuf-text-fidelity（Unicode 还原格） | 组2 |
| 2 | P_HTML_ESCAPES | 同目录 `FormatGoldenTest.java` `htmlPrintToStringSnapshots` GOLDEN_HTML_ESCAPES 格（L68/L192，名录为记号别名，实体=打印快照格） | protobuf-text-fidelity（Html 转义对齐） | 组2 |
| 3 | RT_XML_ESCAPES | `FormatMergeRoundTripTest.xmlMergeRoundTripSnapshots` escapes 格（钉 ParseException `"1:25: Expected \">\"."` L74-76，实体为格形态） | protobuf-text-fidelity（Xml 闭合） | 组3 |
| 4 | RT_PROPS_MSGSET | `FormatMergeRoundTripTest.propsMergeRoundTripSnapshots` msgSet 格（钉 ParseException `"1:30: Expected \".\"."` L107-109，格形态） | protobuf-text-fidelity（消息集对称） | 组3 |
| 5 | unknown 三形态（+两静默） | 抛式三格：`FormatMergeRoundTripTest` XML UNKNOWN L69-71（`"1:42: Expected identifier. --"`）/ PROPS UNKNOWN L104-106（`"2:1: Expected identifier."`）+ `FormatGoldenTest` GOLDEN_HTML_UNKNOWN L66；静默两格：RT_JSON_UNKNOWN_PRINT L138 / RT_COUCH_UNKNOWN_PRINT L142（另 GOLDEN_{XML,JSON,COUCH,PROPS}_UNKNOWN 打印快照 L40/53/92/79） | protobuf-text-fidelity（未知字段显式失败） | 组3 |
| 6 | floatDefChannelMessageDriftPinned | `tny-game-common-lang/.../map/access/MapConvertAccessContractTest.java` | object-access-conversion | 组5 |
| 7 | nullCombinationsSharedAcrossOperators | `tny-game-common-lang/.../number/NumberAideTest.java` | numeric-hash-integrity（null 零元） | 组4 |
| 8 | shortByteBranchesNarrowToFirstOperand | `tny-game-common-lang/.../number/NumberAideTest.java` | numeric-hash-integrity（窄类型提升） | 组4 |
| 9 | demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed | `tny-game-basics/.../EnumRegistryFamilyContractTest.java` | enumeration-facade-semantics R1（严格校验通道） | **本组 6.1（已翻正）** |
| 10 | ItemTypes 五分支用例（ofItemId）＋ ofAlias("") IOOBE 格 | 同上 `itemTypesAliasAndIdTruncationExtensions`（五分支 L399-403 原状→翻正；ofAlias 空串格原状→翻正并补纯分隔符格） | enumeration-facade-semantics R2/R3 | **本组 6.2/6.3（已翻正）** |
| 11 | illegalKeyTextFailsUnderControl（返 null 半段） | `tny-game-common-digest/.../rsa/RSAUtilsTest.java` | digest-codec | 组7 |

**缺项**：无——11 项全部在位；组 2/3/4/5/7 翻正由各域组负责，本域不触碰其文件。

## 二、号段证据（tasks 1.1 前置；D5 Open Question 裁定依据）

- 注册实际来源：`GameEnumClassLoader.createSelector(ItemType.class, ItemTypes::register)` 类扫描装载；仓内可见模型号段样本 =
  测试夹具注册条目（basics：908001/9000000/0；starter 哨兵新注册：2000000/9000000）。
- **正向生成面（关键证据）**：`ItemType.itemIdOf(index) = Long.parseLong(getIdHead() + "" + index)`，
  `getIdHead() = getId()/ID_TAIL_SIZE(1e6)`；`BaseSingleStuffOwner` L52 入库即以 itemIdOf 产全局号。
  → 全局号 = 十进制拼接（首位=idHead，尾=变宽序列号），如 `2000000.head=2 → itemIdOf(500000999)=2500000999`。
- **单档百万制成立**：注册模型段基址恒为 `1e6 × 个位数`（head 1..9 单档）。差量承诺样本 2000001（7 位宽）与
  2500000999（10 位宽，**超 int 位宽**）同归模型 2000000——固定段商 `/S×S` 单常量无法同时成立（S=1e6 时
  2500000999→2.5e9≠2e6；S≥2.5e9 则非 1e6 倍数破坏 getIdHead 契约）；**design D5 字面 `ofModelId((int) id)`
  对 2500000999 即 int 回绕为负→归位失败**。
- **裁定（Open Question 按号段证据落定，实现采最小改动）**：单一商式="反复 ÷10 取商至首位数字"
  （即对 10^(位数−1) 取商），首位=getIdHead 逆还原，`×ID_TAIL_SIZE` 回段基址后交 `ofModelId`（其 `/1e6×1e6`
  对段基址恒等）。段大小不引入多档配置，恒用既有常量 `ID_TAIL_SIZE`（=1e6，单一事实源不动）。
  负值/未注册号段按 `ofModelId→宽松 of` 通道语义返 null。

## 三、红基线摘录（铁律 2：先翻期望值跑红，摘录逐条标 task）

### [1.1/6.3] GameExplorer 消费面哨兵（新文件，修复前恒 0 命中）

```
> Task :tny-game-starter-basics:test FAILED
GameExplorerItemIdSentinelTest > serialsOfSameModelAllResolveBackToModelEntry() FAILED
    org.opentest4j.AssertionFailedError at GameExplorerItemIdSentinelTest.java:78
    ==> itemIdOf 产出的 2 位序列号须归位模型 2000000（现状恒解析段 0=红基线）
        expected: <SENTINEL_MODEL_2E6> but was: <null>
2 tests completed, 1 failed
```
（短路于首格；2000001/2500000999/9999/9e18 各格同族恒段 0——原状阶梯 `商恒<ID_TAIL_SIZE→取整 0`。
`unregisteredHeadSegmentFollowsLenientLookupSemantics` 格首跑即绿，钉未注册边界非翻转面。）

### [6.1/6.2] 首跑 basics 契约矩阵（两方法红）

```
> Task :tny-game-basics:test FAILED
EnumRegistryFamilyContractTest > itemTypesAliasAndIdTruncationExtensions() FAILED
    org.opentest4j.AssertionFailedError at EnumRegistryFamilyContractTest.java:396
        Caused by: java.lang.ArrayIndexOutOfBoundsException at EnumRegistryFamilyContractTest.java:396
EnumRegistryFamilyContractTest > demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed() FAILED
    org.opentest4j.AssertionFailedError at EnumRegistryFamilyContractTest.java:369
    (Expected java.lang.NullPointerException to be thrown, but nothing was thrown)
10 tests completed, 2 failed
```
（369 格=DemandTypes.check(String) 显式失败未发生=6.1 红；396 格=ofAlias("") 越界栈外泄=6.2 红。）

### [6.2 转正后中间跑] 五分支格红（ofItemId 实现未动，红证补录）

```
EnumRegistryFamilyContractTest > itemTypesAliasAndIdTruncationExtensions() FAILED
    org.opentest4j.AssertionFailedError at EnumRegistryFamilyContractTest.java:411
    ==> 翻转（6.3）：9999 首位 9 → 段基址 9000000 归位 TRUNK（TRUNK.itemIdOf(999) 实产样本）
        expected: <PROBE_ITEM_TRUNK> but was: <PROBE_ITEM_ZERO>
10 tests completed, 1 failed
```
（411 格=五分支首支；JUnit 短路，余四支与首支同因（恒 0 段），哨兵红已覆盖同族样本。）

## 四、转绿证据（改实现后）

| 命令 | 结果 |
|---|---|
| `./gradlew :tny-game-basics:test --rerun-tasks` | BUILD SUCCESSFUL；tests=19 failures=0 errors=0 |
| `./gradlew :tny-game-starter-basics:test --rerun-tasks` | BUILD SUCCESSFUL；tests=2 failures=0（GameExplorer 哨兵绿） |
| `./gradlew :tny-game-common-scheduler:test :tny-game-starter-basics:compileJava --rerun-tasks` | BUILD SUCCESSFUL；scheduler tests=50 failures=0（TaskDeliveryContractTest 别名缺席钉桩 `removedAliasLookupEntryIsAbsentFromPublicMethodSurface`/`strictLookup...` 零回归——scheduler 不依赖 basics，其 TaskReceiverTypes 源未动） |

## 五、翻转面清单（铁律 1 自查，`git diff -- tny-game-basics/src/test` 实测）

- `demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed`：仅 2 格 assertNull→assertThrows(NPE,消息逐字)；
  方法名保留（8.3 翻转对照表可按原名索引）。命中格与其余六件套格零改动。
- `itemTypesAliasAndIdTruncationExtensions`：ofAlias("") 1 格 IOOBE→IAE+消息含输入；新增纯分隔符 "$" 1 格（差量点名面）；
  ofItemId 五格全翻（ZERO→TRUNK/null）；ofModelId 三格与 ofAlias 命中/未命中消息格零改动。
- 新增文件：`tny-game-starter-basics/src/test/java/com/tny/game/basics/item/GameExplorerItemIdSentinelTest.java`
  （Mulan PSL v2 头；同包直调包私有 register 按现状可见性，未动生产签名）。
- 实现面：`DemandTypes.check×2`（严格通道薄委托，签名不动）、`ItemTypes.ofAlias`（拆前判非法，null 通道现状不动）、
  `ItemTypes.ofItemId`（阶梯 45 行删除→单一商式 6 行）。

## 六、release-note 行为样本（供组 8）

- `DemandTypes.check("未注册")`：`null`（静默放行）→ `NullPointerException: 获取 未注册 DemandType 不存在`；
  `check(未注册id)` 同理携 ID 消息。全仓零既有调用方（consumer-scan.md）。
- `ItemTypes.ofAlias("")`：`ArrayIndexOutOfBoundsException` 越界栈 → `IllegalArgumentException:
  ItemType 别名查找输入非法（空串或纯分隔符）: ""`（"$" 同）；GameExplorer.getItemByAlias 空别名输入方可见。
- `ItemTypes.ofItemId(2000001L)` / `GameExplorer.getItem(AnyId(…,2000001))`：恒段 0（注册表 0 号位或 null）→
  模型 2000000 条目；9e11/9e15/9e18 等同理归首位段。**道具类型解析链路行为级 BREAKING**（design 公告表
  「枚举行 GameExplorer×2」运营侧核对道具表要点名）。


---
## protobuf 域（tasks 2.1-3.4）

# red-baseline：protobuf 域（tasks 2.1–2.4、3.1–3.4）

取证方式：翻转差量点名格的期望值 → `./gradlew :tny-game-protobuf:test --rerun-tasks` 取失败输出逐条摘录 → 再改实现转绿。
基线（翻转前全量）：`./gradlew :tny-game-protobuf:test --rerun-tasks` 15 用例全绿（2026-10-01 实测 BUILD SUCCESSFUL）。

## Task 2.1 —— RT_JSON/COUCH_ESCAPES 翻"往返逐字节相等" + 全形态往返矩阵（FormatEscapeFidelityTest 新增）

`FormatMergeRoundTripTest > jsonMergeRoundTripSnapshots() FAILED`
`FormatMergeRoundTripTest > couchMergeRoundTripSnapshots() FAILED`

```
org.opentest4j.AssertionFailedError:
expected: <opt_string: "q\"a\'p\\bs\nnr\ttv \001\033\a vk \303\251\360\237\230\200"
opt_bytes: "\a\b\v\f\000\001\037 !\"\'\\\177\200\377Az~">
 but was: <opt_string: "q\"a\'p\\bs\nnr\ttv \001\033\a vk \303\251\360\237\230\200"
opt_bytes: "\a\b\v\f\000\001\037 !\"\'\\\1770\257Az~">
```

现状有损点（即错误公式 `16*3/16*2/16*1/1` 的产物）：`ﾀ`→`0`（`\177 0`）、`￿`→`ﾯ`（`\257`，U+00AF 字节形态，重打印快照即旧钉桩中的 `0ﾯ`）。矩阵格 `\u` 四位权展开样本（FormatEscapeFidelityTest > unicodeWeightFormulaRestoresHighBytesExact() FAILED）：

```
expected: <opt_bytes: "\017\360\217\370\377\177">  （0x0F/0xF0/0x8F/0xF8/0xFF/0x7F 覆盖 16⁰~16³ 各位权）
 but was: <opt_bytes: "\017\240?\250\257\177">
```

`0x0F`（仅末位权）两侧一致、其余各位全偏——钉死"公式为权展开错误而非编码面差异"。

## Task 2.3 —— P_HTML_ESCAPES 翻"值内标记/换行以转义序列承载且结构不破坏"

`FormatGoldenTest > htmlPrintToStringSnapshots() FAILED`，期望（差量承诺形态）：

```
...font-family: sans-serif;">"q\"a'p\\bs\nnr\ttv \u0001\u001b\u0007 vk é😀"</span><br/>...
```

实际（现状不转义形态，opt_string 段）：

```
...font-family: sans-serif;">"<br/>nr\ttv ␁␛␇ vk é😀"</span><br/>...
```

即：值内引号/反斜杠裸出（标记注入面）、换行裸出被 HtmlGenerator 替换为 `<br/>` 打断结构，且首行 `\n` 之前的内容 `"q\"a'p\bs` 在现状 print/write 通道整体丢失（before 样本实测缺失，见 /tmp/protobuf-print-sample/before.txt，本文件末节留样）。其余 Html golden 格（值集无需转义）保持逐字绿。

## Task 3.1 —— RT_XML_ESCAPES 翻"读回闭合成功且值等"

`FormatMergeRoundTripTest > xmlMergeRoundTripSnapshots() FAILED`
`FormatEscapeFidelityTest > allFormEscapeMatrixRoundTripIsByteExact() FAILED`（矩阵 XML 形态格同源）

```
com.tny.game.protobuf.format.Protobuf2XmlFormat$ParseException: 1:25: Expected ">".
```

即旧钉桩逐字消息——Xml 词法对打印产物（`\"` `\'` `\\` 八进制族）不闭合。翻转后期望：`assertEquals(fx.escapes(), mergeRoundTrip("XML", fx.escapes()))` 成功且值逐字节等。

（矩阵"非法文本读回仍显式失败（错误路径）"格 `malformedEscapeTextRejectedWithPosition() FAILED` 同场取证：JSON 残缺 `\u` 转义现状抛 `java.lang.StringIndexOutOfBoundsException: Index 4 out of bounds for length 4`（非受控显式失败），承诺形态为带 `行:列:` 位置的 `Invalid escape sequence` ParseException；Xml 尾反斜杠格现状虽抛 ParseException 但消息为词法 `Expected ">".`，3.1 实现后归一为 `Invalid escape sequence` 消息。）

## Task 3.2 —— 未知字段统一显式拒绝（FormatMergeRoundTripTest 四 UNKNOWN 格翻转 + FormatUnknownFieldRejectionTest 新建）

翻转后、实现前（2026-10-01 实测，17 用例 6 红，UNKNOWN 相关摘录如下）：

`FormatMergeRoundTripTest > jsonMergeRoundTripSnapshots() FAILED` / `couchMergeRoundTripSnapshots() FAILED`

```
org.opentest4j.AssertionFailedError: Expected ...ParseException to be thrown, but nothing was thrown.
```

即现状"静默丢弃"（返回"缺少该字段但成功"的对象，旧快照 `{"opt_string": "known"}` 同义）。

`FormatMergeRoundTripTest > xmlMergeRoundTripSnapshots() FAILED` / `propsMergeRoundTripSnapshots() FAILED`

```
AssertionFailedError: 1:42: Expected identifier. -- ==> expected: <true> but was: <false>   （Xml：词法错位，不可定位编号）
AssertionFailedError: 2:1: Expected identifier.   ==> expected: <true> but was: <false>   （Props：词法错位，不可定位编号）
```

`FormatUnknownFieldRejectionTest > unknownFieldNumberRejectedConsistentlyAcrossReadableForms() FAILED`

```
失败信息需可定位首个未知编号且五形态样例一致: 1:42: Expected identifier. --
```

实现后归一样例（design 开放问题"五形态归一时定样"）：`行:列: Unknown field number: 995.`。

## Task 3.3 —— RT_PROPS_MSGSET 翻转（打印短名路径与读回点号全名不对称）

3.2 实现落地后单独暴露的孤立红（2026-10-01 实测，17 用例 1 红）：

`FormatMergeRoundTripTest > propsMergeRoundTripSnapshots() FAILED`

```
com.tny.game.protobuf.format.Protobuf2JavaPropsFormat$ParseException: 1:30: Expected ".".
```

即旧逐字钉桩消息——打印产物 `[tny.protobuf.test.MsgSetExt]m=7` 无点号分隔，merge 在读回扩展键后强制 `consume(".")`。翻转后期望 `assertEquals(fx.msgSet(), mergeRoundTrip("PROPS", fx.msgSet()))` 往返闭合。

## 矩阵格转绿记录（2.1/2.2 复跑证据）

公式修正实现后，`FormatEscapeFidelityTest > unicodeWeightFormulaRestoresHighBytesExact()` 的有损产物样本面：
修正前二次打印为 `\u000f\uffa0?\b\uffaf`（错误公式把 0xF0→\uffa0、0x8F→`?`、0xF8→\b、0xFF→\uffaf，
即旧 RT_JSON/COUCH_ESCAPES 钉桩快照中 `0`+`\uffaf`（`ﾯ`）现状值的同族产物）；修正后逐字节还原为
`\u000f\ufff0\uff8f\ufff8\uffff`+裸 DEL——`contains(\ufff0/\uff8f/\uffff)` 成立且
`!contains(\uffaf/\uffa0)`（`ﾯ` 现状值失效确认）转绿。矩阵首跑时本格红摘录：
`expected: <opt_bytes: "\017\360\217\370\377\177"> but was: <opt_bytes: "\017\240?\250\257\177">`。

## Task 2.4 —— oplog printFiles 前后文本样本 diff（release-note 素材）

取证方式：`tny-game-protobuf` 编译产物 + `FormatTestProtos` 夹具消息，以独立 runner（/tmp/protobuf-print-sample/PrintSample.java）
对六个夹具消息（full/special/escapes/unknown/msgset/doc）× 六个打印面（json-printFiles、json/couch/xml/props/html-printToString）
在实现前后各捕获一次，逐面对 diff。样本文件：/tmp/protobuf-print-sample/{before,after}.txt（2026-10-01）。

结论（37 个打印面对比）：

1. **oplog 唯一外部消费面 `Protobuf2JsonFormat.printFiles` 前后逐字节一致**（6/6 面零差异）。
   BREAKING(修复向) 全部落在读回/merge 面与 Html 打印面；oplog 既有日志文本字节面不变，
   外部日志解析方无升级义务——release-note 可据此降级 oplog 面的迁移成本描述。
2. **唯一打印面差异：Html printToString 的转义特殊字符集值**（差量 2.3 承诺面，oplog 不消费 Html）：

   BEFORE（值内标记裸出、换行打断结构、且首换行前内容整体丢失）：
   ```
   ...font-family: sans-serif;">"<br/>nr␉tv ␁␛␇ vk é😀"</span><br/>...
   ```
   （`q"a'p\bs` 段在现状 write 通道丢失——不转义缺陷伴生面）

   AFTER（值内标记/换行以转义序列承载、无损、结构不破坏）：
   ```
   ...font-family: sans-serif;">"q\"a'p\\bs\nnr\ttv \u0001\u001b\u0007 vk é\ud83d\ude00"</span><br/>...
   ```

3. Json/Couch/Xml/Props 四形态全部 printToString 面逐字节一致（本变更未触碰其打印侧；修复只落在
   unescape/merge 读回侧与 Props 读回命名闭合）。

## Task 3.4 —— 收口自查

- `./gradlew :tny-game-protobuf:test --rerun-tasks`：**17/17 全绿**（原 15 用例 + 新增 FormatEscapeFidelityTest/FormatUnknownFieldRejectionTest，0 failures 0 errors）。
- `./gradlew :tny-game-oplog:compileJava`：BUILD SUCCESSFUL（唯一外部点）。
- `git diff tny-game-protobuf` 自查：测试面仅八张点名格（RT_{JSON,COUCH}_ESCAPES、P_HTML_ESCAPES、RT_XML_ESCAPES、
  RT_{XML,JSON,PROPS,COUCH}_UNKNOWN、RT_PROPS_MSGSET）期望值翻转 + 快照常量清理/夹具加法；
  其余既有绿格期望值与断言零改动。主源面新增声明仅包私有件（FormatTextSupport.escapeTextHtml、
  Protobuf2JsonFormat.isFieldNumberText 私有方法）——公共签名（方法名/参数/返回类型）零变更。

## 承诺面落位备注（不扩大不缩小）

- 差量 Scenario「未知编号读回五形态一致拒绝」：五打印形态中 **Html 无 merge 读回面**（公共签名冻结、不新增
  merge）——一致拒绝落为四个可读回形态（XML/JSON/PROPS/COUCH）统一样例 `行:列: Unknown field number: 995.`，
  已在 FormatUnknownFieldRejectionTest javadoc 与红基线注明。
- Json/Couch 对**非编号**未知名字段的 handleMissingField 旁路（既有静默通道）不在差量承诺面内，保持原样未动
  （防止自行扩大承诺面）；Protobuf 五形态归一样式按 design 开放问题"实施时定样、不承诺对外格式稳定"处理，
  测试只钉"编号+位置可定位"与样例短语。
- 2.3 的矩阵/钉桩面：Html STRING 转义采用统一短表（JSON 行）+ 尖括号 `\<` `\>` 序列；`<`/`>` 之外的裸出字符
  （如值内裸 `</`）属打印侧既有词法风险，不在差量承诺集合（Scenario 点名尖括号/引号/换行注入面），未顺手扩大。


---
## lang 域（tasks 4.1-5.3）

# Red Baseline — lang 域（task 4.1-4.4、5.1-5.3）

取证方式：翻转期望值 → `./gradlew :tny-game-common-lang:test --tests <类>[.<方法>] --rerun-tasks` 取失败输出 → 实现转绿。
JUnit 方法内断言 fail-fast：同一方法内后续红格被首格遮蔽，分轮次摘录（轮次间隔=实现推进半步）。

## task 4.1（翻转 `NumberAideTest.nullCombinationsSharedAcrossOperators` → 零元语义矩阵）

轮 1（实现前，全矩阵首格遮蔽后续）：

```
NumberAideTest > nullCombinationsSharedAcrossOperators() FAILED
    org.opentest4j.AssertionFailedError: 空被减数=0−5，取反对侧值 ==> expected: <-5> but was: <5>
    (NumberAideTest.java:46)
```

= 在册钉桩方向确认：`sub(null, 5)` 现状直接返回另一操作数原值 5（规格承诺 −5）。
矩阵其余被遮蔽格的现状值（旧实现逐格可复算，与遗留登记第 3 项同源）：
`multiply(null,5)/multiply(5,null)` 现状=5（承诺 0）；`divide(null,5)/mod(null,5)` 现状=5（承诺 0）；
`divide(5,null)/mod(5,null)` 现状=静默返回 5（承诺受控失败）；`divide(null,null)/mod(null,null)` 现状=null（承诺受控失败）；
`divide(1.0f,0.0f)` 现状=Infinity、`mod(1.0f,0.0f)`/`mod(1.0d,0.0d)` 现状=NaN、`divide(1.0d,0.0d)` 现状=Infinity（承诺受控失败，不返伪值）。

轮 2（D1 null 归一半步实现后，浮点零除守门未到位）：

```
NumberAideTest > nullCombinationsSharedAcrossOperators() FAILED
    org.opentest4j.AssertionFailedError: Expected java.lang.ArithmeticException to be thrown, but nothing was thrown.
```

= 浮点通道零除数伪值方向确认（IEEE 语义静默产出 Infinity/NaN，`1.0f/0.0f` 格起遮蔽）。

## task 4.2（实现，转绿证据）

实现点（公开签名零改动，私有分派器单点改，`NumberAide.operate`/`NumberOperation`/`zeroLike`）：
- null→该运算零元：`zeroLike(对侧)` 按对侧类型通道构造数值零后照常分派（sub 空被减数=0−x 取反）；
- 双空：divide/mod 除数侧零化仍零 → 当场 `ArithmeticException`；add/sub/multiply 无落型通道维持返回 null（现状兼容格）；
- 除零显式失败：`NumberOperation.explicitZeroDivisor`（DIVIDE/MOD=true）守门，Float/Double 通道除数 `==0`（含 −0）即抛
  `/ by zero`——方向对齐既有高精度路径（`bigDecimalHighPrecisionPaths` 的 BigDecimal 除零格与 int/long 通道 JVM 抛出本已受控失败，零改动保持绿）。
绿证据：`NumberAideTest` 9 用例 failures=0（其余既有绿格期望值零改动同步通过）。

## task 4.3（翻转 `NumberAideTest.shortByteBranchesNarrowToFirstOperand` + 新增同宽超界/long 混合格）

轮 1（实现前）：

```
NumberAideTest > shortByteBranchesNarrowToFirstOperand() FAILED
    org.opentest4j.AssertionFailedError: short×short 提升 int 通道（BREAKING：此前折叠回 Short）
    ==> Unexpected type, expected: <java.lang.Integer> but was: <java.lang.Short>
```

轮 2（findClass 提升 + 落型单点改实现后，调用面 checkcast 遮蔽格暴露）：

```
NumberAideTest > shortByteBranchesNarrowToFirstOperand() FAILED
    java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.Byte
```

= BREAKING 调用面实证：`mod((byte)7,(byte)2)` 按推断 N=Byte 的调用点，javac 在调用处插入 checkcast Byte，
落型改 Integer 后运行期即 CCE。钉桩既有纯值格 `assertEquals((byte)1, NumberAide.mod((byte)7,(byte)2).byteValue())`
因此必须补 `<Number>` 类型见证（断言值 (byte)1 零改动，仅调用见证补位——同方法内既有格 `add((byte)1,2)` 本已带
`<Number>` 见证，同一形态）。release-note 公告：以窄装箱静态类型推断五算子结果的外部调用点需加宽见证或改接 intValue() 等取值面。
绿证据：`NumberAideTest` 9 用例 failures=0；`NumericHashIntegrityContractTest` 18 用例（含 LocalNum 原宽/窄参数钉桩）failures=0。

## LocalNum 家族委托面自查表（D2 落型影响逐类）

| 委托入口 | 是否经 `NumberAide.operate` | D2 落型影响 | D1 null 影响 | 外显结论 |
|---|---|---|---|---|
| LocalNum.add/sub/multiply/divide/mod(int/long/float/double) | 否（`as(value, this.number)` 直落） | 无 | 参数为原始类型不可空 | 零影响 |
| LocalNum.add/sub/multiply/divide/mod(short/byte) | 否（先 `(long)` 加宽再走上行） | 无 | 同上 | 零影响（"存储值原宽参与"承诺由本类自担，差量回归格保持） |
| LocalNum.add/sub/multiply/divide/mod(Number/LocalNum) | 是 | operate 结果类型变 Integer/Long，但外层 `as(x, this.number)` 立即回折存储原宽 → 逐格同值（如 LocalNum<Byte>(100).add(300)：旧 −112/新 400→回折 −112） | 存储值恒非空（set 判空）；对侧 null 语义随五算子矩阵：divide/mod(Number null) 由"返回存储值"改受控失败（未钉桩方向变化，随 D1 公告） | 值面零回归（18 绿格实证）；除零/零乘 null 入口行为随五算子 |
| Short/Int/Long/Float/DoubleLocalNum 的原始类型入口 | 否（复合赋值 `+=` 隐式窄化，不经 NumberAide） | 无 | 无 | 零影响 |
| Short/Int/Long/Float/DoubleLocalNum 的 add/sub/multiply/divide/mod(LocalNum) | 是 | 同 LocalNum 行：外层 `as(x, 存储原始值)` 回折 → 逐格同值 | 同上 | 值面零回归 |
| LocalBoolean | 否（不引用 NumberAide） | 无 | 无 | 零影响 |
| MathAide（静态 import NumberAide.*） | 仅比较族 less/greater/min/max | findClass 窄→Integer 提升后比较支路取值逐位相同（intValue≡原 short/byte 支路值） | 比较族不在五算子承诺面、未触碰 | 零影响 |
| basics Trade/AlterType/SimpleTrade/CollectTradeItem、starter-basics CapacityCollector 等（静态/显式 NumberAide.add/sub/比较） | 是 | 调用点静态类型均为 Number/Integer/Long（无窄推断 checkcast 面）；Integer×Long 折叠面未改（add(5,3L) 得 Integer 现状绿格钉住） | 生产路径无 null 操作数流入（merge/reduce 无空元素） | 由 4.4 `:tny-game-basics:test` 实证 |

## task 5.1（翻转 `MapConvertAccessContractTest.floatDefChannelMessageDriftPinned` → 两侧逐字相等·装箱形态）

轮 1（实现前）：

```
MapConvertAccessContractTest > floatDefChannelMessageDriftPinned() FAILED
    org.opentest4j.AssertionFailedError: ObjectMap 侧尾段已归一为装箱形态（D8 翻转：此前 '…to float'）
    ==> expected: <class java.lang.Boolean can not convert to class java.lang.Float>
        but was: <class java.lang.Boolean can not convert to float>
```

= 在册分叉方向确认（遗留登记第 1 项：同语义参数两侧尾段 `float` vs `java.lang.Float` 分叉，ObjectMap 侧走原始类尾段）。

## task 5.2（实现，转绿证据）

实现点：
- `MapConvertAccessSupport`：新增私有 `boxed(Class)` 归一（`isPrimitive()` → `Wrapper.getWrapper`），
  convertOrNull/convertRequired/convertIfPresent 三个尾段入口统一装箱后交 `ObjectAide.convertTo`（一处归一、两侧同得消息一致）；
- `ObjectMap.getFloat(key, def)`：缺陷实参 `float.class` → `Float.class`（与 Wrapper 侧同参；成功面行为零变化——
  convertTo 历史演进本已消化原始类参数，本项把"依赖上游宽容"转成显式正确，design D8）。
绿证据：`MapConvertAccessContractTest` 10 用例 failures=0（`floatChannelMatrix` 等其余格期望值零改动、`getFloat(boolVal,def)`
格 messageMustMatch=false 参数面不动、翻转后两侧消息实际亦逐字相等）；`WrapperObjectMapConversionTest` 7 用例 failures=0。
BREAKING 措辞样本（release-note）：
`ObjectMap.getFloat("k", -1f)`，存储值 Boolean.TRUE →
变更前尾段 `class java.lang.Boolean can not convert to float`；
变更后两侧统一 `class java.lang.Boolean can not convert to class java.lang.Float`。

## task 4.4/5.3 门禁记录

- `:tny-game-common-lang:test --rerun-tasks`：BUILD SUCCESSFUL，tests=249 failures=0 errors=0（两轮全模块复跑均绿）。
- `:tny-game-basics:test`：BUILD SUCCESSFUL，tests=19 failures=0 errors=0。
- `:tny-game-expr:test`：BUILD SUCCESSFUL（该模块 src/test/java 无 Java 用例，tests=0；NumberAide/LocalNum 在 expr 主源零引用，grep 实证）。
- `:tny-game-net:test --rerun-tasks`：BUILD SUCCESSFUL，tests=167 failures=0 errors=0（ObjectMap 消费点两处在 relay metadata，无消息解析依赖）。
- 插曲：首轮 `:tny-game-basics:test :tny-game-expr:test` 联跑报 "Could not write XML test results for com.tny.game.basics.item.xml.YamlTest/
  XStreamItemModelManagerTest/YamlItemModelManagerTest" ×3——系与并行 basics 组同时执行同模块 test 任务争写 build/test-results 目录
  （基础设施写冲突、非断言失败；涉事类名中的 xml/Yaml 只是包名巧合，与 lang 域改动无关）；按铁律 5 不杀锁、串行重试后两模块各自 BUILD SUCCESSFUL。



---
## digest 域（tasks 7.1-7.2）

# 红基线 — digest 域（design D7：RSA 文本入口吞异常返 null → 显式失败）

取证命令：`./gradlew :tny-game-common-digest:test --tests "com.tny.game.common.digest.rsa.RSAUtilsTest" --rerun-tasks`（2026-10-01，JDK 21.0.12.1）
结果：`14 tests completed, 1 failed — BUILD FAILED`

## task 7.1 — 翻转 `RSAUtilsTest.illegalKeyTextFailsUnderControl` 的"返 null"半段

翻转前该格钉桩（去重轮遗留登记原文方向）：

```java
// 模/指数入口：非法数值吞异常返 null（该入口不抛受检异常，调用方必须判空）
assertNull(RSAUtils.getPublicKey("非数字", "x"), "模非法时公钥重建必须返回空");
assertNull(RSAUtils.getPrivateKey("非数字", "x"), "模非法时私钥重建必须返回空");
```

翻转为差量承诺方向（非法数字文本 + 非法编码两形态、公/私两入口共四断言）后取红，首个翻断即红：

```
RSAUtilsTest > illegalKeyTextFailsUnderControl() FAILED
    org.opentest4j.AssertionFailedError: Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
        at com.tny.game.common.digest.rsa.RSAUtilsTest.illegalKeyTextFailsUnderControl(RSAUtilsTest.java:199)
```

- L199 = `assertThrows(IllegalArgumentException.class, () -> RSAUtils.getPublicKey("非数字", "x"))`
- 红因：`RSAUtils.getPublicKey/getPrivateKey` 现实现 `catch (Exception e) { e.printStackTrace(); return null; }`——
  `new BigInteger("非数字")` 的 NumberFormatException 被吞，方法正常返 null，故"nothing was thrown"。
- 同格其余三条翻转断言（`getPrivateKey("非数字","x")`、`getPublicKey("0","3")`、`getPrivateKey("0","3")`）
  被首个断言失败短路未执行到，同为吞异常返 null 形态，红因一致。
- 同格既有 toPrivateKey/toPublicKey 三条 `InvalidKeySpecException` 断言零改动（对表方向来源），保持绿。
- 其余 13 例（含合法路径 `keyRebuildFromModulusAndExponent`）本次运行全部保持绿——翻转未波及其他格。

## 转绿承诺（task 7.2）

两入口 catch 不再返 null：数值解析失败包装 `IllegalArgumentException`（消息指明公钥/私钥与失败环节，cause 透传
NumberFormatException），密钥材料生成失败包装 `IllegalArgumentException`（cause 透传 InvalidKeySpecException，
与 toKey 侧同方向）。公共签名（方法名/参数/返回类型/throws 受检面）不动——包装为未受检异常以保持"编译不变"。
