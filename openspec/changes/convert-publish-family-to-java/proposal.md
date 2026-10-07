# Proposal

## Why

整合程序批次路线（D9）中的发布族册：把 buildSrc 最后一批 Groovy 载体——`tny.publish`、`tny.publish.gate`、`tny.publications`、`tny.github-packages`、`tny.central` 五枚脚本——同名 Java 化。本册按既定硬闸门**预立**：规划工件现在定稿，全部实施任务组在任务 1 的闸门核对通过之前不启动。闸门三条件来自已归档两册的成文裁决——其一，在途变更 redesign-devline-integration-model 归档且其 release-versioning、central-publishing、branch-integration-gates 三个规格差量入账本（门禁七段核对、Central 完整性校验、分支流转联动的现行语义由该册定义，本册 MUST 按其归档后现文逐段随迁，先行改写会把未收口语义焊进 Java）；其二，编排线册（convert-orchestration-to-java）先行合入（gate 与 central 按类型读取 `GitFlow`，其 Java 化是本册摆脱弱型读取的前提）；其三，基线在两项完成后重抓（发布族任务面随 redesign 归档演进，旧样件不可沿用）。

预立的收益：范围、拆类、验证设计与任务序列在上下文完整的当下定稿，闸门事件到来后即可直接实施；跨册协调登记（`cross-volume-assembly-line.md`）与正则选点判例表（编排线册产物，含发布族约十处移交行）已就位，本册执行时按表勾验。

## What Changes

- 五枚脚本同名 Java 化（id 全部保留，根装配行与 `tny-game-bom` 自报行零改动——入口收纳属合并册）；逐枚删除脚本与新增注册行锁死同提交，形态断言（描述符指向实现类）逐枚执行。
- `tny.publish.gate` 拆类：七段核对（形态、仓库路由、占号黑名单、标签存证、下一补丁号、依赖形态、豁免清单）判定逻辑拆入纯函数判定类（报错文案逐字承脚本，合流顺序保持），红绿单测入 CI buildSrc 档——门禁七段首次可单测；根级"分支+提交+标签"记忆化与 fail-closed 远端不可达语义逐字保持；读取 `GitFlow` 改类型化（前置条件之一）。
- `tny.publish` 与 `tny.publish.gate` 建立原子对：publish 实现类内部按类应用 gate 插件（二进制互引为规格明文允许），根与 BOM 既有显式 gate 行成为幂等重言保留——杜绝未来再现 0faf6edb 式半配对。
- `tny.publications` 与 `tny.github-packages` 等价迁移：mavenJava 配置方语义、签名挂接、仓命名契约（未命名 maven 仓与 githubPackages 命名仓的任务名派生链）、中央快照通道声明逐句保持；publish 注入的四个凭据 ext 键的读取次序契约（原第 60 行前）随入口顺序注释成文。
- `tny.central` 立口准备（id 仍为 `tny.central`）：nmcp 聚合挂根、发布分支守卫、Central 准入完整性前置校验逐段迁移；nmcp 两枚带版本声明行（`com.gradleup.nmcp` 与 `com.gradleup.nmcp.aggregation`）退役、实现 jar 移入 buildSrc implementation 供给（前册探针实测 all-in-one 构件可行、类加载器同一、双声明硬失败），与根应用行改写同提交原子执行；`centralBundle`/`centralUpload` 的 `-PdryRun` 预览面前后等值入验收。
- 正则选点判例表发布族行落地：编排线册判例表登记的移交行逐条按 matches/find 选定翻译，走查按表勾验。
- 无 **BREAKING**：任务名（`checkPublishPrerequisites`、`centralBundle`、`centralUpload`、`publish*` 全家）、POM 与模块元数据、`.github/workflows/publish.yml` 消费的命令行面零变化。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

（无——纯载体迁移；若实施期 redesign 归档后的现文语义要求调整门禁判定输入，属该册差量入账结果而非本册新增需求；本册声明 `skip_specs: true`。）

## Impact

- **闸门事件依赖**：redesign-devline-integration-model 的归档与 `convert-orchestration-to-java` 的归档是任务组 2 起点；本册立而不启，不与任何在途册抢文件。
- **buildSrc**：五枚新实现类与 gate 判定拆分类、测试族；五枚脚本删除；注册块净变化 ＋5/−0（id 全保留，形态换轨）；nmcp implementation 依赖与版本注释义务（沿 dm/jmh 先例）新增。
- **根构建脚本**：nmcp 两枚带版本声明行退役（同提交）、javaProjects 段 `com.gradleup.nmcp` 裸 id 应用行保留（类由 buildSrc 供给，前册实证通道）、其余零触碰；`tny-game-bom/build.gradle` 五枚自报行零改动（id 保留的直接收益）。
- **CI 与发布链**：`publish.yml`/`build.yml` 零改动；buildSrc 测试步骤自动覆盖 gate 七段新用例。
- **下游与发布产物**：零感知；三线 POM 与模块元数据对**闸门后新基线**逐字节零差异（旧基线样件一律作废，防把 redesign 演进误记为本册漂移）。
- **跨册文档**：执行启动时在 `cross-volume-assembly-line.md` 登记销假；合并册（终态 9 id 收编）继承本册产物做入口收纳。
