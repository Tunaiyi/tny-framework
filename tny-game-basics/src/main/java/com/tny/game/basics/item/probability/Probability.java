/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.probability;

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
