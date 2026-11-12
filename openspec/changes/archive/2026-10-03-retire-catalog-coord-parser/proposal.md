# Proposal

## Why

`buildSrc/src/main/groovy/tny/catalog/CatalogCoord.groovy`（约 80 行）是我们自己写的文件解析代码：打开 `gradle/libs.versions.toml`（版本目录文件——Gradle 官方的依赖集中声明清单，正常应由 Gradle 解析后以 `libs.xxx` 代码形式提供读取），用正则把文本解析成"组:名:版本"字符串。这段代码产生于一个已经消失的前提：当时 buildSrc 目录下的构建插件里拿不到 Gradle 官方提供的版本目录读取能力，只能自己解析文件。2026-10-02 的 marker-declared-isolation-targets 变更给 buildSrc 注册了同一份目录文件之后，插件内已经能用官方方式读取——自己解析文本的代码失去了存在理由，继续留着反而是"同一份文件、两种读法"的重复实现：目录文件格式一旦调整（比如新增带分类器的条目写法），官方读取跟着 Gradle 升级走，自写解析却会悄悄解析错。

## What Changes

- `tny.subprojects-baseline.gradle` 里集中声明依赖版本的那段列表循环，改为通过官方版本目录读取：`dependency coord(alias)` 调用改为插件内一个本地辅助方法——用官方访问器按名字取到依赖信息，按公开属性逐段拼出"组:名:版本"字符串（不依赖对象的 toString 输出格式，理由见 design D2）。
- 删除 `tny/catalog/CatalogCoord.groovy` 及其所在的空 `tny/catalog/` 目录；删除后 buildSrc 内不再有任何自行解析 TOML 的代码。
- 集中声明的版本数值、条目集合、顺序全部不变——改的只是"从哪个通道读"。
- 行为零变化验收照旧：四个模块的依赖解析报告、BOM 与 net 两份 POM 文件、本地仓库构件清单与改动前逐字节一致，全量构建通过。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。本变更不改变任何外部可见行为（依赖解析结果、发布物完全一致），也不改变 gradle-build-style 各需求的文字与判定标准，以 skip_specs 声明零规格差量。

## Impact

- **受影响文件**：`buildSrc/src/main/groovy/tny.subprojects-baseline.gradle`（改读取方式）、删除 `buildSrc/src/main/groovy/tny/catalog/CatalogCoord.groovy`。
- **消费面**：`tny.catalog.CatalogCoord` 全仓只有上述一处调用（删除前用 grep 复核），无其他依赖方。
- **不受影响**：模块构建文件里的 `libs.xxx` 写法、版本目录文件本身、settings.gradle 的注册、发布链、任务清单。
