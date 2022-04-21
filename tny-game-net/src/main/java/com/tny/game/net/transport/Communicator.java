package com.tny.game.net.transport;

import com.tny.game.net.command.*;

/**
 * 联系人
 * 具有用户标识, 有通讯状态的对象.
 * Created by Kun Yang on 2017/3/26.
 */
public interface Communicator<UID> {

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
    boolean isAuthenticated();

}
