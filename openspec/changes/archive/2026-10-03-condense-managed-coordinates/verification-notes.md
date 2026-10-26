# 验证记录

- 1.1 收敛：tny.subprojects-baseline 56→47 行，20 条目集合与顺序不变（redisson 1 + commons 族 14 + 注解对与补仨 5），循环在插件内合规（先例 bom-platform/benchParams）；projects 评估 rc=0。
- 2.1 零差异：四模块 dependencies 报表对 centralize 归档基线节规范化零差异；publishToMavenLocal 后 BOM/net 两份 POM 及 m2 全清单零差异；clean build 全绿 1m6s（本批无偶红）。
- buildSrc 内 coord() 现存调用点仅剩这一处托管表循环（java-module/integration-test 已为 libs. 访问器形态）。
