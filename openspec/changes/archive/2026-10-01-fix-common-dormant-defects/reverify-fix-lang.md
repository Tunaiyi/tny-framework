# 复验收口记录：lang 域（fix-common-dormant-defects）

对应 reverify-report.md 阻断清单中落点在本域的文件面：`tny-game-common-lang/src/main/java/com/tny/game/common/collection/map/FixLinkedHashMap.java` 与其测试。

## 逐条裁决

### 1. [CRITICAL] 并发集合契约——FixLinkedHashMap 非正上限构造无校验（reverify-report 第 11 条）

- **claim**: 规格『有界固定容量映射恰容纳上限条数并按插入序驱逐最旧』承诺『以上限非正数构造 MUST 显式失败』（specs/concurrent-collection-contracts/spec.md:25、Scenario『非正上限构造失败（错误路径）』:35-38）行为未实现——构造器无校验，maxSize<=0 产出 put 即自驱逐的行为未定义实例，且零钉桩。
- **对抗自查（裁决前独立验证）**: 指控属实，非假报告。工作区实现（FixLinkedHashMap.java:25-28）构造器直接赋值无校验；maxSize=0 时 `removeEldestEntry`（`size() > maxSize`）对每个新 put 即为真，条目插入即被驱逐、映射恒空——正是规格禁止的"行为未定义实例"。测试面（ConcurrentCollectionContractsTest.java）仅覆盖正上限（3、1），非正构造零断言。规格措辞自洽且未越界（承诺对象是本能力主语"有界映射"的构造入口），不满足 SPEC_NARROWED 前提。走"已评审规格承诺的兑现"修复路径。
- **调用点核查**: 全仓 grep `new FixLinkedHashMap` 共 3 处构造——类自身 main()（字面量 5）、测试 :69（3）、:82（1）。无任何现存非正实参调用方，加构造校验不破坏既有调用，无需登记/回报调用方改动。
- **verdict**: FIXED
- **evidence**:
  - 钉桩先红（对旧行为留痕）：新增 `nonPositiveMaxSizeConstructionFailsExplicitly`（ConcurrentCollectionContractsTest.java，断言 0/-3 构造抛 IllegalArgumentException 且消息含参数值），修复前定向跑红——`org.opentest4j.AssertionFailedError: 以 0 为上限构造必须显式失败 ==> Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.`（ConcurrentCollectionContractsTest.java:95），`9 tests completed, 1 failed`。
  - 修复落点：FixLinkedHashMap.java 构造器显式失败 `if (maxSize <= 0) throw new IllegalArgumentException("maxSize must be positive, but was: " + maxSize)`（IAE 含非法参数值，满足"给出非法参数的错误信号"）；顺带将同文件 `removeEldestEntry(java.util.Map.Entry…)` 全限定签名规范为简单名 import（域内文件面，无行为变化）。
  - 本域模块测试全绿结果行：`./gradlew :tny-game-common-lang:test` → `BUILD SUCCESSFUL in 30s`；JUnit XML 汇总 `module-total: xml=32 tests=207 failures=0 errors=0 skipped=0`；契约类 `ConcurrentCollectionContractsTest tests=9 failures=0 errors=0`，含钉桩用例 PASS。

## 备注

- tasks.md 4.2 点名的 `.../collection/map/FixLinkedHashMapTest.java` 独立文件在工作区并不存在，本契约实际落在 `com.tny.game.common.concurrent.collection.ConcurrentCollectionContractsTest`（规格 2 的两条兄弟 Scenario 均在此）。本次钉桩按就近原则补于同文件同规格小节，未新建重名文件以免测试面分裂。
- gradle 守护进程与并行代理共享：本域两次构建未遇锁冲突，无需重试、未使用 --stop。
