# Design

## Context

动机与命名裁决见 proposal.md - Why。实施前事实核对（2026-10-03 现场 grep `BenchSuite` 大小写敏感，排除归档与记录面）：现行指称三行——`buildSrc/src/main/groovy/tny/convention/BenchSuite.groovy` 第 9 行类声明、`tny.benchmark-module.gradle` 第 7 行配置面注释括注与第 16 行 `extensions.create('benchmarkSuite', tny.convention.BenchSuite)` 类型参数；类头注释四行经插件改名册已呈新语境（首行即 `tny.benchmark-module`），无旧名残留。判据安全性实测：`BenchmarkSuite` 连续字面不含 `BenchSuite` 子串（Bench 后接小写 mark），大小写敏感 grep 在新旧名共存时零混淆；`split-bench-suites` 等历史变更名连字符小写形态同样不属判据。主规格、CI、docs 对类名零命中。

## Goals / Non-Goals

Goals：benchmark 词族三层（插件 id、扩展名、实现类）齐整收口。Non-Goals：不改扩展名与五个属性名（`routineFamily` 等）；不改任务名与 `-Pbench*` 属性；不改类头注释与插件内注释的历史变更名指称（`split-bench-suites`、`declarative-it-demo-isolation` 等）；不回改归档与 HANDOFF。

## Decisions

**D1 类名取用户字面 `BenchmarkSuite`。** 复数 Suite 保留（对象是"族清单的集合"，与前册 D1 对扩展名的语境论证同词同义）；文件与类同步移名——Groovy 约定类名与文件名一致，两者不同批会被编译与 IDE 双重困惑。
**D2 报错文案与对账逻辑零触碰。** 本册唯一动到行为的行是 `extensions.create` 类型参数（指向新类，实例化行为逐字节同）；其余全是注释与符号名。
**D3 验收以配置期解析为常驻探针。** 类声明与类型参数任何一侧漏改，`tny.benchmark-module` 应用时（三工程之一或装配线）即报类型不存在红——编译器级探测覆盖全部三处指称；再叠加枚举/对账键集比对与 grep 清零。

## Risks / Trade-offs

- 风险：无。改动面三行加两文件移名，探测链完整（编译、枚举、对账、grep）。
- 权衡：与前两册同型再留"注释里历史变更名含 bench-suite 字样"的表面混杂——属记录原则明保内容，非本册债务。

## Migration Plan

第一步，抓基线（剔噪 `tasks --all` 清单段、`jmhList -PbenchAll` 31 键枚举、族对账绿记录）。第二步，git mv 类文件，类声明与插件两行随改。第三步，验收（三探针、grep 清零、`clean build` 绿——偶红按登记标准处置留痕），记 verification-notes.md。回滚为文件回名与三行还原。
