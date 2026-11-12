# 验收记录：adapt-commons-io-214-word-filter

## 结论

验收通过，全绿收口。`LocalWordsFilterTest` 八条用例全绿（此前红的 `reloadFailureKeepsOldTrie` 与 `firstLoadFailurePropagates` 逐条转绿、其余六条零回归）；`:tny-game-redisson:test` 与 `:tny-game-doc:test` 零回归；**全仓 `./gradlew clean build` 恢复全绿**（1 分 30 秒）——本册同时解除了横在三个已归档册验收记录里的 commons-io 测试阻塞。

## 实施内容

- `LocalWordsFilter.doLoad`：词表读取由 `IOUtils.readLines(inputStream, "UTF-8")` 改为 JDK `BufferedReader.readLine()` 显式逐行循环（收集语义与原实现一致：逐行、不含行尾符、空行保留交由既有过滤），守卫块按 git 历史被删原文逐字恢复——reload 且已有词表时读取失败记错误日志、保留旧词表、正常返回（fail-closed），首载失败原样抛 `IOException`；`throws IOException` 签名与日志文案零改动；不再被引用的 `org.apache.commons.io.IOUtils` import 按触碰即改清理。
- 与被删现场的单处差异（design D2 预告）：`str.length() > 0` 保持升级方改写后的 `!str.isEmpty()` 形态不回滚——两者语义等价，非守卫恢复必需面。

## 附带核查结论（design D3）：全仓无第二处受影响

对 commons-io 2.14.0 实际 jar（Gradle 缓存件）以 jshell 只读探针逐处实锤异常类型：

| 调用点 | API | 2.14.0 失败路径实测抛出 | 判定 |
|---|---|---|---|
| LocalWordsFilter.doLoad（本册已修） | `IOUtils.readLines(InputStream, Charset)` | `java.io.UncheckedIOException` | 受影响——包装路径经 `BufferedReader.lines()` 流收集，守卫 catch 不到，正是本册根因的实锤复现 |
| LuaScriptLoader | `IOUtils.toString(InputStream, Charset)` | `java.io.IOException` | 不受影响（原生传播，无包装） |
| ExportTask | `IOUtils.write(CharSequence, Writer)` | `java.io.IOException`（对失败 writer） | 不受影响（逐字写出直调 Writer，无流水线包装） |

结论：无需另册。探针过程一次自纠如实记录：首轮探针误命中 Gradle 缓存里的 2.8.0 旧 jar（`find | head -1` 按目录序取了先者），产出的"IOException"结论当时若直接采信会掩盖问题——按 2.14.0 精确路径重打后才得到上表 readLines 的 UncheckedIOException 实锤；转存型/探针型样本的"版本锚点确认"与"先运行再转存"同属一个教训族，已在探针输出旁注记。

## 判据汇总

| 判据 | 结果 |
|---|---|
| `:tny-game-common-io:test` | BUILD SUCCESSFUL，testsuite 8/8、failures=0、errors=0（before 为 2 failures，件在 baseline/failing-tests-before.xml） |
| 附带两模块测试 | 全绿零回归 |
| 全仓 `clean build` | 全绿（本册修好的正是唯一已知阻塞） |
| 现场稳定性 | 实施前 diff 复核取证一致，实施窗口无并行会话触碰同文件 |

## 连带义务履行

本册 clean build 全绿即为三本归档册（`2026-10-03-trim-it-module-dependencies-block`、`2026-10-03-move-bench-suite-selection-into-plugin`、`2026-10-03-split-publish-consistency-closure`）补跑条款的兑现材料，补记段已分别追加至各册 verification-notes.md，引用本册证据与本时间戳。
