package com.tny.game.net.base;

/**
 * 默认用户类型
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/6 15:18
 **/
public enum NetMessagerType implements MessagerType {

    /**
     * 匿名
     */
    ANONYMITY(0, ANONYMITY_USER_TYPE),

    /**
     * 默认用户
     */
    DEFAULT_USER(1, DEFAULT_USER_TYPE),
    //

    ;

    private final int id;

    private final String group;

    NetMessagerType(int id, String group) {
        this.id = id;
        this.group = group;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String getGroup() {
        return group;
    }
}
