# Proposal

## Why

命名空间分片组件（tny-game-namnspace）的哈希算法库目前只提供 64 位输出宽度的 CityHash 常量（CITY_HASH_64），而 32 位宽度常量全部来自其他算法族（Murmur3、XXHash、XXH3、MetroHash）。当下游部署需要把路由键收敛到 32 位取值域、同时又希望沿用 CityHash 算法族时，现有常量表没有可选项，只能改用别的算法族而改变哈希分布。本次变更补齐 CITY_HASH_32 常量，使 CityHash 算法族在 32 位与 64 位两个宽度上都可选。

## What Changes

- 在 `HashAlgorithms` 公共常量表中新增 `CITY_HASH_32` 常量，供调用方显式传入 `HashAlgorithmHasher` 等哈希计算器选用。
- 在 algorithm 包内新增一个按 Google 原版 CityHash 参考实现（city.cpp 中的 CityHash32 函数）移植的 Java 类。经排查，Maven Central 上不存在提供原版 CityHash32 的第三方库（net.openhft zero-allocation-hashing 0.16 只提供 64 位的 CityHash 1.1，Guava、commons-codec、Netty 均无对应实现），因此采用模块内移植路线，不新增外部依赖。移植文件保留原实现（MIT 许可证）的出处声明，并照常携带本项目的 Apache License 2.0 头注释。
- 为本变更新增的行为契约补充单元测试：tny-game-namnspace 模块当前没有测试源码目录，需要新建 src/test/java 源集并覆盖规格中的每个 Scenario。
- 不修改既有常量的哈希行为，不修改 `HashAlgorithm` 接口，默认算法保持不变（仍为 XXH3_HASH_32）。本次变更为纯增量扩展，无 BREAKING。

## Capabilities

### New Capabilities

- `namespace-hashing`: 命名空间分片哈希组件对外提供的哈希算法选择契约，覆盖各算法常量的输出宽度、取值范围、确定性与种子参与方式。

### Modified Capabilities

（无——本次变更不修改任何既有能力的规格需求。）

## Impact

- 受影响代码：tny-game-namnspace 模块的 `com.tny.game.namespace.algorithm` 包（`HashAlgorithms` 新增常量、新增一个 CityHash32 移植类）。
- 受影响下游：tny-game-namnspace-etcd（经 api 依赖引用该模块，现用集成测试只使用 `HashAlgorithms.getDefault()`，行为不受影响）；tny-game-starter-namnspace（门面装配模块，无需改动，发布件内容随本模块新增常量自然生效）。
- 依赖面：不新增第三方依赖；tny-game-namnspace 现有依赖（common-lang、codec、commons-codec、zero-allocation-hashing）保持不变。
- 公共 API：`HashAlgorithms` 是发布到 Nexus 与 Maven Central 的公共类型，本次只做字段增量，旧调用方可编译运行，向后兼容。
- 测试：需新建 tny-game-namnspace 的 src/test/java 源集与相应 JUnit 5 测试类。

## 实施收口留痕（2026-10-04）

本变更规划完成后，实现曾在构建治理改造中以 hutool 第三方库形态先行落地。后续变更
`remove-hutool-core-api-dependency` 已把 CITY_HASH_32 与 CITY_HASH_64 的实现收口回
本册 design.md 裁定（决策 D1：模块内移植类，D2：经 `hash32(...)` 工厂接入且
`enableSeed=false`，D4：逐行对照 city.cpp 并保留 MIT 出处声明），并同步移除了
hutool 依赖、清账了版本目录与第三方许可证登记；其 64 位档采用回库形态（该点经需求方
在后续变更中另行裁定）。本册的规格增量继续作为 namespace-hashing 能力的需求来源，
测试与交付勾选记录见后续变更账本。
