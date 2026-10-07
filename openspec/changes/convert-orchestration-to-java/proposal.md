# Proposal

## Why

整合程序的装配线三面已由 consolidate-assembly-line 收口，buildSrc 剩余的 Groovy 活体现在只剩编排线（`tny.git`、`tny.release`、`tny.integrate` 三枚脚本与 `GitFlow.groovy`、`GitCli.groovy` 两枚支撑类）与发布族五枚脚本。本册处理编排线：其一，`GitFlow` 是 Groovy 类这一事实正在制造全仓最后一处弱型读取——装配线册因编译墙被迫把派生版本注入点写成 `ProjectsPlugin` 内的 `GroovyObject.getProperty` 反射（该册 design D4 登记的降级），`GitFlow` 转 Java 后该反射即可撤销、恢复全链路类型化；其二，发布族册的 `tny.publish.gate` 与 `tny.central` 在应用期按类型读取 `GitFlow`，若不先完成本册，发布族册将被迫复制同样的反射降级；其三，release/integrate 两脚本承载发布快速通道与分支流转门禁的全部业务判定（下一补丁号校验、集成顺序检查、合并结果完整性检查、退役检查），这些"输入命令输出、输出事实与问题清单"的判定目前只能靠配置期输出与真实发版演练验证，拆为可单测的纯函数是本框架"二进制实现的检查逻辑必须携带单元测试"需求在这些判定上的最后落位。

与在途变更 redesign-devline-integration-model 的并行协调是本册的既定前提（用户在立项指令中明示）：release/integrate/GitFlow/GitCli 四文件在该册 8.1/8.2 演练的修复回落面上。协调条款成文三件——开工时基线以当时 HEAD 重抓；演练若先于本册产出修复提交，修复先落入 Groovy 现文、本册按新现文随迁（读取 `git log` 复核触碰史后决定）；本册先行合入后演练暴露同面缺陷时，修复落点转为本册产物 Java 类并在 redesign 册 apply-notes 登记跨册修订（镜像装配线册对 `DependencyConventionsPlugin` 的既有先例，登记文件 `cross-volume-assembly-line.md` 已备格式）。本册不依赖 redesign 归档事件，与之并行；发布族册依赖本册产出的 Java 化 `GitFlow`，批次顺序为编排线在前。

## What Changes

- `GitCli` 转 Java：保持 `Map run(List)` 返回 `[exit, out, err]` 键形态与 `require` 抛错语义（Groovy 消费方 `r.exit` 属性访问经 Map 键解析原样兼容），`-PgitExe` 解析、LC_ALL=C 环境钉定、双流读取逐句等价。
- `GitFlow` 转 Java 并拆判定：命令输出到业务事实的解析（porcelain 脏项映射、ls-remote 行守卫解析、补丁号计数、分支名与项目版本派生、标签形态判定、裸号 describe 二次过滤）拆入新纯函数类 `GitFacts`（同包），红绿单测覆盖；`GitFlow` 保留全部公开方法签名与 `Map` 返回形态（`tagInfo` 的 `[annotated, commit]`、`remoteRefs` 的映射），字段 final 只读语义不变；`Date.format` 系 Groovy JDK 扩展，Java 化按判例逐点改 `DateTimeFormatter` 并登记 commitTime（提交时刻换算 JVM 默认时区）与 buildTime（默认时区）两口径的等值用例。
- `ProjectsPlugin` 注入点转类型化：删除 `GroovyObject` 反射与弱型读取，改按类型取 `GitFlow` 读 `getProjectVersion()`（装配线册 design D4 登记的降级就此回收）。
- 立口 `tny.release-ops`：吸收 `tny.git`、`tny.release`、`tny.integrate` 三枚脚本（三 id 随吸收注销），入口内部次序逐字为原根脚本三行行序（git 先行创建 gitFlow 扩展，release、integrate 随后注册任务）；任务名 `releaseCut`、`releaseTag`、`integrateMain`、`mergeUpward`、`retireGuard` 与 description 文案逐字不变；全部判定（分支形态白名单、下一补丁号、集成顺序、完整性标记检索、占号黑名单）拆入 `GitFacts` 或任务动作内的纯函数段，可单测面按需求覆盖。
- 正则选点判例表成文（本册工件）：release/integrate/GitFlow 内全部 `==~`（matches）与 `=~`（find）消费点逐条登记选定方向与依据（含 num 闭包"误用 matches 令数值比较静默失效"的既有事故注记），同表登记发布族四脚本与 `tny.publish` 内的选点**不触**、移交发布族册，防误改。
- 根 `build.gradle` 编排三行收成一行（`apply plugin: 'tny.git'`、`'tny.release'`、`'tny.integrate'` 并一，注释登记行序契约）；顶部其他应用行与发布族、装配线区域零触碰。
- 验收纪律沿用既定套件：基线锚定 HEAD 入库（任务图、四命令 `-PdryRun` 等价样本、checker 与线外面样件复跑）、删除与注册同提交锁死、描述符与堆栈形态断言、`-PbenchParams` 类覆写优先级不适用本册但 dryRun 前后等值代之以"当前分支合法命令逐条前后跑比对 stdout"。
- 无 **BREAKING**：不改任何发布产物、任务名、门禁语义与 docs/release-process.md 契约；`GitFlow`/`GitCli` 公开形态逐面兼容（Groovy 消费方 `tny.publish.gate`、`tny.central`、`tny.github-packages` 等按类与属性读取均不变）。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

（无——纯内部载体迁移，不改变 gradle-build-style 或任何能力的需求文本；变更声明 `skip_specs: true`。）

## Impact

- **buildSrc**：新增 `GitCli.java`、`GitFacts.java`、`GitFlow.java`、`ReleaseOpsPlugin.java` 与 release/integrate 判定拆分类；删除 `tny.git.gradle`、`tny.release.gradle`、`tny.integrate.gradle`、`GitFlow.groovy`、`GitCli.groovy` 五文件与 `buildSrc/build.gradle` 对应注册变化（净新增一个 id `tny.release-ops`，注销三枚）；`ProjectsPlugin` 注入段改写；新增 `GitFacts` 与版本派生等值用例的测试族。
- **根构建脚本**：编排区三行收一行＋注释；其余区域零触碰（顶部 redesign 修复面只动本册声明过的编排行）。
- **Groovy 消费方兼容面**：`tny.publish.gate.gradle`、`tny.central.gradle`、`tny.github-packages.gradle`、`tny.publications.gradle`、`tny.publish.gradle` 继续以 Groovy 读 Java 类（同 buildSrc 内 Groovy 编译后于 Java，方向合法），本册零改动；它们对 `GitFlow` 的方法调用签名逐字保持。
- **在途变更协调**：与 redesign-devline-integration-model 共享修复面四文件，协调条款成文于本 proposal 首节并落 `apply-notes`；若开工时该册对四文件已有新修复提交，基线随新现文重锚。
- **下游与发布产物**：零感知——发布物 POM、模块元数据、任务图对外形态零变化（基线样件兜底）；`docs/release-process.md` 命令话术（releaseCut/releaseTag/integrateMain/mergeUpward/retireGuard/`-PdryRun`）全部原样可执行。
- **CI**：`build.yml` 零改动（buildSrc 测试步骤在位，新测试自动入门禁）；`.githooks/commit-msg` 与 publish.yml 不触碰。
