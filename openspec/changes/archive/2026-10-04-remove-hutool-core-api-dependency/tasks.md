# Tasks

## 1. 测试先行（先于实现替换，对应规格增量 namespace-hashing 两条 Requirement）

- [x] 1.1 确认或搭建测试源集：检查 `tny-game-namnspace/src/test/java/com/tny/game/namespace/algorithm/` 是否已因进行中变更 add-city-hash-32-algorithm 建立；未建立则新建，并放入占位测试类确认根工程对测试源集与 JUnit 5 的统一托管生效（不在模块 build.gradle 加程序性配置）。验证：`./gradlew :tny-game-namnspace:compileTestJava` 编译通过。
- [x] 1.2 抓取替换前黄金向量：在 hutool 现形态尚未移除时，对覆盖长度分支各段（0 到 4、5 到 7、8 到 16、17 到 32、33 到 64、64 字节以上，另含空串与含中文表情符号样本）的键集运行现 `CITY_HASH_32`（即 hutool cityHash32）输出，固化为测试常量断言；同批键集运行现 `CITY_HASH_64` 与既有其他算法常量及缺省项，固化"其他选项不受扰动"回归断言与 64 位档旧值记录（旧值仅作记录，不写成等值断言——设计 D3）。验证：抓样清单与固化值提交进测试源码，抓取发生在实现替换之前的工作区状态。
- [x] 1.3 编写 `CityHash32Test` 契约用例：三十二位档黄金向量逐值相等、返回值落在 0 到 4294967295、`getMax()` 等于 4294967295、与 `LongHashFunction.city_1_1` 输出高低 32 位截断值至少一组不等（非折叠判别）、同键种子 7 结果等于"键拼接种子文本"后种子 0 的黄金值；编写 `CityHash64Test` 契约用例：同键同种子重复计算相等、种子 7 与种子 0 结果不同（种子原生参与）、非 ASCII 输入不抛异常且有确定值、`getMax()` 仍为长整型最大值；编写回归用例断言 1.2 固化的其他算法常量样本值不变且缺省项仍为 XXH3_HASH_32。验证：测试代码编译通过且三十二位档黄金用例在现实现（hutool 形态）下先行通过，证明向量本身与算法族一致。
- [x] 1.4 运行 `./gradlew :tny-game-namnspace:test` 并确认当前全绿（此时被测的还是 hutool 形态，64 位档以确定性断言通过、无旧值等值断言）；本组之后进入实现替换。

## 2. 实现替换与依赖移除

- [x] 2.1 在 `com.tny.game.namespace.algorithm` 包内新增 `CityHash32` 移植类：逐行对照 Google city.cpp 的 32 位 CityHash 函数族移植，保留全部长度分支（`Hash32FallbackForKey4To7Bytes` 等）与循环常量；文件头携带项目标准 Apache 2.0 许可证头并注明移植自 Google CityHash（MIT 许可）出处。验证：1.3 的黄金向量用例经该新类单独编译执行通过，与 hutool 形态输出逐值一致（不一致时停下以 city.cpp 定义仲裁）。
- [x] 2.2 替换 `HashAlgorithms` 中两个常量实现并删除 hutool 导入：`CITY_HASH_32` 改为经既有 `hash32(...)` 工厂接入 `CityHash32`（`enableSeed=false`）；`CITY_HASH_64` 改回 `openFht64(LongHashFunction::city_1_1, true)` 形态；删除第 18 行 `import cn.hutool.core.util.HashUtil;`。验证：`./gradlew :tny-game-namnspace:test` 全部用例通过（含 1.2 固化断言）。
- [x] 2.3 移除依赖声明与目录条目：删除 `tny-game-namnspace/build.gradle` 的 `api libs.hutoolCore` 行与 `gradle/libs.versions.toml` 的 `hutoolCore` 条目（先删代码引用再删声明，保持声明与消费一致）。验证：`./gradlew :tny-game-namnspace:compileJava` 与 `./gradlew :tny-game-namnspace:test` 通过，且 `./gradlew :tny-game-namnspace:dependencies --configuration runtimeClasspath` 输出中 `cn.hutool` 零命中。

## 3. 治理面清账与下游核验

- [x] 3.1 从 `THIRD-PARTY-LICENSES.md` 移除 hutool 的表格行与许可证说明段落。验证：全仓构建文件、源码与文档（openspec 变更历史归档中的事实记录除外）检索 `hutool` 零命中。
- [x] 3.2 核验下游编译不受影响：运行 `./gradlew :tny-game-namnspace-etcd:compileTestJava` 与 `./gradlew :tny-game-starter-namnspace:compileJava`，确认 `EtcdNamespaceExplorerIT` 与装配面照常编译。
- [x] 3.3 核验发布门禁与账实一致：运行 `./gradlew :tny-game-namnspace:generatePomFileForMavenPublication`（任务名以模块实际发布配置为准，找不到时以 `./gradlew :tny-game-namnspace:tasks --group publishing` 查名）并确认生成的 POM 不含 `cn.hutool` 坐标；`./gradlew :tny-game-namnspace:test` 全绿收尾。

## 4. 账本衔接

- [x] 4.1 处置两变更重叠：将进行中变更 add-city-hash-32-algorithm 的任务组 2、3 中与本变更重复的移植与接入任务标注为"由 remove-hutool-core-api-dependency 交付"（用 openspec 更新流程改写其 tasks.md 注记），并在其 proposal 留痕说明实现收口路径；随后运行 `openspec validate "remove-hutool-core-api-dependency"` 与 `openspec validate "add-city-hash-32-algorithm"` 确认两册各自有效。
