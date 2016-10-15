package com.tny.game.base.item;

public interface Model {

    /**
     * 事物ID
     *
     * @return
     */
    public int getID();

    /**
     * 获取别名
     *
     * @return
     */
    public String getAlias();

    /**
     * 获取描述
     *
     * @return
     */
    String getDesc();

}
