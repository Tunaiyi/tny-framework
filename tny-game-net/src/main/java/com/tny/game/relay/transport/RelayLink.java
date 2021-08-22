package com.tny.game.relay.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.relay.packet.*;

import java.net.InetSocketAddress;

/**
 * Client -> Gateway -> GameServer
 * <p>
 * ---------------------------------------------------------------------------------------------------
 * |    Client    |               Gateway           |             |            GameServer            |
 * |-------------------------------------------------------------------------------------------------|
 * |ClientTunnel1 | -> ServerTunnel1 ->             |             |                GameServerTunnel1 |
 * |ClientTunnel2 | -> ServerTunnel2 -> RelayLink  | = Socket => | RelayLink  -> GameServerTunnel2 |
 * |ClientTunnel3 | -> ServerTunnel3 ->             |             |                GameServerTunnel3 |
 * ---------------------------------------------------------------------------------------------------
 * <p>
 * 使用 Gateway 架构时候, Pipe 代表 Gateway 到实际服务器的连接.
 * Pipe 管理着多个Tunnel, 每个Tunnel代表某一Client
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 9:06 下午
 */
public interface RelayLink {

	/**
	 * @return 节点 id
	 */
	int getServeNode();

	/**
	 * @return 连接类型
	 */
	String getServeType();

	/**
	 * @return 线路编号
	 */
	int getServeLine();

	/**
	 * @return 创建时间
	 */
	long getCreateTime();

	/**
	 * @return 获取 Tunnel 状态
	 */
	RelayLinkStatus getStatus();

	/**
	 * @return 返回远程地址
	 */
	InetSocketAddress getRemoteAddress();

	/**
	 * @return 返回本地地址
	 */
	InetSocketAddress getLocalAddress();

	//	/**
	//	 * 创建 repeater
	//	 *
	//	 * @param tunnelId   管道 id
	//	 * @param remoteHost 远程域名
	//	 * @param remotePort 远程端口
	//	 * @return 返回用户对应的 repeater
	//	 * @throws PipeClosedException 通讯通道已关闭异常
	//	 */
	//	NetTunnel<UID> connectTunnel(long tunnelId, String remoteHost, int remotePort) throws PipeClosedException;

	/**
	 * 创建发送答应对象
	 *
	 * @return 发送答应对象
	 */
	WriteMessagePromise createWritePromise();

	/**
	 * 开启
	 */
	void open();

	/**
	 * 关闭
	 */
	void close();

	/**
	 * 是否活跃
	 */
	boolean isActive();

	/**
	 * 接收转发数据包
	 *
	 * @param packet 接收的数据包
	 */
	void receive(RelayPacket<?> packet);

	/**
	 * 发送转发数据包
	 *
	 * @param packet 发送的数据包
	 */
	WriteMessageFuture write(RelayPacket<?> packet, boolean promise);

	/**
	 * 转发消息到目标服务器
	 *
	 * @param tunnelId id
	 * @param message  消息
	 * @param promise  发送应答对象
	 * @return 返回转发应答对象
	 */
	WriteMessageFuture relay(long tunnelId, Message message, WriteMessagePromise promise);

	/**
	 * 转发消息到目标服务器
	 *
	 * @param tunnelId  id
	 * @param allocator 消息装配器
	 * @param factory   消息工厂
	 * @param context   消息上下文
	 * @return 返回转发应答对象
	 */
	WriteMessageFuture relay(long tunnelId, MessageAllocator allocator, MessageFactory factory, MessageContext context);

}
