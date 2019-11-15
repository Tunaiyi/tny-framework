package com.tny.game.base.item.behavior;

import java.util.*;

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
    List<DemandResult> getBehaviorDemandResultList();

    /**
     * 获取操作结果Map
     *
     * @return
     */
    Map<Action, ActionResult> getActionResultMap();

}
