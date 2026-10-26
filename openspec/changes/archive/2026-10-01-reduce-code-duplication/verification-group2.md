# Group 2 verification — protobuf 格式族收敛（tasks 2.1-2.6）

日期：2026-10-01。分支 5.7.x。基线（重构前）：c4bbc42d。

## 测试命令与结果

| 命令 | 结果 |
|---|---|
| `./gradlew :tny-game-protobuf:test`（基线，收敛前） | BUILD SUCCESSFUL；FormatGoldenTest 8/8、FormatMergeRoundTripTest 4/4 全绿（钉桩先行） |
| `./gradlew :tny-game-protobuf:test --rerun-tasks`（2.3/2.4/2.5 收敛后） | BUILD SUCCESSFUL；FormatGoldenTest tests=8 failures=0 errors=0；FormatMergeRoundTripTest tests=4 failures=0 errors=0 |
| `./gradlew :tny-game-oplog:test` | BUILD SUCCESSFUL（模块无单元测试源——`test` 任务 NO-SOURCE；消费面回归以 `:tny-game-oplog:compileJava --rerun-tasks` 强制重编译通过 + FormatGoldenTest 内 `printFiles` 快照钉桩承担） |
| 期望值零改动证明 | `git diff 7fac3213..HEAD -- tny-game-protobuf/src/test/` 输出 0 行（基线测试提交与最终态之间测试文件字节不变） |

## 变更文件清单

新增 main（全部包私有 final，无新 public/protected 类型）：
- `FormatTextSupport.java`（595 行）：escapeBytes（LowByteMode OCTAL/UNICODE 参数化）、unescapeBytes（unicodeEscape 布尔参数化，Json 现状 \u 还原公式逐字保留）、escapeText/unescapeText 双形态（Json JSON 语义 vs 其余八进制字节语义）、isOctal/isHex/digitValue、parse*Integer、unsignedToString×2、toStringBuilder；五类各自的包私有 InvalidEscapeSequence(Exception) 克隆合并为单一 `FormatTextSupport.InvalidEscapeSequence`（消息串逐字保持，包私有不可见于消费者）。
- `FormatValueRenderer.java`（139 行）：printFieldValue 类型 switch 单实现 + `ValueSink` 装饰参数；extensionPrintName（MessageSet 特判四份逐字同）、fieldPrintName（GROUP 选名四份同）。
- `FormatTokenizerCore.java`（503 行）：Xml/Json/Props 三份 Tokenizer 机制层逐字收敛；`Kind`（XML/JSON/PROPS）承载 consumeIdentifier 与 string/bytes 消费形态差异；`Failure` 携带现状 "行:列: 消息"，外壳/入口按各自 ParseException 类型重抛（类型与消息对调用方不变）。

修改 main（行数 前→后）：
- `Protobuf2XmlFormat.java` 1340→561（私有 Tokenizer 外壳删除，merge 入口重抛包装；printFieldValue→sink；工具段删除）
- `Protobuf2JsonFormat.java` 1646→886（protected Tokenizer 保留为一行委托壳；protected toStringBuilder/print(Message,JsonGenerator)/mergeField/printUnknownFields/JsonGenerator 逐字保留）
- `Protobuf2HtmlFormat.java` 696→310（死代码工具段删除）
- `Protobuf2JavaPropsFormat.java` 1413→605（私有 Tokenizer 外壳删除；printUnknownFields 四 for 循环类内并表）
- `Protobuf2CouchDBFormat.java` 153→153（零改动；extends Json 与 _id/_rev 覆写链不动）

新增 test（钉桩账，先于实现收敛提交=7fac3213）：
- `src/test/proto/format_test.proto`（protoc 25.3 生成入库，无构建插件改动；runtime protobuf-java 3.22.2 已验证 gencode 兼容编译）
- `src/test/java/com/tny/game/protobuf/format/test/FormatTestProtos.java`
- `FormatTestFixtures.java` / `FormatGoldenTest.java` / `FormatMergeRoundTripTest.java`

