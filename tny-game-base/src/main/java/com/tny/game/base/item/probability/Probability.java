package com.tny.game.base.item.probability;

import java.util.Map;

/**
 * 概率接口
 *
 * @author KGTny
 */
public interface Probability {

    /**
     * 获取影响的概率范围
     *
     * @return 概率范围
     */
    int getProbability(Map<String, Object> attributeMap);

    /**
     * 是否生效
     *
     * @param attributeMap
     * @return
     */
    boolean isEffect(Map<String, Object> attributeMap);

}