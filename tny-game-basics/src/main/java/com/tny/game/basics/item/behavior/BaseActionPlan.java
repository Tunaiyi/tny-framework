/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.behavior;

import com.google.common.collect.*;
import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.simple.*;
import com.tny.game.basics.item.xml.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;

import java.util.*;

/**
 * 操作方案
 *
 * @author KGTny
 */
public abstract class BaseActionPlan extends DemandHolderObject implements ActionPlan {

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
    protected BaseAwardPlan awardPlan;

    /**
     * 消耗方案
     */
    protected BaseCostPlan costPlan;

    /**
     * 操作选项 － 操作选项公式map
     */
    protected Map<Option, ExprHolder> optionMap;

    @Override
    public Set<Action> getActions() {
        return this.actions;
    }

    private Action checkAction(Action action) {
        if (this.actions.contains(action)) {
            return action;
        }
        Asserts.checkNotNull(null, "{] action is null", action);
        return null;
    }

    @Override
    public DemandResultCollector tryToDo(long playerId, boolean tryAll, DemandResultCollector collector, Map<String, Object> attributeMap) {
        this.checkResult(playerId, this.demandList, tryAll, collector, ItemsImportKey.$ACTION_DEMAND_PARAMS, attributeMap);
        if (!tryAll && collector.isFailed()) {
            return collector;
        }
        if (this.costPlan != null) {
            this.costPlan.tryToDo(playerId, tryAll, collector, attributeMap);
        }
        return collector;
    }

    @Override
    public List<DemandResult> countDemandResult(long playerId, Map<String, Object> map) {
        return this.countAllDemandResults(playerId, this.demandList, ItemsImportKey.$ACTION_DEMAND_PARAMS, map);
    }

    @Override
    public ActionResult getActionResult(long playerId, Action action, Map<String, Object> attributeMap) {
        List<DemandResult> resultList = this.countAllDemandResults(playerId, this.demandList, ItemsImportKey.$ACTION_DEMAND_PARAMS, attributeMap);
        List<DemandResult> costResultList = this.costPlan == null ? new ArrayList<>() : this.costPlan.countDemandResultList(playerId, attributeMap);
        return new SimpleActionResult(checkAction(action), resultList, costResultList, this.doAwardList(playerId, action, attributeMap));
    }

    @Override
    public AwardList getAwardList(long playerId, Action action, Map<String, Object> attributeMap) {
        action = checkAction(action);
        if (this.awardPlan == null) {
            return new SimpleAwardList(action);
        }
        return this.doAwardList(playerId, action, attributeMap);
    }

    @Override
    public CostList getCostList(long playerId, Action action, Map<String, Object> attributeMap) {
        action = checkAction(action);
        if (this.costPlan == null) {
            return new SimpleCostList(action);
        }
        setAttrMap(playerId, this.attrAliasSet, attributeMap);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributeMap);
        return this.costPlan.getCostList(playerId, action, attributeMap);
    }

    @Override
    public ActionTrades countTrades(long playerId, Action action, Map<String, Object> attributes) {
        action = checkAction(action);
        Trade award = null;
        Trade cost = null;
        setAttrMap(playerId, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
        if (this.awardPlan != null) {
            award = this.awardPlan.createTrade(playerId, action, attributes);
        }
        if (this.costPlan != null) {
            cost = this.costPlan.createTrade(playerId, action, attributes);
        }
        return new ActionTrades(action, award, cost);
    }

    @Override
    public Trade createAward(long playerId, Action action, Map<String, Object> attributes) {
        action = checkAction(action);
        if (this.awardPlan == null) {
            return new SimpleTrade(action, TradeType.AWARD);
        }
        setAttrMap(playerId, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
        return this.awardPlan.createTrade(playerId, action, attributes);
    }

    @Override
    public Trade createCost(long playerId, Action action, Map<String, Object> attributes) {
        action = checkAction(action);
        if (this.costPlan == null) {
            return new SimpleTrade(action, TradeType.DEDUCT);
        }
        setAttrMap(playerId, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
        return this.costPlan.createTrade(playerId, action, attributes);
    }

    @Override
    public boolean isHasOption(Option option) {
        return this.optionMap.containsKey(option);
    }

    private AwardList doAwardList(long playerId, Action action, Map<String, Object> attributes) {
        if (this.awardPlan == null) {
            return new SimpleAwardList(action);
        }
        setAttrMap(playerId, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
        return this.awardPlan.getAwardList(playerId, action, attributes);
    }

    @Override
    public <O> O countOption(long playerId, Option option, Map<String, Object> attributes) {
        setAttrMap(playerId, this.attrAliasSet, attributes);
        this.countAndSetDemandParams(ItemsImportKey.$ACTION_DEMAND_PARAMS, attributes);
        ExprHolder formula = this.optionMap.get(option);
        if (formula == null) {
            throw new GameRuningException(option, ItemResultCode.ACTION_NO_EXIST);
        }
        return formula.createExpr().putAll(attributes).execute(null);
    }

    @Override
    public void init(ItemModel itemModel, ItemModelContext context) {
        super.init(itemModel, context);
        if (this.optionMap == null) {
            this.optionMap = ImmutableMap.of();
        }
        if (this.action != null) {
            this.actions = ImmutableSet.of(this.action);
        } else if (this.actions != null) {
            this.actions = ImmutableSet.copyOf(this.actions);
        }
        if (this.awardPlan != null) {
            this.awardPlan.init(itemModel, context);
        }
        if (this.costPlan != null) {
            this.costPlan.init(itemModel, context);
        }
        for (String alias : this.attrAliasSet) {
            AliasCollectUtils.addAlias(alias);
        }
    }

}
