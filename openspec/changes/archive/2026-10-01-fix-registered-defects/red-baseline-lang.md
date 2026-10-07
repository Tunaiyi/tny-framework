# Red Baseline — lang 域（task 4.1-4.4、5.1-5.3）

取证方式：翻转期望值 → `./gradlew :tny-game-common-lang:test --tests <类>[.<方法>] --rerun-tasks` 取失败输出 → 实现转绿。
JUnit 方法内断言 fail-fast：同一方法内后续红格被首格遮蔽，分轮次摘录（轮次间隔=实现推进半步）。

## task 4.1（翻转 `NumberAideTest.nullCombinationsSharedAcrossOperators` → 零元语义矩阵）

轮 1（实现前，全矩阵首格遮蔽后续）：

```
NumberAideTest > nullCombinationsSharedAcrossOperators() FAILED
    org.opentest4j.AssertionFailedError: 空被减数=0−5，取反对侧值 ==> expected: <-5> but was: <5>
    (NumberAideTest.java:46)
```

= 在册钉桩方向确认：`sub(null, 5)` 现状直接返回另一操作数原值 5（规格承诺 −5）。
矩阵其余被遮蔽格的现状值（旧实现逐格可复算，与遗留登记第 3 项同源）：
`multiply(null,5)/multiply(5,null)` 现状=5（承诺 0）；`divide(null,5)/mod(null,5)` 现状=5（承诺 0）；
`divide(5,null)/mod(5,null)` 现状=静默返回 5（承诺受控失败）；`divide(null,null)/mod(null,null)` 现状=null（承诺受控失败）；
`divide(1.0f,0.0f)` 现状=Infinity、`mod(1.0f,0.0f)`/`mod(1.0d,0.0d)` 现状=NaN、`divide(1.0d,0.0d)` 现状=Infinity（承诺受控失败，不返伪值）。

轮 2（D1 null 归一半步实现后，浮点零除守门未到位）：

```
NumberAideTest > nullCombinationsSharedAcrossOperators() FAILED
    org.opentest4j.AssertionFailedError: Expected java.lang.ArithmeticException to be thrown, but nothing was thrown.
```

= 浮点通道零除数伪值方向确认（IEEE 语义静默产出 Infinity/NaN，`1.0f/0.0f` 格起遮蔽）。

## task 4.2（实现，转绿证据）

实现点（公开签名零改动，私有分派器单点改，`NumberAide.operate`/`NumberOperation`/`zeroLike`）：
- null→该运算零元：`zeroLike(对侧)` 按对侧类型通道构造数值零后照常分派（sub 空被减数=0−x 取反）；
- 双空：divide/mod 除数侧零化仍零 → 当场 `ArithmeticException`；add/sub/multiply 无落型通道维持返回 null（现状兼容格）；
- 除零显式失败：`NumberOperation.explicitZeroDivisor`（DIVIDE/MOD=true）守门，Float/Double 通道除数 `==0`（含 −0）即抛
  `/ by zero`——方向对齐既有高精度路径（`bigDecimalHighPrecisionPaths` 的 BigDecimal 除零格与 int/long 通道 JVM 抛出本已受控失败，零改动保持绿）。
绿证据：`NumberAideTest` 9 用例 failures=0（其余既有绿格期望值零改动同步通过）。

## task 4.3（翻转 `NumberAideTest.shortByteBranchesNarrowToFirstOperand` + 新增同宽超界/long 混合格）

轮 1（实现前）：

```
NumberAideTest > shortByteBranchesNarrowToFirstOperand() FAILED
    org.opentest4j.AssertionFailedError: short×short 提升 int 通道（BREAKING：此前折叠回 Short）
    ==> Unexpected type, expected: <java.lang.Integer> but was: <java.lang.Short>
```

轮 2（findClass 提升 + 落型单点改实现后，调用面 checkcast 遮蔽格暴露）：

```
NumberAideTest > shortByteBranchesNarrowToFirstOperand() FAILED
    java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.lang.Byte
```

= BREAKING 调用面实证：`mod((byte)7,(byte)2)` 按推断 N=Byte 的调用点，javac 在调用处插入 checkcast Byte，
落型改 Integer 后运行期即 CCE。钉桩既有纯值格 `assertEquals((byte)1, NumberAide.mod((byte)7,(byte)2).byteValue())`
因此必须补 `<Number>` 类型见证（断言值 (byte)1 零改动，仅调用见证补位——同方法内既有格 `add((byte)1,2)` 本已带
`<Number>` 见证，同一形态）。release-note 公告：以窄装箱静态类型推断五算子结果的外部调用点需加宽见证或改接 intValue() 等取值面。
绿证据：`NumberAideTest` 9 用例 failures=0；`NumericHashIntegrityContractTest` 18 用例（含 LocalNum 原宽/窄参数钉桩）failures=0。

## LocalNum 家族委托面自查表（D2 落型影响逐类）

