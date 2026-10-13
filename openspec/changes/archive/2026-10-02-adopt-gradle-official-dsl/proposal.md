# Proposal

## Why

上一变更（sweep-gradle-build-style）已把 gradle-build-style 八条需求的存量违例全仓清零，但脚本体系的三个深层可读性问题是形态清扫解决不了的。第一，坐标与版本引用由 ext 上的 Groovy Map（libs 表与 vers 表）经动态属性解析承载，没有模式约束、没有编辑器补全、拼错键名要到构建执行期才报出，且四十八个模块构建文件都隐性耦合"根脚本恰好定义了这两个 Map"这一实现机制。第二，apply from 脚本链现在有十七个脚本、二十五处引入点，"镜像与独占路由仓库表""零缓存策略""编译编码钉定""默认 logging 绑定排除"等配置块在多个同构脚本里逐字重复，同类修改要多处跟改，违背单一职责的落地方式。第三，编排脚本是解释执行的动态 Groovy，任何引用错误都要跑到那条语句才暴露。Gradle 官方的两项 DSL 设施正面解决这三点：版本目录（gradle/libs.versions.toml）给坐标与版本一个声明式、有模式、有类型化访问器的唯一事实源；约定插件（buildSrc 预编译脚本插件）让公共装配以命名插件形态组合复用、编译期检查、按职责拆分。上一变更的 design 已把这条路线定性为"对 gradle-build-style 能力本身的需求变更，须先以独立变更修订规格"——本提案即该独立变更。

## What Changes

- 引入 `gradle/libs.versions.toml` 版本目录作为第三方依赖坐标与版本的唯一事实源：目录别名一律采用下划线命名（如 `commons_io`），使既有四十八个模块文件里的 `libs.commons_io` 具名引用访问器名逐一保持不变，模块文件接近零改动；`gradle/dependency.gradle` 的 ext.libs 与 ext.vers 两张表废止删除。gradle.properties 保留 BOM 级大头版本（Spring Boot、Netty 等，仍供 plugins 块与 BOM 导入字符串使用），双事实源分工不变。
- 把 `gradle/` 下十七个 apply from 编排脚本迁移为 buildSrc 预编译脚本插件（插件 id 按 `tny.<域>` 命名，如 `tny.java-module`、`tny.publish-gate`）：装配类脚本（子工程基线、插件模块线、Java 模块线、发布元数据、门禁、两级验证通道、BOM 约束、基准套件、集成测试隔离）迁入约定插件；根构建脚本对每条装配线只保留一行 `apply plugin` 引入。重复配置块收编为可组合的公共约定插件（仓库与镜像路由、编译基线、动态依赖零缓存），各线按需组合，不再逐字复制。
- 消除脚本间重复段落：仓库表两处、零缓存策略两处、编码钉定四处、logging 排除两处、createProject 两处等同构内容合并为单一实现（个别语义确有差异的保持分离并在注释写明差异来由）。
- 迁移过程中顺手修正勘察发现的两处既有地雷：tny-game-doc-gradle 构建文件里的裸表达式语句 `libs.'groovy-all'`（无任何作用且在新机制下会因连字符别名解析失败而报错，删除）；`dependencyManagement` 内以 GString 拼接目录对象的写法在新机制下改为目录访问器的确定形态。
- 同步修订 gradle-build-style 能力规格的四条需求文本（程序性行为容身之处、版本与坐标事实源、文件头注释与长度界线的适用载体），使其接纳并约束新结构；需求文本的行为语义等价迁移，不放松任何既有纪律。规格差量详见本变更 specs 工件。
- 验收按零差异红线执行：任务名集合、任务图执行面、解析出的依赖版本、构件内容、发布 POM 元数据、CI 调用面与迁移前基线逐项 diff 为零（与上一变更同一套对照手段：tasks --all、dependencies 报表、publishToMavenLocal 清单）。
- 不触及任何公共 Java API 与报文协议，无 BREAKING。

## Capabilities

### New Capabilities

无。本次不引入新能力：版本目录与约定插件都是 gradle-build-style 所辖同一主题（构建脚本书写形态）下的载体更换，不产生新的外部行为契约。

### Modified Capabilities

- `gradle-build-style`：四条需求随新载体改写。需求"工程配置写声明式语句，程序性行为限定容身之处"把程序性行为的合法容身之处从"任务动作块与 gradle/ 专用编排脚本"扩展为"任务动作块与 buildSrc 约定插件脚本"，根脚本引入形态相应改为命名插件；需求"依赖版本与坐标由单一事实源管理"把两处事实源改写为"gradle.properties 的 BOM 级大头版本与 gradle/libs.versions.toml 版本目录"，模块声明面继续只允许具名引用；需求"注释解释配置来由，编排脚本提供预览通道"与需求"扫读测试与长度界线作为验收标准"的适用载体由"gradle/ 辅助脚本"平移为"约定插件脚本"，界线数值不变。

## Impact

- **受影响文件**：settings.gradle（如需登记目录或调整 pluginManagement 则动，默认目录路径可自动发现，预计不动）、根 build.gradle（引入形态重写）、gradle/ 目录（依赖表脚本删除、编排脚本迁出、新增 libs.versions.toml）、新增 buildSrc/ 目录（约定插件与其构建脚本）、四十八个引用 libs 的模块构建文件（访问器名不变，仅删除 tny-game-doc-gradle 一处裸表达式与个别 GString 拼接点）。
- **公共 API 与下游**：不触及 Java 源码与公共接口；已发布构件坐标、POM 元数据、依赖解析版本经基线对照保持不变，各 starter 与下游业务工程无感知，不标注 BREAKING。
- **构建系统面**：新增 buildSrc 使每次构建多一道插件编译步骤（内容不变时增量跳过，实测增幅记入验证记录，预期在数秒级）；configure-on-demand、JMH、nmcp、GrGit、io.spring.dependency-management 的既有版本与插件声明不动，Gradle 8.5 对版本目录（7.4 起）与预编译脚本插件均原生支持。CI 工作流继续以任务名调用，无需改动。
- **验证手段**：迁移前采集与上一变更同款的三份基线；每批次后跑全量构建与对应定点探针；终验做任务清单、依赖报表、发布构件三方零差异比对。本变更不新增 JUnit 测试（不改公共 API 与协议行为），验证由构建对账承担。
- **回退**：批次化提交边界保留（每批次可单独回退）；迁移完成前 gradle/ 旧脚本与新插件不双跑，逐域替换。
