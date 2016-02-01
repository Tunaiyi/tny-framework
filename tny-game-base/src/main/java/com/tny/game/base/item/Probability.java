package com.tny.game.base.item;

/**
 * 概率接口
 *
 * @author KGTny
 */
public interface Probability extends Comparable<Probability> {

    /**
     * 获取影响的概率范围
     *
     * @return 概率范围
     */
    public int getProbability();

    /**
     * 优先级
     *
     * @return
     */
    public int getPriority();

}
