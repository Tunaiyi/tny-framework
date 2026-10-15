# Tasks

## 1. 意图固化测试（行为不变 → 全绿基线型，无红灯）

- [x] 1.1 `tny-game-net/src/test/java/com/tny/game/net/session/SessionBroadcastIntentTest.java`（真实 CommonSession + 同包访问注册；复用 SessionResendSafetyTest 装配模式，tunnel 用 BaseNetTunnel 真子类记录写出）四场景：
      ① 在线会话收广播（tunnel 收到写出）
      ② 离线会话收广播 → tunnel 无写出，但 `getAllSendMessages()` 含该消息（入窗合同）
      ③ 离线→重连接管新 tunnel→resend(窗口区间) → 历史广播补送写出
      ④ 混合在线+离线广播不抛异常、各自处置
- [x] 1.2 运行确认四用例现状全绿（**本变更测的是"意图与现状一致"——任何一条绿变红说明提案对现状行为的理解有误，立即停下上报**）

## 2. 命名迁移

- [x] 2.1 `SessionKeeper`：新增 `void send2All(MessageContent context);`；`send2AllOnline` 改 `@Deprecated default` 委托 `send2All`（注释指向规格条款）
- [x] 2.2 `AbstractSessionKeeper`：实现更名 `send2All`，删除旧实现体（由接口桥承接）；`ContactService:254` 切换 `send2All`
- [x] 2.3 运行 1.1 测试 + 全量：`./gradlew :tny-game-net:test :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>` 全绿；`./gradlew compileJava compileTestJava --continue` 零失败（桥接兼容性证据）

## 3. 收尾

- [x] 3.1 release-note：改名指引（新代码用 send2All；旧名长期可用但 deprecated）、**意图声明醒目化**（"离线入窗是特性，勿按在线过滤'修复'"——写给未来的审计者与模型）
