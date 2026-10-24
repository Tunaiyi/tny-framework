# 红灯基线说明（任务 1.5）——重构型变更的例外披露

本变更的测试引用了新引入的包级方法（`ensureParentGroup` 等），旧实现下**不可编译**，
无法做传统红灯。采用的替代保证：

1. **断言设计评审**（每条标注旧语义失败方式，写入测试注释）：
   - `assertNotSame(aParent, bParent)`：旧 static 字段两实例必同一对象 → 必红；
   - `assertFalse(bParent.isShuttingDown())`：旧 close 关共享组 → 必红；
   - `assertNotSame(first, rebuilt)`：旧 static final 不可复活 → 必红；
   - isBound `assertTrue`：旧实现字面 `return false` → 必红（该条不依赖新 API，语义自明）。
2. **修复前行为已由两轮 socket 试装实验旁证**（红灯阶段旧代码在装配失败前的
   close 路径与恒 false 均经阅读确认）。
3. 验证层级降级披露：CI 覆盖到"资源排他/可重建/状态真值"层；"连接受理端到端"
   移交 demo 手动验收（清单在 release-note）。降级原因是 guide 全装配需
   NetBootstrap.prepareStart 的 6+ unit 链，其中多个默认实现无 @Unit 注解不可注册。
