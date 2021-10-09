package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.behavior.*;

import java.util.*;

/**
 * 行为结果集
 *
 * @author KGTny
 */
public class SimpleBehaviorResult implements BehaviorResult {

    private List<DemandResult> behaviorResult;

    private Map<Action, ActionResult> actionResultMap;

    public SimpleBehaviorResult(List<DemandResult> behaviorResult, Map<Action, ActionResult> actionResultMap) {
        super();
        this.behaviorResult = Collections.unmodifiableList(behaviorResult);
        this.actionResultMap = Collections.unmodifiableMap(actionResultMap);
    }

    @Override
    public List<DemandResult> getBehaviorDemandResultList() {
        return behaviorResult;
    }

    @Override
    public Map<Action, ActionResult> getActionResultMap() {
        return actionResultMap;
    }

}
