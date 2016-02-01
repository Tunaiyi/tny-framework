package com.tny.game.base.item.behavior;

import com.tny.game.base.item.Trade;

/**
 * 尝试执行某操作的结果
 *
 * @author KGTny
 */
public interface TryToDoResult {

    /**
     * 获取尝试的操作
     *
     * @return
     */
    public Action getAction();

    /**
     * 尝试是否成功成功
     *
     * @return 尝试成功返回true失败返回false
     */
    public boolean isSatisfy();

    /**
     * 尝试失败的原因
     *
     * @return 尝试失败的原因，成功返回null
     */
    public DemandResult getUnsatisfyResult();

    /**
     * 获取奖励交易对象
     *
     * @return
     */
    public Trade getAwardTrade();

    /**
     * 获取扣除交易对象
     *
     * @return
     */
    public Trade getCostTrade();

}
