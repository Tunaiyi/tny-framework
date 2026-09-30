# Tasks

> 按 design.md Migration Plan 批次：组 1 前置 → 组 2 protobuf（最大热点）→ 组 3~4 lang 三族 → 组 5 reflect → 组 6 注册表 → 组 7 net 测试脚手架 → 组 8 starter/data → 组 9 集成收口。每族"行为钉桩测试先行（重构前绿）→ 实现收敛 → 模块绿"；全程禁止顺带修行为（发现即钉桩入册，见 D4）。

## 1. 前置确认与基线固化

- [ ] 1.1 确认两归档落库：`openspec/changes/archive/` 下存在 `2026-10-01-fix-common-audit-findings` 与 `2026-10-01-fix-common-dormant-defects`（或当日实际日期前缀），且 `git status` 中两变更目录已不在 changes/ 活跃区——本变更目标文件面（NumberAide、ObjectMap、worker futures）与 wf_51271e24 修复重叠，未归档禁止开工
- [x] 1.2 基线可复跑校验：`python3 baseline/dupscan.py && python3 baseline/dupmerge.py` 在当前工作区运行成功，输出与 `baseline/dup-islands.md` 的 M1/M3 冗余行合计一致（2,653/5,609，容差 ±5%，因归档合入产生的漂移需重设基线并在本变更目录注明新基线数字）——实测 M1/M2 零误差（2,653），M3 +37 行（5,646，+0.66% 容差内）；漂移归因首扫与 wf_51271e24 修复交错，已以修复后树重锚基线（`baseline/dup-islands.md` 现为 v2：**收口口径 M1 ≤1,060 / M3 ≤3,387**）

## 2. protobuf 格式族（golden 先行，最大热点 ~1,400 行）

- [x] 2.1 新增 `tny-game-protobuf/src/test/java/com/tny/game/protobuf/format/FormatGoldenTest.java`：五格式类 × `print/printToString` 输出快照钉桩——覆盖全标量类型、repeated（含 packed 与 JavaProps 下标形态）、unknown fields、extension/MessageSet 特判分支、嵌套消息；专列现状差异用例：Html 值渲染不转义、JavaProps ENUM 不加引号、CouchDB `_id/_rev` 映射（重构前必须绿，作为行为账）
- [x] 2.2 新增 `FormatMergeRoundTripTest.java`：五类 print→merge 往返等价（含转义特殊字符集：`\uXXXX`、八进制、`\n\r\t\\"`、高位字节；Xml 尖括号 vs Json/Props 花括号 consume 差异），重构前绿
- [x] 2.3 按 D2 实现：转义/unescape/八进制十六进制/digitValue/parse*Integer、Generator 基础设施段收敛到包私有共享工具（Xml 1014-1063≈Json 1187-1234 等实测岛；default 分支八进制 vs \uXXXX 差异参数化保留）
- [ ] 2.4 按 D2 实现：`printFieldValue` 类型 switch 收敛为"装饰策略参数化"单实现（引号器+转义器入参），`print(Message,gen)/printField/printSingleField` 字段遍历骨架按模板方法收敛；四类 public 静态签名逐字不动
- [ ] 2.5 按 D2 实现：merge 解析层 Tokenizer 共享（正则/括号差异参数化，design Open Question 按"能过即收"渐进）；`printUnknownFields` 四 for 循环等类内自克隆并表
- [ ] 2.6 验证：`./gradlew :tny-game-protobuf:test` 全绿（golden+round-trip 零改动期望值）；`./gradlew :tny-game-oplog:test` 通过（printFiles 唯一外部消费面回归）

## 3. lang 双 future（~250 行逐字克隆）

- [ ] 3.1 新增 `tny-game-common-lang/src/test/java/com/tny/game/common/worker/FutureStateMachineParityTest.java`：`AbstractFuture` 与 `FutureTask` 各走 cancel（未开始/运行中/已完成三态）、get×2 超时与中断、isDone/isCancelled、done 钩子触发次数、reset 后可重用、await 唤醒路径的语义钉桩（重构前两类各自绿；不假设两实现互等之外的新行为）
- [ ] 3.2 按 D3 实现：Sync 状态机逐字相同段（tryAcquireShared/tryReleaseShared/innerGet×2/innerSet/innerSetException/innerCancel/reset 等）下沉包私有共享实现；两 public 类构造器/方法签名/implements 关系冻结；FutureTask 独有 callable/RUNNING/innerRun 留本类
- [ ] 3.3 验证：`./gradlew :tny-game-common-lang:test` 全绿

## 4. lang 转换链 + NumberAide

- [ ] 4.1 新增 `tny-game-common-lang/src/test/java/com/tny/game/common/collection/map/access/MapConvertAccessContractTest.java`：14 个转换 getter × ObjectMap/WrapperObjectMap 两侧 × {直取命中、可转换值、不可转换失败、null 键缺省、默认值路径} 全矩阵；**`ObjectMap.getFloat(key,def)` 值存在抛 CCE 现状钉桩**（注释注明 float.class 缺陷与"禁止顺手修"依据）；`ObjectMap.getObject(key,def)` 独有方法与 Wrapper `toMap` 不可变语义差异保留用例
- [ ] 4.2 按 D4 实现：转换取值链收敛共享引擎，`float.class` vs `Float.class` 差异由两侧门面以参数承载（单一事实源+现状语义双保）
- [ ] 4.3 `NumberAideTest` 补算术等价钉桩：add/sub/multiply/divide/mod × {两侧 null 组合（含 `sub` 的 `one==null` 返回 other 现状）、BigDecimal/BigInteger 混合高精度路径、7 路基本类型 isAssignableFrom 分支}，重构前绿
- [ ] 4.4 按 D8 实现：五运算符收敛为私有分派器 + 运算符 lambda（表驱动）；公开五方法签名不动；同时收敛 150-169/186-205/222-241/322-342 实测四段
- [ ] 4.5 验证：`./gradlew :tny-game-common-lang:test` 全绿；`./gradlew :tny-game-basics:test :tny-game-net:test` 通过（NumberAide/ObjectMap 活跃消费面哨兵）

