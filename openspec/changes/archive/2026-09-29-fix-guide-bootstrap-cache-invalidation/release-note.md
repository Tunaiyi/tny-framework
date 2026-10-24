# RELEASE NOTE（fix-guide-bootstrap-cache-invalidation）

- 履约修复：两个服务端 guide 的 close() 现同步销毁 ServerBootstrap 缓存
  （此前只清线程组，重开时 DCL 命中缓存复用绑定死组的旧构建器——
  net-guide-lifecycle "关闭后可重新开启" 合同处于违约态，本次兑现）。
- 违约来源如实记录：由外部审计变更交叉验证发现，非本链自察；根因是
  此前 reopen E2E 测试因装配成本降级为结构断言——降级留下的敞口。
- client 两 guide 豁免裁定（closed 终态闸，无重开承诺），未纳入。
- 行为影响：仅 close→reopen 路径由死变活；不存在依赖旧行为的正确用户。
