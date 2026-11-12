# Proposal

## Why

发布门禁标签存证段的远端名推定存在死代码：`buildSrc/src/main/groovy/tny.publish.gradle:135`
用了 `@{upname}`——这不是 `git rev-parse --symbolic-full-name` 支持的引用写法（有效形式是
`@{upstream}` 或 `@{u}`），该行在任何仓库上都以 fatal 告终，被紧邻的 catch 吞掉后永远落入
"仅当仓库只有一个远端才采用"的兜底分支。后果：在当前单远端仓库上行为恰好正确所以无人察觉，
但注释声明的"当前分支配置了上游则取上游远端"从未生效过；一旦仓库出现第二个远端（fork 工作流、
镜像仓），门禁会以"无法确定远端"拒绝一个本有明确上游的合法发布，属 fail-closed 方向的误伤。

## What Changes

- `@{upname}` 改为 `@{upstream}`，一行修复；其后的取值逻辑（按第一个斜杠切远端名）与
  catch 兜底路径原样保留。
- 行为变化范围：仅门禁存证段的远端名推定——上游存在时从"恒失败走兜底"变为"真实采用上游
  远端"；无上游时仍走单远端兜底；门禁五重校验的判定语义与全部文案不变。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无。主账本 `release-versioning` 第 5 条只约束"远端存在附注标签且解引用指向构建提交"，
不约束远端名的选择方式；本修复让既有实现与门禁代码注释的既定语义一致，属死代码修复而非
行为契约变化，故 `.openspec.yaml` 声明 `skip_specs: true`。）

## Impact

- **构建脚本**：`buildSrc/src/main/groovy/tny.publish.gradle` 一处一行改动；其余零触碰。
- **产品模块与下游**：无。
- **验证面**：现仓库上直接执行被修复的同款命令即可证明上游解析生效（应输出
  `github/5.7.x` 而非 fatal）；开发线门禁路径复跑回归；发布分支形态的完整存证路径与
  `revise-release-branch-flow` 挂账③同池，在首个真实发布（5.7.9 或 5.8.0）时一并核对。
- **关联**：源自 `revise-release-branch-flow` 归档卷宗挂账发现 16（`@{upname}` 无效引用），
  该册门禁 Non-Goal 约束在本册解除——本册只做这一处，不夹带门禁其他改写。
