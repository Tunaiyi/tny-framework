package com.tny.game.base.item;

public interface Any<M extends Model> extends Identifiable {

    /**
     * 获取对象ID
     *
     * @return
     */
    long getID();

    /**
     * 获取对象别名
     *
     * @return
     */
    String getAlias();

    /**
     * 获取该事物对象ID
     *
     * @return
     */
    int getItemID();

    /**
     * 获取该事物对象的模型
     *
     * @return
     */
    M getModel();

}
