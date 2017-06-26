package com.tny.game.base.item.behavior;

import com.tny.game.base.exception.GameRuningException;
import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.ActionTrades;
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
    public List<DemandResult> countAllDemandResults(long playerID, Map<String, Object> map) {
        return this.countAllDemandResults(playerID, this.demandList, map);
    }

    @Override
    public DemandResultCollector tryToDo(long playerID, Action action, boolean tryAll, Map<String, Object> attributeMap) {
        DemandResultCollector collector = new DemandResultCollector();
        ActionPlan actionPlan = this.getActionPlan0(action);
        this.checkResult(playerID, this.demandList, tryAll, collector, attributeMap);
        if (!tryAll && collector.isFailed()) {
            return collector;
        }
        actionPlan.tryToDo(playerID, tryAll, collector, attributeMap);
        return collector;
    }

    @Override
    public Trade countCost(long playerID, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return actionPlan.createCost(playerID, action, attributeMap);
    }

    @Override
    public Trade countAward(long playerID, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return actionPlan.createAward(playerID, action, attributeMap);
    }

    @Override
    public ActionTrades countTrades(long playerID, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return actionPlan.countTrades(playerID, action, attributeMap);
    }

    @Override
    public ActionResult getActionResult(long playerID, Action action, Map<String, Object> attributeMap) {
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        List<DemandResult> resultList = this.countAllDemandResults(playerID, this.demandList, attributeMap);
        ActionPlan actionPlan = this.getActionPlan0(action);
        ActionResult actionResult = actionPlan.getActionResult(playerID, action, attributeMap);
        return new SimpleActionResult(action, resultList, actionResult);
    }

    @Override
    public AwardList getAwardList(long playerID, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return actionPlan.getAwardList(playerID, action, attributeMap);
    }

    @Override
    public CostList getCostList(long playerID, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return actionPlan.getCostList(playerID, action, attributeMap);
    }

    @Override
    public BehaviorResult countBehaviorResult(long playerID, Map<String, Object> attributeMap) {
        List<DemandResult> behaviorDemandResults = this.countAllDemandResults(playerID, this.demandList, attributeMap);
        Map<Action, ActionResult> actionResultMap = new HashMap<>();
        for (Entry<Action, ActionPlan> entry : this.actionPlanMap.entrySet()) {
            for (Action action : entry.getValue().getActions())
                actionResultMap.put(entry.getKey(), entry.getValue().getActionResult(playerID, action, attributeMap));
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
        return actionPlan.isHasOption(option) || this.optionMap.containsKey(option);
    }

    private ActionPlan getActionPlan0(Action action) {
        ActionPlan plan = this.actionPlanMap.get(action);
        if (plan == null)
            throw new GameRuningException(action, ItemResultCode.ACTION_NO_EXIST);
        return plan;
    }

}
