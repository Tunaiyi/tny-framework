# Tasks

> 每组"翻转/新建钉桩（红）→ 实现（绿）→ 模块验证"；钉桩翻转只动期望值方向、不动其余绿格。红基线摘录记入本变更目录 `red-baseline.md`（沿用三轮实践）。

## 1. 前置哨兵与红基线建账

- [x] 1.1 消费面哨兵先行：`ItemTypes` 注册表取真实模型号段，新增 `tny-game-starter-basics` 侧 GameExplorer 解析哨兵用例（修复前恒 0 号位=红基线，修复后归位模型条目）；grep 表达式资源（MVEL/GraalJS 脚本、配置文件）对 `−112/方向错误常量` 依赖扫描，结论记本变更目录 `consumer-scan.md`
- [x] 1.2 钉桩名清单核对：六组归档 verification 的"遗留登记"逐条对照现测试代码，确认 11 个在册钉桩（RT_JSON/COUCH_ESCAPES、P_HTML_ESCAPES、RT_XML_ESCAPES、RT_PROPS_MSGSET、unknown 三形态、floatDefChannelMessageDriftPinned、nullCombinationsSharedAcrossOperators、shortByteBranchesNarrowToFirstOperand、demandTypesCheckIsLenient…、ofItemId 五分支、illegalKeyTextFailsUnderControl 的 null 半段）全部在位且各自对应本变更差量 Scenario；缺项补齐后进入修复组

## 2. protobuf 转义保真（D4 + Unicode 公式）

- [x] 2.1 翻转 Unicode 还原公式钉桩：`RT_JSON/COUCH_ESCAPES` 高位字节格改"往返逐字节相等"断言并新增全形态往返矩阵（含 `\u` 四位权展开样本与 `ﾯ` 现状值失效确认），跑红并摘录
- [x] 2.2 实现：`FormatTextSupport.unescapeBytes` 四位权展开修正（16³/16²/16¹/16⁰）；复跑 2.1 转绿
- [x] 2.3 翻转 Html 转义钉桩：`P_HTML_ESCAPES` 期望改"值内标记/换行以转义序列承载且结构不破坏"；实现 Html 形态装饰接入统一转义表；`./gradlew :tny-game-protobuf:test --rerun-tasks` 全绿（其余 golden 格零改动）
- [x] 2.4 验证：`:tny-game-protobuf:test` 全绿 + `:tny-game-oplog:compileJava`（唯一外部点）+ oplog printFiles 前后文本样本 diff 记入 `red-baseline.md`（供 release-note）

## 3. protobuf 往返闭合、未知策略、消息集对称（D3/D5p）

- [x] 3.1 翻转 `RT_XML_ESCAPES`（特殊字符集读回由抛错改成功且值等）；实现 Xml 词法对打印产物闭合；`:tny-game-protobuf:test` 相关格转绿
- [x] 3.2 未知字段策略统一：新建五形态一致拒绝用例（同输入含未知编号→全部显式失败且信息可定位编号），对现实现跑红；实现各形态读回侧统一拒绝路径（消息含首个未知编号）；翻转 Json/Couch"静默丢弃"重打印快照为拒绝断言，转绿
- [x] 3.3 消息集扩展对称：翻转 `RT_PROPS_MSGSET`（打印短名/读回全名分裂 → 同一标识规则往返闭合）；实现打印与读回命名归一；`:tny-game-protobuf:test` 全绿
- [x] 3.4 验证：`./gradlew :tny-game-protobuf:test --rerun-tasks` 全绿（golden/round-trip 其余格期望值零改动自查 git diff）

## 4. lang 数值语义（D1/D2）

