/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.basics.item.behavior;

import java.util.Map;

/**
 * 条件接口
 *
 * @author KGTny
 */
public interface Demand {

    /**
     * 获取条件相关的Item ID
     *
     * @param attributeMap
     * @return
     */
    String getItemAlias(Map<String, Object> attributeMap);

    /**
     * 获取条件类型
     *
     * @return
     */
    DemandType getDemandType();

    /**
     * ItemID的别名
     *
     * @return
     */
    String getName();

    /**
     * 是否能达到条件
     *
     * @param attribute 条件计算参数
     * @return 成功返回true 失败返回false
     */
    boolean isSatisfy(long playerId, Map<String, Object> attribute);

    /**
     * 计算条件期望值
     *
     * @param attribute 条件值期望值计算参数
     * @return 返回条件期望值
     */
    Object countExpectValue(long playerId, Map<String, Object> attribute);

    /**
     * 计算当前值
     *
     * @param attribute 当前值计算参数
     * @return 返回当前值
     */
    Object countCurrentValue(long playerId, Map<String, Object> attribute);

    //	/**
    //	 * 获取条件结果集
    //	 *
    //	 * @param attribute
    //	 *            计算参数
    //	 * @return 返回条件结果集
    //	 */
    //	public DemandDetail createDemandDetail(long playerId, Map<String, Object> attribute);

    DemandResult checkDemandResult(long playerId, Map<String, Object> attribute);

}