主源码合计：5,248 → 3,752 行；`git diff c4bbc42d..HEAD -- tny-game-protobuf/src/main`：+1,490 / −2,987（净 −1,497）。

## 公共签名冻结自查

脚本比对 c4bbc42d 与最终态的 public/protected 声明集合：
- 五类全部 public static 面（print(Message,Appendable)/print(UnknownFieldSet,Appendable)/printToString×2/printFiles/print(Collection)/merge 全重载/printField/printFieldToString）逐字在位。
- Json protected 面（Tokenizer/JsonGenerator 类、print(Message,JsonGenerator)、mergeField、printUnknownFields、toStringBuilder）在位；Tokenizer 方法集合与基线一致（一行委托实现）。
- 删除的成员仅存在于 `private static final class Tokenizer` 内（Xml/Props），消费者不可命名 → 非 API。
- 新增声明仅两类：包私有 final 共享件（FormatTextSupport/FormatValueRenderer/FormatTokenizerCore，含嵌套 Kind/Failure/ValueSink/LowByteMode，均非 public）；四类 printFieldValue 内匿名 ValueSink 实现的 `public` 覆写（匿名类不可被外部引用，非可命名 API）。
- CouchDB extends Json 关系、Tokenizer/CouchDBGenerator 覆写链、_id/_rev 映射逐字不动。

## 遗留登记（现状差异/可疑语义，一律未修、已钉桩）

1. **Json `unescapeBytes` 的 `\u` 还原公式错误**（`16*3*d1 + 16*2*d2 + 16*d3 + d4`，应为 16³/16²/16¹/16⁰）：高位字节打印为 `\uffXX` 后往返有损。RT_{JSON,COUCH}_ESCAPES 以"合并后重打印"快照钉死现状（`ﾯ` 等），原样搬入 FormatTextSupport.unescapeBytes(unicodeEscape=true)。修复另立变更。
2. **Html printFieldValue STRING 不转义**：值原样 toString 仅外包引号（对照 BYTES 走八进制转义；值内 \n 被 HtmlGenerator 转 <br/>）——golden P_HTML_ESCAPES 钉死。D2 明确保持。
3. **Xml print→merge 对转义特殊字符集不闭合**：`q"a'p\bs\n...` 打印产物分词后 merge 抛 `1:25: Expected ">".`，RT_XML_ESCAPES 消息逐字钉死。
4. **unknown fields 三形态不闭合**：Xml merge 抛 `1:42: Expected identifier. --`；Props merge 抛 `2:1: Expected identifier.`；Json/CouchDB 静默丢弃（重打印快照 `{"opt_string": "known"}` 钉死）。
5. **Props MessageSet 扩展 print/merge 不对称**：打印用短名路径前缀（indent(field.getName())），merge 期待全名+`.`，RT_PROPS_MSGSET 抛 `1:30: Expected ".".` 钉死。
6. **Xml/Html/Props printField 等 public 方法签名引用 private 嵌套 Generator 类型**（外部不可命名参数类型）——签名冻结原样保留。
7. **printToString(Message)/printToString(UnknownFieldSet) 每类两变体差一行**：public 重载模板样板，未并表（并表需引入函数参数或类型联合，收益为负，止步）。
8. **五个 Generator 内部类未共享**：XmlGenerator（裸 append）、JsonGenerator（\n 断行+空格缩进）、HtmlGenerator（\n→<br/>+div 缩进）、JavaPropsGenerator（\n 断行+路径前缀缩进）语义各不相同，无逐字同段可收，"能过即收"止步。
9. **props `unescapeText`/Html 全套 parse/unescape 工具为死代码**（无 merge/调用点）：原样删除克隆体（包私有、零调用面），无行为变更。
10. **Json Tokenizer `consumeIdentifier` 剥引号 `replaceAll("\"|'", "")`**、Props 版不剥——以 Kind 参数化保留，未统一。

