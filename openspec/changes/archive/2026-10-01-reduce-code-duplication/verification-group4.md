# Group 4 verification — lang 转换链 + NumberAide（tasks 4.1-4.5，design D4+D8）

日期：2026-10-01。分支 5.7.x。模块：tny-game-common-lang。工作树基线含 `fix-common-dormant-defects`/`fix-common-audit-findings` 未合入 commit 的在途改动——本组以**工作树现状**为重构前基线（ObjectMap 在 HEAD 干净；WrapperObjectMap/NumberAide 含在途修复，开工行号即修复后形态，符合 tasks 1.1 时序条款）。

## 测试命令与结果

| 命令 | 结果 |
|---|---|
| `./gradlew :tny-game-common-lang:test --tests "com.tny.game.common.collection.map.access.MapConvertAccessContractTest"`（4.2 收敛前钉桩先行） | BUILD SUCCESSFUL；tests=10 failures=0 errors=0 skipped=0 |
| 同命令 + `--tests "…WrapperObjectMapConversionTest"`（收敛后，两个钉桩文件均零改动复跑） | BUILD SUCCESSFUL；MapConvertAccessContractTest tests=10 failures=0；WrapperObjectMapConversionTest（在途 fix 轮既有哨兵）tests=7 failures=0 |
| `./gradlew :tny-game-common-lang:test --tests "com.tny.game.common.number.NumberAideTest"`（4.4 收敛前） | BUILD SUCCESSFUL；tests=9 failures=0 errors=0（8 新算术钉桩 + 1 既有转换用例） |
| 同命令（4.4 收敛后，期望值零改动） | BUILD SUCCESSFUL；tests=9 failures=0 |
| `./gradlew :tny-game-common-lang:test --rerun-tasks`（4.5 真实证据，强制全重跑） | BUILD SUCCESSFUL；34 suites / tests=249 / failures=0 / errors=0（含组 3 的 24 例、本组新增 18 例） |
| `./gradlew :tny-game-basics:test :tny-game-net:test`（4.5 消费面哨兵） | BUILD SUCCESSFUL；basics 1 suite/10 tests、net 50 suites/167 tests，failures=0 errors=0（结果 XML 时间戳为本次运行） |

期望值零改动证明：三枚测试文件最后一次修改均发生在"重构前绿"运行**之前**（MapConvertAccessContractTest 的两处 run 前修正——删除未用变量、float 通道字符串格由"预期 CCE"改为"预期 NFE"——是对 recon 假设的实测校正，发生在 4.2/4.4 实现落笔之前）；此后所有实现编辑未伴随任何测试文件编辑。

全矩阵构成（4.1）：8 类型 ×2 重载 = 16 个转换 getter（recon 计 14，实测 16，全部入矩阵）× 两侧 × {直取命中、可转换（数字串→数值、数值跨型窄化/拓宽、任意值→toString）、不可转换显式失败、null 值键、缺键、默认值路径}，另有 getObject(key,def) 等价格、getMapAccessor、getNotNullToFunction、toMap 不可变 + 活包装 vs 构造期拷贝差异格。

## 变更文件清单

新增 main（包私有，非发布 API）：
- `collection/map/access/MapConvertAccessSupport.java`（66 行）：asObject/asNotNullObject/getNotNullToFunction 三条取值链尾段的共享引擎（convertOrNull/convertRequired/convertIfPresent）；方法体逐字搬运；valueClass 形参承载两侧差异（ObjectMap 的 `float.class` 原缺陷参数 vs Wrapper 的 `Float.class`），CCE 消息尾段分叉逐字保留。命名后缀照 FormatTextSupport 先例。

修改 main（行数 前→后，前=工作树现状）：
- `ObjectMap.java` 207→203：三个私有/公共 helper 收敛为一行委托；getFloat(key,def) 的 `float.class` 实参逐字保留并加"禁止顺手修"现状注释；16 个转换 getter 门面、构造器、getObject/getMapAccessor、`extends HashMap implements TypeMap` 全部不动。取径差异（本类经可被子类化覆写的 getObject(key)）留门面。
- `WrapperObjectMap.java`（类级 package-private，非发布名）211→204：同形态委托（取径 map.get(key) 直调保留）；`Float.class` 已修侧逐字保留；toMap/size/getObject 差异语义不动。
- `number/NumberAide.java` 817→771（工作树现状口径，含在途 fix 轮）：add/sub/multiply/divide/mod 五段同形 20+ 行方法体（实测 recon 段号 150-169/186-205/222-241/322-342 对应本组开工文件内 add/sub/multiply/divide 与 mod 段，全部覆盖）收敛为单一私有分派器 `operate(one, other, NumberOperation)` + 表驱动五常量 `ADD/SUBTRACT/MULTIPLY/DIVIDE/MOD`（运算符 lambda：BigDecimal/BigInteger 方法引用 + 基本类型三路共用 IntBinaryOperator（Short/Byte 段 int 提升即原语义）+ 私有 @FunctionalInterface FloatBinaryOperator 补缺）；divide 的 DECIMAL128、mod 的 remainder 截断语义、sub 的 one==null 返回 other 等现状全部进分派器逐字保留。

