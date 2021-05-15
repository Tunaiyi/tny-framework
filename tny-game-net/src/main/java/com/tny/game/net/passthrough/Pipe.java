package com.tny.game.net.passthrough;

import com.tny.game.net.passthrough.exception.*;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Client -> Gateway -> GameServer
 * <p>
 * -----------------------------------------------------------------------------------------------------
 * |    Client    |               Gateway           |             |            GameServer               |
 * |----------------------------------------------------------------------------------------------------|
 * |ClientTunnel1 | -> ServerTunnel1 ->             |             |                   GameServerTubule1 |
 * |ClientTunnel2 | -> ServerTunnel2 -> GatewayPipe | = Socket => | GameServerPipe -> GameServerTubule2 |
 * |ClientTunnel3 | -> ServerTunnel3 ->             |             |                   GameServerTubule3 |
 * -----------------------------------------------------------------------------------------------------
 * <p>
 * 使用 Gateway 架构时候, Pipe 代表 Gateway 到实际服务器的连接.
 * Pipe 管理着多个Tubule, 每个Tubule代表某一Client
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 9:06 下午
 */
public interface Pipe<UID> {

    /**
     * @return 获取 Tunnel 状态
     */
    PipeStatus getStatus();

    /**
     * @return 返回远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return 返回本地地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * 获取Tubule
     *
     * @param tunnelId 代理tunnel id
     * @return 返回 id
     */
    Optional<Tubule<UID>> getTubule(long tunnelId);

    /**
     * 关闭 Tubule
     *
     * @param tunnelId 代理tunnel id
     * @return 返回用户对应的 Tubule
     */
    Optional<Tubule<UID>> closeTubule(long tunnelId);

    /**
     * 创建 Tubule
     *
     * @param tunnelId      管道 id
     * @param remoteAddress 远程地址
     * @return 返回用户对应的 Tubule
     */
    Tubule<UID> connectTubule(long tunnelId, InetSocketAddress remoteAddress) throws PipeClosedException;

    /**
     * 关闭
     */
    void close();

    /**
     * 是否活跃
     *
     * @return
     */
    boolean isActive();

}
