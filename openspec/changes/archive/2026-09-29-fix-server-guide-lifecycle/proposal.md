# Proposal

## Why

网络引导器（Guide）家族的线程池生命周期建模错误且波及全部四个实现：`NettyServerGuide`（boss+child）、`NettyClientGuide`、`NettyRelayServerGuide`、`NettyRelayClientGuide` 的 `EventLoopGroup` 全部声明为 **static**，而 `close()` 将其关闭——同一 JVM 内多实例（如 demo 网关+业务服同进程）互相扼杀 IO 线程；关闭后的组不可复活，任何"重启监听"场景（集成测试、Spring 上下文重建、动态换端口重绑）永久失效。伴随两处同源缺陷：`isBound()` 硬编码返回 false（接口契约假实现，下游无从判断监听真实状态）；`bind()` 重绑路径误用 `close().awaitUninterruptibly()` 返回值决定摘除、且绑定超时/失败时留下的半开 channel 无人回收。

## What Changes

- 四个 Guide 的 `EventLoopGroup` 从 static 改为**实例持有**（构造/懒建随实例，`close()` 只关自己的组）；`shutdownGracefully` 幂等性保证重复 close 安全。
- `isBound()` 实现为真实状态查询（存在活跃 server channel 即为已绑定）。
- `bind()` 重绑路径修复：旧 channel 无条件关闭并摘除（不再以 close 等待结果作摘除条件）；绑定失败/超时路径关闭半开 channel。

## Capabilities

### New Capabilities

- `net-guide-lifecycle`: 服务器/客户端引导器的实例隔离、监听状态真实性、重绑与失败回收行为契约。

### Modified Capabilities

（无。）

## Impact

- `tny-game-net-netty4`：`network/NettyServerGuide`、`network/NettyClientGuide`、`relay/NettyRelayServerGuide`、`relay/NettyRelayClientGuide`（四文件，构造器/close/bind 局部改动；基类 `NettyBootstrap.createLoopGroup` 工厂保留复用）。
- `ServerGuide.isBound()` 接口声明不变（net 抽象模块零改动，P1），实现由假变真——**行为变更**：此前恒 false，修复后如实反映；调用方仅受益于正确值，无兼容负担（恒 false 不可能有正确依赖方）。
- 多实例部署行为变更（**BREAKING 的正面**）：此前第二个 guide 的 close 会杀死全部 guide 的线程池，修复后互不影响——依赖"关一个全停"作为变相关停机制的部署需改为显式逐个 close（RELEASE NOTE 声明）。
- 线程资源：多实例场景每组独立 boss+child 线程（原共享变独立），同 JVM 多服务器时总线程数上升——按 demo 级实例数可接受；海量实例编排非本框架用法。
- 测试：`tny-game-net-netty4` 新增 guide 生命周期测试（多实例隔离/重启/isBound 真值/失败回收），装配复用 `TestGeneralServerTunnel` 既有工厂面。
