package com.tny.game.basics.item.behavior;

import com.tny.game.basics.module.*;
import com.tny.game.common.enums.*;

/**
 * 行为类型接口
 *
 * @author KGTny
 */
public interface Behavior extends EnumIdentifiable<Integer> {

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