新增 test：
- `src/test/java/com/tny/game/common/collection/map/access/MapConvertAccessContractTest.java`（288 行，task 4.1，Mulan 头）。
- `NumberAideTest.java` 17→165 行（task 4.3 补 8 例，原有用例逐字保留）。

主源码合计：本组 1235（207+211+817）→ 1244（203+204+66+771）行，净 +9——物理删除克隆段（五算术体 ~165 行 + 两文件 helper 尾段 ~35 行）后由共享件文件固有开销（许可头/imports/D4 注释）回填；NumberAide 单文件净 −46。收敛度以 dupscan 岛口径在收口组度量（D9）。

## 公共签名冻结自查

脚本比对（类成员层 `^    (public|protected)` 声明行逐行 diff，HEAD vs 工作树）：
- ObjectMap / WrapperObjectMap / NumberAide：IDENTICAL。ObjectMap 16+2 getter、getNotNullToFunction、四构造器、`implements TypeMap` 不动；WrapperObjectMap 类本身包私有（消费者经 MapAccessors.wrap 取得），面级同样零 diff；NumberAide 公开五算术方法签名（`public static <N extends Number> N add(N one, N other)` 等）逐字在位，divideAsDouble/divideAsFloat/比较族/工具面零触碰。
- 新增声明仅包私有 `MapConvertAccessSupport`（final，非 public）与 NumberAide 内 private 嵌套（NumberOperation/FloatBinaryOperator）+ private 静态常量。
- TypeMap/MapAccessor 接口零触碰（design 不动面）。
- 消费面复核：net 两处 `new ObjectMap`、MapAccessors.wrap 经 :tny-game-net:test 167 例全绿；NumberAide MVEL/GraalJS/basics 活跃面经 basics 10 例全绿 + 全仓既有引用编译通过（test 编译含）。

## 遗留登记（现状差异/可疑语义，一律未修、已钉桩）

1. **ObjectMap.getFloat(key,def) 的 `float.class` 参数——recon 结论"值存在必抛 CCE"在基线树已不复现**：本链尾段自 e3d4a5a1 起走 ObjectAide.convertTo，其"基本类型→Wrapper.getWrapper 归一"分支在 HEAD 即存在，把 float.class 消化为 Float.class 语义。现状可观察差异收窄为：不可转换值格的 CCE 消息尾段（"…can not convert to float" vs "…can not convert to class java.lang.Float"），已由 floatDefChannelMessageDriftPinned 逐字钉死；可转换格两侧同值（getFloatChannelMatrix 钉"值存在现状=正常返回"）。**缺陷实参 float.class 原样保留（参数承载、禁止顺手修）**；D4 口径从"抹平"不变——修复（改 Float.class 并统一消息）另立变更。记忆账目升级措辞建议："单实现上显式参数化现状（钉桩在册）：后果面已被 convertTo 历史演进中和，参数与消息分叉待另案"。
2. **两侧取径差异未归一**：ObjectMap 链经 getObject(key)（HashMap 子类可覆写面）、Wrapper 经 map.get(key) 直取；归一会改变 ObjectMap 子类化的可观察行为，留门面。
3. **NumberAide 五方法 one==null 一律返回 other**：sub/divide/mod 不取反、不抛、不以零元语义处理（sub(null,5)=5）——分派器共用段原样保留（nullCombinationsSharedAcrossOperators）。
4. **混合类型结果恒按 one 折叠**：运算通道由 findClass 优先级决定（如 Integer×Long 走 long 通道），落型恒 as(x, one)→add(Integer,Long) 得 Integer、add((byte)100,(short)300) 溢出 −112（shortByteBranchesNarrowToFirstOperand/integerBranches… 钉桩）。
5. **第 7 路尾部 `return as(double 兜底)` 在 findClass 现行返回域下不可达**（六基本类型格必被前路命中，未知形态兜底返回 Double.class 也走 Double 格）：逐字保留；其可达面表现为未知 Number 实现（AtomicLong 等）经 Double 格计算后 as() 显式受控失败（fix 轮 BREAKING 现状，unknownNumberTypeFallsToDoubleChannelThenExplicitFailure）。
6. **divideAsDouble/divideAsFloat 内 `findClass` 结果在 divideAsDouble 中计算后弃用**（死计算）：不在 D8 声明范围，零触碰。
7. **比较族 less/lessEqual/greater/greaterEqual/equal 仍为六路同形克隆**：D8 只承诺五算术方法；比较族留作下轮候选（本变更不动清单外扩=过度）。
8. **MapConvertAccessContractTest 中 getBoolean(trueStr) 等格两侧同抛同消息**（Boolean.class 实参两侧一致）——确认"差异仅在 float.def 一格"，其余 15 格现状完全同构，为共享引擎的合法性提供矩阵证据。

## 收口压缩（终值度量前最后一批，2026-10-01；域=lang/转换链）

目标岛（`baseline/dup-after.md` M1 [跨文件] 冗余108行）：`ObjectMap.java:53-162` ↔ `WrapperObjectMap.java:45-152`。

