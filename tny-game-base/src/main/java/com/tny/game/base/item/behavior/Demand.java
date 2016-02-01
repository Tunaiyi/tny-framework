package com.tny.game.base.item.behavior;

import java.util.Map;

/**
 * 条件接口
 *
 * @author KGTny
 */
public interface Demand {

    /**
     * 当前值在公式中所表示的名字
     */
    public static final String CURRENT_VALUE = "current";
    /**
     * 期望值在公式中所表示的名字
     */
    public static final String EXPECT_VALUE = "expect";
    /**
     * 当前条件相关item对象
     */
    public static final String DEMAND_ITEM = "demandItem";
    /**
     * 当前条件相关item对象
     */
    public static final String DEMAND_MODEL = "demandModel";
    /**
     * 计算是否符合消耗物品的条件
     */
    public static final String FX_FORMULA = CURRENT_VALUE + " >= " + EXPECT_VALUE;

    /**
     * 获取条件相关的Item ID
     *
     * @param atrributeMap
     * @return
     */
    public String getItemAlias(Map<String, Object> atrributeMap);

    /**
     * 获取条件类型
     *
     * @return
     */
    public DemandType getDemandType();

    /**
     * ItemID的别名
     *
     * @return
     */
    public String getName();

    /**
     * 是否能达到条件
     *
     * @param attribute 条件计算参数
     * @return 成功返回true 失败返回false
     */
    public boolean isSatisfy(long playerID, Map<String, Object> attribute);

    /**
     * 计算条件期望值
     *
     * @param attribute 条件值期望值计算参数
     * @return 返回条件期望值
     */
    public Object countExpectValue(long playerID, Map<String, Object> attribute);

    /**
     * 计算当前值
     *
     * @param attribute 当前值计算参数
     * @return 返回当前值
     */
    public Object countCurrentValue(long playerID, Map<String, Object> attribute);

//	/**
//	 * 获取条件结果集
//	 *
//	 * @param attribute
//	 *            计算参数
//	 * @return 返回条件结果集
//	 */
//	public DemandDetail createDemandDetail(long playerID, Map<String, Object> attribute);

    public DemandResult checkDemandResult(long playerID, Map<String, Object> attribute);


}