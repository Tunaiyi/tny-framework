# RELEASE NOTE（tny-game-net-netty4 · fix-server-guide-lifecycle）

## 多实例隔离与重启能力（修复）

- 四个 Guide（network server/client、relay server/client）的 EventLoopGroup 由 static 共享
  改为实例排他（懒建/关闭释放/重启重建）。此前：同 JVM 关闭任一 guide 会拖死其它 guide 的
  IO 线程池，且任何 guide 关闭后无法重新开启（集成测试/Spring 上下文重建/动态重绑全部失效）。
- `isBound()` 由恒 false 改为真实监听状态。
- `bind()` 重绑不再以旧通道关闭结果决定摘除；绑定失败/超时回收半开通道（新增失败日志含 cause）。

## 行为变更与迁移

- 依赖"关一个 guide 顺带停掉全部"作为整体停机手段的部署：改为显式逐个 close
  （框架标准停机链 `NetApplication` 本就逐个 guide.close，标准用法零影响）。
- 同 JVM 多服务器实例现在各持 boss+child 线程组（约 1+2×核数/实例），海量实例编排请评估线程预算。

## demo 手动验收清单（net-guide-lifecycle 行为层）

1. 启动两个不同端口服务器（如 demo gateway + game server 同进程），关闭其一，另一端口
   `nc -vz 127.0.0.1 <port>` 连接保持可服务，客户端 ping/pong 往返正常；
2. 对被关服务器执行重启（再次 open），端口恢复监听并可握手登录；
3. 对已占用端口 open：日志输出带 cause 的失败记录，进程不残留监听线程。

## 登记遗留

- `NettyRelayServerGuide/RelayClientGuide` 的通道重绑细节未套用 D3（仅组实例化），
  relay 侧 bind 语义如需同等加固并入 relay 专项变更。
- `NetApplication:86` 被注释的 guide.close 行待清理（卫生项）。
