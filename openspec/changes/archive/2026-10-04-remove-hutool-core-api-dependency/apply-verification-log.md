# 实施验证记录（2026-10-04）

本记录按 operationGuidance 要求汇总 apply 阶段各轮测试与核验结果。所有抓样与比对在同一
Gradle daemon 会话内完成，中文样本经 UTF-8 编码读写，未出现编码噪声。

## 黄金向量抓取（任务 1.2，实现替换前的 hutool 形态）

- 样本键 22 个，按 UTF-8 字节长度覆盖 0、1 到 4、5 到 12、13 到 24、25 以上各分支段，
  含空串、中文键"命名空间分片"、表情符号键"🎮分片键😀"与"中文😀ABC"。
- CITY_HASH_32 种子 0 与种子 7 的全部输出固化为 CityHash32Test 的断言常量。
- 重要发现：CITY_HASH_64 的 hutool 形态与 zero-allocation-hashing `city_1_1` 在种子 0
  与种子 10 下逐值相等（22 组样本全数一致），故 64 位档实现替换实际取值零漂移；
  该等式同时写入 CityHash64Test 作为契约断言。

## 测试执行轮次摘要

1. 基线轮（hutool 形态，任务 1.4）：CityHash32Test 5 用例、CityHash64Test 4 用例、
   HashAlgorithmsRegressionTest 2 用例、TestSourceSetSmokeTest 1 用例全部通过，0 失败。
2. 移植类验证轮（任务 2.1）：CityHash32Test 增加移植类直接断言用例后共 6 用例，全部通过——
   移植类输出与 hutool 形态黄金向量逐值相等，无需仲裁。
3. 实现替换轮（任务 2.2 至 2.3，openhft 形态）：14 个正式用例全部通过，0 失败。

## 依赖面与发布面核验

- `./gradlew :tny-game-namnspace:dependencies --configuration runtimeClasspath`：`cn.hutool` 零命中。
- `generatePomFileForMavenJavaPublication` 生成的 POM：依赖仅 common-lang、codec、commons-codec、
  zero-allocation-hashing，无 `cn.hutool` 坐标。
- 下游编译：`:tny-game-namnspace-etcd:compileTestJava` 与 `:tny-game-starter-namnspace:compileJava`
  均成功（ABI 未变判定 UP-TO-DATE）。

## 收尾记录（同日补记）

- 两个临时测试类（`CityHashGoldenDumpTest.java`、`TestSourceSetSmokeTest.java`）已删除
  （`rm` 形态两次被权限拒绝后经需求方授权，改用 `find -delete` 完成）。删除后重跑
  `./gradlew :tny-game-namnspace:cleanTest :tny-game-namnspace:test`：三个正式测试类
  共 12 用例全部通过，0 失败。
- 任务 3.1 验收口径的达成情况说明：依赖声明（模块 build.gradle）、版本目录
  （libs.versions.toml）、生产源码（main 源集）与第三方许可证登记（THIRD-PARTY-LICENSES.md）
  四处均为 hutool 零命中；test 源集中仅余 CityHash32Test 与 CityHash64Test 的三处
  javadoc 溯源注释提到 hutool——那是设计 D3 明确要求的黄金向量出处留痕（事实记录，
  非依赖残留），与本条验收"openspec 账本归档事实记录除外"的同款豁免口径一致，故据此
  判定任务 3.1 达成，如实记录解释口径于此处供评审复核。
