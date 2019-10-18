package com.tny.game.base.item.behavior;

/**
 * 交易类型
 *
 * @author KGTny
 */
public enum TradeType {

    /**
     * 奖励
     */
    AWARD(1),

    /**
     * 消耗
     */
    COST(2);

    private final int id;

    private TradeType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static TradeType get(int id) {
        if (AWARD.id == id)
            return AWARD;
        else if (COST.id == id)
            return COST;
        throw new NullPointerException("不存在TradeType id 为" + id);
    }
}
