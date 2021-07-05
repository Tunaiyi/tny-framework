package com.tny.game.net.agency;

import javax.annotation.Nullable;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 11:45 上午
 */
public interface NetPipe<UID> extends Pipe<UID> {

    /**
     * 开启
     */
    void open();

    /**
     * 移出指定 tubule
     *
     * @param tubule 移出的tubule
     */
    void destroyTubule(NetTubule<UID> tubule);

    /**
     * @return 获取透传数据发送器
     */
    AgentDatagramTransmitter getTransmitter();

    /**
     * 获取Tubule
     *
     * @param tunnelId 代理Tubule
     * @return 返回 获取Tubule, 无则返回 null
     */
    @Override
    @Nullable
    NetTubule<UID> getTubule(long tunnelId);

    /**
     * 关闭 Tubule
     *
     * @param tunnelId 代理tunnel id
     * @return 返回用户对应的 Tubule, 没有则返回 null
     */
    @Override
    NetTubule<UID> closeTubule(long tunnelId);

}
