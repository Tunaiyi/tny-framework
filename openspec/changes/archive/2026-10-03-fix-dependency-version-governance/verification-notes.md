# 验收记录：fix-dependency-version-governance

## 第 1 组：守卫探针与改造前基线（2026-10-03）

- 1.1 探针结论：设计 D1 前置假设成立。依赖管理插件的合并后托管版本映射（managedVersions，1472 条）
  可在 gradle.projectsEvaluated 时机被脚本读取。探针实测值与已发布构件元数据逐字符一致：
  log4j-api=2.21.1（与 tny-game-oplog-log4j-5.7.x-SNAPSHOT.module 记录一致，坐实失守形态）、
  slf4j-api=2.0.10、netty-common=4.1.137.Final、jackson-databind=2.16.1、protobuf-java=3.22.2、
  testcontainers-bom 所辖 junit-jupiter=5.10.1、guava=32.0.1-jre。
  环境注记：抓取窗口并发会话已把 wrapper 升至 Gradle 8.14.5，构建在 8.14.5 上进行；
  io.spring.dependency-management 1.1.0 在 8.14.5 下产生弃用告警（构建输出可见），
  插件升版属本册 Non-Goal（后续 Gradle 基线册前置项），本册全部验证在 8.14.5 同一 daemon 环境抓取。
- 1.2 基线三件入 baseline/（deps-runtime-before.txt 15 项目段、deps-compile-before.txt 15 项目段、
  deps-it-runtime-before.txt 537 行），抓样环境与口径记录见 baseline/README.md。
  现状快照：log4j-api 2.21.1、guava 全图钉扎 32.0.1-jre（jetcd 通道 33.0.0-jre 请求被压降）、slf4j 2.0.10。

## 第 2 组：log4j 导入次序修复（2026-10-03）

- 2.1：tny.dependency-management.gradle 的 imports 块中 log4j-bom 导入句移至 spring-boot-dependencies 之后，
  块头注释成文覆盖次序契约。验证：重抓 oplog-log4j 与 boot-log4j2 的 runtimeClasspath 报表，
  log4j-api、log4j-core、log4j-slf4j-impl 全部解析为声明值 2.22.1（改造前为 Boot 的 2.21.1）。
- 2.2：`./gradlew :tny-game-oplog-log4j:test :tny-game-boot-log4j2:test` 通过（BUILD SUCCESSFUL，46 秒内，
  16 executed / 28 up-to-date；输出含 Gradle 9 弃用告警，来源见第 1 组环境注记）。

## 第 3 组：等值托管替换与托管层补建（2026-10-03）

- 3.1：slf4j 导入坐标由 slf4j-parent 换为官方 slf4j-bom。验证：namnspace-etcd 报表中 slf4j-api 仍解析为 2.0.10（1.7.x 请求被抬至 2.0.10 的形态不变）。
- 3.2：gradle.properties 新增 grpcVersion=1.60.0，grpc-bom 导入排在 Boot 之后。验证：etcd 工程 grpc 六件全部 1.60.0，与改造前传递值等值。
  等值前提复核记录：改造前 net 与 protoex 报表中的 grpc 1.50.2 请求行形如 "1.50.2 -> 1.60.0"（jetcd 通道最高版本已在整个工程内胜出），
  全图不存在裸 1.50.2 生效坐标，因此收编为具名钉扎后无任何工程发生版本位移。
- 3.3：io.vertx:vertx-grpc 以 vertxGrpc 别名收入版本目录（4.5.1）并加入集中托管清单（清单注释改为"条目增删须经变更册裁决"）。验证：etcd 报表 vertx-grpc:4.5.1 等值。
- 3.4：`./gradlew :tny-game-namnspace-etcd:test` 通过。
- 全量回归：15 模块 runtimeClasspath 重抓后与 before 做"内容行全集"集合差，唯一差异为 log4j 族 2.21.1 恢复为 2.22.1（第 2 组预期修复项），
  第 3 组自身贡献零漂移。抓样方法论勘误见 baseline/README.md：多工程合并单次调用存在段间输出串扰，比对判据改用内容全集，
  后续 after 件改为逐工程单独调用抓取。

## 第 4 组：配置期版本面对账守卫（2026-10-03）

