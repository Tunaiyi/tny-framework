package com.tny.game.net.session;

import com.tny.game.common.concurrent.StageableFuture;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface Communicator<UID> {

    /**
     * @return 用户ID
     */
    UID getUid();

    /**
     * @return 用户组
     */
    String getUserGroup();

    /**
     * @return 登陆凭证
     */
    NetCertificate<UID> getCertificate();

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
     *
     * @return 是否关闭成功, 成功返回true 失效返回false
     */
    StageableFuture<Void> close();

}
