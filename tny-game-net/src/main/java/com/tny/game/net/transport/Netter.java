package com.tny.game.net.transport;

import com.tny.game.common.context.*;

/**
 * 网络用户
 * Created by Kun Yang on 2017/3/26.
 */
public interface Netter<UID> {

    /**
     * @return 用户ID
     */
    UID getUserId();

    /**
     * @return 用户组
     */
    String getUserType();

    /**
     * @return 登陆凭证
     */
    Certificate<UID> getCertificate();

    /**
     * @return 是否登陆认证
     */
    boolean isLogin();

    /**
     * @return 是否关闭终端
     */
    boolean isClosed();

    /**
     * 关闭终端
     */
    void close();

    /**
     * @return 获取会话属性
     */
    Attributes attributes();

}
