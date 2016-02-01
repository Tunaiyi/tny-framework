package com.tny.game.base.item.behavior;

import com.tny.game.base.item.Trade;

import java.util.Map;

/**
 * 交易(扣除/奖励)方案接口
 *
 * @author KGTny
 */
public interface TradePlan {

    /**
     * 创建交易结果集
     *
     * @param action       创建奖励的行为
     * @param atrributeMap 计算参数
     * @return 返回交易结果集
     */
    public Trade createTrade(long playerID, Action action, Map<String, Object> attributeMap);

    /**
     * 交易的方式
     *
     * @return 返回交易方式
     */
//	public AlertType getAlertType();

}
