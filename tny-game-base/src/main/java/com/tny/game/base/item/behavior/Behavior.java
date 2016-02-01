package com.tny.game.base.item.behavior;

import com.tny.game.base.module.Feature;
import com.tny.game.common.enums.EnumID;

/**
 * 行为类型接口
 *
 * @author KGTny
 */
public interface Behavior extends EnumID<Integer> {

    /**
     * 获取模块类型
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

    /**
     * 通过指定值获取对应的Action
     *
     * @param value 对应直
     * @return 对应的Action
     */
    Action forAction(Object value);

}
