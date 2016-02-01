package com.tny.game.base.item;

public interface CountableStuff<IM extends ItemModel> extends Stuff<IM> {

    int UNLIMINT = -1;

    /**
     * 是否有上限 <br>
     *
     * @return
     */
    boolean isNumberLimit();

    /**
     * 获取上限 <br>
     *
     * @return
     */
    long getNumberLimit();

    /**
     * 物品数量 <br>
     *
     * @return
     */
    long getNumber();

    /**
     * 判断是否足够
     *
     * @param costNum
     * @return
     */
    boolean tryEnough(long costNum);

    /**
     * 是否超出资源上限
     *
     * @return
     */
    boolean tryFull(long costNum);

    /**
     * 是否超过上线
     *
     * @return
     */
    boolean isFull();

}