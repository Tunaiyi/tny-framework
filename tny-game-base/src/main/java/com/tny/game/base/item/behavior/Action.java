package com.tny.game.base.item.behavior;

import com.tny.game.base.module.Feature;
import com.tny.game.common.enums.EnumID;

/**
 * 行为的操作类型接口
 *
 * @author KGTny
 */
public interface Action extends EnumID<Integer> {

    /**
     * 行为的操作ID
     *
     * @return
     */
    Integer getID();

    /**
     * 所属行为
     *
     * @return
     */
    Behavior getBehavior();

    /**
     * 所属系统
     *
     * @return
     */
    Feature getFeature();

    /**
     * 标识
     *
     * @return
     */
    String name();

    /**
     * 描述
     *
     * @return
     */
    String getDesc();

}
