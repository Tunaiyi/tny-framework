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