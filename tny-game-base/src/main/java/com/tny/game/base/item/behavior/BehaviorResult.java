package com.tny.game.base.item.behavior;

import java.util.List;
import java.util.Map;

/**
 * 行为结果
 *
 * @author KGTny
 */
public interface BehaviorResult {

    /**
     * 获取行为的条件结果
     *
     * @return
     */
    public List<DemandResult> getBehaviorDemandResultList();

    /**
     * 获取操作结果Map
     *
     * @return
     */
    public Map<Action, ActionResult> getActionResultMap();

}
