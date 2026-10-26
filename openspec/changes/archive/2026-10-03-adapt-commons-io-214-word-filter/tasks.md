# Tasks

## 1. 前置核对与基线

- [x] 1.1 核对现场：`git diff HEAD -- tny-game-common-io/` 的守卫删除 diff 仍在案、`LocalWordsFilter.java` 自取证后未被并行的版本治理工作再改动（若有新改动即停回用户裁决，design D4 风险条）；把两条失败用例的 test-results XML 失败段转存本变更目录 baseline/ 作 before（转存判据物已在 build 产物中，属"先由测试运行生成、再转存"的既有件，直接转存即可）。捕获口径按 openspec/config.yaml context"零差异验收基线抓样口径"一条执行。

## 2. 守卫恢复实施

- [x] 2.1 `LocalWordsFilter.doLoad`：词表读取从 `IOUtils.readLines(inputStream, "UTF-8")` 改为 JDK `BufferedReader` 显式 `readLine()` 逐行循环（禁用 `BufferedReader.lines()` 流式方法，它与 readLines 同走 UncheckedIOException 包装路径，design D1）；按被删 git 原文恢复守卫：reload 且 rootNode 非空时读取失败——记错误日志、保留旧词表、正常返回，否则原样抛出 `IOException`；`throws IOException` 签名与日志文案逐字承前（D2）。验证：`./gradlew :tny-game-common-io:test` 全绿——第一判据，`reloadFailureKeepsOldTrie` 与 `firstLoadFailurePropagates` 逐条转绿、其余六条零回归。

## 3. 附带核查与收口

- [x] 3.1 附带核查（design D3）：对 `LuaScriptLoader` 的 `IOUtils.toString(InputStream, Charset)` 与 `ExportTask` 的 `IOUtils.write(CharSequence, Writer)` 逐处判定 commons-io 2.14.0 下是否存在异常包装或抛出类型漂移（读该版本实现的 throws 声明与失败路径，必要时以只读探针单测验证），结论逐处记 verification-notes.md；确认受影响的即停，回报用户决定是否另册，不在本册扩面。
- [x] 3.2 收口：`./gradlew :tny-game-redisson:test :tny-game-doc:test` 零回归；before/after 失败件对照与全部结论记 verification-notes.md；若全仓 `clean build` 仍被版本治理在途工作的其余未适配面阻塞，按归档三册同款带痕条款注明归属，不追修。
