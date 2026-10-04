# Tasks

> 依赖版本演进变更（无公共 API 改动）：JUnit 5 测试前置条款不适用——既有 `EtcdNamespaceExplorerIT` 全量 36 用例即本变更的判据测试，本册不新增测试代码。操作纪律：本地重跑只用任务级 `--rerun`，禁 `--rerun-tasks`；跑前确认无并行 Gradle 构建。

## 1. 候选档位调研与矩阵计划（design D2/D3 的输入固化）

- [x] 1.1 从 Maven Central 拉取 gRPC 发布清单（`io.grpc:grpc-netty` 的 maven-metadata.xml 与候选档 POM），逐档记录其声明的 Netty 依赖版本；产出候选梯度（约 1.61、1.66、1.68、1.71/1.72、1.74，按实际发布线调整）。验证：`verification/matrix-plan.md` 存在，含每档一行"版本—内嵌 Netty 版本—入选理由"。
- [x] 1.2 查证 jetcd 发布线：`io.etcd:jetcd-core` 是否存在 0.7.7 之后的版本及其 gRPC 需求，结论定 D3 副轴是否激活。验证：matrix-plan.md 追加 jetcd 调研行（版本、gRPC 需求、副轴 激活/休眠）。
- [x] 1.3 组验证：matrix-plan.md 通读自洽——每个候选档都能被 2.x 的单命令覆写方案执行（`-PgrpcVersion=<档> -PnettyVersion=4.1.137.Final`），无隐藏前提。

## 2. 矩阵执行（design D5，本地筛选器）

- [x] 2.1 基线复确认：默认 grpc 1.60 + Netty 137 跑 `EtcdNamespaceExplorerIT`（预期复现 36 挂 31 量级），证明测试环境自洽。验证：结果写入 matrix.md 基线行，与诊断案卷（fix-ci-unit-flakes 登记簿 IT 回归节）的失败签名一致。
- [x] 2.2 逐候选档执行（每档一条命令：`./gradlew :tny-game-namnspace-etcd:integrationTest -PincludeDocker --tests "*EtcdNamespaceExplorerIT*" --rerun -PgrpcVersion=<档> -PnettyVersion=4.1.137.Final`）。验证：matrix.md 每档一行（通过数/36、首个失败签名、耗时），无一缺席。
- [x] 2.3 分界细化：对"通过↔失败"分界两侧各插测一档（仅一次，围绕分界）。验证：matrix.md 记录细化档与分界结论。
- [x] 2.4 组验证：按 D2 选定规则得出唯一候选版本（或得出"全红"结论）；通过档加跑辅判据（`:tny-game-starter-namnspace:test` 与全仓配置阶段 `./gradlew help` 含对账守卫）。验证：选定行写入 matrix.md 并附一句选择理由（对应 D2 的哪条）。

## 3. 落库与本地终验

- [x] 3.1 `gradle.properties` 的 `grpcVersion` 改为选定值（仅此一行；全红则跳过本组，执行 5.2 回退）。验证：`git diff` 单行；`./gradlew help` 对账守卫绿。
- [ ] 3.2 本地全量回归：etcd 模块（test 与 integrationTest docker 档）、starter-namnspace 装配、`./gradlew build --continue`。验证：三组命令全绿，结果摘要记入 matrix.md 落库行。

## 4. CI 终验与案卷销账

- [ ] 4.1 推送并在 CI 终验三条通道（unit、docker 档 integration、e2e 中继拓扑），观察连续三轮 push 无 etcd 相关红。验证：`ci-it-diag` 无新投递且 CI 记录可查（git fetch 案卷分支尖端不变即证）。
- [ ] 4.2 案卷互销：在 `fix-ci-unit-flakes/diagnosis.md` 的"IT 回归跨线记录"节追加终裁行（本册名称、选定版本、CI 三轮通过记录号）。验证：两册记录互相引用闭合。

## 5. 收口（两态通用）

- [ ] 5.1 正常态：`release-note.md`——版本键变更声明、矩阵结论表、对治理册两处账目的更正提醒（"guava 唯一数值变更"与 Netty 实改矛盾；CLAUDE.md 技术栈行 Netty 版本号过时）。验证：`openspec validate upgrade-grpc-for-netty-137` 通过。
- [ ] 5.2 回退态（仅矩阵全红时执行）：以 matrix.md 全红证据向治理册线出具移交记录（建议方案与 D4 一致），本册以"矩阵结论即交付物"收口。验证：移交记录存在且证据链完整（基线行+全候选行）。
- [ ] 5.3 记忆登记：把矩阵实测的兼容分界结论（"jetcd 0.7.7 生态下 gRPC 与 Netty 137 的兼容档位/或全不兼容"）写入项目记忆，注明证据指向本册 matrix.md。验证：记忆条目含可复核的矩阵文件路径。
