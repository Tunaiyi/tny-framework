package com.tny.game.net.base;

/**
 * 默认 app 范围
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/9 17:25
 **/
public enum DefaultAppScope implements AppScope {

    /**
     * 上线
     */
    ONLINE(1, "online"),

    /**
     * 开发
     */
    DEVELOP(2, "develop"),

    /**
     * 测试
     */
    TEST(3, "test"),

    ;

    private final int id;

    private final String scopeName;

    DefaultAppScope(int id, String scopeName) {
        this.id = id;
        this.scopeName = scopeName;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String getScopeName() {
        return scopeName;
    }

}
