package com.tny.game.base.item;

/**
 * @author KGTny
 * @ClassName: ItemOwner
 * @Description: 物品项拥有者
 * @date 2011-11-3 上午9:50:52
 * <p>
 * <p>
 * <br>
 */
public interface CountableStuffOwner<IM extends ItemModel, S extends CountableStuff<?>> extends Owner<S> {

    /**
     * 检测是否满了
     *
     * @param model
     * @param number
     * @return
     */
    boolean isOverUpperLimit(IM model, AlterType type, long number);

    /**
     * 检测是否足够
     *
     * @param model
     * @param number
     * @return
     */
    boolean isOverLowerLimit(IM model, AlterType type, long number);

}