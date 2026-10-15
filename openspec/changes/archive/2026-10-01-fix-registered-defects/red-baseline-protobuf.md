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
