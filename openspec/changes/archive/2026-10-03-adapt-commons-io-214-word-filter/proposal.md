# Proposal

## Why

commons-io 从 2.8.0 升级到 2.14.0 之后（升级由本仓另一项在途工作带入工作树、尚未提交），`IOUtils.readLines(InputStream, String)` 读流中途失败时的行为变了：2.14.0 的实现经由 `BufferedReader.lines()` 流式收集，`IOException` 在读取环节被包装成 `java.io.UncheckedIOException`（运行时异常）抛出，不再以 `IOException` 原类型传出。`LocalWordsFilter.doLoad` 里守卫这一行为的整段 try/catch（热更读取失败保留旧词表并记错日志、首载失败原样抛 `IOException`——即回归记录 D2 建立的 fail-closed 保护）在升级时因 javac 报"catch 不可达"编译错误而被整块删除，编译恢复绿了，产品语义却断了：`word-filtering` 规格 Purpose 明文要求"首载失败必须显式失败、热更失败必须保留旧词表（fail-closed），杜绝审核整体静默失效"，现在热更失败会把异常穿透给调用方、敏感词过滤的旧词表保护不复存在。两个看守该契约的单元测试已经红了，就是直接证据：`reloadFailureKeepsOldTrie` 因异常穿透、断言根本执行不到而失败；`firstLoadFailurePropagates` 断言抛出类型时实报"expected: IOException but was: UncheckedIOException"（异常传播这件事还在，传播的类型错了）。本变更把被删掉的产品保护按原契约恢复，并让异常类型重新稳定可依赖。

## What Changes

- `LocalWordsFilter.doLoad` 的词表读取从 `IOUtils.readLines(inputStream, "UTF-8")` 改为 JDK 标准库的显式逐行读取（`BufferedReader.readLine()` 循环）：readLine 循环失败时抛出的就是 `IOException` 本身，没有包装路径，"catch IOException 后分流处理"的守卫形态从此不依赖第三方库的内部实现选择。注意 `BufferedReader.lines()` 流式方法同样会把 IOException 包成 UncheckedIOException，与本问题同源，不得使用。
- 守卫块按被删前的语义恢复：reload 且已有词表时读取失败——记错误日志、保留旧词表、正常返回（fail-closed）；首载失败——异常原样抛给调用方（`doLoad` 的 `throws IOException` 签名不变）。恢复的判定结构、注释与日志文案以工作树 git 历史中的被删原文为蓝本。
- 测试面零改动：那两个失败的单元测试正是本变更的验收判据，不修改任何用例文本。
- 附带核查（本册执行、结论入验收件）：全仓另两处 commons-io 调用点——`LuaScriptLoader` 的 `IOUtils.toString(InputStream, Charset)` 与 `ExportTask` 的 `IOUtils.write(CharSequence, Writer)`——是否存在同类的"抛出类型变化或包装传播"；若确认受影响，其修复按 codec 错登先例另册处理，本册不扩面。
- 本变更不触碰任何版本声明文件（commons-io 保持 2.14.0 不回退）、不触碰升级批次的其余未适配范围。

## Capabilities

### New Capabilities / Modified Capabilities

无。`word-filtering` 规格的行为契约零改动——被破坏的正是该规格 Purpose 已经写明的契约（首载显式失败、热更保留旧词表），本变更是让实现重新追上规格条文，不新增也不修改任何需求。skip_specs。

## Impact

- **受影响文件**：`tny-game-common-io/src/main/java/com.tny.game.common.io/word/LocalWordsFilter.java`（doLoad 一处）。测试、其余模块、构建脚本零触碰。
- **公共 API 面**：`LocalWordsFilter` 与 `FileLoader` 的方法签名与异常契约不变（`throws IOException` 原样），下游消费零波及；不属 BREAKING。
- **与在途升级工作的关系**：commons-io 升级本身（`gradle/libs.versions.toml` 版本行）由并行的版本治理工作负责，其案卷在册（fix-dependency-version-governance）；本册只修复该升级在词表装载一处引入的产品缺陷，两册文件零交集。若并行会话在本册实施期间改动同一文件，按停止条款回到用户裁决，不静默合并。
- **连带解锁**：本册测试转绿后，全仓 `clean build` 的 commons-io 测试红阻塞解除——已归档三册（trim-it-module-dependencies-block、move-bench-suite-selection-into-plugin、split-publish-consistency-closure）验收件里立的"补跑全绿记入归档件"条款随之可执行，该补跑属归档件追加，另报用户安排。
- **验收基线**：失败件全文（test-results XML 两条失败栈）已在档可作 before；落地后 `./gradlew :tny-game-common-io:test` 全绿（重点两条用例逐条转绿）、`:tny-game-redisson:test` 与 doc 模块零回归、两处附带核查结论入件。基线抓样按 openspec/config.yaml context"零差异验收基线抓样口径"执行。
