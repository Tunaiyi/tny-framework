package com.tny.game.actor.util;

/**
 * 子分类接口
 * Created by Kun Yang on 16/1/17.
 */
public interface Subclassification<T> {

    /**
     * x 是否与 y相等
     * @param x x对象
     * @param y y对象
     * @return 是否相等
     */
    boolean isEqual(T x, T y);

    /**
     * x 是否是 y的子类型
     * @param x x对象
     * @param y y对象
     * @return 是否是子类型
     */
    boolean isSubclass(T x, T y);

}
