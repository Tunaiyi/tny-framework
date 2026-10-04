# Proposal

## Why

分支流探索核出了现行发布机制的三处结构矛盾、一个流程空白，以及分支分层与 `main` 的两个
定位问题。发布机制方面：合回用变基加 `--force-with-lease` 重写发布容器，把容器头与标签
拆离，使"一次性发布容器"与"补丁谱系不经过开发线"两条书面声称被机制本身击穿，而且合回之后
对容器分支重放 CI 必然被门禁第 5 重校验拒绝；打标签绑死在切支瞬间，hotfix 的标签必然落后
于制品提交，命令文档被迫写明"hotfix 不要用 releaseCutAndTag"，工具只覆盖发布流程的一半；
维护线的修复如何传播到更新版本线没有任何流程回答。分层与主干方面：现行模型允许补丁发布
携带新特性（5.7.8 之后线头继续长特性、随 5.7.9 发出），对一个"下游兼容性是硬约束"的框架
而言，补丁号对下游说谎；而 `main` 头停着 2024 年 1 月 11 日的提交、落后当前开发线 129 个
提交且零独有内容，却仍占据 GitHub 默认分支、PR 默认底分支与 CI 触发清单三个入口位置——
框架需要一条固定名字的开发顶点，落地页填不上这个位子。

## What Changes

- 分支体系定形为三层（2026-10-02 用户拍板，详见 design.md 决策 D8）：`main` 是特性与
  日常修复的开发顶点（不发布制品，是所有 PR 的默认底分支与 CI 恒常基线）；`5.7.x` 是
  功能冻结线（切自 main，冻结该次版本特性，此后只接收修复，夜间产出滚动快照）；
  `5.7.k.release` 容器加标签 `v5.7.k` 是不可移动的发布层。开新次版本线改为从 main 切出，
  不再从旧线切出。
- 合回机制从"重写发布容器再快进开发线"改为"把容器独有提交重放到所属线头、普通推送"；
  发布容器分支与其标签一经创建推送，任何后续操作都不再移动。`--force-with-lease` 及其
  失败降级分支整体退役。
- 切支与打标签解耦为三步任务：`releaseCut`（切出并推送容器分支）、`releaseTag`（在容器
  HEAD 建附注标签并推送）、`releaseMergeBack`（重放合回所属线）。常规发布是"切支后立即
  打标签"，hotfix 是"修复定型后打标签"，两种场景走同一条工具路径；hotfix 的切基固定
  为上一个版本的标签（制品证据点）。**BREAKING**（工具界面破坏：任务 `releaseCutAndTag`
  与 `releaseRebaseBack` 退役更名，引用它们的文档与命令由本变更同步改写；CI 工作流不引用
  这两个任务名，无 CI 影响）。
- **BREAKING（下游契约变更，2026-10-02 用户确认）**：`5.7.x-SNAPSHOT` 的语义从"线上最新
  一切内容"收窄为"5.7 系列的最新维护态"——特性不再随补丁版本或线内快照到达下游，只随
  次版本线（如 `5.8.x-SNAPSHOT` 与 5.8.0 发布）到达。本变更附带下游迁移说明义务
  （任务 5.4）。
- 跨线传播与分支生命周期七条流程入文档（design.md 决策 D4、D9）：创建特性版本分支、
  创建特性分支、rebase 特性分支、发布特性版本、创建 fix 分支、rebase fix 分支、
  同步 fix 提交。传播方向为"最上游优先"：修复原型落 main（常规）或所属线（线原生），
  一笔修复最多停靠两站；波及线清单写在修复 PR 描述里，`git cherry` 做只读对账。
  不建下传工具任务（波及判定是人的决定）。
- `main` 转正而非冻结：实测 `main` 是 `5.7.x` 的严格祖先，故以一次普通快进推送
  （`git push github 5.7.x:main`）把 main 对齐到当前内容顶点，非快进即拒是天然保险丝；
  GitHub 默认分支保持 main 不变；本地化石分支 `master` 删除；`build.yml` 的 `main`
  触发项由死配置转为活主干，线入口按形态通配或列举核实。
