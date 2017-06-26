package com.tny.game.base.item.behavior;

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
    boolean isSatisfy(long playerID, Map<String, Object> attribute);

    /**
     * 计算条件期望值
     *
     * @param attribute 条件值期望值计算参数
     * @return 返回条件期望值
     */
    Object countExpectValue(long playerID, Map<String, Object> attribute);

    /**
     * 计算当前值
     *
     * @param attribute 当前值计算参数
     * @return 返回当前值
     */
    Object countCurrentValue(long playerID, Map<String, Object> attribute);

//	/**
//	 * 获取条件结果集
//	 *
//	 * @param attribute
//	 *            计算参数
//	 * @return 返回条件结果集
//	 */
//	public DemandDetail createDemandDetail(long playerID, Map<String, Object> attribute);

    DemandResult checkDemandResult(long playerID, Map<String, Object> attribute);

}