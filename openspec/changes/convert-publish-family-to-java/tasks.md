# Tasks

## 1. 闸门核对（全组通过前，后续任何任务组不得启动）

- [ ] 1.1 确认 redesign-devline-integration-model 已归档：`openspec list --json` 活动目录无该册、`openspec/changes/archive/` 存在其编号目录，且主规格 `openspec/specs/` 中 release-versioning、central-publishing、branch-integration-gates 三能力现文含该册差量入账的需求（逐能力抽查一条该册新写需求名）
- [ ] 1.2 确认 convert-orchestration-to-java 已归档（`GitFlow`/`GitCli` 为 Java 类：`ls buildSrc/src/main/groovy/tny/convention/` 无二者），其正则选点判例表 apply-notes 可读
- [ ] 1.3 重读五脚本与三能力现文与本册 design 锚点事实做差比对：差异逐条记入本册 apply-notes"现文回写"小节，并修订拆类表与文案引用（以现文为准，design 不改快照句）；无差异也须记录"零差异复核"结论
- [ ] 1.4 重抓本册新基线入 `baseline/`（Python 正则剔噪口径）：全量任务图、`publish --dry-run` 两线任务名清单、`centralUpload -PdryRun` 与 `centralBundle` 干跑输出、三线 POM 与 BOM POM、`-PgitExe` 缺席下 gate 记忆化冒烟记录、warm 耗时三连；README 锚定当前 HEAD
- [ ] 1.5 把判例表发布族移交行（约十处）转录本册 apply-notes 并逐行标注 matches/find 选定；验证：与编排线册移交表行号 grep 交叉核对计数一致

## 2. tny.publish.gate 同名 Java 化（测试先行）

- [ ] 2.1 先写红用例：`PublishGateCheckTest` 七段各一绿一红（形态、路由、占号黑名单、标签存证、下一补丁号、依赖形态豁免清单、fail-closed 远端不可达），断言问题清单文案含判红对象；`PublishTaskNamingTest` 覆盖谓词全串/前缀/ToMavenLocal 链排除三向；记录实现前红态
- [ ] 2.2 实现 `PublishGateCheck`、`PublishTaskNaming` 与 `PublishGatePlugin`（七段合流顺序逐字、根级记忆化三元组键语义保持——缓存载体注明、`checkPublishPrerequisites` 名字串惰性挂接教训注释随迁、`GitFlow` 类型化）；同提交：注册行新增＋删除 `tny.publish.gate.gradle`（`git rm` 后不再 add 该路径）；验证：2.1 全绿、描述符断言、`./gradlew :tny-game-net:publish --dry-run` 任务图对 1.4 新基线零差异

## 3. tny.publish 同名 Java 化与原子对

- [ ] 3.1 实现 `PublishPlugin`：首段按类应用 `PublishGatePlugin`（原子对，根与 BOM 显式 gate 行为幂等重言的说明注释）；凭据 ext 四键、谓词挂接 matching＋configureEach 惰性、doFirst 属性断言文案逐字；同提交换轨（注册＋删脚本）；验证：`publish --dry-run` 两线清单零差异、BOM `checkPublishPrerequisites` 挂接边在位、供给断供破坏探针复跑（注释根 gate 行不再致 BOM 半配对——原子对生效实证，探针后还原）

## 4. tny.publications 同名 Java 化

- [ ] 4.1 实现 `PublicationsPlugin`：mavenJava 配置方语义、签名挂接（凭据对守卫）、POM 元数据段、未命名 maven 仓与 centralSnapshots 声明、nmcp 模块插件按 id 应用（裸 id，类路径 buildSrc 供给前的现行为：此时 nmcp 仍由根声明行供给——本组不动根）；`gitFlow` 与 publish ext 四键读取次序注释；同提交换轨；验证：三线 POM 对 1.4 基线逐字节一致、`generatePomFileForPluginMavenPublication` 面一致

## 5. tny.github-packages 同名 Java 化

- [ ] 5.1 实现 `GithubPackagesPlugin`：githubPackages 命名仓注入、凭据成对守卫、镜像任务面逐字；同提交换轨；验证：`publishAllPublicationsToGithubPackagesRepository` 在两线与 BOM 的任务名清单零差异、未成对凭据机器上守卫报错文案等值（以无凭据环境冒烟）

## 6. tny.central 同名 Java 化与 nmcp 供给迁移（四件同提交）

- [ ] 6.1 先写红用例：`CentralIntegrityCheckTest`（聚合成员缺 mavenJava/jar/sources/javadoc 各判红与齐备放行）
- [ ] 6.2 实现 `CentralPlugin`：内部 `apply("com.gradleup.nmcp.aggregation")` 后配置 nmcpAggregation（三属性与超时 providers 链逐字）、发布分支守卫、完整性前置校验委托 `CentralIntegrityCheck`；同提交四件：buildSrc 增 `implementation 'com.gradleup.nmcp:nmcp:1.6.2'`（版本注释义务）、根两枚带版本声明行与 `apply plugin: 'com.gradleup.nmcp.aggregation'` 行退役、注册行新增＋删除 `tny.central.gradle`；验证：`centralBundle` 构建面与 `centralUpload -PdryRun` 输出对 1.4 基线逐字等值、`-p buildSrc test` 绿、双声明负向冒烟（临时恢复根声明行应复现硬失败，随即还原）

## 7. 全量回归

- [ ] 7.1 九样件对 1.4 新基线全部逐字节一致；groovy 目录终核：仅剩编排线册移交前清单中的零枚发布族脚本（五枚全退役），`find buildSrc/src/main/groovy -name "*.gradle" | wc -l` 与预期清单一致并记 apply-notes
- [ ] 7.2 全量：`./gradlew -p buildSrc test` 绿、`check --continue` 绿、`publish --dry-run` 任务图绿且 BOM 门禁挂接在位、CodOD 单工程样件、耗时三连对 1.4 读数（阈值 3 秒）、checker 四破坏探针复跑
- [ ] 7.3 判例表逐行勾验（本册转换面的 matches/find 与表一致）；触碰文件按主规格全部需求走查表入 apply-notes

## 8. 收口

- [ ] 8.1 `/opsx:verify convert-publish-family-to-java` 无 CRITICAL；用户批准后归档（摘要注明：五枚发布族脚本 Java 化收口，buildSrc 预编译脚本余量与 id 账对账、gate 七段首次可单测、publish↔gate 结构免疫半配对复发）
- [ ] 8.2 `cross-volume-assembly-line.md` 追加销假登记；合并册立项时本册产物与 id 保留事实为其输入（引用出处留档）
