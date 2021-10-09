package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;
import com.tny.game.common.result.*;

import java.util.List;

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
     * 所有尝试失败的原因
     *
     * @return
     */
    List<DemandResult> getAllFailResults();

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
