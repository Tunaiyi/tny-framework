# Design

## Context

实施前事实核对（2026-10-03 现树，本册前身记载被勘误后的重测）：`tny-game-integration-test/build.gradle` 85 行；dependencies 块第 42 至 85 行，块内无 each/循环/moduleProjects 任何程序性形态（全仓 grep 与 `git log --all -S` 双重零命中，见归档审计册勘误段）。成员语句定位：外部库六项在第 72、73、76、77、78、81 行，各自随行的来由注释两组在第 74 至 75 行（spring-boot-starter-log4j2 替代手工拼装）与第 79 至 80 行（log4j-core 提至 compile 的损坏帧告警捕获来由）；第 62 至 63 行注释为 P3 档公共语义（全部挂 integration 配置、普通 test 不受影响），覆盖工程语句与 bundle 语句两侧，留在原位。四个留位外部库：junitJupiter（58）、mockitoJunitJupiter（59）、log4jApi（82）、log4jSlf4j2Impl（83）。catalog `gradle/libs.versions.toml` 115 行现仅 `[versions] [libraries]` 形态（`[bundles]` 零使用），六个成员键逐字存在于第 98、99、102、104、106、17 行一带。buildSrc 经 settings 只读视图共用同一 catalog，bundle 访问器两侧可见。

## Goals / Non-Goals

Goals：模块回到 80 行界线内；外部坐标事实保持版本目录单一事实源；挂接形态读起来是"一行一个事实"。Non-Goals：不动 `project(':...')` 工程依赖（装配线事实，需求九通道，留在模块）；不把依赖整体下沉 `tny.integration-test` 插件（插件脚本内引用 `libs.*` 会迫使外部坐标脱离目录或改写字符串坐标，新增唯一事实源违例——该路线否决）；不动 integrationImplementation/integrationRuntimeOnly 配置名与任何解析语义；不建第二个 bundle。

## Decisions

**D1 修复形态取版本目录 bundle，弃"整体下沉插件"与"压注释"两路线。** 备选一（整体下沉 `tny.integration-test` 守卫段）被否决：模块的 `project()` 语句无法在插件里以目录访问器表达兄弟工程引用（现树插件零先例），且 `libs.*` 搬入插件后 bundle 之外的留位项也要随行或改字符串坐标，破坏"版本只住目录"的事实源纪律。备选二（压缩注释行数）被否决：来由注释是项目文字规则要求的完整语义载体，为凑界线删注释属把债从行数挪到可读性。bundle 路线一行挂接表达"容器/全栈夹具栈"一个配置事实，注释收进坐标定义现场（目录第 92 行一带已有同族集中注释先例）。
**D2 bundle 成员边界按"栈语义"而非"行省"划。** 收编六项的共同语义：Spring Boot 全栈装配 + log4j2 官方集成 + Testcontainers 容器驱动，即 P3 容器/全栈档的外部面。不收编 junit/mockito 两项的原因：它们是夹具测试 API（与 58/59 行的声明位置同段），语义属测试框架族而非被测栈；不收编 log4jApi/log4jSlf4j2Impl 的原因：这两项是 slf4j2 provider 绑定的另一半（第 79 至 80 行注释的语义整体描述的是"绑定族"，log4jCore 单独入栈会拆散该注释的归属——折衷：注释随 log4jCore 进 bundle 集中段，绑定语句留位并加一行指回）。若解析红线与注释归属冲突，以红线为准。
**D3 逐字红线沿用前身册 D2 并升级引用。** 两配置解析集 before/after 逐行零差异为第一判据；bundle 展开集与移除的六条语句坐标集必须逐项一致（catalog 键逐字对照）；diff 非空无论方向即停回用户裁决，不静默吸收。
**D4 基线口径引用权威文本。** 抓样按 openspec/config.yaml context"零差异验收基线抓样口径"一条执行：控制台转存件先运行再转存，before 与 after 同一 Gradle daemon、UTF-8 locale 确认钉住（前身册立项时的口径缺口已在本批补齐，本册重写直接按新权威文本书写，不再各抄一份）。

## Risks / Trade-offs

- 风险一：bundle 访问器命名 `libs.containerStack`（catalog 键 `containerStack` 驼峰转小写连字符的实际访问器形态需实施时以首编译验证）——`projects -q` 评估即暴露拼错，常驻探针。
- 风险二：Gradle 8.5 的 bundle 对 `integrationImplementation` 自定义配置直接适用（bundle 成员为库模块坐标，与普通库引用同型），若目录 bundle 语法在该配置上出现解析差异判据立即兜住（D3）。
- 权衡：catalog 首次引入 `[bundles]` 节，评审面新增一种形态；换来的是模块一行一事实与界线回归，且 bundle 是官方目录形态，不造私有词汇。

## Migration Plan

第一步，按 D4 口径抓四件基线（两解析集、dry-run 任务图、`tasks --all` 剔噪清单、行数与块行界）。第二步，catalog 增 `[bundles]` 节（含集中来由注释），模块按 D1/D2 替换语句区。第三步，复跑比对与 `clean build`，全部结论记 verification-notes.md。回滚：模块恢复六条语句与两组注释，catalog 删除新增节。
