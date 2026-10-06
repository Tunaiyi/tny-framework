# 沙箱实测记录（任务 1.3 至 1.7 交付物）

实测场地：私有沙箱仓库 `Tunaiyi/gpr-sandbox`（任务 5.4 删除）。工程为最小 java 库（组号 `io.sandbox.probe`，构件名 `gpr-sandbox`，另配一个构件名含点号的 `gpr.probe.marker`），Gradle 8.14.5，发布端 `maven-publish`。本地实测凭据为操作者现有 personal access token (classic)（scope 含 repo 与 write:packages，不含 delete:packages），CI 侧实测凭据为工作流内置 `GITHUB_TOKEN`（permissions 含 packages: write）。实测日期 2026-10-05。原始日志存本目录 `logs/`。

## 结论一览

| 序号 | 实测项 | 结果 | 证据 |
|---|---|---|---|
| 1 | 快照版本重复上传（CI 全新构建，先后四次） | **接受**——Gradle 上传快照用带时间戳的新文件名（形如 `gpr-sandbox-0.0.1-20261005.042623-2-javadoc.jar`），每次构建文件名不同故不冲突 | `logs/ci-probe-results.txt` 第 1 组行（run 37266292344、37266493950、37266549023、37267019911、37267153169） |
| 2 | 快照的消费侧可解析性 | **不可解析（决定性结论）**——字面名 `gpr-sandbox-0.0.1-SNAPSHOT.jar` 与 `-SNAPSHOT.pom`、`.module` 全部 404；`maven-metadata.xml` 的 `<versions>` 列出快照坐标但没有 `<snapshot>` 时间戳块；注册表无目录列举端点。Maven 与 Gradle 消费方解析 `0.0.1-SNAPSHOT` 必然失败，上传产物是不可发现的孤儿文件 | `logs/local-http-probe.txt` |
| 3 | 正式版重复上传同一版本（同号二次） | **拒绝**——第一次成功，第二次对同名字节文件 PUT 返回 HTTP 409 并快速失败 | `logs/rel-build1.log`、`logs/rel-build2.log` |
| 4 | 正式版字面文件可解析性 | **正常**——`0.0.2` 的 `.pom` 与 `.jar` 按字面名请求返回 302（鉴权后重定向到存储） | `logs/local-http-probe.txt` |
| 5 | 删除包版本：内置 `GITHUB_TOKEN` 经 GraphQL | **可行**——`deletePackageVersion` 变异以全局节点 ID 调用成功（`{"data":{"deletePackageVersion":...}}`），无需任何新增 secret；注意 REST 数字 ID 与 GraphQL 全局 ID 不通用，须先经 GraphQL 查询取 `...versions.nodes.id`；`gh` 在 Actions 内必须显式注入 `GH_TOKEN` 环境变量 | `logs/ci-probe-results.txt` 第 3、4 组行（run 37267153169） |
| 6 | 删除包版本：classic PAT（无 delete:packages scope） | **同样可行**——含 repo 与 write:packages 的令牌即可删除（官方 scope 矩阵所述 delete:packages 在本案例未成为必要条件） | `logs/local-graphql-delete.txt` |
| 7 | 删除后同号立即重传 | **成功**——本地删除 `0.0.2` 后重传 `0.0.2` BUILD SUCCESSFUL；CI 删除快照版本后重传 accepted | `logs/local-republish-after-delete.log`、`logs/ci-probe-results.txt` 第 5 组行 |
| 8 | 含点号 artifactId 构件 | **被接受**——`gpr.probe.marker` 全构件族上传 BUILD SUCCESSFUL。官方构件命名字符集条文不含点号，实测未触发 422；此数据仅留存，Gradle 插件模块是否纳入镜像仍留待后续独立变更裁决 | `logs/marker.log` |
| 9 | 校验和附属文件（md5、sha1、sha256、sha512） | 全部随构件成功上传，未观测到任何校验和被拒告警（任务 1.6 第三项：无异常可记） | `logs/rel-build1.log` |

## 对本变更设计的裁决影响

1. 结论 2 推翻了拍板决定一（快照进镜像）的可用前提：快照即使持续上传，消费方也无法解析任何快照构件。经用户 2026-10-05 复裁，**撤回决定一，快照不进镜像**（design D2 按此定案：守卫按版本形态排除快照；`publish.yml` 不新增快照镜像步骤；快照的历史可追溯与滚动分发继续由内网 Nexus 快照仓与 Central 快照仓承担）。
2. 结论 1 与结论 2 合并修正了调研存证的两处偏差：Gradle 8.14 快照上传实际使用时间戳文件名（调研推断"不带时间戳的同名文件"不成立），真正障碍不是同号 409 而是消费侧解析缺失。`PLAN-发布构件到GitHub-Packages-2026-10-05.md` 第二节事实 6 与事实 7 按本记录为准。
3. 结论 3 与 7 支持规格需求"同号正式版构件一次成型且重复上传可判读"：正式版重传必 409，删除后重发技术上可行，处置阶梯仍按"升号优先、删后重发需实据"成文。
4. 结论 5 与 6 支持运维预案：镜像半途失败需要删版本时，正式版步骤现有的 `GITHUB_TOKEN`（packages: write）即可执行 GraphQL 删除，无需新增 secret；操作路径已写入 `docs/release-process.md` 新节。
5. 结论 8 解除未来插件线纳入的一个不确定项，但不改变首批排除的决定（proposal 拍板决定三仍有效，裁决推迟到独立变更）。
