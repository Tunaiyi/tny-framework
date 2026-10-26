# Tasks

## 1. 生命周期测试先行（红灯基线）

- [x] 1.1 摸清测试装配：从 `TestGeneralServerTunnel`/`MockNettyClient` 提炼 `NettyServerGuide` 的最小构造路径（appContext/setting/bindAddress 链），必要时在测试源码建 `GuideFixture`
- [x] 1.2 `tny-game-net-netty4/src/test/.../network/GuideLifecycleTest.java`——四用例（临时端口 bind(0) 取 OS 分配端口）：
      ① 双实例隔离：guide A/B 分别监听，close(A)，向 B 发起真实 Socket 连接+完整报文往返成功（当前 A 关会拖死 B 的组，应红）
      ② 关闭重启：A close 后再次 open 同地址，Socket 可连接（当前组已死，应红）
      ③ isBound 真值链：open 前 false / open 成功 true / close 后 false（当前 open 后仍 false，应红）
      ④ 占用端口重绑失败：先手动 ServerSocket 占端口 → guide open 失败返回不抛挂、isBound false、无半开 channel（`channels` 表空）
- [x] 1.5 运行新测试，红/绿矩阵记入变更目录 `red-baseline.md`

## 2. 实现修复

- [x] 2.1 `NettyServerGuide`：两 group 去 static（构造期实例创建）；`isBound()` 按 D2 实现（channels 存在未关闭 channel）；`bind()` 按 D3 重构（摘旧无条件、失败/超时 close 半开通道不置入）
- [x] 2.2 `NettyClientGuide`、`NettyRelayServerGuide`、`NettyRelayClientGuide`：group 同样实例化，close 只关自有组（grep 逐个核对 close 现状）
- [x] 2.3 运行任务 1.2 的四个测试转绿

## 3. 回归与收尾

- [x] 3.1 `./gradlew :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>` 全绿（既有 42 例 + 新 4 例）
- [x] 3.2 核实 starter 停机链（`NetAutoConfiguration`/`NetApplication` 是否逐 guide close）并记录结论（design R1 实际概率），写入变更目录
- [x] 3.3 `./gradlew :tny-game-starter-net-netty4:compileJava :tny-game-net:test -Dorg.gradle.java.home=<corretto-21>` 编译与守护回归
- [x] 3.4 变更目录写 release-note：多实例隔离修复、isBound 真值化、"关一个全停"巧合行为的迁移说明