design Open Question「merge 解析层收敛深度」按"能过即收"落地：机制层全收（词法扫描/消费/定位消息），各格式 mergeField/handleValue 语法骨架（尖括号 vs 花括号 vs 点号路径）差异大，保留在各格式本类。

## 提交序列

- `7fac3213` test(protobuf): 格式族行为钉桩先行——golden 快照+print→merge 往返重构前全绿
- `b3e46251` refactor(protobuf): 工具层收敛到 FormatTextSupport（零行为变更）
- `43ba9e30` refactor(protobuf): 渲染骨架与解析层 Tokenizer 收敛（零行为变更）
- （本文件 + tasks.md 勾记随后）

## 环境备注

`:tny-game-oplog:test` 首跑因 tny-game-common-io/FileMonitor.java 被并行代理在途编辑而编译失败；等待后复跑通过。属并行组工作面，非本组变更引入。

## 收口压缩（终值度量前最后一批，2026-10-01）

零行为变更判据全程执行：`git diff -- tny-game-protobuf/src/test/` 输出 0 行（golden/round-trip 期望值一字未动）；
`./gradlew :tny-game-protobuf:test --rerun-tasks` BUILD SUCCESSFUL（FormatGoldenTest 8/8、FormatMergeRoundTripTest 4/4）；
`./gradlew :tny-game-oplog:compileJava --rerun-tasks` 通过（唯一仓外消费面）。
度量复跑：同口径 `baseline/dupscan.py`+`dupmerge.py` 复扫，本组三个目标岛处置如下。

### 1. FormatTextSupport.java 同文件自克隆（M1 岛"冗余51行×3区域"）——SQUEEZED

- 上批引入债：escapeBytes/escapeTextJson/unescapeTextJson 三处 switch 短转义表互为克隆（71-104/280-304/350-375）。
- 处置：新增包私有短转义对表 `SHORT_ESCAPE_ACTUALS/SHORT_ESCAPE_SEQUENCES`（10 行，序列第 2 字符即反转义标识符，
  单一事实源）+ 两个查表方法；三处现状使用子集以**表前缀行数参数**保留（escapeBytes 全 10 行、escapeTextJson 7 行、
  unescapeTextJson 8 行含从不产出的单引号行——未统一、未抹平）；`\u` 四位还原公式（遗留①）与 octal/unicode
  default 分支逐字未动。unescapeBytes 不在本岛区域，未触碰。
- 证据：复扫 M1 岛消失（FormatTextSupport 零命中）；P_*/RT_* ESCAPES golden 与 RT_JSON/COUCH_ESCAPES 快照期望值未改仍绿。

### 2. Protobuf2CouchDBFormat×5 区域"冗余139行"岛——拆分处置

- 2a. printToString(Message)/printToString(UnknownFieldSet) ×5 类（+Props printFieldToString）——**SQUEEZED**。
  方法体逐字相同、零现状差异表达（Props 源仅将异常串写作两段拼接，运行时字符串逐字相同，视同一致）。
  上批遗留⑦"需引入函数参数、收益为负"按收口判据重判否决：参数化装饰即本变更 D2 已采认的 ValueSink 先例形态，
  收敛后各门面降为一行 lambda 委托，哪个 print 重载仍由门面类自身静态解析决定（CouchDB 门面 lambda 解析到
  CouchDB 自有 print，`_id/_rev` 覆写链与 extends Json 关系逐字未动）。
  证据：复扫该组合岛 139→54（仅剩 2b/2c）；FormatGoldenTest 的 printToString 断言（含 COUCH 重打印快照）期望值未改仍绿。
