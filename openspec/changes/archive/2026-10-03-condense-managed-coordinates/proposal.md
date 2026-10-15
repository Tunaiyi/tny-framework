# Proposal

## Why

访问器改造（marker-declared-isolation-targets 组 3）之后，buildSrc 里剩下的最后一处同构样板是 tny.subprojects-baseline 的 dependencyManagement 托管表：20 行 `dependency tny.catalog.CatalogCoord.coord(project, '<别名>')`。该表的 API 只接受字符串 notation（coord() 桥接因此必要、保留），但"每条目一行调用"的写法是纯样板——列表加循环能收成两行，读起来才像配置说明书上的一句话："这些坐标按目录版本托管"。

## What Changes

- tny.subprojects-baseline 的 dependencyManagement dependencies 块改为：一个按族分组的别名列表（redisson、commons 族 15 条、checkerframework/errorprone、guava/javassist/xstream，组间注释一行）加一行 `each` 循环调用 `dependency coord(it)`（循环体在约定插件内，程序性容身之处合规，先例为 benchParams 迁入 tny.bench-suite）。
- coord() 辅助、版本目录、托管表条目集合本身全部不变——纯写法收敛，20 条目一条不增不减。
- 零差异红线照旧：四模块 dependencies 报表、BOM/net 两份 POM 对基线零差异，全量构建绿。
- 不新增公共 API、不改行为，无 BREAKING。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

无。托管表的书写形态属 gradle-build-style 声明域内的等价改写（需求文本不点名逐行或循环形态；插件内循环本就是允许的编排写法），以 skip_specs 声明零规格差量。

## Impact

- **受影响文件**：仅 buildSrc/src/main/groovy/tny.subprojects-baseline.gradle。
- **消费面**：托管版本表语义（哪些坐标以何版本供版）逐条不变；io.spring.dependency-management 对声明顺序不敏感，列表保持原行序以最小化 diff 噪声。
- **验证手段**：`./gradlew :tny-game-net:dependencies :tny-game-doc:dependencies :tny-game-namnspace:dependencies :tny-game-expr-graaljs:dependencies` 四报表对 marker 变更归档基线节规范化零差异；`publishToMavenLocal` 后两份 POM 与 m2 清单零差异；`./gradlew clean build` 绿。
