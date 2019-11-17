package com.tny.game.base.item.behavior;

import com.tny.game.base.item.*;

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
     * @param playerId     玩家ID
     * @param action       行为
     * @param attributeMap 计算参数
     * @return 返回交易结果集
     */
    Trade createTrade(long playerId, Action action, Map<String, Object> attributeMap);

    /**
     * 交易的方式
     *
     * @return 返回交易方式
     */
    //	public AlertType getAlertType();

}
