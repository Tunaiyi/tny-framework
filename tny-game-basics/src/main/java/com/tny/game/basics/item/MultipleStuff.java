package com.tny.game.basics.item;

public interface MultipleStuff<IM extends MultipleStuffModel, N extends Number> extends Stuff<IM> {

    int UNLIMITED = -1;

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
    N getNumberLimit();

    /**
     * 物品数量 <br>
     *
     * @return
     */
    N getNumber();

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