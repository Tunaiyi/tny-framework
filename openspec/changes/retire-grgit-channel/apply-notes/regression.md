# retire-grgit-channel 回归记录（2026-10-06）

## 4.1 e2e 十八断言
守卫化修复后全量重跑：**退出码 0、18/18 全绿**（沙箱最新镜像）。

## 4.2 门禁矩阵三例（守卫化后复测）
- G1 main 分支：拒绝文案命中 1 ✓（"不属于可发布形态"原样）
- G3b release/5.8.x 正号 5.8.3：仅剩"发布标签在远端不存在"单条 ✓（形态/下一补丁号/路由全通过）
- G4 祖父线 5.7.x 快照：阻断数 0（全项放行）✓

## 4.3 worktree 冒烟与脏检查五态
- 主仓 HEAD 建 detached linked worktree：`./gradlew help` **成功**（旧禁令解除实证，
  .git 指针文件布局由 CLI 正确解析）；首轮曾在沙箱踩到"worktree 铺的是提交时旧代码"
  与"`-q` 吞 lifecycle 级警告"两个测试方法坑，均已修正后复测。
- 脏检查五态（porcelain 映射对 grgit 桶位的等价性逐态实测）：干净=0、跟踪修改=1、
  staged 新增=1、未跟踪=0、跟踪删除=1——全部符合设计 D2 预期。

## 过程战果（回归抓出的真缺陷）
releaseCut 崩溃 `ArrayIndexOutOfBoundsException: Index 1`：remoteRefNames 对 ls-remote
输出行直接 `split[1]`，遇无空白分隔行越界（e2e 第十四轮暴露，五态探针同点短路）。
已守卫化修复并回归（提交 03150803）。

## 4.4 校验
`openspec validate retire-grgit-channel` 通过；grgiter 活代码零引用；
构建期版本派生回归 5.7.x-SNAPSHOT 不变。
