# Red baseline — basics 域（tasks 1.1/1.2/6.1-6.4，design D5/D6）

日期：2026-10-01。分支 5.7.x。铁律自查：本域测试翻转只动差量点名格（对照表见文末"翻转面清单"），
其余既有绿格期望值与断言零改动（`git diff -- tny-game-basics/src/test` 仅 2 方法 + 新文件，见下）。

## 一、11 在册钉桩核对表（tasks 1.2；六组归档 verification 遗留登记 → 现测试代码）

| # | 钉桩名（design Context 名录） | 现测试代码位置（在位确认） | 对应差量 Scenario | 本域归属 |
|---|---|---|---|---|
| 1 | RT_JSON/COUCH_ESCAPES | `tny-game-protobuf/.../FormatMergeRoundTripTest.java` `jsonMergeRoundTripSnapshots`（RT_JSON_ESCAPES_PRINT L140）/ `couchMergeRoundTripSnapshots`（RT_COUCH_ESCAPES_PRINT L144） | protobuf-text-fidelity（Unicode 还原格） | 组2 |
| 2 | P_HTML_ESCAPES | 同目录 `FormatGoldenTest.java` `htmlPrintToStringSnapshots` GOLDEN_HTML_ESCAPES 格（L68/L192，名录为记号别名，实体=打印快照格） | protobuf-text-fidelity（Html 转义对齐） | 组2 |
| 3 | RT_XML_ESCAPES | `FormatMergeRoundTripTest.xmlMergeRoundTripSnapshots` escapes 格（钉 ParseException `"1:25: Expected \">\"."` L74-76，实体为格形态） | protobuf-text-fidelity（Xml 闭合） | 组3 |
| 4 | RT_PROPS_MSGSET | `FormatMergeRoundTripTest.propsMergeRoundTripSnapshots` msgSet 格（钉 ParseException `"1:30: Expected \".\"."` L107-109，格形态） | protobuf-text-fidelity（消息集对称） | 组3 |
| 5 | unknown 三形态（+两静默） | 抛式三格：`FormatMergeRoundTripTest` XML UNKNOWN L69-71（`"1:42: Expected identifier. --"`）/ PROPS UNKNOWN L104-106（`"2:1: Expected identifier."`）+ `FormatGoldenTest` GOLDEN_HTML_UNKNOWN L66；静默两格：RT_JSON_UNKNOWN_PRINT L138 / RT_COUCH_UNKNOWN_PRINT L142（另 GOLDEN_{XML,JSON,COUCH,PROPS}_UNKNOWN 打印快照 L40/53/92/79） | protobuf-text-fidelity（未知字段显式失败） | 组3 |
| 6 | floatDefChannelMessageDriftPinned | `tny-game-common-lang/.../map/access/MapConvertAccessContractTest.java` | object-access-conversion | 组5 |
| 7 | nullCombinationsSharedAcrossOperators | `tny-game-common-lang/.../number/NumberAideTest.java` | numeric-hash-integrity（null 零元） | 组4 |
| 8 | shortByteBranchesNarrowToFirstOperand | `tny-game-common-lang/.../number/NumberAideTest.java` | numeric-hash-integrity（窄类型提升） | 组4 |
| 9 | demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed | `tny-game-basics/.../EnumRegistryFamilyContractTest.java` | enumeration-facade-semantics R1（严格校验通道） | **本组 6.1（已翻正）** |
| 10 | ItemTypes 五分支用例（ofItemId）＋ ofAlias("") IOOBE 格 | 同上 `itemTypesAliasAndIdTruncationExtensions`（五分支 L399-403 原状→翻正；ofAlias 空串格原状→翻正并补纯分隔符格） | enumeration-facade-semantics R2/R3 | **本组 6.2/6.3（已翻正）** |
| 11 | illegalKeyTextFailsUnderControl（返 null 半段） | `tny-game-common-digest/.../rsa/RSAUtilsTest.java` | digest-codec | 组7 |

**缺项**：无——11 项全部在位；组 2/3/4/5/7 翻正由各域组负责，本域不触碰其文件。

## 二、号段证据（tasks 1.1 前置；D5 Open Question 裁定依据）

- 注册实际来源：`GameEnumClassLoader.createSelector(ItemType.class, ItemTypes::register)` 类扫描装载；仓内可见模型号段样本 =
  测试夹具注册条目（basics：908001/9000000/0；starter 哨兵新注册：2000000/9000000）。
- **正向生成面（关键证据）**：`ItemType.itemIdOf(index) = Long.parseLong(getIdHead() + "" + index)`，
  `getIdHead() = getId()/ID_TAIL_SIZE(1e6)`；`BaseSingleStuffOwner` L52 入库即以 itemIdOf 产全局号。
  → 全局号 = 十进制拼接（首位=idHead，尾=变宽序列号），如 `2000000.head=2 → itemIdOf(500000999)=2500000999`。
- **单档百万制成立**：注册模型段基址恒为 `1e6 × 个位数`（head 1..9 单档）。差量承诺样本 2000001（7 位宽）与
  2500000999（10 位宽，**超 int 位宽**）同归模型 2000000——固定段商 `/S×S` 单常量无法同时成立（S=1e6 时
  2500000999→2.5e9≠2e6；S≥2.5e9 则非 1e6 倍数破坏 getIdHead 契约）；**design D5 字面 `ofModelId((int) id)`
  对 2500000999 即 int 回绕为负→归位失败**。
- **裁定（Open Question 按号段证据落定，实现采最小改动）**：单一商式="反复 ÷10 取商至首位数字"
  （即对 10^(位数−1) 取商），首位=getIdHead 逆还原，`×ID_TAIL_SIZE` 回段基址后交 `ofModelId`（其 `/1e6×1e6`
  对段基址恒等）。段大小不引入多档配置，恒用既有常量 `ID_TAIL_SIZE`（=1e6，单一事实源不动）。
  负值/未注册号段按 `ofModelId→宽松 of` 通道语义返 null。

