package com.tny.game.net.transport;


import com.tny.game.common.enums.EnumIdentifiable;


public enum CertificateStatus implements EnumIdentifiable<Integer> {

    /**
     * 无效的
     */
    INVALID(0, false),

    /**
     * 未认证
     */
    UNAUTHERIZED(1, false),

    /**
     * 已认证
     */
    AUTHERIZED(2, true),

    /**
     * 续约认证
     */
    RENEW(3, true),
    ;

    private Integer id;

    private boolean autherized;

    CertificateStatus(Integer id, boolean autherized) {
        this.id = id;
        this.autherized = autherized;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public boolean isAutherized() {
        return autherized;
    }
}