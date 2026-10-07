# Tasks

## 1. 收敛改写

- [x] 1.1 tny.subprojects-baseline 的 dependencyManagement dependencies 块改为分组别名列表加 each 循环（组间一行注释：redisson 单列、commons 族、注解校验对、根 BOM 外补仨；循环行注释写明托管表只吃字符串故经 coord 取串），条目集合与顺序保持 20 项原序；运行 `./gradlew projects -q` 评估通过。

## 2. 零差异验收

- [x] 2.1 四模块 dependencies 报表对 openspec/changes/archive/2026-10-02-centralize-resolution-config/baseline/ 四报表（节规范化比对）零差异——marker 变更组 3 已证与同一基线零差异，链式等价；`./gradlew publishToMavenLocal` 后 BOM 与 net 两份 POM 及 m2 清单零差异；`./gradlew clean build` 绿；三项结论记入变更目录 verification-notes.md（如遇案卷在册偶红按单任务复跑取证纪律处理）。