- 2b. merge 三重载样板（CouchDB:50-82 vs Json:549-581，残余 33 行）——**JUSTIFIED_KEEP**。
  方法体已是一行委托（`merge(input, ExtensionRegistry.getEmptyRegistry(), builder)` /
  `merge(toStringBuilder(input), …)`，工具段上批已共享）；残余重复=public 冻结签名+javadoc+原注释文本，属窗口内最小可分单元；
  再并需以回调包装一行委托，收益为负且新增行为风险面（CouchDB merge 的 Tokenizer `_id/_rev` 覆写链不许动）。
- 2c. print(UnknownFieldSet, Appendable) 包裹骨架与 printToString 委托行（残余 Html/Xml 21 行簇）——**JUSTIFIED_KEEP**。
  包裹文本（`"<html>"+META_CONTENT` / `"<message>"` / `"{"`、Props 无包裹）即各格式现状差异表达，generator 类型各自私有/受保护；
  簇内已含 2a 收敛后的委托体，剩余为 javadoc+签名样板，公共签名冻结不许并。

### 3. Protobuf2JavaPropsFormat 89 行岛（Props/Xml/Json handlePrimitive 标量类型分派×3）——SQUEEZED

- 上批"merge 解析层收敛深度止步于机制层"按收口判据续收：三份逐字同构的类型 switch（consumeXxx 调用序列、
  ENUM 数字/名称双路径校验、`MESSAGE/GROUP → RuntimeException("Can't get here.")`）收敛到新增包私有
  `FormatValueReader.readPrimitive`（与打印面 FormatValueRenderer.ValueSink 对称的"分派单实现+扫描面参数化"）。
- 现状差异全部由 Scanner 承载，零抹平：Xml/JavaProps 直传 FormatTokenizerCore 本尊（其包私有类型上新实现
  `Scanner<Failure>`，public 成员对包外不可命名，非发布面变更）；Json 经 `FormatValueReader.TokenizerScanner`
  走外壳现状虚分派——`consumeIdentifier` 仍经 Protobuf2JsonFormat.Tokenizer 多态，CouchDB Tokenizer 的
  `_id/_rev` 覆写链继续参与 ENUM 标识符读取；异常类型各如现状（Json: ParseException 经外壳转换/parseExceptionPreviousToken，
  Xml/Props: Failure）；Props `consume("=")` 前缀、Json `"null"` 字面量旁路留各格式本方法。
- 未被 golden 覆盖的 ENUM 失败消息路径以运行时探针核证类型与"行:列: 消息"逐字不变
  （`1:14: Enum type "tny.protobuf.test.Color" has no value named "PURPLE".` 等，JSON/COUCH/XML/PROPS 四类，
  `number 99.` 含 Props 现状 `'.'` 字符拼接的运行时等值）；RT_PROPS_MSGSET/RT_XML_ESCAPES 等既有逐字钉桩未改仍绿。
- 证据：复扫 89 行岛与 FormatTestProtos 无关、主源岛零命中（Props/Xml/Json handlePrimitive 区域全部消失）。

### 度量与账目

- 主源 M1 岛冗余行：279 → 54（残余 54=2b+2c，均 JUSTIFIED_KEEP）；protobuf 模块 M1 总冗余 497→272（差值主体为
  FormatTestProtos 生成码岛，非本批目标、维持既有"生成码不动"口径）。
- 原始行账：既有 8 文件 diff +156/−445，新增 FormatValueReader.java 224 行，模块主源净 −65 行；净收益以岛冗余行收敛为准。
- 公共签名冻结自查：git 声明集比对 HEAD 与工作区，五类 public/protected 面零增删（新增声明仅存在于包私有
  FormatValueReader/FormatTextSupport 表与 FormatTokenizerCore——包私有持有类型）。
- 本批未触碰：五个 Generator 内部类（遗留⑧维持 JUSTIFIED_KEEP，语义各不相同无同段可收）、unescapeBytes `\u`
  缺陷（遗留①）、Html 不转义（遗留②）；未 commit。
