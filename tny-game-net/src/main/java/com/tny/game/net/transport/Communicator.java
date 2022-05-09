package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.message.*;

/**
 * 联系人
 * 具有用户标识, 有通讯状态的对象.
 * Created by Kun Yang on 2017/3/26.
 */
public interface Communicator<UID> extends Messager {

    /**
     * @return 用户ID
     */
    default UID getUserId() {
        return this.getCertificate().getUserId();
    }

    /**
     * @return 用户组
     */
    default String getUserGroup() {
        return this.getCertificate().getMessagerType().getGroup();
    }

    @Override
    default MessagerType getMessagerType() {
        return this.getCertificate().getMessagerType();
    }

    @Override
    default long getMessagerId() {
        return this.getCertificate().getMessagerId();
    }

    /**
     * @return 登陆凭证
     */
    Certificate<UID> getCertificate();

    /**
     * @return 是否登陆认证
     */
    default boolean isAuthenticated() {
        return this.getCertificate().isAuthenticated();
    }

}
