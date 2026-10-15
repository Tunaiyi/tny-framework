# 全绿基线记录（行为保持型变更，无红灯——意图验证过程）

最终 5/5 绿：**领域意图（离线入窗、恢复补收）与现状行为一致**，rename 零行为变更成立。

过程三次红灯全部归因于测试夹具/选择器，未一例指向产品代码：
1. TestTunnel 缺 open()（INIT 态 write 短路）——夹具错；
2. allocate 短路位置错——**关键验证**：读 NettyChannelMessageTransport.write 证实真实链路
   在 eventLoop 任务内无条件 allocate（入窗）后才 writeAndFlush（静默失败）——
   "离线入窗"就是现状物理事实，夹具改为对齐真实时序；
3. resend 区间边界（from==to+OPEN=空集）——换谓词重载规避未声明的边界语义。

## 顺带发现的规格空白（登记，不扩面）
`getSentMessages(fromId, toId, FilterBound)` 的边界语义（OPEN/CLOSE 何端开闭）无文档与测试——
本次绕行。登记进未来 net-session 补约清单（与 send2AllOnline→B 无关的独立小账）。
