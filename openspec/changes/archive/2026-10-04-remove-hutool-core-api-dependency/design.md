# Design

## Context

动机见 proposal.md - Why。当前实现状态：`tny-game-namnspace/build.gradle` 声明五行 api 依赖（common-lang、codec、commonsCodec、hutoolCore、zeroAllocationHashing）；`HashAlgorithms.java` 第 18 行导入 `cn.hutool.core.util.HashUtil`，`CITY_HASH_32` 经 `hash32((value, seed) -> Integer.toUnsignedLong(HashUtil.cityHash32(...)), false)` 接入（hutool 32 位函数无种子参数，故走键拼接约定），`CITY_HASH_64` 经 `hash64((value, seed) -> HashUtil.cityHash64(bytes, seed), true)` 接入（种子原生参与）。全仓检索确认 hutool 引用仅此一处：其他模块与 etcd 集成测试源码均不 import hutool。

既有设计衔接：进行中变更 add-city-hash-32-algorithm 的 design.md 已裁定 CityHash32 走模块内移植路线（其 D1：第三方路线经 Maven Central 检索证伪；D2：经 `HashFunctionAlgorithm` 骨架以 `hash32(...)` 接入且 `enableSeed=false`；D4：逐行对照 city.cpp 移植并保留 MIT 出处声明）。hutool 形态在实现期偏离了该设计，本变更即收口动作：32 位档按原设计落地移植类，64 位档回到 zero-allocation-hashing 的 `city_1_1` 带种子实现（该形态正是引入 hutool 之前仓库中 `CITY_HASH_64` 的实现形态，zero-allocation-hashing 本就在 api 面）。

爆炸半径（codegraph 与全仓检索摘要）：对 `HashAlgorithms.java` 常量区符号的 modify 分析为低风险，跨文件引用点仅两处——`HashAlgorithmHasher` 使用 `HashAlgorithms.getDefault()`（缺省算法不动，不受影响）与 tny-game-namnspace-etcd 集成测试 `EtcdNamespaceExplorerIT` 的只读类引用（不触及 hutool）。`cn.hutool` 坐标的仓内消费者除本文件外为零，版本目录条目删除无悬挂引用。

## Goals / Non-Goals

**Goals:**
- hutool-core 从依赖声明、版本目录、代码引用、第三方许可证登记四处同步清干净，发布 POM 不再出现该坐标。
- `CITY_HASH_32` 与 `CITY_HASH_64` 以零外部 CityHash 依赖的形态继续提供，各自与 Google 参考实现语义一致。
- 哈希行为对外契约（确定性、取值域、上界报告、种子参与方式）符合本变更规格增量。

**Non-Goals:**
- 不追求两档 CityHash 与 hutool 中间形态的逐值零漂移（需求方已裁定允许取值变化，前提是无下游采用）。
- 不改动 `HashAlgorithm` 接口、算法集其他常量与缺省算法，不清理 `main()` 调试脚手架与既有常量宽度声明违例（如 `FARM_HASH_32`），这些不在触碰范围（存量违例触碰即改仅覆盖改动区域内形态，本变更改动区仅两个常量声明行）。
- 不评审其他模块对 hutool 的未来使用（全仓已无引用，无需登记豁免）。

## Decisions