## 三、红基线摘录（铁律 2：先翻期望值跑红，摘录逐条标 task）

### [1.1/6.3] GameExplorer 消费面哨兵（新文件，修复前恒 0 命中）

```
> Task :tny-game-starter-basics:test FAILED
GameExplorerItemIdSentinelTest > serialsOfSameModelAllResolveBackToModelEntry() FAILED
    org.opentest4j.AssertionFailedError at GameExplorerItemIdSentinelTest.java:78
    ==> itemIdOf 产出的 2 位序列号须归位模型 2000000（现状恒解析段 0=红基线）
        expected: <SENTINEL_MODEL_2E6> but was: <null>
2 tests completed, 1 failed
```
（短路于首格；2000001/2500000999/9999/9e18 各格同族恒段 0——原状阶梯 `商恒<ID_TAIL_SIZE→取整 0`。
`unregisteredHeadSegmentFollowsLenientLookupSemantics` 格首跑即绿，钉未注册边界非翻转面。）

### [6.1/6.2] 首跑 basics 契约矩阵（两方法红）

```
> Task :tny-game-basics:test FAILED
EnumRegistryFamilyContractTest > itemTypesAliasAndIdTruncationExtensions() FAILED
    org.opentest4j.AssertionFailedError at EnumRegistryFamilyContractTest.java:396
        Caused by: java.lang.ArrayIndexOutOfBoundsException at EnumRegistryFamilyContractTest.java:396
EnumRegistryFamilyContractTest > demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed() FAILED
    org.opentest4j.AssertionFailedError at EnumRegistryFamilyContractTest.java:369
    (Expected java.lang.NullPointerException to be thrown, but nothing was thrown)
10 tests completed, 2 failed
```
（369 格=DemandTypes.check(String) 显式失败未发生=6.1 红；396 格=ofAlias("") 越界栈外泄=6.2 红。）

### [6.2 转正后中间跑] 五分支格红（ofItemId 实现未动，红证补录）

```
EnumRegistryFamilyContractTest > itemTypesAliasAndIdTruncationExtensions() FAILED
    org.opentest4j.AssertionFailedError at EnumRegistryFamilyContractTest.java:411
    ==> 翻转（6.3）：9999 首位 9 → 段基址 9000000 归位 TRUNK（TRUNK.itemIdOf(999) 实产样本）
        expected: <PROBE_ITEM_TRUNK> but was: <PROBE_ITEM_ZERO>
10 tests completed, 1 failed
```
（411 格=五分支首支；JUnit 短路，余四支与首支同因（恒 0 段），哨兵红已覆盖同族样本。）

## 四、转绿证据（改实现后）

| 命令 | 结果 |
|---|---|
| `./gradlew :tny-game-basics:test --rerun-tasks` | BUILD SUCCESSFUL；tests=19 failures=0 errors=0 |
| `./gradlew :tny-game-starter-basics:test --rerun-tasks` | BUILD SUCCESSFUL；tests=2 failures=0（GameExplorer 哨兵绿） |
| `./gradlew :tny-game-common-scheduler:test :tny-game-starter-basics:compileJava --rerun-tasks` | BUILD SUCCESSFUL；scheduler tests=50 failures=0（TaskDeliveryContractTest 别名缺席钉桩 `removedAliasLookupEntryIsAbsentFromPublicMethodSurface`/`strictLookup...` 零回归——scheduler 不依赖 basics，其 TaskReceiverTypes 源未动） |

## 五、翻转面清单（铁律 1 自查，`git diff -- tny-game-basics/src/test` 实测）

- `demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed`：仅 2 格 assertNull→assertThrows(NPE,消息逐字)；
  方法名保留（8.3 翻转对照表可按原名索引）。命中格与其余六件套格零改动。
- `itemTypesAliasAndIdTruncationExtensions`：ofAlias("") 1 格 IOOBE→IAE+消息含输入；新增纯分隔符 "$" 1 格（差量点名面）；
  ofItemId 五格全翻（ZERO→TRUNK/null）；ofModelId 三格与 ofAlias 命中/未命中消息格零改动。
- 新增文件：`tny-game-starter-basics/src/test/java/com/tny/game/basics/item/GameExplorerItemIdSentinelTest.java`
  （Mulan PSL v2 头；同包直调包私有 register 按现状可见性，未动生产签名）。
- 实现面：`DemandTypes.check×2`（严格通道薄委托，签名不动）、`ItemTypes.ofAlias`（拆前判非法，null 通道现状不动）、
  `ItemTypes.ofItemId`（阶梯 45 行删除→单一商式 6 行）。

## 六、release-note 行为样本（供组 8）

- `DemandTypes.check("未注册")`：`null`（静默放行）→ `NullPointerException: 获取 未注册 DemandType 不存在`；
  `check(未注册id)` 同理携 ID 消息。全仓零既有调用方（consumer-scan.md）。
- `ItemTypes.ofAlias("")`：`ArrayIndexOutOfBoundsException` 越界栈 → `IllegalArgumentException:
  ItemType 别名查找输入非法（空串或纯分隔符）: ""`（"$" 同）；GameExplorer.getItemByAlias 空别名输入方可见。
- `ItemTypes.ofItemId(2000001L)` / `GameExplorer.getItem(AnyId(…,2000001))`：恒段 0（注册表 0 号位或 null）→
  模型 2000000 条目；9e11/9e15/9e18 等同理归首位段。**道具类型解析链路行为级 BREAKING**（design 公告表
  「枚举行 GameExplorer×2」运营侧核对道具表要点名）。
