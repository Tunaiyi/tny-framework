# Verification Summary（reduce-code-duplication）

## 度量收口（9.1）

同口径复扫（`baseline/dupscan.py`+`dupmerge.py`，窗口/最小区块/排除目录不变），终值见 `baseline/dup-after.md`：

| 指标 | 基线 v2 | 终值 | 变化 | 目标 | 判定 |
|---|---|---|---|---|---|
| M1 全口径冗余行 | 2,653 | **1,086** | **-59.1%** | ≥60%（≤1,060） | **差 26 行未达全口径线** |
| M1 main/混合岛（Top25 可见口径） | 1,849 | 681 | **-63.2%** | — | 达标 |
| M3 main/混合岛（Top25 可见口径） | 3,501 | 2,198 | **-37.2%** | ≥40%（全口径） | 未达，残余=不动清单族 |
| M3 全口径 | 5,646 | 6,856 | +21% | — | **测试侧污染**：两轮修复与本变更钉桩测试新增 ~2,544 行结构同形（契约测试同手法是交付物，不是债） |

**残余定性（按 9.1 条款"禁止为凑指标破 D 裁决"如实报）**：M1 残余 main 侧逐岛均已判 JUSTIFIED_KEEP——ObjectMap/Wrapper 门面骨架 108（四条归一路径全触红线，见 verification-group4.md 收口压缩节）、AbstractFuture/FutureTask 门面委托骨架 68（D3 已迁 Sync，削门面=层级变更或 JDK 接口 default 化，均不可）、EmptyImmutableList/Set 53 与 lifecycle 三阶段 36（不动清单/P10 容忍）、CouchDB-Json 33+21（D2 继承链承诺+格式包裹差异表达）；纯测试岛 385 行属钉桩交付。M3 残余 main 侧主体=DTO 十胞胎 784、异常构造器族、oplog 孪生——全部在册不动清单。

**首轮中途数**（收口压缩批次前）：M1 全口径 1,342（-49.4%）；压缩批（FormatTextSupport 内部 51 消、protobuf printToString/handlePrimitive 228 消、Wrapper getMapAccessor 5 消、Void 插件对 31 消）后 1,086。

## 全仓门禁（9.2）

- `./gradlew test -x :tny-game-namnspace-etcd:test` → **BUILD SUCCESSFUL**；测试 XML 汇总 **19 模块 / 929 用例 / 0 failure / 0 error / 0 skipped**（首轮审计时 609 → +320 为两轮钉桩/契约/golden 新增）。
- 分域终检（组 3~8，`--rerun-tasks` 强制全新）：九模块 114 suites / **599 tests / 0F / 0E**；protobuf golden+round-trip 12/12、reflect 40、basics 19、scheduler 50、data 7、starter-data 12、starter-net-netty4 25、net 167。
- 一次性故障登记：首轮全仓跑 FAILED（日志被管道截断丢失，复跑即绿、13 executed 全过）——特征吻合已另案登记的 `tny-game-net-test` 并行编译脆弱性（跨编译单元包私有引用，间歇性），非本变更引入。
- 消费面哨兵：`:tny-game-oplog:compileJava`（printFiles 唯一外部点）、`:tny-game-protoex:test`、`:tny-game-net:test`、`:tny-game-basics:test`、`:tny-game-mongodb/redisson:compileJava` 均通过。

## 保行为证据链（各族）

- **测试 diff 零改动即行为等价的机器证明**：组 2 `git diff 7fac3213..HEAD -- src/test` = 0 行（golden 基线提交先于实现收敛）；组 3/4/5/6/8 各自 parity/契约测试"重构前绿→重构后期望值一字不改仍绿"（各组 verification-group*.md 留有红绿时序）。
- **签名冻结自查**：工作树 diff 中本变更目标文件 public/protected 消费者可见签名零变更（终检代理逐文件头归属核验；混入的 dormant/audit 轮未提交签名变更已逐项排除归属）。唯一豁免 D7：三个新 public abstract 父类纯增量、Spring 注解全留子类（`-@/+@` 零行核验）。
- **缺陷零顺手修**：ObjectMap float.class CCE、sub null 语义、Html 不转义、Json `\u` 还原有损、`\0` 八进制等全部以现状钉桩（各组"遗留登记"合计 20+ 项在册）。

## 提交状态（截至本文件）

已提交：组 2（43ba9e30/b3e46251/7fac3213/9bf579ff）+ 组 7（971298da）。**组 3~8、收口压缩批与 openspec 文档在工作树未提交**（两轮审计修复的 160+ 文件改动亦在工作树，非本变更混排面），收口提交方式由用户定夺。

## validate（9.4）

`openspec validate reduce-code-duplication --strict` → valid（见任务勾选留痕）。
