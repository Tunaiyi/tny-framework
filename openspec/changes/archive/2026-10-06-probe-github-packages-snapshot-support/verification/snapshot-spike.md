# 快照镜像可行性实测结论（probe-github-packages-snapshot-support）

实测日期 2026-10-05。场地：一次性私有沙箱仓库 `Tunaiyi/gpr-snapshot-spike`（任务 6.2 删除）。生产仓库与主账本在本调查中零触碰。原始取证文件均在本目录，逐项论断给出文件级引用。

## 结论一句话

**上一轮调查得出的"Gradle 快照在 GitHub Packages 上无法被消费方解析"结论被本轮真实消费端实测推翻**：注册表服务端会自动为 Gradle 上传的快照维护完整的 unique snapshot 元数据，Gradle 消费端可解析同一快照坐标并按发布滚动取到最新构建（内容级验证）。主账本能力 `github-packages-mirror` 的需求"快照构件不进入镜像通道"的事实依据不再成立，其"是否保留"从技术判定转为价值判定。

## 事实分层：上一轮误判在哪里

上一轮（归档于 `openspec/changes/archive/2026-10-05-add-github-packages-channel/verification/sandbox-probe.md`）的两条相关结论，一条仍成立、一条被推翻：

1. 仍成立：快照构件以带时间戳的文件名存储，不存在字面名 `-SNAPSHOT.jar` 文件（`producer-layout.txt` 末段复验：字面名 404）。
2. **被推翻**：其结论 2 声称"注册表不生成含快照时间戳块的元数据"——原因是当时只检查了 artifact 级 `maven-metadata.xml`（该级确实无 `<snapshot>` 块），漏检了 Maven 快照解析真正读取的**版本目录级** `maven-metadata.xml`。本轮直查该级文件（`producer-layout.txt`）：注册表在 Gradle 发布快照后自动生成 `<snapshot><timestamp>20261005.075156</timestamp><buildNumber>1</buildNumber></snapshot>` 与逐扩展 `snapshotVersions` 映射（观察到 50 条记录，含 jar/pom/module/四组校验和），内容与实际上传的时间戳文件一致。

教训成文：**对注册表行为的"消费方必然失败"类断言，必须以真实构建工具的解析行为为准，curl 探测只能证明单个 URL 的存在性，不能证明协议层成败。**

## 逐项证据（本轮实测）

| 编号 | 论断 | 证据文件与要点 |
|---|---|---|
| S1 | Gradle 发布快照成功且服务端自动维护目录级 unique 元数据 | `producer-layout.txt`：0.1.0-SNAPSHOT 发布 BUILD SUCCESSFUL；目录级元数据含 snapshot 块与 snapshotVersions |
| S2 | 同一快照坐标连续发布，服务端时间戳与 buildNumber 自动推进，旧构建记录共存 | `producer-layout.txt`＋会话记录：0.2.0-SNAPSHOT 三连发后元数据 `<timestamp>20261005.075833</timestamp><buildNumber>2</buildNumber>`（build3 后为 3），build1/build2 时间戳文件均 302 可取回 |
| S3 | Gradle 消费端解析快照坐标成功，请求序列为"目录级元数据→时间戳文件" | `exp1-gradle-consumer.txt`：HEAD/GET `0.3.0-SNAPSHOT/maven-metadata.xml` → GET 时间戳 pom/module/sha1 → HEAD 时间戳 jar → `RESOLVED` |
| S4 | 滚动新鲜度：内容变更的第三次构建被消费端取到 | 消费端 `--refresh-dependencies` 解析 0.2.0-SNAPSHOT，解包 jar 的 class 常量含 `snap-build3` 字符串（build3 的源码改动），同轮 build1/2 无该串 |
| S5 | 客户端自造 unique 布局被服务端接受且基本保留 | `exp2-put-log.txt`：pom/jar/metadata 三 PUT 全 200，响应原文 "Successfully registered maven upload"；服务端保留客户端 timestamp 与 buildNumber，仅重写 `lastUpdated` |
| S6 | 自造布局可被消费且可继续滚动 | `exp2-rollover.txt`：第二次 PUT（buildNumber=2）后元数据推进，消费端解析命中 `092000-2` 文件组 |
| S7 | 正式版对照组解析正常 | `exp1-gradle-consumer.txt` 对照组段＋`producer-layout.txt`：0.1.0 字面 pom/jar 302，Gradle 解析成功 |
| S8 | Maven 命令行消费端解析（容器 maven:3.9，镜像重拉后） | `exp1-maven-consumer.txt`：`dependency:get` 对自造布局坐标 `0.3.0-SNAPSHOT` 成功——先 Download 目录级 maven-metadata.xml（855 B），再 Download 滚动后的 `0.3.0-20261005.092000-2.pom/.jar`，BUILD SUCCESS；正式版对照 `0.1.0` 亦 BUILD SUCCESS。Maven 协议与 Gradle 协议在两端独立确认（此前一次取证失败系镜像容器挂死的环境故障，重拉后完成） |

## 判定表落格与裁决建议

design D6 判定表落 **E-A**："服务端接受 unique 快照形态且消费端（Gradle）可解析"。技术上开通快照镜像通道的剩余改动只有：插件守卫删去版本子句、CI 增加快照触发路径（沿用"镜像仅由 CI 执行"拍板则需为开发线快照设计 CI 触发形态）。

但**建议维持快照不进镜像的现状，不为本结论单独立项开通**，理由三条：

1. 价值缺口：GitHub Packages 对公开包同样强制凭据下载，外部消费者取开发线快照走 Maven Central 快照仓（免凭据、已开通、90 天自动清理）体验严格优于镜像；镜像快照唯一受益者是"只信 GitHub 域内来源"的极少数消费场景。
2. 账本负担：注册表对时间戳旧构建无清理记载（S2 观察到 build 记录共存），镜像快照会随每次开发线发布单调累积不可回收的存储条目，与"尽力镜像"的定位（权威账本在内网仓）相悖。
3. 纪律成本：为快照开通 CI 通道需要重新设计开发线触发形态（此前拍板已否决 push 自动触发），复杂度不成比例。

主账本需求"快照构件不进入镜像通道"建议**保留义务、改写依据**：其"消费方不可解析"的事实从句已被证伪，真实理由是上述价值判断。这一改写属规格维护（不影响任何 SHALL/MUST 义务行为），应由下一个触碰该能力的变更顺带完成，或单独立极小的文档性变更；本调查变更按纪律不代改主账本。

## 与主账本条文的对账

| 主账本句 | 状态 |
|---|---|
| 需求"快照构件不进入镜像通道"正文中"注册表不生成含快照时间戳块的元数据，也不提供目录列举，消费方按快照坐标解析必然失败，上传产物是不可被任何构建工具发现的孤儿文件" | **失实**（本轮 S1 至 S3 推翻），待按上节改写为价值理由 |
| 同需求义务句"快照形态的构件 MUST NOT 进入 GitHub Packages 镜像通道" | **建议维持**（价值判定成立） |
| 同需求场景"开发线即便凭据在位也不声明镜像目的地"与"镜像仓库中不出现快照形态版本" | 不受影响（义务不变则场景不变） |
| 归档版 sandbox-probe.md 结论 2 | 历史存证不改写，以本文件为更正后终版（上文"事实分层"节已声明勘误关系） |
