# Design

## Context
单文件微变更，动机与形态选择已在 proposal.md - Why/What Changes 记录（托管表 notation 只吃字符串故 coord 桥保留；20 行同构样板收成分组列表+循环）。

## Decisions
本变更无独立技术决定需要记录；唯一有记录价值的一条：
**循环放插件不放模块**——gradle-build-style 需求一（程序性行为容身于约定插件与任务动作块）的直接适用，先例为 benchParams 迁入 tny.bench-suite、bom-platform 的 each 装配。被否决备选：维持 20 行逐条（否决：与去样板目标相悖，且逐行并无额外信息量——条目名即全部语义）。

## Risks / Trade-offs
无超出 proposal 已述者。验收链式基线等价性论证见 verification-notes.md（marker 组 3 已证同一基线零差异）。
