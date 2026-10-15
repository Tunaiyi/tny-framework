# Tasks

## 1. 守卫探针与改造前基线

- [x] 1.1 在 buildSrc 内做最小探针：验证依赖托管插件的合并后托管版本映射能否在配置收尾时机被工程脚本读取（设计 D1 的前置假设）；读取形态不成立则停止后续实施并回报修订规格差量。验证方式：探针输出某日志族坐标的生效版本与已发布构件元数据实测值逐字符一致。
- [x] 1.2 按 config 上下文的零差异验收基线抓样口径，生成并发布改造前的全量依赖解析报表，转存到本变更目录 baseline/ 子目录（先运行生成任务再转存）。验证方式：baseline 目录内每个受影响模块的报表非空且含版本列。

## 2. 修正导入次序并把覆盖契约成文（设计 D2）

- [x] 2.1 把 log4j-bom 导入语句移到 spring-boot-dependencies 之后，并在 imports 块头部注释写明"后导入覆盖先导入、显式大头一律排 Boot 之后"的次序契约。验证方式：重抓 oplog-log4j 与 boot-log4j2 的解析报表，log4j 族生效版本等于 gradle.properties 声明值 2.22.1。
- [x] 2.2 运行 `./gradlew :tny-game-oplog-log4j:test :tny-game-boot-log4j2:test` 并确认通过，将结果摘要记入变更目录。

## 3. 等值托管替换与托管层补建（设计 D5、D4）

- [x] 3.1 将 slf4j 的导入坐标由 slf4j-parent 替换为官方 slf4j-bom，版本键不变。验证方式：重抓报表，slf4j-api 与 slf4j-simple 生效版本与替换前逐坐标等值（均为声明值 2.0.10）。
- [x] 3.2 在 gradle.properties 新增 grpcVersion 键（初值 1.60.0），按次序契约把 grpc-bom 导入排在 Boot 之后。验证方式：namnspace-etcd 报表中 grpc 族生效版本与改造前传递值等值。
- [x] 3.3 把 vertx-grpc 以单条显式托管（初值 4.5.1）加入集中托管清单与版本目录，并把清单头注释的"条目集合与顺序保持不变"改写为"条目增删须经变更册裁决"。验证方式：报表等值 + 清单计数注释与实际条目数一致。
- [x] 3.4 运行 `./gradlew :tny-game-namnspace-etcd:test` 并确认通过，结果摘要记入变更目录。

## 4. 落地配置期版本面对账守卫（设计 D1）

- [x] 4.1 在 tny.module-checker.gradle 新增第三项配置期对账：对 gradle.properties 每个大头键所辖族的关键坐标，断言托管生效值等于声明值，不一致即配置失败并逐项列出坐标、声明值、生效值。验证方式：临时把某键改为错误值执行任意任务确认配置报红，还原后再次配置通过。
- [x] 4.2 把 gradle-build-style 规格差量中"托管版本面在配置期与声明事实源对账"的两个场景写成可重复执行的验证记录（报红输出样件与通过后静默的观察记录）存入变更目录。

## 5. 解除 guava 强制降级（设计 D3，2026-10-03 实测勘误后经用户裁决改走"升钉"路线）

- [x] 5.1 把版本目录 guava 条目的声明值从 32.0.1-jre 升为 33.0.0-jre（jetcd-core 0.7.7 POM 声明的最低满足值，保留集中托管条目），并在条目注释登记升钉理由与被压降的历史形态；实测勘误记录：原方案的"摘除托管条目"经依赖解析调试证实无效——依赖管理插件的隐式收集器会把带具体版本号的直连依赖自动转为全工程强制托管，摘除显式条目后 jetcd 的 33.0.0-jre 请求仍被压回 32.0.1-jre。验证方式：etcd 工程解析中 guava 无 "-> 32.0.1-jre" 压降箭头且直读为 33.0.0-jre；全图内容集合比对显示 guava 族由 32.0.1-jre 统一上移至 33.0.0-jre，无其他族漂移。
- [x] 5.2 运行 `./gradlew :tny-game-namnspace-etcd:test :tny-game-protoex:test :tny-game-actor:test :tny-game-common-lang:test :tny-game-common-digest:test :tny-game-common-lifecycle:test :tny-game-doc:test` 并确认通过（覆盖全部七处 guava 直连消费模块），结果摘要记入变更目录。

## 6. 发布形态门禁与豁免登记（设计 D6）

- [x] 6.1 建立版本库内豁免登记清单文件，初版两条：tools-template 快照坐标与 quartz 候选版坐标，每条写明保留理由与处置去向。验证方式：文件入版本控制、条目字段齐全且被门禁代码读取。
- [x] 6.2 在发布执行现场的既有门禁形态处新增校验：发布线构件的编译与运行域直接依赖坐标携带快照、候选发布或里程碑后缀且未豁免时拒绝发布，错误信息列出违规坐标与声明构件；门禁输出打印当前豁免条目。验证方式：临时给某发布构件加一个快照依赖执行发布确认被拒，移除后执行确认通过且输出含豁免清单。

## 7. 契约边收缩与直达边补建（设计 D7，BREAKING）

