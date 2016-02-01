package com.tny.game.base.item.behavior.simple;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.ActionResult;
import com.tny.game.base.item.behavior.BehaviorResult;
import com.tny.game.base.item.behavior.DemandResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
