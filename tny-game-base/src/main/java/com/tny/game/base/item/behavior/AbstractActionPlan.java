package com.tny.game.base.item.behavior;

import com.google.common.collect.*;
import com.tny.game.base.exception.*;
import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.simple.*;
import com.tny.game.base.item.xml.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;

import java.util.*;

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
    protected Map<Option, ExprHolder> optionMap;

    @Override
    public Set<Action> getActions() {
        return actions;
    }

    private Action checkAction(Action action) {
        if (actions.contains(action))
            return action;
        Throws.checkNotNull(null, "{] action is null", action);
        return null;
    }

    @Override
    public DemandResultCollector tryToDo(long playerID, boolean tryAll, DemandResultCollector collector, Map<String, Object> attributeMap) {
        this.checkResult(playerID, this.demandList, tryAll, collector, ItemsImportKey.$ACTION_DEMAND_PARAMS, attributeMap);
        if (!tryAll && collector.isFailed())
            return collector;
        if (this.costPlan != null) {
            this.costPlan.tryToDo(playerID, tryAll, collector, attributeMap);
        }
        return collector;
    }

    @Override
    public List<DemandResult> countDemandResult(long playerID, Map<String, Object> map) {
        return this.countAllDemandResults(playerID, this.demandList, ItemsImportKey.$ACTION_DEMAND_PARAMS, map);
    }

    @Override
    public ActionResult getActionResult(long playerID, Action action, Map<String, Object> attributeMap) {
        List<DemandResult> resultList = this.countAllDemandResults(playerID, this.demandList, ItemsImportKey.$ACTION_DEMAND_PARAMS, attributeMap);
        List<DemandResult> costResultList = this.costPlan == null ? new ArrayList<>() : this.costPlan.countDemandResultList(playerID, attributeMap);
        return new SimpleActionResult(checkAction(action), resultList, costResultList, this.doAwardList(playerID, action, attributeMap));
    }

    @Override
    public AwardList getAwardList(long playerID, Action action, Map<String, Object> attributeMap) {
        action = checkAction(action);
        if (this.awardPlan == null)
            return new SimpleAwardList(action);
        return this.doAwardList(playerID, action, attributeMap);
    }

    @Override
    public CostList getCostList(long playerID, Action action, Map<String, Object> attributeMap) {
        action = checkAction(action);
        if (this.costPlan == null)
            return new SimpleCostList(action);
        setAttrMap(playerID, this.attrAliasSet, attributeMap);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributeMap);
        return this.costPlan.getCostList(playerID, action, attributeMap);
    }

    @Override
    public ActionTrades countTrades(long playerID, Action action, Map<String, Object> attributes) {
        action = checkAction(action);
        Trade award = null;
        Trade cost = null;
        setAttrMap(playerID, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
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
        setAttrMap(playerID, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
        return this.awardPlan.createTrade(playerID, action, attributes);
    }

    @Override
    public Trade createCost(long playerID, Action action, Map<String, Object> attributes) {
        action = checkAction(action);
        if (this.costPlan == null)
            return new SimpleTrade(action, TradeType.COST);
        setAttrMap(playerID, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
        return this.costPlan.createTrade(playerID, action, attributes);
    }

    @Override
    public boolean isHasOption(Option option) {
        return this.optionMap.containsKey(option);
    }

    private AwardList doAwardList(long playerID, Action action, Map<String, Object> attributes) {
        if (this.awardPlan == null)
            return new SimpleAwardList(action);
        setAttrMap(playerID, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
        return this.awardPlan.getAwardList(playerID, action, attributes);
    }

    @Override
    public <O> O countOption(long playerID, Option option, Map<String, Object> attributes) {
        setAttrMap(playerID, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
        ExprHolder formula = this.optionMap.get(option);
        if (formula == null)
            throw new GameRuningException(option, ItemResultCode.ACTION_NO_EXIST);
        return formula.createExpr().putAll(attributes).execute(null);
    }


    @Override
    public void init(ItemModel itemModel, ItemModelContext context) {
        super.init(itemModel, context);
        if (optionMap == null)
            optionMap = ImmutableMap.of();
        if (action != null) {
            actions = ImmutableSet.of(action);
        } else if (actions != null) {
            actions = ImmutableSet.copyOf(actions);
        }
        if (this.awardPlan != null)
            this.awardPlan.init(itemModel, context);
        if (this.costPlan != null)
            this.costPlan.init(itemModel, context);
        for (String alias : this.attrAliasSet) {
            AliasCollectUtils.addAlias(alias);
        }
    }

}
