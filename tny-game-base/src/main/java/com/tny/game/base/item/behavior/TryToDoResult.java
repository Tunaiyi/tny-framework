package com.tny.game.base.item.behavior;

import com.tny.game.base.item.Trade;
import com.tny.game.common.result.ResultCode;

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
    Action getAction();

    /**
     * 尝试是否成功
     *
     * @return 尝试成功返回true失败返回false
     */
    boolean isSatisfy();

    /**
     * 尝试是否失败
     *
     * @return 尝试失败返回true失败返回false
     */
    default boolean isUnsatisfied() {
        return !isSatisfy();
    }

    /**
     * 尝试失败的原因
     *
     * @return 尝试失败的原因，成功返回null
     */
    DemandResult getFailResult();

    /**
     * @return 获取结果码
     */
    default ResultCode getResultCode() {
        if (isSatisfy()) {
            return ResultCode.SUCCESS;
        }
        return getFailResult().getResultCode();
    }


    /**
     * 获取奖励交易对象
     *
     * @return
     */
    Trade getAwardTrade();

    /**
     * 获取扣除交易对象
     *
     * @return
     */
    Trade getCostTrade();

}
