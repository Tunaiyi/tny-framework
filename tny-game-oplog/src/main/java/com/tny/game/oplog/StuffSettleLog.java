package com.tny.game.oplog;

public abstract class StuffSettleLog {

    /**
     * @return 物品ItemID
     */
    public abstract int getItemID();

    /**
     * @return 存量
     */
    public abstract long getNumber();

    /**
     * @return 获得数量
     */
    public abstract long getReceiveNum();

    /**
     * @return 消耗数量
     */
    public abstract long getConsumeNum();

    /**
     * 获得当前物品 alter 数量
     *
     * @param number 存量
     * @param alter  获得量
     */
    protected abstract void receive(long number, long alter);

    /**
     * 消耗当前物品 alter 数量
     *
     * @param number 存量
     * @param alter  消耗量
     */
    protected abstract void consume(long number, long alter);

}
