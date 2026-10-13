# Design

## Context

现场事实（2026-10-03 取证，全部在档）：commons-io 2.14.0 下 `IOUtils.readLines(InputStream, String)` 经 `BufferedReader.lines()` 流式收集，读流失败抛 `java.io.UncheckedIOException`（栈顶 `BufferedReader$1.hasNext → ReferencePipeline.collect → IOUtils.readLines`）；升级时为消 javac 的"catch 不可达"编译错误，`LocalWordsFilter.doLoad` 的守卫块（reload 保留旧词表 / 首载 rethrow）被整块删除（工作树 diff 原文在案）。`word-filtering` 规格 Purpose 明写"首载失败必须显式失败、热更失败必须保留旧词表（fail-closed）"。爆炸半径（codegraph impact，doLoad 第 170 行，changeType=modify）：调用方仅 `FileLoader` 的 load/reload 模板与 `LocalWordsFilterTest`；WordsFilter 消费面不触 doLoad——改动面单文件单方法。

## Goals / Non-Goals

Goals：恢复被删的 fail-closed 守卫，使两个看守测试转绿；让异常契约不再随第三方库实现选择漂移。Non-Goals：不回退 commons-io 版本；不改测试文本；不触碰 `LuaScriptLoader`/`ExportTask` 的修复（附带核查只登记结论）；不动 FileLoader 模板。

## Decisions

**D1 词表读取改用 JDK `BufferedReader.readLine()` 显式循环，替代 `IOUtils.readLines`。** 依据 P2（高层依赖契约而非低层实现细节）：契约异常类型应锚定在稳定出处——readLine 循环失败抛出的就是 `IOException` 本身，`catch (IOException)` 守卫形态从此不依赖第三方库的内部实现。备选一"原样恢复被删守卫（readLines + catch IOException）"被否决：2.14.0 下 readLines 对读流失败不再以 IOException 传出，javac 会再次报 catch 不可达（这正是升级会话撞上的编译错误），恢复原形态在物理上不成立。备选二"catch (IOException | UncheckedIOException) 后解包 cause 重抛 IOException"被否决：契约类型要靠手工解包维持，第三方库实现每变一次、catch 形态就要跟改一次，守卫的稳定性问题没有根除，只是换了位置。否决记录同时说明：`BufferedReader.lines()` 流式方法与被否决的 readLines 同走包装路径，实现时必须用逐行循环。
**D2 守卫语义与文案逐字以 git 历史被删原文为蓝本。** 判定结构（reload 且 rootNode 非空 → 记错日志保留旧表返回；否则 rethrow）、日志文案（"#词表热更#读取 … 失败，保留当前词表"）与被注释的 fail-closed 说明原样恢复；`doLoad` 的 `throws IOException` 签名不动。与并行归档批"逐字迁入不顺手压缩"同纪律：行为恢复之外零改写；被删现场同时删除的 `str.length() > 0` 到 `!str.isEmpty()` 改写与 ImmutableList import 清理不属于守卫恢复必需——`!str.isEmpty()` 语义等价且为现行形态，保留现状不回滚，该差异在验收件注明。
**D3 附带核查条款先行、修复扩面须另裁。** `LuaScriptLoader`（`IOUtils.toString`）与 `ExportTask`（`IOUtils.write`）在两测试转绿后用同一 broken-stream 探针思路核查异常类型是否漂移；确认受影响的，按 codec 错登先例停下回报用户决定另册，不在本册静默扩面。
**D4 验收即那两个看守测试。** `./gradlew :tny-game-common-io:test` 全绿为第一判据（`reloadFailureKeepsOldTrie`、`firstLoadFailurePropagates` 逐条转绿，其余六条零回归）；`:tny-game-redisson:test` 与 `:tny-game-doc:test` 顺带零回归；失败件 XML 已在档作 before。全仓 `clean build` 若因升级批其余未适配面仍红，与本册无关、以模块判据收口，补跑条款在案。

## Risks / Trade-offs

- 风险：readLine 循环与 `IOUtils.readLines` 在行尾界定、空末行处理上必须逐字对齐原语义——两测试的断言（含跨词、掩码用例的词表装载）兜住；实现时以"八条用例全绿"验收装载行为等价。
- 风险：并行升级会话可能继续触碰同一文件（其案卷 fix-dependency-version-governance 在册）——apply 开工前重读现文 diff、落地窗口内若发现同文件外部改动即停回用户裁决（proposal Impact 已立条款）。
- 权衡：放弃 commons-io 便利 API 换契约稳定，方法体多四五行——值。

## Migration Plan

第一步，读工作树与 git 历史被删守卫原文，确认现文未被并行会话再改动。第二步，按 D1/D2 实现替换与守卫恢复。第三步，跑 `:tny-game-common-io:test` 与附带两模块测试，D3 核查结论与全部证据记 verification-notes.md。回滚即还原 doLoad 方法体单处改动。
