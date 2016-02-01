package com.tny.game.base.item.behavior;

import com.tny.game.base.exception.GameRuningException;
import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.behavior.simple.SimpleActionResult;
import com.tny.game.base.item.behavior.simple.SimpleBehaviorResult;
import com.tny.game.common.formula.FormulaHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 抽象行为方案
 *
 * @author KGTny
 */
public abstract class AbstractBehaviorPlan extends DemandHolderObject implements BehaviorPlan {

    /**
     * 行为
     */
    protected Behavior behavior;

    /**
     * 操作方案
     */
    protected Map<Action, ActionPlan> actionPlanMap;

    /**
     * 操作选项 － 操作选项公式map
     */
    protected Map<Option, FormulaHolder> optionMap;

    @Override
    public Behavior getBehavior() {
        return this.behavior;
    }

    @Override
    public Map<Action, ActionPlan> getActionPlanMap() {
        return this.actionPlanMap;
    }

    @Override
    public ActionPlan getActionPlan(Action action) {
        return this.getActionPlan0(action);
    }

    @Override
    public List<DemandResult> countDemandResult(long playerID, Map<String, Object> map) {
        return this.countDemandResultList(playerID, this.demandList, map);
    }

    @Override
    public DemandResult tryToDo(long playerID, Action action, Map<String, Object> atttributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        DemandResult demandResult = this.checkResult(playerID, this.demandList, atttributeMap);
        if (demandResult != null)
            return demandResult;
        return actionPlan.tryToDo(playerID, atttributeMap);
    }

    @Override
    public Trade countCostResult(long playerID, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return actionPlan.createCost(playerID, attributeMap);
    }

    @Override
    public Trade countAwardResult(long playerID, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return actionPlan.createAward(playerID, attributeMap);
    }

    @Override
    public ActionResult getActionResult(long playerID, Action action, Map<String, Object> attributeMap) {
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        List<DemandResult> resultList = this.countDemandResultList(playerID, this.demandList, attributeMap);
        ActionPlan actionPlan = this.getActionPlan0(action);
        ActionResult actionResult = actionPlan.getActionResult(playerID, attributeMap);
        return new SimpleActionResult(action, resultList, actionResult);
    }

    @Override
    public AwardList getAwardList(long playerID, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return actionPlan.getAwardList(playerID, attributeMap);
    }

    @Override
    public CostList getCostList(long playerID, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return actionPlan.getCostList(playerID, attributeMap);
    }

    @Override
    public BehaviorResult countBehaviorResult(long playerID, Map<String, Object> atttributeMap) {
        List<DemandResult> behaviorDemandResults = this.countDemandResultList(playerID, this.demandList, atttributeMap);
        Map<Action, ActionResult> actionResultMap = new HashMap<Action, ActionResult>();
        for (Entry<Action, ActionPlan> entry : this.actionPlanMap.entrySet()) {
            actionResultMap.put(entry.getKey(), entry.getValue().getActionResult(playerID, atttributeMap));
        }
        return new SimpleBehaviorResult(behaviorDemandResults, actionResultMap);
    }

    @Override
    public <O> O countOption(long playerID, Action action, Option option, Map<String, Object> attributes) {
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributes);
        ActionPlan actionPlan = this.getActionPlan0(action);
        if (actionPlan.isHasOption(option)) {
            return actionPlan.countOption(playerID, option, attributes);
        }
        FormulaHolder formula = this.optionMap.get(option);
        if (formula == null)
            throw new GameRuningException(action, ItemResultCode.ACTION_NO_EXIST);
        return formula.createFormula().putAll(attributes).execute(null);
    }

    @Override
    public boolean isHasOption(Action action, Option option) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        return actionPlan.isHasOption(option) ? true : this.optionMap.containsKey(option);
    }

    private ActionPlan getActionPlan0(Action action) {
        ActionPlan plan = this.actionPlanMap.get(action);
        if (plan == null)
            throw new GameRuningException(action, ItemResultCode.ACTION_NO_EXIST);
        return plan;
    }

}