## 5. reflect 平行对（96+83 行）

- [ ] 5.1 新增 `tny-game-common-reflect/src/test/java/com/tny/game/common/reflect/AccessorPairParityTest.java`：CGlib/JSsist PropertyAccessor 与 CGlibUtils/JavassistAccessors 对同一目标类的属性读写结果一致、varargs 与 `new Object[]{...}` 实参形态等价性、`getPropertyDescriptor/setPropertyDescriptor` protected 注入面行为——重构前绿
- [ ] 5.2 按侦察 E/D5 先例实现：两对类抽包内共享骨架（落包就近，design Open Question 实施期定）；public 工厂签名、protected setter 保留；`JavassistAccessors` 跨模块消费形态不动
- [ ] 5.3 验证：`./gradlew :tny-game-common-reflect:test` 全绿；`./gradlew :tny-game-protoex:test :tny-game-net:test :tny-game-data:test` 通过

## 6. 注册表族（9 类 ~366 行）

- [ ] 6.1 新增 `tny-game-basics/src/test/java/com/tny/game/basics/EnumRegistryFamilyContractTest.java`：8 注册表类的 of/check(String)/check(int)/option/all/enumerator 契约矩阵（含非法值失败语义）、`ItemTypes` 别名扩展（ofAlias/ofModelId/ofItemId）、`Moulds.register` public 与 `Actions.getAll()` 异名现状钉桩；scheduler `TaskReceiverTypes` 转发段用例并入 `tny-game-common-scheduler` 侧测试（含其 static 注册块装载现状）
- [ ] 6.2 按 D5 实现：basics 包内泛型中间层承载转发六件套（形态照 `AbstractExpr` 模板方法先例；`ClassImporter/EnumeratorHolder` 零触碰）；9 类留常量、register 入口（可见性原样）、自有扩展；装载入口 `GameEnumClassLoader::createSelector(Xxx::register)` 不动
- [ ] 6.3 验证：`./gradlew :tny-game-basics:test :tny-game-common-scheduler:test` 全绿；`./gradlew :tny-game-net:compileJava` 通过（ClassImporter 其他继承点哨兵）

## 7. net 测试脚手架下沉（~260 行）

- [x] 7.1 在 `tny-game-net/src/test/java/` 新增共享 `TestTunnel` fixture（形态照 `RpcContextFixture` 先例，同 test 源集；不引入 java-test-fixtures 插件）：resetSession/onOpen/onClose/doDisconnect/isActive/双地址桩全能力覆盖 8 文件现用面
- [x] 7.2 改造 8 个测试（RequestResponseClosure/TunnelEventOrder/SessionOfflineOnce/SessionResendSafety/TunnelUnboundRejection/ResendFilterConsistency/SessionBroadcastIntent/MockNetTunnelClose）改引用 fixture；`RacingTunnel` 竞态钩子以 fixture 子类保留；逐文件确认行为等价（断言零改动）
- [x] 7.3 验证：`./gradlew :tny-game-net:test` 全绿

## 8. starter properties + data EntityManager

- [ ] 8.1 新增绑定回归测试：starter-data 三 `*Properties` 与 starter-net-netty4 两 `*Properties` 各一例 Spring `Binder` 绑定用例（属性文件→对象，键集/默认值/嵌套 Setting 现状一致钉桩；测试放对应 starter 模块，无测试目录则新建 src/test）
- [ ] 8.2 按 D7 实现：提同包 public abstract 父承载访问器样板；子类保留链式 setter 协变覆写、字段初始化器与懒造默认值、类上全部 Spring 注解（`@ConditionalOnMissingBean` 注册点不动）
- [ ] 8.3 新增 `tny-game-data/src/test/java/com/tny/game/data/EntityManagerBatchTest.java`：`insert/update/save/deleteEntities` 四 default 批量方法行为钉桩（桩实现计次+返回值累加+空集合边界），重构前绿
- [ ] 8.4 按 D8 实现：四方法体收敛 `default int applyEach(Collection<E>, ToIntFunction<E>)`；4 个 default 与全部抽象方法签名不动
- [ ] 8.5 验证：`./gradlew :tny-game-data:test :tny-game-starter-data:test :tny-game-starter-net-netty4:test` 全绿；`./gradlew :tny-game-mongodb:compileJava :tny-game-redisson:compileJava` 通过（实现方哨兵）

## 9. 集成门禁与收口

- [ ] 9.1 复扫度量：重跑 `baseline/` 双脚本产出 `baseline/dup-after.md`，对照基线 M1 冗余行 ≤1,060（降 60%）、M3 ≤3,365（降 40%）；不达标回溯最大残余族；**残余若全部属不动清单则如实报口径缩减并记录，禁止为凑指标破 D 裁决**
- [ ] 9.2 全仓门禁：`./gradlew test -x :tny-game-namnspace-etcd:test` BUILD SUCCESSFUL（排除项沿用既有环境债），结果摘要记入本变更目录 `verification.md`
- [ ] 9.3 账目收口：更新记忆 `common-modules-audit-2026-09-30`——`ObjectMap.getFloat` 缺陷措辞升级为"单实现上显式参数化现状（钉桩在册）待另案修复"、8 族否决清单入册；本变更目录追加 `release-note.md`（零 BREAKING 声明 + Compatibility Impact 摘要 + 各族删除行数）
- [ ] 9.4 `openspec validate reduce-code-duplication --strict` 通过
