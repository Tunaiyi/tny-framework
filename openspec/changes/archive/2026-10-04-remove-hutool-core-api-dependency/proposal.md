# Proposal

## Why

`tny-game-namnspace` 当前以 `api libs.hutoolCore` 把第三方库 hutool-core（版本 5.8.25）声明进命名空间哈希模块的编译与发布面，`HashAlgorithms` 中的 `CITY_HASH_32` 与 `CITY_HASH_64` 两个公共常量经由 hutool 的 `HashUtil` 计算。`api` 声明使 hutool-core 进入发布 POM，下游业务工程即使不选用 CityHash 选项也要在编译类路径与依赖树上承载这个此前不在框架依赖组合中的大型工具库，扩大了传递依赖面与许可证面。此前完成的 namespace-hashing 设计决策（add-city-hash-32-algorithm 变更 D1）已裁定 CityHash32 应采用模块内移植、不引入外部依赖，当前 hutool 形态与该设计相悖，需要收口。

## What Changes

- 移除 `tny-game-namnspace/build.gradle` 中的 `api libs.hutoolCore` 依赖声明；全仓检索确认该库在仓库内只有 `HashAlgorithms.java` 一个引用点，移除后版本目录 `gradle/libs.versions.toml` 的 `hutoolCore` 条目无任何消费者，一并清账删除。
- `CITY_HASH_32` 改为使用在 `com.tny.game.namespace.algorithm` 包内新增的 CityHash32 移植类（逐行对照 Google CityHash 参考实现 city.cpp 移植，文件头保留 MIT 出处声明与本仓库标准 Apache 2.0 许可证头）。这正是先前 add-city-hash-32-algorithm 变更 design.md 决策 D1 与 D4 已裁定的路线。
- `CITY_HASH_64` 改回使用模块既有 api 依赖 zero-allocation-hashing 的 `LongHashFunction.city_1_1` 带种子实现，恢复该常量在引入 hutool 之前的实现形态。
- 同步从 `THIRD-PARTY-LICENSES.md` 移除 hutool 条目（表格行与许可证说明段落）。
- 取值口径：经需求方裁定，两个常量的哈希值允许与 hutool 中间形态不同（当前尚无下游工程实际采用这两个常量的哈希结果），但新实现必须各自与其声称算法的 Google 参考实现一致，且替换后哈希行为对外保持确定性与取值域契约。本变更不改动 `HashAlgorithm` 接口、不改动其他算法常量与缺省算法。因下游无既有采用，本变更按行为兼容处理，不标注 BREAKING（判定依据与残余风险记录于 design.md 的 Compatibility Impact）。

## Capabilities

### New Capabilities

（无——哈希算法行为契约已由进行中变更 add-city-hash-32-algorithm 声明为新能力 `namespace-hashing`；本变更对该能力追加发布形态与实现接替两条新需求。）

### Modified Capabilities

- `namespace-hashing`（增量追加，见 specs/namespace-hashing/spec.md）：新增"发布依赖面不携带非选用算法库"与"CityHash 选项实现接替后维持各自算法参考语义、取值漂移不受零变化承诺保护但确定性与取值域不变"两条 Requirement。注意：该能力规格尚未归档进主账本（上一变更仍在实施前状态），两个变更的规格增量合入主账本时由归档顺序保证叠加。

## Impact

- 受影响代码：`tny-game-namnspace` 的 `HashAlgorithms.java`（两处常量实现替换、删除 hutool 导入）、新增 CityHash32 移植类、`tny-game-namnspace/build.gradle`（删一行依赖）、`gradle/libs.versions.toml`（删目录条目）、`THIRD-PARTY-LICENSES.md`（删 hutool 登记）。
- 受影响下游模块：`tny-game-namnspace-etcd`（api 依赖本模块，编译面不再看到 hutool 类；其源码未引用 hutool，编译不受影响）；`tny-game-starter-namnspace`（门面装配，发布件传递依赖自然收缩）。
- 发布面：本模块 POM 与 Gradle Module Metadata 不再声明 `cn.hutool:hutool-core`，下游依赖树相应节点消失。构建侧 `tny.module-checker` 配置期对账守卫与发布门禁 `checkPublishPrerequisites` 不受影响（移除依赖只减不增，无需登记豁免）。
- 版本目录：`hutoolCore` 条目删除后，若未来重新引入需按依赖治理册重新评审。
- 测试：延续 add-city-hash-32-algorithm 任务组 1 的测试源集建设（该变更与本变更共用 `tny-game-namnspace` 的 `src/test/java` 与 `CityHash32Test`；实施顺序上建议先应用本变更或合并实施，避免同一文件冲突——记录于 design.md）。