- 4.1：tny.module-checker.gradle 新增第三项校验"托管版本面对账守卫"（文件头职责注释同步改为三项）。
  落地面经探针预检：八个族 15 个代表坐标在托管模型中全部存在且当前生效值等于声明值（log4j 2.22.1、netty 4.1.137.Final、
  jackson 2.16.1、protobuf 3.22.2、slf4j 2.0.10、testcontainers 1.21.4、grpc 1.60.0、alibaba 2022.0.0.0）。
  spring-boot-dependencies 族不入对账（成员版本与键值不同值，如 spring-core 6.1.2 对键 3.2.1，理由注释在位）。
  语义：代表坐标生效值不等于键声明值即失配；从托管面消失（取不到值）同计失配。
- 4.2：规格两场景的证据件留存 verification/：
  guard-drift-red-sample.txt（临时把 log4j-bom 移回 Boot 之前注入遮蔽，配置阶段报红并逐工程列出
  "声明 2.22.1 实际生效 2.21.1"）；guard-consistent-green-sample.txt（还原次序后配置通过且对账零输出）。
  注入与还原各执行一次，全程未提交，tny.dependency-management.gradle 的 git diff 复核为 16 增 3 删（仅第 2 组合法改动）。

## 第 5 组：解除 guava 强制降级（设计 D3，机制勘误与用户裁决记录）

- 前置试验证伪初版机制：摘除集中托管清单的 guava 条目后（managedVersions 实测已无 guava 键），
  etcd 工程 dependencyInsight 显示 jetcd 的 33.0.0-jre 请求仍被 "selected by rule" 压回 32.0.1-jre。
  DEBUG 日志指认 io.spring 插件 ImplicitDependencyManagementCollector：带具体版本号的直连依赖被自动
  登记为该工程全配置范围的强制托管——`api libs.guava` 的内联版本本身就是施压源，摘条目无效。
  对照试验：/tmp 最小工程（同版本组合、不装该插件）解析得 33.0.0-jre，原生裁决无恙。
- 诊断过程中本册守卫（第 4 组）意外立功：二分试验误造"grpcVersion 键在、导入句无"状态，
  配置期对账即报红并逐项列出"该族无托管源"——规格第三路径获得现场证据，守卫启用状态全程未受损。
- 用户裁决（2026-10-03，选项一）：版本目录 guava 32.0.1-jre 升钉为 33.0.0-jre（jetcd-core 0.7.7 的最低满足值），
  保留显式托管条目；D3/design/proposal/tasks 四件工件已回写勘误。本册唯一版本数值变更。
- 5.1 验证：etcd 工程 guava 无 "-> 32.0.1-jre" 压降箭头（jetcd 声明裸值直读）；
  15 模块 runtime 全图内容集合与基线比对，差异完整归类为——guava 族上移（含伴生件 failureaccess 1.0.1→1.0.2）、
  log4j 族恢复声明值（第 2 组）、checker-qual 与 errorprone 仅请求侧显示变化（生效值仍为托管钉扎的 3.42.0/2.24.1 不变）、
  无其他任何族漂移。
- 5.2 验证：七处直连模块测试 BUILD SUCCESSFUL。其中 common-lang 的
  CollectionLockTest.exclusiveMixturesNeverOverlap（读读并行驻留观测，时序敏感）首跑失败一条：
  连跑三次 + --rerun 强制重跑均绿，被测 lock 包经 grep 证实零 guava 触点，定性为与本册升钉无关的
  既有时序抖动（账本在途册 fix-ci-unit-flakes 的已知领域），如实记录不掩饰。

## 第 6 组：发布形态门禁与豁免登记（2026-10-03）

- 6.1：豁免登记清单落地 gradle/publish-prerelease-exemptions.txt（先例形态承 gradle/released-legacy.txt），
  两条初版条目（tools-template 快照、quartz 候选版）各含理由与处置去向。
- 6.2：tny.publish.gradle 门禁新增第五段 validatePreReleaseDependencies + loadPrereleaseExemptions，
  合流顺序追加在原四段之后，豁免条目经根工程记忆化在一次构建中打印一次。
  挂接注意记档：该文件同时携带在途册 split-publish-consistency-closure 的未提交拆分改动，本组改动叠加其上，
  提交归属由用户在两册间裁分。
- 实施教训（如实记录）：首版豁免匹配全部失效——Groovy 插值模板产出 GString 类型，作为 LinkedHashMap 查询参数
  与 String 键哈希永不相等（现场打印证实 coord 与 keys 肉眼逐字符相同而 hit=false）；修复为坐标比较两侧
  一律 String（查询侧 .toString()，清单值侧改拼接），教训注释写入代码两处。
