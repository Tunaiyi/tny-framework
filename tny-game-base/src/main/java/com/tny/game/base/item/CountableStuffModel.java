package com.tny.game.base.item;

/**
 * stuff模型接口，于数量相关的事物模型接口
 *
 * @author KGTny
 */
public interface CountableStuffModel extends ItemModel {

    /**
     * 是否有数量限制 <br>
     *
     * @return
     */
    boolean isNumberLimit();

    /**
     * 数量上线 <br>
     *
     * @param stuff 计算参数
     * @return -1 表示无限制
     */
    long countNumberLimit(Stuff<?> stuff);

}