package com.tny.game.net.transport;

import com.tny.game.net.exception.ValidatorFailException;

import java.util.Optional;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface NetTunnel<UID> extends Tunnel<UID> {

    /**
     * ping
     */
    void ping();

    /**
     * pong
     */
    void pong();

    /**
     * 打开
     */
    boolean open();

    /**
     * 断开
     */
    void disconnect();

    /**
     * 认证
     *
     * @param certificate 认证的凭证
     * @throws ValidatorFailException 接受凭证失败
     */
    void authenticate(Certificate<UID> certificate) throws ValidatorFailException;

    /**
     * @return 返回是否绑定终端
     */
    boolean isBind();

    /**
     * 终端 Endpoint
     *
     * @param endpoint 终端
     * @return 返回是否绑定成功
     */
    boolean bind(NetEndpoint<UID> endpoint);

    /**
     * @return 获取绑定中断
     */
    Optional<Endpoint<UID>> getBindEndpoint();

    /**
     * 设置访问 Id
     *
     * @param accessId 访问 Id
     */
    NetTunnel<UID> setAccessId(long accessId);

}