- [x] 4.1 翻转 null 组合钉桩：`nullCombinationsSharedAcrossOperators` 改零元语义矩阵（sub 空被减数=取反、divide/mod 零除数=受控失败、add 兼容格保持），跑红摘录
- [x] 4.2 实现五算子 null 归一与除零显式失败（私有分派器单点改，公开签名不动）；`:tny-game-common-lang:test` 4.1 转绿
- [x] 4.3 翻转窄类型提升钉桩：`shortByteBranchesNarrowToFirstOperand` 改"byte100+short300=400/Integer"，新增同宽超界与 long 混合格；实现 `findClass`/落型按 JLS 提升通道；lang 全模块零回归（LocalNum 家族委托面自查表记入 verification）
- [x] 4.4 验证：`./gradlew :tny-game-common-lang:test --rerun-tasks` 全绿；`./gradlew :tny-game-basics:test :tny-game-expr:test` 通过（NumberAide/脚本暴露面回归）

## 5. lang 转换失败消息一致化（D8）

- [x] 5.1 翻转 `floatDefChannelMessageDriftPinned`：两侧同输入消息尾段改"逐字相等"断言（装箱形态），跑红
- [x] 5.2 实现：共享引擎参数装箱归一 + ObjectMap 门面 `float.class`→`Float.class`；`getFloatChannelMatrix` 等其余格零改动转绿
- [x] 5.3 验证：`./gradlew :tny-game-common-lang:test` 全绿；`./gradlew :tny-game-net:test`（ObjectMap 消费点）通过

## 6. basics 枚举门面三修（D5/D6）

- [x] 6.1 翻转 `demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed`：未注册身份改显式失败；实现 check 双形态接入既有严格通道；跑绿
- [x] 6.2 别名空输入：`ItemTypes.ofAlias("")` 改受控失败用例（越界栈不再出现）；实现拆分前判非法；`:tny-game-common-scheduler` 侧既有别名缺席钉桩零回归自查
- [x] 6.3 ofItemId 单一商式：翻转五分支用例为"序列号归位模型条目"断言（1.1 哨兵同步转绿）；实现删阶梯改 `ofModelId((int) id)`（段基址大小按 1.1 实测号段裁定）；未注册段返回语义按既有宽松/严格入口各归其位
- [x] 6.4 验证：`./gradlew :tny-game-basics:test --rerun-tasks` 全绿；`./gradlew :tny-game-starter-basics:test :tny-game-starter-basics:compileJava` 通过（GameExplorer 哨兵绿）

## 7. digest RSA 失败方向统一（D7）

- [x] 7.1 翻转 `illegalKeyTextFailsUnderControl` 的返 null 半段：`getPublicKey/getPrivateKey` 非法输入改显式失败断言，跑红（红基线：`red-baseline-digest.md`，首个翻断 RSAUtilsTest.java:199 "nothing was thrown"）
- [x] 7.2 实现两入口去吞异常（透传 `InvalidKeySpecException`/`IllegalArgumentException` 方向，与 toKey 系对表）；`./gradlew :tny-game-common-digest:test --rerun-tasks` 全绿（49 例 0 败，其余 35 例既有钉桩零改动）

## 8. 门禁与收口

- [x] 8.1 全仓门禁：`./gradlew test -x :tny-game-namnspace-etcd:test` BUILD SUCCESSFUL，摘要记本变更目录 `verification.md`——实测 BUILD SUCCESSFUL，20 模块 937 用例 0F/0E/0skip（+8 为哨兵/拒绝矩阵/闭合用例新增）
- [x] 8.2 release-note.md：BREAKING 表逐面成文（protobuf×5、lang×3、basics×3、digest×1——含 oplog 逐字节一致实测、GameExplorer 运营公告、窄见证 CCE 连带升级路径）；`openspec validate fix-registered-defects --strict` → valid
- [x] 8.3 账目同步：记忆 `common-modules-audit-2026-09-30` 划账（11 项钉桩翻正、ofItemId/getFloat 措辞更新、残余不修面复核）；verification 补"翻转对照表"（原钉桩名→新断言名）——记忆已加"第四轮更新"段；对照表 12 行落 verification.md；差量 R3 措辞按 D5 裁定同步并复验 valid
