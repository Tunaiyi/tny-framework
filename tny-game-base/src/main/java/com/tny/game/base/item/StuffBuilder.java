package com.tny.game.base.item;

/**
 * stuff构建器
 *
 * @param <S>  stuff类型
 * @param <SM> stuffModel类型
 * @param <B>  stuffBuilder类型
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public abstract class StuffBuilder<S extends AbstractItem<SM>, SM extends CountableStuffModel, B extends StuffBuilder<S, SM, B>> extends
        ItemBuilder<S, SM, B> {

    protected int number;

    /**
     * 设置玩家ID <br>
     *
     * @param playerID 玩家id
     * @return 构建器
     */
    public B setNumber(int number) {
        this.number = number;
        return (B) this;
    }

}
