package com.tny.game.base.item.behavior;

import com.tny.game.base.exception.GameRuningException;
import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.ActionTrades;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.behavior.simple.SimpleActionResult;
import com.tny.game.base.item.behavior.simple.SimpleAwardList;
import com.tny.game.base.item.behavior.simple.SimpleCostList;
import com.tny.game.base.item.behavior.simple.SimpleTrade;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.formula.FormulaHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 操作方案
 *
 * @author KGTny
 */
public abstract class AbstractActionPlan extends DemandHolderObject implements ActionPlan {

    /**
     * 操作行为
     */
    protected Action action;

    /**
     * 操作行为
     */
    protected Set<Action> actions;

    /**
     * 奖励方案
     */
    protected AbstractAwardPlan awardPlan;

    /**
     * 消耗方案
     */
    protected AbstractCostPlan costPlan;

    /**
     * 操作选项 － 操作选项公式map
     */
    protected Map<Option, FormulaHolder> optionMap;

    @Override
    public Set<Action> getActions() {
        return actions;
    }

    private Action checkAction(Action action) {
        if (actions.contains(action))
            return action;
        ExceptionUtils.checkNotNull(null, "{] action is null", action);
        return null;
    }

    @Override
    public DemandResultCollector tryToDo(long playerID, boolean tryAll, DemandResultCollector collector, Map<String, Object> attributeMap) {
        this.checkResult(playerID, this.demandList, tryAll, collector, attributeMap);
        if (!tryAll && collector.isFailed())
            return collector;
        if (this.costPlan != null) {
            this.costPlan.tryToDo(playerID, tryAll, collector, attributeMap);
        }
        return collector;
    }

    @Override
    public List<DemandResult> countDemandResult(long playerID, Map<String, Object> map) {
        return this.countAllDemandResults(playerID, this.demandList, map);
    }

    @Override
    public ActionResult getActionResult(long playerID, Action action, Map<String, Object> attributeMap) {
        List<DemandResult> resultList = this.countAllDemandResults(playerID, this.demandList, attributeMap);
        List<DemandResult> costResultList = this.costPlan == null ?
                new ArrayList<>() :
                this.costPlan.countDemandResultList(playerID, attributeMap);
        return new SimpleActionResult(checkAction(action), resultList, costResultList, this.doAwardList(playerID, attributeMap));
    }

    @Override
    public AwardList getAwardList(long playerID, Action action, Map<String, Object> attributeMap) {
        action = checkAction(action);
        if (this.awardPlan == null)
            return new SimpleAwardList(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return this.awardPlan.getAwardList(playerID, action, attributeMap);
    }

    @Override
    public CostList getCostList(long playerID, Action action, Map<String, Object> attributeMap) {
        action = checkAction(action);
        if (this.costPlan == null)
            return new SimpleCostList(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributeMap);
        return this.costPlan.getCostList(playerID, action, attributeMap);
    }

    @Override
    public ActionTrades countTrades(long playerID, Action action, Map<String, Object> attributes) {
        action = checkAction(action);
        Trade award = null;
        Trade cost = null;
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributes);
        if (this.awardPlan != null)
            award = this.awardPlan.createTrade(playerID, action, attributes);
        if (this.costPlan != null)
            cost = this.costPlan.createTrade(playerID, action, attributes);
        return new ActionTrades(action, award, cost);
    }

    @Override
    public Trade createAward(long playerID, Action action, Map<String, Object> attributes) {
        action = checkAction(action);
        if (this.awardPlan == null)
            return new SimpleTrade(action, TradeType.AWARD);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributes);
        return this.awardPlan.createTrade(playerID, action, attributes);
    }

    @Override
    public Trade createCost(long playerID, Action action, Map<String, Object> attributes) {
        action = checkAction(action);
        if (this.costPlan == null)
            return new SimpleTrade(action, TradeType.COST);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributes);
        return this.costPlan.createTrade(playerID, action, attributes);
    }

    @Override
    public boolean isHasOption(Option option) {
        return this.optionMap.containsKey(option);
    }

    private AwardList doAwardList(long playerID, Map<String, Object> attributes) {
        if (this.awardPlan == null)
            return new SimpleAwardList(action);
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributes);
        return this.awardPlan.getAwardList(playerID, action, attributes);
    }

    @Override
    public <O> O countOption(long playerID, Option option, Map<String, Object> attributes) {
        setAttrMap(playerID, this.attrAliasSet, this.itemModelExplorer, this.itemExplorer, attributes);
        FormulaHolder formula = this.optionMap.get(option);
        if (formula == null)
            throw new GameRuningException(option, ItemResultCode.ACTION_NO_EXIST);
        return formula.createFormula().putAll(attributes).execute(null);
    }

}
