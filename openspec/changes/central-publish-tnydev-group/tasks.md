# Tasks

## 1. Central 命名空间验证（前置闸口）

- [ ] 1.1 在 Central Portal 提交 `com.tnydev.game` 命名空间验证：按其指引在 `tnydev.com` 域名添加验证记录并确认生效（验证：Portal 界面显示该命名空间 verified；未通过则停止后续任务并回 proposal 重新定组号）

## 2. 组号单一事实源收敛

- [x] 2.1 先写组号对账断言：构建配置期校验所有发布构件组号等于根声明点值，不等即判红并列出违例模块（对应 spec"模块硬编码组号被判红"；验证：临时在某模块加一行错误组号声明触发判红后移除）
- [x] 2.2 `build.gradle:88` 组声明改为 `com.tnydev.game`，删除 43 个模块首行的 `group "com.tny.game"` 硬编码（验证：2.1 断言全仓通过且 `./gradlew :tny-game-bom:properties -q` 输出组号为新值；grep 全仓 `group "com.tny.game"` 零残留）
- [x] 2.3 运行 `./gradlew :tny-game-net:test` 与 `./gradlew :tny-game-common-lang:test` 确认组号改动无回归（验证：两条命令通过，结果摘要记入变更目录）

## 3. Central 准入完整性（POM 与产物）

- [x] 3.1 POM 许可证字段保持 Apache License 2.0（2026-10-02 全仓源文件头已由 Mulan PSL v2 切换为 Apache License 2.0，两处声明已一致），补齐 name、description、url、developers、scm 必填字段（验证：任一模块 `publishToMavenLocal` 后检查生成的 POM 六字段齐备且 license 与源文件头一致）
- [x] 3.2 为发布模块挂接 sources jar 与 javadoc jar 标准生成；先在 `tny-game-net` 与 `tny-game-common-lang` 两模块实测 javadoc 生成的 doclint 报错面，据实决定全量严格或降级豁免并在 design 决策 D6 下记录（对应 spec"缺签名或缺附属构件被前置拒绝"的构件前提；验证：代表性模块产物含主 jar、sources、javadoc、POM 四件套；豁免决定若发生须写明范围）
- [x] 3.3 配置 GPG 签名链路：本地以测试密钥完成一次可签可验的闭环，私钥文件确认不入库（验证：对 3.2 产物执行签名与校验命令通过；`git status` 确认无私钥文件）
- [x] 3.4 运行 `./gradlew :tny-game-net:test` 与 `./gradlew :tny-game-common-lang:test` 确认 3.1-3.3 无回归（验证：两条命令通过，结果摘要记入变更目录）

## 4. Central 通道接入与门禁联动

- [x] 4.1 引入 Central 上传通道：nmcp（com.gradleup.nmcp + aggregation 1.6.2，Gradle 团队维护，"消费既有 publication 不接管模型"）；双仓声明为 Nexus 仓库（既有）+ nmcpAggregation.centralPortal（凭据 centralUsername/centralPassword→mavenCentralUsername/Password 用户级属性，仓库零明文）（验证：:tny-game-net:tasks 双通道任务面可枚举；git status 无凭据入库；实施记录见 design D3 补记，vanniktech 接管模型经对照后否决）
- [x] 4.2 Central 通道参与条件与既有门禁同源：centralCheck 分支守卫（非发布分支拒绝并给出 spec 文案，dev 线实测红、临时 release 分支实测绿）；快照通道定案（spec R4 修订版）——开发线快照经 centralSnapshots 原生仓分发 Central（与内网同经 publish），nmcp 自带快照任务禁用防双发（禁用文案实测可见）；聚合任务图 54 模块 staging+check 挂接（-m 演练）；快照真实首探见任务 7.2
- [x] 4.3 Central 推送前置完整性校验：centralCheck（分支+凭据）+ publishAggregationToCentralPortal.doFirst 产物完整性（每模块 POM/jar/sources/javadoc 齐备且逐文件签名配对，缺失逐项列出拒绝于传输前；校验逻辑在自建阶段已对 52 模块 621 文件实测零缺失，doFirst 挂载随首发射线复验）
- [ ] 4.4 双仓独立与幂等验证：Nexus 通道代码路径零改动（独立性成立）；Central 重试幂等由 nmcp 承担——验证收口于 7.1 首发后的重跑演练（对已发布版本重复执行 centralUpload，确认"已存在按等价成功"与错误信息指明通道）

## 5. GitHub Actions 发布工作流（前置：1.1 完成）

- [ ] 5.1 新建发布工作流：GitHub release 事件触发，checkout 加 setup-java 加 setup-gradle，执行 `./gradlew publish`；Central token 与 GPG 私钥经 secrets 注入并确认脱敏（对应 design D3；验证：workflow 文件 lint 通过；以仓库内测试 tag 触发 dry-run 版走通到"推送前校验"步骤并停止，不外发真实制品）
- [ ] 5.2 工作流失败路径演练：缺标签、缺签名、缺附属件三种注入各触发对应拒绝且日志指明通道与缺失项（对应 spec release-versioning 修改的"Central 通道共用同一前置校验"；验证：三条注入演练的日志摘要记入变更目录）

## 6. 文档与下游坐标联动

- [x] 6.1 `docs/site/index.html` 与 README 示例坐标改为 `com.tnydev.game`（含 BOM 写法示例），并在 docs 记录组号迁移公告与"包名不变"决策（验证：grep 两文件无旧组号残留；公告段落可读）
- [x] 6.2 `docs/release-process.md` 补 Central 双仓发布步骤、失败重试语义与 secrets 清单；`.claude/commands/tny/release.md` 的标准发布第 3 步同步双仓事实（验证：文档与 spec 双仓/快照/插件排除条款逐条对照无缺漏）

## 7. 首版发布验证

- [ ] 7.1 经 `/tny:release` 流程产出首个正式版并按新工作流发布 Central 与内网双仓；在 Central 检索确认该版本全部发布模块可见且 POM 字段完整，将检索证据记入变更目录（验证：Central 版本号可检索、模块数与待发布清单一致；本次发布即本变更的最终验收）

- [x] 7.2 快照 Central 首探（spec R4 修订验收）：开发线对单模块执行 centralSnapshots 仓库发布任务，随后以 curl 核验 `central.sonatype.com/repository/maven-snapshots/com/tnydev/game/tny-game-common-lang/` 出现 `5.7.x-SNAPSHOT` 目录与时间戳产物，证据记入变更目录
