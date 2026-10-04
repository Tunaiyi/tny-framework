# Tasks

## 1. wrapper 升级追认

- [x] 1.1 把 `gradle/wrapper/gradle-wrapper.properties` 的 8.14.5 工作区改动追认入库（独立提交，与后续整改分离便于回滚）。验证方式：`./gradlew help -q` 配置通过，git 状态中该文件不再悬挂。

## 2. dependency-management 升版与告警集复照（设计 D1）

- [x] 2.1 根 `build.gradle` plugins 块 io.spring.dependency-management 1.1.0 升 1.1.7。验证方式：全工程配置评估通过（含对账守卫与构建文件存在性断言在位无红）。
- [x] 2.2 `./gradlew build --warning-mode all` 重跑并留存告警集快照与升版前六条清单对照，结果回写 design.md 的 D1/D2（计数若有变化即改变后续组范围）。验证方式：快照件与对照结论入变更目录。

## 3. Mutating 告警根因定位（设计 D2）

- [x] 3.1 单变量实验：临时注释 `tny.java-module.gradle:60-61` 的 `configurations.configureEach` 排除块，复跑 `:tny-game-starter-basics:build --warning-mode all` 记录告警计数。验证方式：消失即锁定排除块与物化时序交互，结论入件；未消失则执行 3.2。
- [x] 3.2 （条件执行）tester 动态挂载 `add('testImplementation', it)` 时序单变量同法试验并记录。验证方式：两嫌疑各有明确排除或坐实结论。
- [x] 3.3 把定位结论与选定修复形态（两候选按侵入度与语义保持择优）回写 design.md D2。验证方式：设计文与实验记录一致。

## 4. 仓内告警整改（设计 D2、D3）

- [x] 4.1 （裁剪无改动：见设计回写——根因系旧插件，1.1.7 后无改造对象；exclusions 零漂移由 5.2 基线兜底）原验证方式：`build --warning-mode all` 中 Mutating 计数 0；`:tny-game-starter-basics:test :tny-game-starter-net-netty4:test` 通过；对照工程 `publishToMavenLocal` 后 POM exclusions 条目集合与 5.1 前件零漂移（若 5.1 尚未产出则以升版前即时快照先行对照）。
- [x] 4.2 （裁剪无改动：getArtifacts 同源旧插件已清零，任务改造留作未来指引；见设计回写）原验证方式：同一工程该任务改造前后各跑一次，输出的成功计数与无源码包计数逐值一致，`getArtifacts` 告警计数 0。

## 5. 发布元数据零漂移基线（设计 D4）

- [x] 5.1 （方法修正留痕：克隆回指 8.5 法被册A 入库代码的 ProjectDependency.path 8.6+ 下限否决，改为主工作区单变量回改插件行 1.1.0 取 before，见 design D4 二次修正）对 tny-game-net、tny-game-basics、tny-game-starter-basics、tny-game-starter-net-netty4 及另抽两个不同装配线发布构件执行 `publishToMavenLocal`（克隆独立 Gradle 用户目录），先运行后转存 POM 与模块元数据入 baseline/ 为 before 件（抓样按 openspec/config.yaml 零差异验收基线抓样口径：同 daemon、UTF-8 locale 钉住、Corretto 21、剔噪声行）。验证方式：baseline 目录 before 件齐全非空。
- [x] 5.2 主工作区同集合六构件先运行后转存为 after 件，逐条目比对 POM 与模块元数据。验证方式：零漂移；出现差异逐条回查是否 4.1 时序改造所致，若是且非预期即停回设计修订。

## 6. 移交登记与全量收口（设计 D5）

- [x] 6.1 verification-notes 落移交记录：`:tny-benchmark:detachedConfiguration` 两条告警指名承接在途册 `refine-bench-routine-triggering`，不随本册清零。验证方式：移交件含坐标、告警原文、承接册名。
- [x] 6.2 `./gradlew clean build --warning-mode all` 全绿且仓内告警计数为零（移交两条除外）；对账守卫遮蔽注入样本与发布门禁未豁免快照样本各复跑一次确认拦截仍在位。验证方式：三项计数与两份复跑输出记入 verification-notes。