- 验证三件：doc-gradle 与 common-scheduler 经豁免放行且门禁打印两条豁免（BUILD SUCCESSFUL）；
  临时注入未豁免快照 com.tnydev.game:tny-game-common-lang:5.7.x-SNAPSHOT 执行门禁被拒，错误文案列出
  违规坐标与声明构件；移除后恢复绿。临时件均已还原，common-digest 工作区无残留。

## 第 7 组：契约边收缩与直达边补建（BREAKING，2026-10-03）

- 7.1：redisson 删除 api boot 死边。POM 核对：tny-game 族仅剩 common-io 与 codec-jackson，boot 全域消失。
- 7.2：net 的 api expr-groovy 降为 implementation，补 api expr 直达边。POM 核对：compile 域含 expr、expr-groovy 仅 runtime。
- 7.3：basics 同形（api expr 补边、引擎降 implementation）。POM 核对：compile 含 expr 不含 expr-groovy、runtime 保留引擎。
- 7.4：doc 的 expr-mvel 降 implementation。POM 核对：expr-mvel 仅 runtime，compile 域消失（22:04 文件直查确认，
  首轮打印被截断误判缺失）。
- 连带修正三处（均为 BREAKING 面的仓内实证命中，全仓 compileJava+compileTestJava 扫描定位，非计划外扩面）：
  其一 starter-basics 的 BasicsAutoConfiguration 直接 import mvel 包（第 27 行），自声明 api expr-mvel；
  其二 starter-net-netty4 的 NetAutoConfiguration 直接 import expr 与 expr.groovy（第 18、19 行），自声明 api expr-groovy；
  其三 net 主源码六处以上直接使用 common-lifecycle 类型却从未直连声明（曾靠引擎链传递），补 api common-lifecycle，
  该边同时恢复下游（net-netty4 曾以 16 处 @UnitInterface/UnitLoader 引用靠传递获得 lifecycle 可见性）。
  三处均为"装配者/使用者显式声明所依赖之物"的直声明形态，对外逐配置暴露范围与收缩前等值。
- 任务文案勘误：7.2 验证方式中"删除 expr-groovy 工程后 net 主源码仍编译（P1 判据）"经实施证伪——net 主源码在
  CommandPluginHolder.java:62 与 DefaultMessageDispatcher.java:32 两处直接实例化 GroovyExprHolderFactory，
  该"new 实现类"代码味属原则卷 P2 反例既有形态，重构代码超出本册边收缩范围；实际判据取"POM compile 域收缩 +
  全仓编译通过 + 下游测试通过"三件，如实记录。
- 验证：全仓主与测试源码编译 BUILD SUCCESSFUL；7.5 的七模块测试（net、basics、doc、rpc、starter-basics、
  starter-redisson、net-netty4）全部通过。

## 第 8 组：死键死声明与悬空条目清理、账实注释修正（2026-10-03）

- 8.1：gradle.properties 删除 groovyVersion、graalvmVersion、icu4jVersion、springCloudVersion 四死键
  （零取用点经本册与审计双重全仓穷举确证），版本区注释改为记录清除事实与 groovy/graalvm 事实源位置。
  配置评估与对账守卫通过。
- 8.2：根 build.gradle 删除 org.springframework.boot 与 com.gradle.plugin-publish 两条 apply false 死声明
  （删除前再次全仓复证：两插件标识符除声明行本身零命中）；tny-game-doc-gradle 的三行注释掉的
  Plugin Portal 配置残留清除并留决策注记。springBootVersion 键与 BOM 导入句不受影响（键仍有两处取用）。
  验证：grep 零应用点复证、:tny-game-doc-gradle:build 通过、全量配置绿。
- 8.3：版本目录三处失实注释修正（jprotobuf 内联自管表述、coord() 改为真实辅助函数 dependencyNotation()、
  cglib 托管表述与清单对齐）。
- 8.4：settings.gradle 删除悬空的 tny-game-codec-protoex 引入行并留因由注释；tny.module-checker 组号对账
  块新增"装配线成员构建文件存在性"断言（探针实证悬空工程的 buildFile 为指向不存在路径的非空对象，
  断言用 exists 判定），异常表头改为涵盖两类违例。双向验证：还原绿；把真实悬空引入临时加回立即报红
  并点名该工程。注意：该工程目录下的四个未跟踪构建残留文件删除操作被权限系统拒绝，未处置，
  需用户手工 rm（目录已不在装配线内，残留不影响构建与发布）。
- 8.5：tny.java-module.gradle 夹具自动挂载谓词追加 it != project 排除宿主（前置冲突检查：该文件
  在途未提交改动仅一行缩进修正，与编辑区不重叠）。验证：tester 的 testCompileClasspath 零自边、
  common-lang 仍挂载 tester、:tny-game-tester:test 通过。