- **D1：CityHash32 落地为模块内移植类 `com.tny.game.namespace.algorithm.CityHash32`，`CITY_HASH_32` 经既有 `hash32(...)` 工厂以 `enableSeed=false` 接入。** 这是直接沿用 add-city-hash-32-algorithm 变更 design.md 的 D1/D2/D4 结论（P3 新增类承载新算法、P6 官方 32 位函数无种子参数故诚实走键拼接约定、P8 复用 `HashFunctionAlgorithm` 组合点），此处不重复论证。被否决的备选：a) 保留 hutool 仅降为 implementation——Maven POM 中 implementation 映射为 runtime 作用域，传递依赖仍进下游运行类路径，达不到"发布面不携带未选用算法库"的规格要求，且用户要求的是移除；b) 用 zero-allocation-hashing 的高位折叠形态顶替——违反上一变更规格"三十二位档与参考实现逐值一致、非折叠"的 Requirement；c) 引入其他第三方库——Maven Central 无原版 CityHash32 库（上一变更 design.md 已证伪检索）。
- **D2：`CITY_HASH_64` 回到 `openFht64(LongHashFunction::city_1_1, true)` 形态，不手写 CityHash64 移植类。** 依据 P5（算法正确性责任交给已被组件长期信任的实现库，模块自持代码只补库缺失的 32 位档）与 P8（复用已在 api 面的依赖，避免约三百行第二移植代码的维护负担）。openhft `city_1_1(long seed)` 与 hutool `cityHash64(bytes, seed)` 同为 CityHash 1.1 带种子 64 位函数的移植，语义类别一致（种子原生参与），逐值可能不同——由需求方"允许取值变化"裁定覆盖，规格增量以"与参考实现带种子语义一致"为验收基线。被否决的备选：双移植（32 与 64 全手写）——移植体量与维护面翻倍，在已有可信库形态可用时不必要（P10 精神：不提前扩大自持代码面）；保留 hutool 64 位、只换 32 位——`api libs.hutoolCore` 无法删除，目标落空。
- **D3：黄金向量以"替换前 hutool 现形态输出加参考实现定义"双源取得。** 32 位档：实施时先运行现实现抓取样本键输出固化为测试断言（hutool `cityHash32` 本身即原版移植，其输出是参考实现值的一手样本），移植类必须逐值复现；不一致时以 city.cpp 定义仲裁。64 位档：不设与 hutool 的等值断言（漂移已获准），改以 openhft 实现在替换前后自洽断言（同一键两种子确定性）加参考语义抽查。依据 P13：每个 Scenario 有对应测试且取证路径可执行。被否决的备选：为 64 位档也固化 hutool 旧值——与"允许变化"裁定冲突且把中间形态错误神圣化。
- **D4：依赖清账一次做全：模块 build.gradle 行、libs.versions.toml 条目、源码 import、THIRD-PARTY-LICENSES.md 登记四处同批移除。** 依据 P5 的账实一致（依赖目录与许可证登记是同一治理动作的四个面，留任何一面都会产生死账；与既有"死面清账"先例同法）。`hutoolCore` 条目在全仓无任何消费者（已检索证实），属目录死条目。被否决的备选：保留 toml 条目备未来用——目录条目存在即被视为在用治理面，依赖重新引入须重走治理评审（P11 合同面纪律）。
- **D5：与 add-city-hash-32-algorithm 变更的共用面按"本变更吸收其移植任务"处理。** 两变更共享同一测试源集目录、同一移植类与同一测试类（`CityHash32Test`）。本变更的 tasks 覆盖上一变更任务组 2、3 的实质内容（移植类与常量接入、黄金向量、契约测试），并将上一变更中已被现状实现取代的任务标记为以本变更交付；规格两增量同能力叠加（各写各的 Requirement，标题不冲突）。实施顺序上若两个变更并行应用会改写同一文件造成冲突，建议先应用本变更、随后核验归档上一变更残量。被否决的备选：两个变更各自独立实施——同一 `HashAlgorithms.java` 两个补丁必然冲突（P9 直接伙伴原则下的变更编排常识）。

## Risks / Trade-offs

- 64 位档取值漂移影响未知下游 → 缓解：需求方裁定前提为"无下游实际采用 CityHash 结果"；发布说明条目应声明该行为变化，万一存在隐性采用者可从 hutool 形态版本回退（本变更是独立提交，可单点 revert）。
- CityHash32 移植出错（长度分支、循环常量抄漏）→ 缓解：D3 的双源取证——hutool 现实现输出作为一手黄金向量全分支覆盖比对，不一致以 city.cpp 仲裁；测试固化后受长期回归保护。
- openhft city_1_1 与 hutool cityHash64 的分布质量差异未被断言捕获 → 缓解：两档都是同一参考算法的移植，语义类别（带种子 64 位 CityHash）由规格钉住；分布差异不属本变更契约面。
- 两变更共用文件引发实施顺序耦合 → 缓解：D5 已成文处置路径，验收标准以本变更 tasks 为准。

## Compatibility Impact

`HashAlgorithms` 是发布到 Nexus 与 Maven Central 的公共 API。签名面：无删除、无重命名，`CITY_HASH_32`/`CITY_HASH_64` 字段与类型不变，旧调用方可编译运行。行为面：两档 CityHash 对同一键的返回值可能与 hutool 中间形态不同，这是需求方知情裁定的允许漂移（判据：当前无下游采用该结果）；因此不标注 BREAKING，但发布说明必须如实记载取值变化。编译面：api 传递依赖移除使下游编译类路径不再含 hutool 类——若存在未申报的隐性依赖（直接 import hutool 而未自声明），该下游将编译失败，这是规格增量第二条 Scenario 成文的预期后果，修复路径为其自行声明所需依赖。依赖治理面：无需门禁豁免登记（只减不增），`tny.module-checker` 配置期对账与 `checkPublishPrerequisites` 照常通过。
