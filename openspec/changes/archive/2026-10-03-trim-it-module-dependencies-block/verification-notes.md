# 验收记录：trim-it-module-dependencies-block

## 收口裁决（2026-10-03，3.1 收口时点）

用户裁决本册**带痕收口**：自身判据（四件零差异、行数界线、配置评估）全部闭合，探针反证把 clean build 的编译红完整归因于外部 commons-io 升级，3.1 据此勾选；后续条款——待并行会话把 common-io 源码适配 commons-io 2.14 后，补跑一次 `clean build` 全绿并把结果记入本归档件的 verification-notes.md 追加段（先例：bench 链归档后回归补记）。

## 结论（截至本段落纸时点）

本册 bundle 收编经四件判据在新基线下全部零差异：`integrationImplementation` 解析集、`integrationRuntimeClasspath` 解析集、`integrationTest --dry-run` 任务图、`tasks --all` 剔噪清单。模块行数 85 回到 80（满足需求七 ≤80 界线；比 design 预估 77 多三行，因绑定族来由说明按项目文字规则写全为两行加指回一行，未违例）。`./gradlew clean build` 全绿一项被**外部改动阻塞**：并行会话在本册执行期间把 `commons-io` 从 2.8.0 升级到 2.14.0（未提交），该升级令 `tny-game-common-io` 的 `LocalWordsFilter.java:176` 冗余 `catch (IOException)` 因 API throws 签名收窄而编译失败——经反证探针确证与本册零关（临时回退版本到 2.8.0 即绿，随即恢复 2.14.0）。3.1 的勾选方式待用户裁决。

## 时序全链（按实际发生）

1. **1.1 首抓（本册改动落地前）**：四件基线存 baseline/（deps-*-before.txt 535 行 runtime、tasks-all-before.txt 3457 行、catalog 六键定位件）。首抓窗口 `commons-lang3` 仍为 3.12.0、netty 仍为 4.1.104。
2. **2.1 实施与两轮报红（常驻探针如期兜住）**：
   - 第一轮红：bundle 成员写成内联表 `{ id = ... }` 被 Gradle 8.5 拒绝（"Bundles can only contain references to existing library aliases"）——正确形态是**别名裸字符串数组**（tasks 2.1 示例文本的 `{ alias = ... }` 同错，一并勘误）。
   - 第二轮红：`libs.containerStack` 属性不存在——bundle 访问器挂 **`libs.bundles.`** 命名空间，更正为 `libs.bundles.containerStack` 后 `projects -q` 绿。
3. **3.1 首跑与第一次停止条款触发**：`integrationImplementation` 零差异；`integrationRuntimeClasspath` diff 35 行对全部为 `commons-lang3:3.12.0→3.18.0`——取证发现工作树混入**外部未提交改动**（toml 的 jprotobuf/commons-io/commons-lang3 三坐标升级、IT 模块 `configurations.all→configureEach`、java-module 一行缩进）。按 D3"diff 非空无论方向即停"回用户，裁决"升级属合法，新现状重基"。
4. **重基第一轮（before2/after2）再被外部击穿**：runtime 的 diff 收敛为 bundle 顺序问题（初次实施把 log4jCore 排前，偏离原声明顺序；bundle 拼接顺序进入声明面与 POM 顺序，按"逐项一致"红线改回原顺序 starter/autoconfigure/starterLog4j2/tcJunit/tcMongodb/log4jCore）；但重跑窗口外部又把 netty 连推 4.1.104→4.1.135→4.1.137（`gradle.properties`）。
5. **剥离自伤一次（如实记录）**：用自写正则剥离 [bundles] 节时，非贪婪匹配终止于节头 `[bundles]` 自身的右括号，把数组体遗留在 `[libraries]` 节内，catalog 校验报红"Alias definition 'containerStack' is invalid"——改为按精确字符串块替换并断言键名残留后剥离干净，`projects -q` 绿。教训：剥离件用精确块匹配，断言要查键名而非仅查节名。
6. **重基第二轮（before3/after3，判据锚定于此）**：剥离态（HEAD+外部全部改动，netty 4.1.137、commons-io 2.14.0）抓 before3（runtime 532 行）→重贴本册两文件改动→抓 after3→**四件全部零差异**；比对窗口内 `git diff --stat` 复核外部无新动作。
7. **clean build 与反证探针**：`:tny-game-common-io:compileJava` 配置后编译红（9 秒即败）；临时回退 commonsIo 至 2.8.0 → 该任务绿；恢复 2.14.0。红因锁定外部升级，本册改动（catalog [bundles] 节 + IT 模块依赖语句形态）不触及 common-io 源码与其依赖清单。

## 判据与证据件对照

| 判据 | 基线件 | 复跑件 | 结果 |
|---|---|---|---|
| integrationImplementation 解析集 | deps-…-before3.txt | deps-…-after3.txt | 逐行零差异 |
| integrationRuntimeClasspath 解析集 | deps-…-before3.txt（532 行） | deps-…-after3.txt | 逐行零差异 |
| integrationTest --dry-run 任务图 | it-dryrun-before3.txt | it-dryrun-after3.txt | 零差异 |
| tasks --all 剔噪清单 | tasks-all-before3.txt（3457 行） | tasks-all-after3.txt | 零差异 |
| bundle 成员与移除语句逐项一致 | catalog-members-before.txt | 模块现文本 | 六键对六成员，声明顺序原样 |
| 行数界线 | 85 行（before） | 80 行（after） | ≤80 达成 |
| 配置期评估 | — | projects -q | 绿（含两轮报红的常驻探测记录） |
| clean build 全绿 | — | — | **被外部 commons-io 升级阻塞**（反证探针在案） |

首任基线件（before/after、before2/after2 共十件）保留在案不删，作为外部漂移与顺序修正的完整时间线证据；判据以 before3/after3 为准，前两轮件的形态差异均已在第 3、5 条归因。

## 噪声与口径记录

- 本册起 daemon 工具链扫描告警行（"Invalid Java installation found at …jdk1.8.0_311…"）出现位置随缓存状态漂移（归档册 merge-publish-gate 先例所述），before3/after3 双侧对称剔除；此为对既有剔噪口径的增补，与 config.yaml 权威口径"剔除 daemon 噪声行"同义扩展。
- 转存型与逐行判据均按 openspec/config.yaml context"零差异验收基线抓样口径"执行：全程同一 daemon、UTF-8 钉定（本窗口零问号）。

## 归档后补跑记（2026-10-03，兑现本册补跑条款）

commons-io 2.14 升级引入的 `LocalWordsFilterTest` 测试红已由变更 `adapt-commons-io-214-word-filter` 修复（守卫恢复 + JDK 逐行读取，其验收件记录全仓 `./gradlew clean build` 恢复全绿，1 分 30 秒）。本册归档时立下的"外部适配完成后补跑全绿记入归档件"条款就此兑现；补跑证据归属与全文见 `openspec/changes/adapt-commons-io-214-word-filter/verification-notes.md`。
