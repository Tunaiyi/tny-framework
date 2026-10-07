# Tasks

## 1. 消费面核查（先于实施）

- [x] 1.0 全仓 grep `linkMap`/`getLink` 类查询与 `closeTunnel` 后重新 `getTunnel` 的潜在依赖点，确认无代码把"僵尸条目可查"当功能使用（design R1）；结论记入 `red-baseline.md`

## 2. 行为测试先行（红灯基线）

- [x] 2.1 `tny-game-net/src/test/java/com/tny/game/net/relay/link/RelayExplorerRegistryTest.java`（mock RelayTransport/RelayTunnel，真实 CommonServerRelayLink/BaseRelayExplorer）六用例：
      ① link 物理断开（transport 断链模拟→link.disconnect()）→ 注册表按 id 不可查（当前应红）
      ② link 协议/本地 close() → 同样摘除；且同键置换新 link 后 close 新 link 不误摘（值 CAS；当前摘除缺失应红）
      ③ 隧道 putTunnel 后同标识再次 putTunnel（re-CONNECT 模拟）→ 新条目可查、旧 tunnel `disconnect()` 恰一次（现状应已绿——防回归固化迁移置换）
      ④ link 断开后其隧道条目仍 `getTunnel` 可查（迁移保留反例固化，当前即绿）
      ⑤ `closeTunnel` 后 `getTunnel` 返回 null 且 tunnel.close 恰一次（当前应红）
      ⑥ 重复 `closeTunnel` 第二次为无害空操作（当前应红——二次 close 或 NPE 皆不可）
- [x] 2.2 运行记录红/绿矩阵到 `red-baseline.md`（预期 ①②⑤⑥ 红、③④ 绿）

## 3. 实现摘除

- [x] 3.1 `BaseRelayExplorer.closeTunnel`：`T removed = tunnelMap.remove(key); if (removed != null) removed.close();`（D2）
- [x] 3.2 `DefaultServerRelayExplorer.acceptOpenLink`：`link.open()` 成功路径后 `link.eventWatch()` 挂 RelayLinkListener（仅 onDisconnect 需动作：`linkMap.remove(id, link)`；其余方法空实现或 default——若接口无 default 需实现四方法）（D1）
- [x] 3.3 运行 2.1 六用例：红转绿、绿保持绿

## 4. 责任划分与回归

- [x] 4.1 client 侧核查：`BaseClientRelayExplorer`/`NettyClientRelayExplorer` 的 link/tunnel 注册与清理现状（connector 生命周期内是否已闭环），有同类缺口则同修并入本变更（列 4.2），否则记录结论
- [x] 4.2 `./gradlew :tny-game-net:test :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>` 全量绿
- [x] 4.3 release-note：两类泄漏修复与规模收敛语义；**迁移窗口双路径说明（防后人过度清理）**；"隧道放弃未关闭"边界登记（R3）与需流量数据定策的说明


## Post-archive 验证强化（归档后同日追加，用户指示）

- [x] 5.1 追加四用例（复合键作用域/并发唯一胜者/同键重建防误摘/保留→显式关闭全序列）
- [x] 5.2 用例⑨抓出 equals 语义致误摘缺陷 → computeIfPresent+引用相等修复 → 10/10 绿