- [x] 7.1 删除 tny-game-redisson/build.gradle 对 tny-game-boot 的 api 边。验证方式：redisson 编译与测试通过，其发布 POM 的 compile 域不再含 boot 坐标。
- [x] 7.2 把 tny-game-net 对 expr-groovy 的 api 边降为 implementation，并新增对 tny-game-expr 的 api 直达边。验证方式：`./gradlew :tny-game-net:compileJava` 通过；删除 expr-groovy 工程后 net 主源码仍编译（P1 判据）；POM 的 compile 域含 expr 不含 expr-groovy。
- [x] 7.3 对 tny-game-basics 执行与 7.2 相同的降边与补边。验证方式同 7.2，另确认 basics 主源码十余个契约类型引用点的 import 全部指向 expr 包。
- [x] 7.4 把 tny-game-doc 对 expr-mvel 的 api 边降为 implementation。验证方式：doc 编译通过、POM compile 域不含 expr-mvel、包私有 MVELExporter 的运行装配经 doc 自身测试确认仍可达。
- [x] 7.5 运行 `./gradlew :tny-game-net:test :tny-game-basics:test :tny-game-doc:test :tny-game-rpc:test :tny-game-starter-basics:test :tny-game-starter-redisson:test` 并确认通过，结果摘要记入变更目录。

## 8. 死键死声明与悬空条目清理、账实注释修正（设计 D10、D8、D9）

- [x] 8.1 删除 gradle.properties 的 groovyVersion、graalvmVersion、icu4jVersion、springCloudVersion 四键，并在版本区注释注明 groovy 与 graalvm 的事实源位置。验证方式：任意构建配置通过且第 4 组守卫对全部大头键通过。
- [x] 8.2 删除根 build.gradle 两条零应用点插件声明（Spring Boot 插件与插件发布插件的 apply false 行），清理 tny-game-doc-gradle/build.gradle 中注释掉的发布配置残留行。验证方式：全仓 grep 确认两个插件标识符零命中后 `./gradlew :tny-game-doc-gradle:build` 通过。
- [x] 8.3 修正版本目录三处失实注释（jprotobuf 的托管表述改为内联自管、coord() 表述改为实际辅助函数名、cglib 的托管条目表述与清单对齐）。验证方式：逐条对照实际机制复核注释自洽。
- [x] 8.4 删除 settings.gradle 对 tny-game-codec-protoex 的 include 行并清理其 build 残留目录；在配置期对账处新增"装配线成员工程必须有已跟踪构建文件"断言。验证方式：`./gradlew projects` 配置通过、BOM 约束条目不再含该坐标、临时加一个悬空 include 确认断言报红后移除。
- [x] 8.5 给 tny.java-module.gradle 的公共测试夹具自动挂载谓词追加排除宿主工程自身（前置：确认该文件的在途未提交改动与 move-bench-suite-selection-into-plugin 册的触碰区已入库或无冲突）。验证方式：tester 工程的 testImplementation 配置不再含自身路径，`./gradlew :tny-game-tester:test` 通过。
- [x] 8.6 对九个"托管而无消费"的 commons 族条目逐一核对传递落点：无落点者连同托管声明与目录条目删除，有落点者在声明处注释登记所钉传递坐标。验证方式：删除项以 1.2 基线报表做前后对照零差异，保留项注释与报表证据一致。

## 9. 集成回归

- [x] 9.1 运行 `./gradlew clean build` 全量通过（守卫与门禁全程在位），确认无工程因新增对账被误报红。
- [x] 9.2 执行本地发布快照，核对已发布构件元数据：log4j 族为 2.22.1、门禁输出含两条豁免登记、发布 POM 编译域除豁免外无预发布后缀坐标。
- [x] 9.3 把各任务组留存的测试摘要与报表对照汇总成 verification-notes.md 存入变更目录，供 verify 与归档引用。

## 10. 补充：构建弃用警告治理（用户指令追加，2026-10-03）

- [x] 10.1 消除本册已知的脚本级弃用告警：settings.gradle 八处与 tny.dependency-management/tny-game-doc-gradle 共十处空格赋值改为等号赋值；tny.java-module 的项目级 source/targetCompatibility 直写改经 java 扩展块（消除 JavaPluginConvention 弃用）；tny.module-checker 与 tny.integration-test 的 getDependencyProject 改以工程路径解析。验证方式：`./gradlew build --warning-mode all` 中 space_assignment、JavaPluginConvention、getDependencyProject 三类计数均为零，构建通过。
- [x] 10.2 剩余六条 Mutating/getArtifacts 告警定性：以 HEAD 提交构建共享克隆并钉同一 Gradle 8.14.5 复测，纯净基线报出完全相同的六条——坐实为在途 wrapper 升级（8.5 至 8.14.5）引出的既有配置排除形态与 Gradle 新校验的交互，非本册引入。处置：登记进 Gradle 基线升级册（与 dependency-management 升 1.1.7 同路），本册不改动排除语义（该形态涉全仓发布面，盲改风险大于收益）。验证方式：主树与基线克隆同命令同计数（六对六）。
- [x] 10.3 环境类与源码类警告归账如实登记：JVM native-access 与 CDS sharing 告警源自 Gradle 分发与 JDK21 组合，非仓内脚本问题；javac 的"已过时 API"提示与 javadoc"无效输入 '<'"注释告警（CapacityObjectStorer 等）在基线克隆同样存在，属代码既有债，登记不吞（后者归注释规范清扫案卷）。
