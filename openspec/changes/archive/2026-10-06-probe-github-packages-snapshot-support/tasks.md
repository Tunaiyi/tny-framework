# Tasks

> 任务规则适配注记：本变更是纯实测调查（skip_specs），不修改生产代码、公共 API 或协议，因此"JUnit 5 测试任务排在实现任务之前"与"每组装完跑 `./gradlew :受影响模块:test`"两条无对应物；各组的收口判据是该组实验产出的取证文件（请求序列、HTTP 状态、元数据字节对比）落到本变更目录 `verification/`。全部真实上传只发生在一次性沙箱 `Tunaiyi/gpr-snapshot-spike`，生产仓库与主账本零触碰。凭据纪律遵循 design D7：令牌只经环境变量临时注入，任何落盘记录先脱敏。

## 1. 沙箱与连通前置

- [x] 1.1 经用户确认后创建一次性私有沙箱仓库 `Tunaiyi/gpr-snapshot-spike`，验证：`gh repo view` 返回 PRIVATE 可见性。
- [x] 1.2 连通性前置检查：本机对 `https://maven.pkg.github.com` 的 GET 返回 401（可达且要求认证）；`docker run --rm maven:3.9-eclipse-temurin-21 curl -s -o /dev/null -w '%{http_code}' https://maven.pkg.github.com` 返回 401（容器内网络可达），验证：两个状态码记录入 `verification/preflight.txt`；容器不通即暂停上报，不带病进组 2。

## 2. 生产侧落库与对照组

- [x] 2.1 在 `/tmp/gpr-snap-spike-producer` 建最小 Gradle 发布工程（组号 `io.sandbox.snap`，构件 `gpr-snap`，守卫形态对齐生产插件），发布 `0.1.0-SNAPSHOT` 与 `0.1.0` 各一次（凭据经 `ORG_GRADLE_PROJECT_*` 临时注入），验证：两次发布 BUILD SUCCESSFUL；`gh api users/Tunaiyi/packages?package_type=maven` 出现沙箱包。
- [x] 2.2 落库形态存档：GET 沙箱构件的 artifact 级 `maven-metadata.xml` 与快照目录清单（含 Gradle 实际上传的时间戳文件名样本），存 `verification/producer-layout.txt`。验证：文件含两级元数据全文与至少一组时间戳文件名实例。
- [x] 2.3 组末验证：正式版本对照坐标 `0.1.0` 的字面 `.pom` 与 `.jar` 携凭据 GET 返回 200/302（对照组成立），记录同文件。

## 3. 实验一：真实消费端解析普通快照

- [x] 3.1 Gradle 消费端：`/tmp/gpr-snap-spike-consumer-gradle` 工程声明沙箱仓（属性注入凭据），自定义任务对 `io.sandbox.snap:gpr-snap:0.1.0-SNAPSHOT` 执行 `configuration.resolve()`，`--info` 全量日志存档并脱敏，验证：`verification/exp1-gradle-consumer.txt` 含失败（或成功）的请求序列——每一步请求的 URL 与响应状态码。
- [x] 3.2 Maven 消费端：Docker 容器挂载一次性 `settings.xml`（server 凭据，`/tmp` 内、结束即删），执行 `mvn -B dependency:get -DremoteRepositories=https://maven.pkg.github.com/tunaiyi/gpr-snap-spike -Dartifact=io.sandbox.snap:gpr-snap:0.1.0-SNAPSHOT`，验证：`verification/exp1-maven-consumer.txt` 含 Maven 的元数据读取与文件请求序列及最终错误形态。
- [x] 3.3 对照组复核：两消费端对正式版本坐标 `0.1.0` 各解析一次应成功，验证：`exp1-*.txt` 中对照组请求以解析成功结束（排除消费端工程自身配置问题）。
- [x] 3.4 组末验证：把实验一结论（哪个环节失败、失败形态与 curl 推断是否一致，有无新细节如重试序列或降级行为）摘要写入 `verification/snapshot-spike.md` 的实验一节。

## 4. 实验二：unique 快照布局与服务端接管

- [x] 4.1 手工 PUT unique 布局（design D4）：从 2.1 产物复制 jar/pom 为时间戳文件名，连同含 `<snapshot><timestamp><buildNumber><lastUpdated>` 的目录级 `maven-metadata.xml` 逐个 PUT 至 `0.2.0-SNAPSHOT` 目录（新坐标以免与实验一混淆），验证：`verification/exp2-put-log.txt` 记录每次 PUT 的 URL、HTTP 状态、响应摘要。
- [x] 4.2 服务端接管三态判定（design D5）：PUT 后立即 GET 目录级元数据全文存档并与发送内容逐字节对比，验证：`verification/exp2-metadata-takeover.txt` 明确落"保留原样/改写内容/拒绝"三态之一，附对比差异。
- [x] 4.3 消费端复核：两消费端对 `0.2.0-SNAPSHOT` 坐标重跑解析（同 3.1、3.2 手法），验证：`verification/exp2-consumers.txt`（4.3 当时结果随两消费端命令执行取证，收口时从 `exp1-gradle-consumer.txt` 与 `exp1-maven-consumer.txt` 归集成文）含两端请求序列与成败判定。
- [x] 4.4 滚动轮：第二次 PUT 新时间戳、buildNumber 递增的布局（模拟持续发布），GET 元数据观察滚动行为（服务端是否递增或保留），消费端解析应命中最新构建，验证：`verification/exp2-rollover.txt` 记录两轮对比。
- [x] 4.5 组末验证：实验二结论按 D6 判定表落入 `verification/snapshot-spike.md`（E-A、E-B、E-C 之一），每个论断有对应文件行级引用。

## 5. 结论报告与裁决建议

- [x] 5.1 完成 `verification/snapshot-spike.md`：证据分层总表（本轮新增 vs 上一轮既有）、D6 判定表落格、裁决建议（E-A 时列出开通快照通道需要回答的后续设计问题与预期成本；E-B/E-C 时给出"主账本现行需求获得终局实证"的注记文案建议），验证：报告每段自解释，引用文件全部存在于 `verification/`。
- [x] 5.2 组末验证：向用户呈报结论与建议路线，明确本变更不实施任何开通动作；后续路线（另立开通评估变更，或在主账本登记终局实证注记）由用户裁决。

## 6. 清理

- [x] 6.1 清理：一次性工程目录与含令牌文件已由用户于 2026-10-05 全部删除（`/tmp/gpr-mvn-settings.xml`、`/tmp/gpr-snap-spike-producer`、`/tmp/gpr-snap-spike-consumer-gradle`、`/tmp/unique-put`、`/tmp/m2cache`、`/tmp/gpr-sandbox`、`/tmp/probe-logs` 均不存在，验证 `ls` 报 No such file）；本变更 `verification/` 全部文件扫描 `ghp_` 零命中；发布者本机用户级 gradle.properties 镜像凭据键核对——双属性时代残留两行死配置（第 36 至 37 行 githubPackagesUsername/githubPackagesToken，已不被任何代码读取）已提请用户清理或轮换，属用户本机文件不由变更代为删除。
- [x] 6.2 清理：沙箱仓库 `Tunaiyi/gpr-snapshot-spike` 已删除（2026-10-05 复验 `gh repo view` 报 Could not resolve to a Repository，`gh api users/Tunaiyi/packages?package_type=maven` 返回空数组，探针包随仓库消失）。
