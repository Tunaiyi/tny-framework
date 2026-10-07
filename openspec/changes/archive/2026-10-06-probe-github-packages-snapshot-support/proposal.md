# Proposal

## Why

主账本能力 `github-packages-mirror` 的需求"快照构件不进入镜像通道"所依据的"快照不可用"结论存在证据分层缺口：2026-10-05 沙箱实测（归档记录 `openspec/changes/archive/2026-10-05-add-github-packages-channel/verification/sandbox-probe.md`）已经证实两件事——Gradle 上传快照动作可行（带时间戳文件名互不冲突）、curl 直连探测下快照字面名文件返回 404 且注册表元数据不含快照时间戳块。但两件事没有证实：其一，**真实构建工具作为消费方**解析镜像上的快照坐标时的实际行为形态（Gradle 与 Maven 各如何失败、失败在哪一步）；其二，**注册表服务端是否接受外部发布者自造的 unique snapshot 布局**（即客户端生成含 `<snapshot>` 时间戳块的目录元数据加带时间戳文件名的构件，Maven 官方部署工具链的标准产物形态）——官方文档对 Maven 工具向 GitHub Packages 发布 unique 快照的行为零记载，而这是判断"快照镜像通道在技术上是否根本不可能"的决定性事实。在该事实缺位的情况下，"将来若 GitHub 生态消费方出现快照需求，应走什么路线"无法回答，主账本现行需求的"不可用"依据也只能算强推断而非终局实证。

本变更是一次纯实测调查（spike）：在一次性沙箱仓库完成上述两组实验，把证据补齐并给出裁决建议。

## What Changes

- 新建一次性私有沙箱仓库（如 `Tunaiyi/gpr-snapshot-spike`），生命周期在本变更内闭环（实测完成后删除，随包一并清理）。
- 实验一（消费端真实解析）：向沙箱镜像发布 Gradle 快照构件后，用独立的消费者工程分别以 Gradle 依赖解析与 Maven 命令行 `dependency:get`（本机无 mvn，经 Docker 容器 `maven:3.9-eclipse-temurin-21` 执行）解析该快照坐标，记录各自的请求序列与失败形态。
- 实验二（服务端对 unique 快照布局的受理性）：用 HTTP 客户端直接向沙箱镜像的快照目录 PUT 一套按 Maven 规范自造的 unique 布局（含 `<snapshot><timestamp><buildNumber>` 的目录级 `maven-metadata.xml` 加带时间戳文件名的 POM 与 jar），随后观察注册表是否保留该元数据（还是覆盖或拒绝），并再次用实验一的两种消费端解析同一坐标，验证成败。附加观察：对同一快照版本第二次 PUT 新时间戳构建，元数据可否滚动。
- 交付物是实测记录与裁决建议（写入本变更目录 `verification/snapshot-spike.md`），三种结果各对应明确的后续路线建议（详见 design 的判定表）。
- **零生产代码改动**：不修改本仓构建脚本、CI 工作流、文档与主账本规格；主账本需求"快照构件不进入镜像通道"在调查期间维持原样，是否修订由实测结论经独立变更裁决。

## Capabilities

### New Capabilities

（无。本变更为纯实测调查，不引入新契约。）

### Modified Capabilities

（无。本变更不修改任何既有需求的条文；`github-packages-mirror` 的现行需求保持不变，调查结论作为未来是否修订它的裁决材料登记于本变更归档目录。）

> 本变更 `.openspec.yaml` 已声明 `skip_specs: true`：调查不改变外部可观察行为，按规格流程规则不得为通过校验虚构需求。

## Impact

- 生产代码：零改动（构建脚本、CI、文档、主账本均不触碰）。
- GitHub 侧：新建并删除一次性私有沙箱仓库 `gpr-snapshot-spike`；向沙箱 Packages 真实上传测试构件（体量 KB 级，公共计费额度内）；凭据使用本机 gh 登录态的 classic 令牌经环境变量临时注入，不落盘不进日志。
- 交付工件：本变更目录内 `verification/snapshot-spike.md` 实测记录与裁决建议；对账清单（哪个实验对应主账本哪句依据）。
- 下游模块与 starter：无影响（消费端实验使用独立临时工程，不涉及框架模块）。
- 结论的三个出口：实验证明 unique 布局可被接受且消费端可解析时，建议另立变更评估"引入 unique 快照发布端"的成本收益（涉及 Maven 工具链或自制生成任务，预期代价高）；证明服务端拒绝或消费端仍不可解析时，主账本"快照构件不进入镜像通道"的需求获得终局实证，在归档注记中登记即可关闭该疑问。
