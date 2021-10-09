package com.tny.game.basics.item.behavior;

import com.google.common.collect.ImmutableMap;
import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.simple.*;
import com.tny.game.expr.*;

import java.util.*;
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
    protected Map<Option, ExprHolder> optionMap;

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
    public List<DemandResult> countAllDemandResults(long playerId, Map<String, Object> attributes) {
        return this.countAllDemandResults(playerId, this.demandList, ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributes);
    }

    @Override
    public DemandResultCollector tryToDo(long playerId, Action action, boolean tryAll, Map<String, Object> attributeMap) {
        DemandResultCollector collector = new DemandResultCollector();
        ActionPlan actionPlan = this.getActionPlan0(action);
        this.checkResult(playerId, this.demandList, tryAll, collector, ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributeMap);
        if (!tryAll && collector.isFailed()) {
            return collector;
        }
        actionPlan.tryToDo(playerId, tryAll, collector, attributeMap);
        return collector;
    }

    @Override
    public Trade countCost(long playerId, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerId, this.attrAliasSet, attributeMap);
        this.countAndSetDemandParams(ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributeMap);
        return actionPlan.createCost(playerId, action, attributeMap);
    }

    @Override
    public Trade countAward(long playerId, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerId, this.attrAliasSet, attributeMap);
        this.countAndSetDemandParams(ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributeMap);
        return actionPlan.createAward(playerId, action, attributeMap);
    }

    @Override
    public ActionTrades countTrades(long playerId, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerId, this.attrAliasSet, attributeMap);
        this.countAndSetDemandParams(ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributeMap);
        return actionPlan.countTrades(playerId, action, attributeMap);
    }

    @Override
    public ActionResult getActionResult(long playerId, Action action, Map<String, Object> attributeMap) {
        setAttrMap(playerId, this.attrAliasSet, attributeMap);
        this.countAndSetDemandParams(ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributeMap);
        List<DemandResult> resultList = this.countAllDemandResults(playerId, this.demandList, ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributeMap);
        ActionPlan actionPlan = this.getActionPlan0(action);
        ActionResult actionResult = actionPlan.getActionResult(playerId, action, attributeMap);
        return new SimpleActionResult(action, resultList, actionResult);
    }

    @Override
    public AwardList getAwardList(long playerId, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerId, this.attrAliasSet, attributeMap);
        this.countAndSetDemandParams(ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributeMap);
        return actionPlan.getAwardList(playerId, action, attributeMap);
    }

    @Override
    public CostList getCostList(long playerId, Action action, Map<String, Object> attributeMap) {
        ActionPlan actionPlan = this.getActionPlan0(action);
        setAttrMap(playerId, this.attrAliasSet, attributeMap);
        this.countAndSetDemandParams(ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributeMap);
        return actionPlan.getCostList(playerId, action, attributeMap);
    }

    @Override
    public BehaviorResult countBehaviorResult(long playerId, Map<String, Object> attributeMap) {
        List<DemandResult> behaviorDemandResults = this
                .countAllDemandResults(playerId, this.demandList, ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributeMap);
        Map<Action, ActionResult> actionResultMap = new HashMap<>();
        for (Entry<Action, ActionPlan> entry : this.actionPlanMap.entrySet()) {
            for (Action action : entry.getValue().getActions())
                actionResultMap.put(entry.getKey(), entry.getValue().getActionResult(playerId, action, attributeMap));
        }
        return new SimpleBehaviorResult(behaviorDemandResults, actionResultMap);
    }

    @Override
    public <O> O countOption(long playerId, Action action, Option option, Map<String, Object> attributes) {
        setAttrMap(playerId, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$BEHAVIOR_DEMAND_PARAMS, attributes);
        ActionPlan actionPlan = this.getActionPlan0(action);
        if (actionPlan.isHasOption(option)) {
            return actionPlan.countOption(playerId, option, attributes);
        }
        ExprHolder formula = this.optionMap.get(option);
        if (formula == null)
            throw new GameRuningException(action, ItemResultCode.ACTION_NO_EXIST);
        return formula.createExpr().putAll(attributes).execute(null);
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

    @Override
    public void init(ItemModel itemModel, ItemModelContext context) {
        super.init(itemModel, context);
        if (this.optionMap == null)
            this.optionMap = ImmutableMap.of();
        this.optionMap = ImmutableMap.copyOf(this.optionMap);
        doInit(itemModel, context);
    }

    protected void doInit(ItemModel itemModel, ItemModelContext context) {

    }
}
