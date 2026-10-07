# Design

## Context

动机见 proposal.md - Why。当前实现状态：`com.tny.game.namespace.algorithm` 包内有 `HashAlgorithm` 接口（方法 `hash(String key, int seed)` 与 `getMax()`）和常量表 `HashAlgorithms`。常量表内部已有四个策略实现骨架（`HashOpenHftAlgorithm`、`HashApacheAlgorithm`、`HashGuavaAlgorithm`、`HashFunctionAlgorithm`），全部继承 `BaseHashAlgorithm`；`BaseHashAlgorithm` 提供两个既有机制：其一，`enableBit32` 声明输出宽度并把 32 位选项的取值上界报告为 4294967295；其二，`enableSeed` 为 false 时按既有约定把种子文本拼接到键之后再计算哈希。`HashFunctionAlgorithm` 以 `ToLongBiFunction<String, Integer>` 接纳任意"字符串加种子返回长整型"的函数，是接入自有算法函数的现成通道。

约束条件：经排查确认 Maven Central 上不存在提供原版 CityHash32 的第三方库（`net.openhft:zero-allocation-hashing` 0.16 的 `LongHashFunction` 只有 64 位形态的 `city_1_1`，commons-codec 1.15 与 guava 33.0.0-jre 无 CityHash，netty-common 4.1.104 不含 `io.netty.util.hash` 包），且用户已明确选择"与官方 CityHash32 逐值一致"而非既有的高位折叠形态，因此实现路线只能是模块内移植。tny-game-namnspace 模块目前没有测试源码目录，需要为验收新建。

爆炸半径（codegraph 与全仓检索摘要）：对 `HashAlgorithms` 类的 modify 分析结果为低风险、无跨文件受影响符号；对 `BaseHashAlgorithm.countHash` 的分析结果为直接受影响符号 0 个。全仓检索显示该常量表的引用点只有两处：模块内 `HashAlgorithmHasher` 使用 `HashAlgorithms.getDefault()` 兜底（本设计不改缺省项，不受影响），以及 tny-game-namnspace-etcd 的集成测试 `EtcdNamespaceExplorerIT` 引用了该类（只读引用，增量字段不破坏编译）。`CITY_HASH_32` 新常量在本仓库内没有既有调用方，供下游业务工程选用。

## Goals / Non-Goals

**Goals:**
- 提供与 Google 参考实现 city.cpp 中 32 位 CityHash 函数逐值一致的 `CITY_HASH_32` 公共常量（种子为零时）。
- 种子语义与非原生种子算法的既有键拼接约定一致，取值范围与上界报告符合 namespace-hashing 规格增量。
- 为规格增量的每个 Scenario 建立可重复执行的单元测试。

**Non-Goals:**
- 不修改任何既有算法常量的行为，不更换缺省算法，不引入新的第三方依赖。
- 不实现 CityHash128 或带原生种子参数的变体。
- 不清理 `HashAlgorithms.main()` 中的历史调试脚手架代码，也不为新增量向其补演示输出（该类只承载一次性人工比对用途，不属于行为契约）。
- 不修正既有常量中疑似不一致的宽度声明（如 `FARM_HASH_32` 实际走 64 位形态），存量违例不在本变更触碰范围内。

## Decisions

