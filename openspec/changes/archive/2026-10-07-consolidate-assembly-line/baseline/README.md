# 基线快照 README — consolidate-assembly-line

锚定提交：531e9c4b（本册立项提交，构建文件形态等同 f6bea21a 终态）。

抓样环境沿前册 `archive/2026-10-07-consolidate-binary-conventions/baseline/README.md` 成文口径：Corretto 21、UTF-8 钉住（file.encoding 由 gradle.properties jvmargs）、独立守护进程注册表（改造前后同一 daemon）、PATH 前置 /opt/homebrew/bin:/usr/bin（本机 /usr/local/bin/git 为坏架构二进制的在册环境事实）。

## 样件清单（改造前，2026-10-07 抓取，十一件）

| 文件 | 内容 | 行数 |
|---|---|---|
| tasks-all-before.txt | 全量任务图（剔噪后） | 3586 |
| deps-net-compile-before.txt / deps-net-runtime-before.txt | tny-game-net 双配置依赖清单 | 73 / 94 |
| pom-java-line-before.xml / pom-plugin-line-before.xml / pom-bom-before.xml | 三线发布 POM | 167 / 76 / 302 |
| doc-gradle-supply-before.txt | tny-game-doc-gradle 的 compileGroovy 与 publish 干跑任务行（publishing 仓路由快照）加 encoding/group/version 属性读取记录——插件线 maven-publish 唯一供给面的显名样件 | 全量 |
| publish-tasklines-javaline-before.txt / publish-tasklines-bom-before.txt | java 线（tny-game-net）与 BOM 线 publish 干跑任务名清单（仓库命名契约面：未命名 maven 仓与 githubPackages 命名仓） | 全量 |
| tasks-benchmark-before.txt / tasks-integrationtest-before.txt | 两枚线外模块任务图样件（同名 Java 化行为面锚） | 见文件 |
| help-timing-before.txt | warm help 三连耗时（改造前读数） | 三读 |
