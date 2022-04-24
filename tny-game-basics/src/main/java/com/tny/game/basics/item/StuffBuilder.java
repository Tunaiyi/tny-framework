package com.tny.game.basics.item;

/**
 * stuff构建器
 *
 * @param <S>  stuff类型
 * @param <SM> stuffModel类型
 * @param <B>  stuffBuilder类型
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public abstract class StuffBuilder<S extends BaseItem<SM>, SM extends StuffModel, B extends StuffBuilder<S, SM, B>> extends
        ItemBuilder<S, SM, B> {

    protected int number;

    /**
     * 设置number <br>
     *
     * @param number 数量
     * @return 构建器
     */
    public B setNumber(int number) {
        this.number = number;
        return (B)this;
    }

}