- 8.6：八项"托管而无消费"commons 条目对账删除（commonsBeanutils、commonsCollections、commonsConfiguration、
  commonsCrypto、commonsDbcp2、commonsLang、commonsPool、commonsPool2）——判据为五类作用域全图解析树
  （runtime/test/compile 全 54 工程 + integrationRuntimeClasspath + jmhRuntimeClasspath，共 14.9 万行）
  零出现，删除结构性零差异。两项有真实传递落点保留并在托管清单声明处注释登记：commonsCompress
  （钉扎 testcontainers 栈请求，压降与漏洞线一并归版本升级册）、commonsMath3（钉扎 jmh-core 1.37 传递件）。
  删除后复扫：八项前后均零、两项落点计数不变、commons-lang3 5366 处正常。

## 第 9 组：集成回归与收口（2026-10-03）

- 9.1：`./gradlew clean build` BUILD SUCCESSFUL（344 项任务，335 执行，退出码 0），全程配置期守卫在位无工程误报。
- 9.2 三项判据核对：
  其一，log4j 族已发布元数据：oplog-log4j 的 Gradle module 文件记录 log4j-api 与 log4j-core 均为 2.22.1
  （改造前同一文件为 2.21.1，名实恢复闭合）。
  其二，发布 POM 预发布坐标扫描：52 个 com.tnydev.game 组 POM 全量解析，预发布坐标仅 quartz 2.5.0-rc1
  （common-scheduler，豁免内）；例外组号 com.tny.game 补扫中出现的 9 月 27 日 tny-game-common-scheduler
  陈旧文件为组号迁移前本机残留、非本轮发布物，本轮 doc-gradle 的 tools-template 快照豁免内。未豁免违规零。
  其三，门禁豁免打印：checkPublishPrerequisites --rerun-tasks 输出两条当前生效豁免（tools-template、quartz）。
- 9.3：本文件即汇总。与工件口径的两处已记录偏差：D3 机制勘误改走升钉（用户裁决）；7.2 的 P1 判据文案证伪
  （net 主源码存在 new 实现类代码味，判据以 POM 域 + 全仓编译 + 下游测试三件替代）。
- 遗留待用户处置（非本册范围阻塞项）：tny-game-codec-protoex/ 目录内四个未跟踪构建残留文件（rm 被权限系统拒绝）；
  本册改动与工作区在途改动（split-publish-consistency-closure 对 tny.publish.gradle 的拆分、commons-io/xstream
  升版、benchmark 线）交错于同批文件，提交切分由用户裁决；CI 的 setup-java '21' 第二书写点、dependency-management
  1.1.7 前置升级、Gradle 基线注记（本册实施环境实际已在 8.14.5）均属后续案卷。

## 第 10 组（补充）：构建弃用警告治理（用户指令追加，2026-10-03）

- 修复清零十三类脚本级弃用告警：空格赋值十处（settings 八处 url、tny.dependency-management 的 group/version、
  tny-game-doc-gradle 的 group）、JavaPluginConvention 一处（java-module 的 source/targetCompatibility 改经
  java 扩展块）、getDependencyProject 两处（module-checker 与 integration-test 改以工程路径解析，后者在
  integrationTest 执行路径，常规构建不触发但同属 Gradle 9 移除清单）。
- 归因实验记录（如实含弯路）：首轮 git worktree 对照因 grgit/jgit 不识别 worktree 元数据而构建失败，
  其"零告警"读数无效；改用 git clone 共享对象克隆 HEAD 并钉同一 8.14.5 复测成功——纯净基线报出与主树
  完全相同的六条 Mutating/getArtifacts，证明该六条由在途 wrapper 升级（并行会话提交前状态）引出、
  与本册改动无关；期间单变量实验（禁守卫、回退 java 块、回退第 7 组六文件）先后排除本册嫌疑，
  第 7 组文件曾临时回退 HEAD 做对照、随后自 /tmp 备份完整还原并复验编译与 D7 标记在位。
- 主树最终读数（build --warning-mode all）：space_assignment 0、JavaPluginConvention 0、getDependencyProject 0、
  Mutating/getArtifacts 6（等于基线）、BUILD SUCCESSFUL。
- 遗留环境类与源码类告警分类见 tasks 10.3；诊断产物 /tmp/tny-head-check（git worktree）与 /tmp/tny-base-clone
  的清理命令被权限系统拒绝，需用户手工删除（worktree 需先 git worktree remove 或 prune）。
