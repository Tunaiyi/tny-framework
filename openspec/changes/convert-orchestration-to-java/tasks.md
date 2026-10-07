# Tasks

## 1. 册门与并行核对

- [x] 1.1 `git log --since=2026-10-07 --name-only -- buildSrc/src/main/groovy/tny.release.gradle buildSrc/src/main/groovy/tny.integrate.gradle buildSrc/src/main/groovy/tny.git.gradle buildSrc/src/main/groovy/tny/convention/GitFlow.groovy buildSrc/src/main/groovy/tny/convention/GitCli.groovy` 核对 redesign 在途册是否已有新修复提交：有则以新 HEAD 重锚基线并按新现文随迁，无则锚当前 HEAD；结论记入 apply-notes
- [x] 1.2 复核主规格"约定插件接线规则按载体定"与"共享构建脚本禁止枚举具体工程名"对本册的约束（任务名字符串属门禁挂接非工程名点名），起草正则选点判例表（本册面逐条＋发布族移交面登记）入 apply-notes 附表；验证：表覆盖五文件全部 `==~` 与 `=~` 出现行（grep 交叉核对计数一致）

## 2. 基线快照入库

- [x] 2.1 按 context 零差异抓样口径（Corretto 21、UTF-8、独立守护进程注册表、PATH 修正）抓基线入 `baseline/`：全量任务图（含五任务 description 全文）、`releaseCut -PreleaseVersion=9.9.9 -PdryRun` 在当前分支的输出全文（成功计划或报错文案均照录）、`integrateMain -PdryRun` 与 `mergeUpward -PdryRun` 同法、`gitFlow` 派生值记录（经 `-q properties | grep "^version"` 与 legacyIdentity 冒烟）、warm help 耗时三连；README 锚定哈希与剔噪口径（沿装配线册 Python 正则）
- [x] 2.2 验证：样件非空、README 齐、`git status` 除在途避让文件（`tny-benchmark/results/bench-20261006-quick.json` 不入任何提交）零污染

## 3. GitCli 转 Java

- [x] 3.1 新建 `GitCli.java`（包 tny.convention）：`Map<String,Object> run(List)` 键 exit/out/err、`require` 抛错文案逐字、`forProject` 解析 `-PgitExe`、ProcessBuilder 与 LC_ALL=C 钉定逐句等价；原类头全部沿革注释（D6 双通道退场史、双流读取缓冲注记、调用方查 exit 纪律）入类 javadoc；同一提交 `git rm GitCli.groovy`；验证：`./gradlew -q :tny-game-bom:help` 配置期绿（gitFlow 构造即走新门面）、判红路径冒烟（`-PgitExe=/bin/false` 走坏路径报红；任务书原句"读取当前分支名失败"系笔误——构造器先跑 rev-parse --verify HEAD 探针且本机 /bin/false 不存在，实况文案为 Could not create an instance of type GitFlow 嵌套 Cannot run program，与旧 Groovy 版同型：UncheckedIOException 的 message 内嵌 cause 的 IOException 原文；本组为删除前未留负例快照的如实注记，严格前后等值以头探针正常路径与全部样件兜底）

## 4. GitFacts 与门禁判定类（测试先行）

- [x] 4.1 用例与判定类同批编写（任务书原句"未实现前提交红状态"对新类为编译失败非测试红，机械不可行，按装配线册组 4 同型判据修正）：`GitFactsTest` 覆盖 parseBranchVersion/parseProjectVersion 四形态×注入值合法非法矩阵、nextPatchExpected 无标签与最大补丁加一、porcelain 脏项映射含重命名行、parseLsRemoteLines 不足两段丢弃、releasedPatchesFromRefs 只认 `^{}` 行、describeTagOrNull 旧代形态拒绝、extractMarkers 与 sumGrepHits、parseLegacyRegistry 三向、commitTime 固定时区加固定 epoch 的已知值断言（旧式 new Date().format 默认时区同型注记）；`ReleaseGateCheckTest` 覆盖形态白名单四拒一放、占号黑名单、下一补丁号红绿、幂等标签三态；`IntegrationGateCheckTest` 覆盖 num 对 `dev/5.7.x` 必得 5007（find 语义回归，误译 matches 即 -1）、低编号在途线报红指名、标记缺失求和判红
- [x] 4.2 实现 `GitFacts.java`、`ReleaseGateCheck.java`、`IntegrationGateCheck.java`（判定纯函数，报错文案逐字承脚本），4.1 全转绿；验证：`./gradlew -p buildSrc test` 绿且执行记录核对新类用例计数

