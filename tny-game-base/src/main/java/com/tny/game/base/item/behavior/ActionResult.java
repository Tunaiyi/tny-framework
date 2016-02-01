package com.tny.game.base.item.behavior;

import java.util.List;

/**
 * 操作结果 记录操作的条件结果,奖励信息
 *
 * @author KGTny
 */
public interface ActionResult {

    public Action getAction();

    /**
     * 获取该操作涉及的非消耗的条件结果
     *
     * @return
     */
    public List<DemandResult> getDemandResultList();

    /**
     * 获取该操作设计的消耗条件结果
     *
     * @return
     */
    public List<DemandResult> getCostDemandResultList();

    /**
     * 获取该操作的奖励列表
     *
     * @return
     */
    public AwardList getAwardList();

}
