/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
     * @return 获取尝试的操作
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
     * @return 所有尝试失败的原因
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
