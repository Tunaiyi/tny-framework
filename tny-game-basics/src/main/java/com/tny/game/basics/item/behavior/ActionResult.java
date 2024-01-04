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

import java.util.List;

/**
 * 操作结果 记录操作的条件结果,奖励信息
 *
 * @author KGTny
 */
public interface ActionResult {

    Action getAction();

    /**
     * 获取该操作涉及的非消耗的条件结果
     *
     * @return
     */
    List<DemandResult> getDemandResultList();

    /**
     * 获取该操作设计的消耗条件结果
     *
     * @return
     */
    List<DemandResult> getCostDemandResultList();

    /**
     * 获取该操作的奖励列表
     *
     * @return
     */
    AwardList getAwardList();

}