- 发布能力新增一条规格需求："发布容器与标签一经创建不可移动"（`release-versioning` 能力
  的 ADDED 差量，四个场景）；既有五项需求文本零改动。
- 通道基线沿用 5.7.8 首发实战后的现状：写操作走 git 子进程，只读查询走 grgit；
  `migrate-git-calls-to-grgit` 变更"写路径统一 grgit 化"的目标由本变更宣告终结（其实录
  保留在该变更目录中供追溯）。
- `buildSrc/src/main/groovy/tny.release.gradle`（原 `gradle/release.gradle` 经
  `adopt-gradle-official-dsl` 插件化后的后继文件）重写为满足 `gradle-build-style`
  规格全部八条需求的约定插件（惰性任务注册、单引号纪律、注释只记原因、三十秒扫读可答问）。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `release-versioning`: 新增一条需求"发布容器与标签一经创建不可移动"（差量文件
  `specs/release-versioning/spec.md`，ADDED 段落，四个场景在三层形态下逐字成立）；
  既有五项需求（裸号形态、快照坐标稳定、白名单、同号黑名单、标签存证前置）文本与判定
  零改动。

## Impact

- **构建脚本**：`buildSrc/src/main/groovy/tny.release.gradle`（原 `gradle/release.gradle`
  经 buildSrc 插件化合入后的后继文件）整体重写（三个任务替代两个任务），根
  `build.gradle:20` 的引用注释随行更名（`apply plugin: 'tny.release'` 单行装配不变）。
  门禁插件 `buildSrc/src/main/groovy/tny.publish.gradle` 与版本派生插件 `tny.git.gradle`、
  扩展类 `tny.convention.GitFlow` 在本变更范围内零改动——五重校验的判定输入在新时序下
  天然成立；main 不发布制品由既有白名单需求直接覆盖，无需新校验。
- **产品模块与下游**：不改任何 `tny-game-*` 模块的公共 API、报文协议或 starter。下游
  可感知的变化只有一处：滚动快照坐标的内容口径收窄（见 What Changes 的契约变更条），
  坐标字符串本身不变。
- **文档与命令**：`docs/release-process.md` 重写为"分支角色总表＋七条生命周期流程＋发布
  三步时序＋容器标签不可移动律＋传播纪律＋契约变更声明"；`.claude/commands/tny/release.md`
  按三步任务改写操作流程。
- **仓库运维（手工步骤，进任务清单）**：main 快进转正（一次普通推送）；删除本地
  `master`；`build.yml` 触发清单核对与真实触发验证（main 与现役线各一次）；GitHub
  默认分支不动。
- **关联变更**（开工时核销）：`migrate-git-calls-to-grgit` 已于 2026-10-03 归档，其写通道
  统一目标由本变更定案终结（终结说明登记在本变更账本）；`sweep-gradle-build-style` 与
  `adopt-gradle-official-dsl` 已合入 HEAD，组 1 以插件化后的文件形态为起点；
  `central-publish-tnydev-group` 的验证记录引用旧任务名，属对方会话账本，不在本变更改写；
  进行中的 `adopt-gradle-8-14-baseline` 不触碰发布插件，其根脚本在途改动与本变更的
  注释行编辑无文本重叠。
- **存量边界**（verify 轮修正）：5.7.8 首发按旧机制完成，其实测容器头与标签解引用已经
  分叉（旧机制在发布后移动过容器头），按"旧机制末例"处置——不追溯、对账以标签为准，
  详见 design.md Context 的存量分叉段；新机制自下一个发布（5.7.9 起）生效。现有 `5.7.x`
  线的直接未来身份是"5.7 冻结线"，无需迁移操作；特性开发区自本变更运维步骤完成起改在
  main 上继续。
