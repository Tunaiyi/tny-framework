# Tasks

> **衔接注记（2026-10-04）**：本册的任务实质——测试源集搭建、CityHash32 移植类、
> `HashAlgorithms.CITY_HASH_32` 常量接入与契约测试——已随后续变更
> `remove-hutool-core-api-dependency` 一并实施交付（该变更把实现从 hutool 中间形态
> 收口为模块内移植类，并在其中完成了黄金向量固化与全部测试）。本册按原文执行时会与
> 现状冲突的条目差异为：2.1 预期的"编译不过红灯"未出现（实现期常量已经存在），
> 2.2 的"独立第二实现"取值为 hutool 现形态输出（同为 Google city.cpp 的移植）。
> 本册保留规格增量与设计的裁决记录作用；实施勾选以对方账本为准。

## 1. 测试源集搭建

- [x] 1.1（由 remove-hutool-core-api-dependency 交付） 新建 `tny-game-namnspace/src/test/java/com/tny/game/namespace/algorithm/` 源集目录，确认工程根对测试源集与 JUnit 5 依赖的统一托管对本模块生效（不需要在模块 build.gradle 新增程序性配置；如约定插件已有 test 源集约定则直接沿用）。验证：放入一个空壳测试类后运行 `./gradlew :tny-game-namnspace:compileTestJava` 编译通过。

## 2. 契约先行测试（先于实现编写，对应规格增量 namespace-hashing）

- [x] 2.1（由 remove-hutool-core-api-dependency 交付） 编写 `CityHash32Test` 的契约用例：引用 `HashAlgorithms.CITY_HASH_32`（此时编译不过属预期红灯），断言同一键与种子重复计算结果相等、返回值落在 0 到 4294967295 区间、`getMax()` 等于 4294967295、含中文与表情符号的键计算不抛异常且不同文本取值不同。验证：本组任务以 3.3 的全绿执行为完成判据，此处以测试代码可读性自查通过为准。
- [x] 2.2（由 remove-hutool-core-api-dependency 交付） 固化黄金向量：实施独立第二套公开许可的 Java CityHash32 移植的临时交叉比对（对照来源与许可证记录在案的公开实现），在覆盖长度分支各段（0 到 4、5 到 7、8 到 16、17 到 32、33 到 64、64 字节以上）的样本键上逐值比对，把一致的样本值作为常量写入测试断言；交叉比对不一致时停下，以 Google city.cpp 原始定义逐段仲裁后再固化。同时把"与 `LongHashFunction.city_1_1` 输出高 32 位及低 32 位截断值至少一组不等"的判别样本写入测试，证明非折叠形态。验证：交叉比对全过程留存记录，固化值逐分支覆盖六个长度段。
- [x] 2.3（由 remove-hutool-core-api-dependency 交付） 编写种子语义用例与向后兼容回归用例：断言同一键在种子 7 下的返回值等于对"键文本拼接种子文本 7 后序列"以种子 0 计算的黄金向量值且不同于种子 0 的结果；对既有各算法常量（至少含 `CITY_HASH_64`、`XXH3_HASH_32`、缺省项）在改动前抓取样本返回值并固化为断言，验证缺省算法仍为 XXH3_HASH_32。验证：以 3.3 全绿为完成判据。
- [x] 2.4（由 remove-hutool-core-api-dependency 交付） 运行 `./gradlew :tny-game-namnspace:test` 确认当前仅因缺少实现而失败（红灯为预期，不允许出现断言之外的编译错误形态）。

## 3. 移植实现与常量接入

- [x] 3.1（由 remove-hutool-core-api-dependency 交付） 在 `com.tny.game.namespace.algorithm` 包内新建 `CityHash32` 类：逐行对照 Google city.cpp 的 32 位 CityHash 函数族移植，保留全部长度分支与循环常量；文件头携带项目标准 Apache 2.0 许可证头并注明移植自 Google CityHash（MIT 许可）出处；对外提供"字符串按键的 UTF-8 字节序列计算、返回 0 到 4294967295 区间 long"的静态方法。验证：2.2 的黄金向量用例在本类完成后即可单独编译执行并通过。
- [x] 3.2（由 remove-hutool-core-api-dependency 交付） 在 `HashAlgorithms` 中 `CITY_HASH_64` 之后新增公共常量 `CITY_HASH_32`，经既有 `hash32(...)` 私有工厂以 `HashFunctionAlgorithm` 骨架接入移植方法，`enableSeed` 取 false（种子走键拼接约定）。验证：2.1 的契约用例断言逐条通过。
- [x] 3.3（由 remove-hutool-core-api-dependency 交付） 运行 `./gradlew :tny-game-namnspace:test` 并确认全部测试通过（含 2.1 至 2.3 编写的所有用例转绿）。

## 4. 集成与收尾核验

- [x] 4.1 验证下游编译不受影响：运行 `./gradlew :tny-game-namnspace-etcd:compileTestJava` 确认集成测试对 `HashAlgorithms` 的引用照常编译（其使用的 `getDefault()` 语义未变）。
- [x] 4.2 运行 `openspec validate --change "add-city-hash-32-algorithm"` 确认规格增量与工件齐备校验通过。