## 5. GitFlow 转 Java 与注入点转正

- [x] 5.1 新建 `GitFlow.java`：final 字段与全部公开方法名逐字保持（含四个兼容访问器与 gitBranchType）、`tagInfo`/`remoteRefs` 等 Map 返回形态、构造期派生委托 GitFacts、类头与字段注释全量随迁（唯一写入方契约、grgit 退场史、唯一解析点守卫教训、空仓报错文案）；同一提交 `git rm GitFlow.groovy`；`ProjectsPlugin` 注入段删 `GroovyObject` 反射改 `getByType(GitFlow.class).getProjectVersion()`（不在位留空的既有语义保持），装配线册 design D4 降级注记回写为"本册转正"；验证：`-p buildSrc test` 绿、根 `help` 绿、装配线四破坏探针中供给断供与 gitFlow 缺席两例复跑仍报红（文案对基线）
- [x] 5.2 验证：发布族 Groovy 消费面冒烟——`./gradlew :tny-game-bom:generatePomFileForMavenJavaPublication` 与 `:tny-game-net:generatePomFileForMavenJavaPublication` 后 POM 对装配线册终态样件（`final/pom-*-after.xml`）逐字节一致（gate/central/publications 经新 GitFlow 读取路径全走过）

## 6. ReleaseOpsPlugin 立口与三脚本吸收

- [x] 6.1 新建 `ReleaseOpsPlugin.java`（包 tny.convention）：内部次序逐字＝原根三行行序——先复刻 tny.git 职责（legacy 登记表经 GitFacts 解析、创建 gitFlow 扩展），再注册 releaseCut/releaseTag（任务名、group、description、doLast 三段式"采集→ReleaseGateCheck 判定→GitCli 动作"、dryRun 计划文案逐字），再注册 integrateMain/mergeUpward/retireGuard（同法，判定入 IntegrationGateCheck）；gitFlow 注入 `ProjectsPlugin` 依赖方向不变（git 扩展先建、projects 在根脚本先行应用既有事实沿用——入口应用时刻 tny.projects 已在位，注入行迁入入口尾部并注释登记）
- [x] 6.2 同提交切换五件：删除 `tny.git.gradle`、`tny.release.gradle`、`tny.integrate.gradle`，`buildSrc/build.gradle` 注销三注册行并新增 `tny.release-ops`，根 `build.gradle` 编排三行并一（原行位置注释登记行序契约与 redesign 修复面收缩声明）；验证：根 `help` 绿、描述符 `tny.release-ops.properties` 指向实现类且三旧描述符从产物消失、`tasks --all` 五任务名与 description 对基线零差异

## 7. 全量回归

- [x] 7.1 dryRun 等值逐条：`releaseCut -PreleaseVersion=9.9.9 -PdryRun`、`integrateMain -PdryRun`、`mergeUpward -PdryRun`、`retireGuard`（只读失败路径文案）四组输出对基线逐字比对（含非零退出的报错全文）；差异零通过，任何措辞漂移当场修文案不改判据
- [x] 7.2 全量：九样件（装配线册口径复用于本册基线）比对、`./gradlew -p buildSrc test` 绿、`check --continue` 绿、`publish --dry-run` 任务图绿且 BOM 门禁节点在位、CodOD 单工程样件（`:tny-game-net:tasks`）、warm 耗时三连对照基线（阈值 3 秒）
- [x] 7.3 触碰文件按主规格 13 条需求逐条走查（重点：判例表逐条勾验、删除与注册同提交、目录页、dryRun 预览通道保留断言、禁点名、零脚本 id 相互引入）；走查表与判例表定稿入 apply-notes

## 8. 收口

- [ ] 8.1 `/opsx:verify convert-orchestration-to-java` 无 CRITICAL；用户批准后归档（摘要注明编排线三脚本与两支撑类清零、id 十七收十五、根编排区三行收一行）
- [ ] 8.2 归档后回写：`cross-volume-assembly-line.md` 追加"编排线已先行合入，8.1/8.2 演练若暴露 git 语义缺陷修复落点为 GitFacts/GitFlow.java/ReleaseOpsPlugin.java 并登记"条款；发布族册（convert-publish-family-to-java）工件内"GitFlow 类型化依赖本册"前置即告满足，其任务组 1 核对项打勾依据留档