### 1. SQUEEZED — WrapperObjectMap.getMapAccessor(key, def) 覆写删除（5 行逐字重复方法体）
- 判定依据：该覆写与 **MapAccessor 接口 default 同形态既有件**逐字同形（`Object value = getObject(key); return MapAccessors.cast(value, defaultValue);`，仅 `this.` 前缀之差）——正是"经共享引擎的既有形态消化"的命中项，非硬并。
- 零行为变更链：WrapperObjectMap 为包私有类（消费者只经 `MapAccessors.wrap/empty` 取 `MapAccessor` 面，外部字节码不可命名 → 无链接面），删除后 `invokeinterface` 解析落接口 default → 回调 `this.getObject(key)` → 本类 `getObject`（= `as(map.get(key))`）→ `MapAccessors.cast`，逐路径与覆写等值；`MapConvertAccessContractTest.mapAccessorChannelAgrees`（child/absent-def 两格）期望值一字未改仍绿。
- 度量口径如实说明：该覆写位于岛窗口 45-152 **之外**，故复扫岛尺寸不变（新窗口 ObjectMap:55-164 ↔ Wrapper:45-152，仍冗余108——行号位移源于第 2 条的注释回填）。本条是岛外真实逐字冗余的消化，不计入岛缩减。

### 2. JUSTIFIED_KEEP — 16 转换 getter 门面阶梯（岛体本体，窗口内已是最小可分单元）
每格体已是**单行**委托到共享引擎 `MapConvertAccessSupport` 的三形态之一（convertOrNull/convertRequired/convertIfPresent），进一步归一的四条路全部被设计或规则封死：
- (a) 把实现体上移为 `MapAccessor`/`TypeMap` 接口 default = 触碰本组"接口零触碰（design 不动面）"，在发布接口新增实现体属 P11 长期合同；且 `getFloat(key,def)` 的 `float.class`（ObjectMap 原缺陷参数，D4"缺陷原样搬家"）与 `Float.class`（Wrapper fix 轮已修侧）差异**无法由一份 default 承载**——default 化即抹平 = 行为变更，越出本变更口径。
- (b) 两侧取径差异（ObjectMap 经可被子类化覆写的 `getObject(key)`、Wrapper 经 `map.get(key)` 直取）为在册行为差异（本文件遗留登记 2），归一会改变 ObjectMap 子类化的可观察行为。
- (c) Wrapper 继承 ObjectMap 或新增公共父类 = 层级变更 + 活包装 vs 构造期拷贝语义分裂（`toMapImmutabilityAndCopySemantics` 钉"不归一"），且 ObjectMap 已占用 `extends HashMap` 单继承位。
- (d) 为让 80-token 窗口跌破阈值做同义改写/文本扰动 = 刷指标，铁律禁止。
结论：岛体是**同一 public 契约在两个实现类上的必声明骨架**（逻辑段已单一事实源化），M1 精确克隆的残余属结构性而非逻辑性冗余，保留并在两处门面注释登记处置。

### 3. JUSTIFIED_KEEP — ObjectMap.getObject(key, defaultValue) 覆写（与接口 default 逐字同形但未删）
- 与第 1 条同形判定的分界：ObjectMap 是**已发布 public 类**，删除类上声明即收窄可链接公共面——既有下游对 `ObjectMap.getObject(String,Object)` 的类面引用在本变更"全部 public 签名零变更"口径（Compatibility Impact 表 ObjectMap 行）下属破坏性，按 D3 同类先例"public 面删除=另案"处理；等价性现状已由 `MapConvertAccessContractTest.getObject(int/absent,def)` 两格钉桩（group4 类注释 31 行"另钉覆写与接口默认实现等价的现状"）。
- 处置：原位保留 + 2 行注释记录理由（非行为面改动）。

### 门禁与复扫证据
- `./gradlew :tny-game-common-lang:test --rerun-tasks`：BUILD SUCCESSFUL；34 suites / tests=249 / failures=0 / errors=0（与 4.5 同计数，**测试文件零改动**——MapConvertAccessContractTest 10 例、WrapperObjectMapConversionTest 7 例、FutureStateMachineParityTest 24 例分别绿）。
- 消费面哨兵 `./gradlew :tny-game-net:test --rerun-tasks`：BUILD SUCCESSFUL；167 tests / 0 failures / 0 errors（ObjectMap/MapAccessors 活跃面）。
- 原样复跑 `python3 baseline/dupscan.py && python3 baseline/dupmerge.py`：M1 27 岛 / 冗余 1,311 行（dup-after 为 28 岛 / 1,342；−31 行差值来自**并行 net 域**同期把 `VoidCommandPlugin/VoidInvokeCommandPlugin` 岛收敛到 `VoidCommandPluginSupport`，与本组处置无关），tny-game-common-lang M1 冗余仍 322，本组两岛尺寸不变——已如实记于第 1/2 条。
- 物理行账目：删除 5 行逐字重复覆写（Wrapper），回填 5 行处置注释（Wrapper 3 + ObjectMap 2），main 源码净 0 行；岛度量无退行。
