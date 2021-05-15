package com.tny.game.net.transport;

import com.tny.game.common.enums.*;

public enum CertificateStatus implements EnumIdentifiable<Integer> {

    /**
     * 无效的
     */
    INVALID(0, false),

    /**
     * 未认证
     */
    UNAUTHENTICATED(1, false),

    /**
     * 已认证
     */
    AUTHENTICATED(2, true),

    /**
     * 续约认证
     */
    RENEW(3, true),

    //
    ;

    private Integer id;

    private boolean authenticated;

    CertificateStatus(Integer id, boolean authenticated) {
        this.id = id;
        this.authenticated = authenticated;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }
}