- **D1：CityHash32 以模块内移植类落地，包位置 `com.tny.game.namespace.algorithm`，类名 `CityHash32`。** 依据原则 P3（新增能力等于新增类，既有类只加一行常量注册）与 P5（算法数学与常量装配分属两个变更理由，各自独立成类）。被否决的备选：a) 引入第三方 Maven 依赖——经 Maven Central 全库检索确认不存在提供原版 CityHash32 的构件，无坐标可引；b) 引用 JitPack 形态的 GitHub 移植仓库坐标——本框架向 Maven Central 与 Nexus 发布，引入 JitPack 依赖会迫使所有下游工程添加非常规仓库且候选仓库维护状态不可控，用户已否决；c) 手写独立发明的等价算法——无法回答"与参考实现逐值一致"的验收 Scenario，否决。命名沿用模式卷第一节"第三方隔离/契约后缀 Xxx"先例与 commons-codec `XXHash32` 同类名风格（M3：算法实名，不用 Manager/Util 后缀）。
- **D2：经既有 `HashFunctionAlgorithm` 骨架接入，`enableSeed` 取 false（键拼接约定），`enableBit32` 取 true。** 依据原则 P6（实现必须守约：官方 CityHash32 函数签名只接受字节序列、没有种子参数，若谎称支持原生种子会让调用方传入的 seed 被静默丢弃，违背接口语义；键拼接机制正是 `BaseHashAlgorithm` 为非原生种子算法准备的既有约定，`cityHash32` 计算前由骨架完成"键文本拼接种子文本"）与 P8（复用组合点而不是新开子类——`ToLongBiFunction` 通道已经存在，直接喂入移植函数即可）。`enableBit32` 取 true 使 `getMax()` 报告 4294967295，且移植函数返回值本就在 0 到 4294967295 区间，`BaseHashAlgorithm` 的高位折叠分支对其恒不触发。被否决的备选：a) `enableSeed` 取 true 并把 seed 传入移植函数后忽略——与 P6 冲突，制造"种子参与计算"的假象；b) 新建专用内部子类——同问题已有骨架解法而未复用，违反模式卷 M1；c) `openFht32(LongHashFunction::city_1_1, true)` 折叠一行接入——产物是 CityHash 1.1 六十四个位结果的高位折叠值，不是官方 CityHash32，与用户在需求阶段拍定的"原版语义"及 namespace-hashing 规格增量第三条 Requirement 直接矛盾，否决。
- **D3：新常量 `CITY_HASH_32` 声明在 `CITY_HASH_64` 之后，命名遵循静态常量 UPPER_SNAKE_CASE 既有规范。** 依据项目上下文命名规范与常量表"按算法族分组"的既有排列形状（Murmur3、XXHash、XXH3、CityHash、FarmHash、MetroHash 各成一段）。这是公共 API 的唯一新增面。被否决的备选：放入新枚举或注册表——`HashAlgorithms` 常量字段是既有选择通道（P10 三次法则：算法选择机制已存在且第二次复用，不提前再造新抽象）。
- **D4：移植实现逐行对照 city.cpp 的 `CityHash32` 函数族（含 `Hash32FallbackForKey4To7Bytes` 等长度分支），文件头携带 Apache 2.0 标准头并注明移植出处与 MIT 许可。** 依据原则 P13 与 P11：移植忠实性即合同内容。长度分支边界（0-4、5-7、8-16、17-32、33-64、64 字节以上）是历史缺陷高发区，逐分支保留是正确性判据。
- **D5：正确性验证采用"黄金向量固化加独立实现交叉比对"双轨。** 本机构建环境无法运行 Google C++ 参考实现，黄金向量按以下顺序取得：实施阶段先完成移植，再对照公开可得的第二套独立 Java 移植实现（如 GitHub 上以 MIT 或 BSD 许可发布的 cityhash Java 移植）在相同样本键上运行逐值比对，比对通过的样本值固化进单元测试作为长期回归基线。该轨替换掉"只能信单一移植"的风险。测试源集新建在 `src/test/java`，JUnit 5 由工程根统一托管。对应规格 Scenario 清单：确定性（同输入重复计算相等）、取值范围与上界报告（断言 `0 ≤ hash ≤ 4294967295` 且 `getMax() == 4294967295`）、非 ASCII 有值（中文与 emoji 样本不抛异常）、黄金向量一致（覆盖长度分支各段）、与 64 位高位截断值可区分（`city_1_1` 结果高/低 32 位比对至少一组不等）、种子键拼接语义（seed 7 结果等于"键拼接种子文本"的 seed 0 结果）、既有选项不受扰动（回归比对既有常量样本值不变）。

## Risks / Trade-offs

- 手工移植出错风险（长度分支与循环常量抄漏）→ 缓解：逐行对照原始实现；用独立第二实现交叉比对全部分支边界样本；黄金向量固化进测试，后续改动受回归保护。
- 交叉比对的第二实现本身也可能错 → 缓解：双轨不一致即停下核对原始 city.cpp 仲裁，不以第二实现单独定值。
- 新建测试源集可能触碰工程测试约定（模块测试布局、JUnit 版本托管）→ 缓解：实施前按根构建与 convention 插件的既有测试源集约定对齐，不自行在模块 build.gradle 加程序性配置。
- 键拼接种子语义使"同一键不同种子"不再是算法级盐值而是输入级拼接 → 缓解：该行为已在规格增量中显式成文，与其他非原生种子算法一致，调用方可预期。

## Compatibility Impact

`HashAlgorithms` 与 `HashAlgorithm` 是发布到 Nexus 与 Maven Central 的公共 API。本次变更为纯增量：不删除、不重命名、不改动任何既有字段与方法的类型和语义；缺省算法不变。旧版下游升级后行为逐值不变，无感升级；新版下游可选用新增的 `CITY_HASH_32` 常量。`tny-game-namnspace-etcd` 与 `tny-game-starter-namnspace` 无需修改源码，重新构建即自然包含新增量。无 BREAKING 面。