| 委托入口 | 是否经 `NumberAide.operate` | D2 落型影响 | D1 null 影响 | 外显结论 |
|---|---|---|---|---|
| LocalNum.add/sub/multiply/divide/mod(int/long/float/double) | 否（`as(value, this.number)` 直落） | 无 | 参数为原始类型不可空 | 零影响 |
| LocalNum.add/sub/multiply/divide/mod(short/byte) | 否（先 `(long)` 加宽再走上行） | 无 | 同上 | 零影响（"存储值原宽参与"承诺由本类自担，差量回归格保持） |
| LocalNum.add/sub/multiply/divide/mod(Number/LocalNum) | 是 | operate 结果类型变 Integer/Long，但外层 `as(x, this.number)` 立即回折存储原宽 → 逐格同值（如 LocalNum<Byte>(100).add(300)：旧 −112/新 400→回折 −112） | 存储值恒非空（set 判空）；对侧 null 语义随五算子矩阵：divide/mod(Number null) 由"返回存储值"改受控失败（未钉桩方向变化，随 D1 公告） | 值面零回归（18 绿格实证）；除零/零乘 null 入口行为随五算子 |
| Short/Int/Long/Float/DoubleLocalNum 的原始类型入口 | 否（复合赋值 `+=` 隐式窄化，不经 NumberAide） | 无 | 无 | 零影响 |
| Short/Int/Long/Float/DoubleLocalNum 的 add/sub/multiply/divide/mod(LocalNum) | 是 | 同 LocalNum 行：外层 `as(x, 存储原始值)` 回折 → 逐格同值 | 同上 | 值面零回归 |
| LocalBoolean | 否（不引用 NumberAide） | 无 | 无 | 零影响 |
| MathAide（静态 import NumberAide.*） | 仅比较族 less/greater/min/max | findClass 窄→Integer 提升后比较支路取值逐位相同（intValue≡原 short/byte 支路值） | 比较族不在五算子承诺面、未触碰 | 零影响 |
| basics Trade/AlterType/SimpleTrade/CollectTradeItem、starter-basics CapacityCollector 等（静态/显式 NumberAide.add/sub/比较） | 是 | 调用点静态类型均为 Number/Integer/Long（无窄推断 checkcast 面）；Integer×Long 折叠面未改（add(5,3L) 得 Integer 现状绿格钉住） | 生产路径无 null 操作数流入（merge/reduce 无空元素） | 由 4.4 `:tny-game-basics:test` 实证 |

## task 5.1（翻转 `MapConvertAccessContractTest.floatDefChannelMessageDriftPinned` → 两侧逐字相等·装箱形态）

轮 1（实现前）：

```
MapConvertAccessContractTest > floatDefChannelMessageDriftPinned() FAILED
    org.opentest4j.AssertionFailedError: ObjectMap 侧尾段已归一为装箱形态（D8 翻转：此前 '…to float'）
    ==> expected: <class java.lang.Boolean can not convert to class java.lang.Float>
        but was: <class java.lang.Boolean can not convert to float>
```

= 在册分叉方向确认（遗留登记第 1 项：同语义参数两侧尾段 `float` vs `java.lang.Float` 分叉，ObjectMap 侧走原始类尾段）。

## task 5.2（实现，转绿证据）

实现点：
- `MapConvertAccessSupport`：新增私有 `boxed(Class)` 归一（`isPrimitive()` → `Wrapper.getWrapper`），
  convertOrNull/convertRequired/convertIfPresent 三个尾段入口统一装箱后交 `ObjectAide.convertTo`（一处归一、两侧同得消息一致）；
- `ObjectMap.getFloat(key, def)`：缺陷实参 `float.class` → `Float.class`（与 Wrapper 侧同参；成功面行为零变化——
  convertTo 历史演进本已消化原始类参数，本项把"依赖上游宽容"转成显式正确，design D8）。
绿证据：`MapConvertAccessContractTest` 10 用例 failures=0（`floatChannelMatrix` 等其余格期望值零改动、`getFloat(boolVal,def)`
格 messageMustMatch=false 参数面不动、翻转后两侧消息实际亦逐字相等）；`WrapperObjectMapConversionTest` 7 用例 failures=0。
BREAKING 措辞样本（release-note）：
`ObjectMap.getFloat("k", -1f)`，存储值 Boolean.TRUE →
变更前尾段 `class java.lang.Boolean can not convert to float`；
变更后两侧统一 `class java.lang.Boolean can not convert to class java.lang.Float`。

## task 4.4/5.3 门禁记录

- `:tny-game-common-lang:test --rerun-tasks`：BUILD SUCCESSFUL，tests=249 failures=0 errors=0（两轮全模块复跑均绿）。
- `:tny-game-basics:test`：BUILD SUCCESSFUL，tests=19 failures=0 errors=0。
- `:tny-game-expr:test`：BUILD SUCCESSFUL（该模块 src/test/java 无 Java 用例，tests=0；NumberAide/LocalNum 在 expr 主源零引用，grep 实证）。
- `:tny-game-net:test --rerun-tasks`：BUILD SUCCESSFUL，tests=167 failures=0 errors=0（ObjectMap 消费点两处在 relay metadata，无消息解析依赖）。
- 插曲：首轮 `:tny-game-basics:test :tny-game-expr:test` 联跑报 "Could not write XML test results for com.tny.game.basics.item.xml.YamlTest/
  XStreamItemModelManagerTest/YamlItemModelManagerTest" ×3——系与并行 basics 组同时执行同模块 test 任务争写 build/test-results 目录
  （基础设施写冲突、非断言失败；涉事类名中的 xml/Yaml 只是包名巧合，与 lang 域改动无关）；按铁律 5 不杀锁、串行重试后两模块各自 BUILD SUCCESSFUL。

