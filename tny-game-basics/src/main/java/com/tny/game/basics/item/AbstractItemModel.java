/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.google.common.collect.*;
import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.simple.*;
import com.tny.game.basics.item.model.*;
import com.tny.game.basics.item.xml.*;
import com.tny.game.common.collection.empty.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;
import org.apache.commons.lang3.builder.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 抽象事物模型
 *
 * @author KGTny
 */
public abstract class AbstractItemModel extends BaseModel<ItemModelContext> implements ItemModel, ItemsImportKey {

    /**
     * 事物模型id
     */
    protected int id;

    /**
     * 别名
     */
    protected String alias;

    /**
     * 描述
     */
    protected String desc;

    /**
     * 行为 － 行为方案map
     */
    protected Map<Behavior, BehaviorPlan> behaviorPlanMap;

    /**
     * 操作 － 行为方案map
     */
    protected Map<Action, BehaviorPlan> actionBehaviorPlanMap;

    /**
     * 事物总管理器
     */
    protected ItemModelContext context;

    /**
     * 对象计算附加参数列表
     */
    protected Set<String> attrAliasSet;

    /**
     * 对象能力 － 能力公式map
     */
    protected Map<Ability, ExprHolder> abilityMap;

    /**
     * 能治值
     */
    protected volatile ExprHolder currentFormulaHolder;

    /**
     * 能治值
     */
    protected volatile ExprHolder demandFormulaHolder;

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    //    @Override
    //    public String getItemName() {
    //        return this.itemName;
    //    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    protected void setAttrMap(long playerId, Collection<String> aliasList, Map<String, Object> attributeMap) {
        for (String alias : aliasList)
            this.setAttrMap(playerId, alias, attributeMap);
    }

    protected Item<?> setAttrMap(long playerId, String alias, Map<String, Object> attributeMap) {
        ModelExplorer itemModelExplorer = this.context.getItemModelExplorer();
        ItemModel model = itemModelExplorer.getModelByAlias(alias);
        if (model == null) {
            throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
        }
        ItemExplorer itemExplorer = this.context.getItemExplorer();
        if (itemExplorer.hasItemManager(model.getItemType())) {
            Item<?> item = itemExplorer.getItem(playerId, model.getId());
            attributeMap.put(alias, item);
            return item;
        }
        return null;
    }

    protected TryToDoResult doTryToDo(long playerId, Item<?> item, Action action, boolean award, boolean tryAll, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        DemandResultCollector collector = behaviorPlan.tryToDo(playerId, action, tryAll, attributeMap);
        if (collector.isFailed()) {
            return new SimpleTryToDoResult(action, collector.getFailedDemands());
        }
        return new SimpleTryToDoResult(action, award ?
                behaviorPlan.countAward(playerId, action, attributeMap) :
                new SimpleTrade(action, TradeType.AWARD),
                new SimpleTrade(action, TradeType.DEDUCT, collector.getCostDemands().stream()
                        .filter(d -> d.getDemandType() == TradeDemandType.DEDUCT_DEMAND_GE)
                        .map(d -> new SimpleTradeItem<>(d, d.getAlterType(), d.getParamMap()))
                        .collect(Collectors.toList())));
    }

    protected BehaviorResult doCountBehaviorResult(long playerId, Item<?> item, Behavior behavior, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByBehavior(behavior);
        List<DemandResult> resultList = behaviorPlan.countAllDemandResults(playerId, attributeMap);
        Map<Action, ActionResult> actionResultMap = new HashMap<>();
        for (Entry<Action, ActionPlan> entry : behaviorPlan.getActionPlanMap().entrySet()) {
            ActionPlan actionPlan = entry.getValue();
            for (Action action : actionPlan.getActions())
                actionResultMap.put(entry.getKey(), actionPlan.getActionResult(playerId, action, attributeMap));
        }
        return new SimpleBehaviorResult(resultList, actionResultMap);
    }

    protected ActionTrades doCreateActionTrades(long playerId, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        Trade award = behaviorPlan.countAward(playerId, action, attributeMap);
        Trade cost = behaviorPlan.countCost(playerId, action, attributeMap);
        return new ActionTrades(action, award, cost);
    }

    protected Trade doCountCostTrade(long playerId, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        return behaviorPlan.countCost(playerId, action, attributeMap);
    }

    protected AwardList doGetAwardList(long playerId, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        return behaviorPlan.getAwardList(playerId, action, attributeMap);
    }

    protected CostList doGetCostList(long playerId, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        return behaviorPlan.getCostList(playerId, action, attributeMap);
    }

    protected ActionResult doCountActionResult(long playerId, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        return behaviorPlan.getActionResult(playerId, action, attributeMap);
    }

    protected Trade doCountTradeAward(long playerId, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        return behaviorPlan.countAward(playerId, action, attributeMap);
    }

    protected <A> A doCountAbility(long playerId, Item<?> item, Ability ability, Class<A> clazz, Object... attributes) {
        ExprHolder formula = this.abilityMap.get(ability);
        if (formula == null) {
            return null;
        }
        Map<String, Object> attributeMap = new HashMap<>();
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        return formula.createExpr().putAll(attributeMap).execute(clazz);
    }

    @Override
    public boolean hasAbility(Ability ability) {
        return this.abilityMap.containsKey(ability);
    }

    @Override
    public boolean hasBehavior(Behavior behavior) {
        return this.behaviorPlanMap.containsKey(behavior);
    }

    @Override
    public boolean hasAction(Action action) {
        return this.actionBehaviorPlanMap.containsKey(action);
    }

    @Override
    public boolean hasOption(Action action, Option option) {
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        return behaviorPlan.isHasOption(action, option);
    }

    private <O> O doCountActionOption(long playerId, Item<?> item, Action action, Option option, Object... attributes) {
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        if (!behaviorPlan.isHasOption(action, option)) {
            return null;
            //			throw new GameRuningException(option, ItemResultCode.OPTION_NO_EXIST, action, option);
        }
        Map<String, Object> attributeMap = new HashMap<>();
        this.setAttrMap(playerId, attributeMap, item, attributes);
        this.setAttrMap(playerId, this.attrAliasSet, attributeMap);
        return behaviorPlan.countOption(playerId, action, option, attributeMap);
    }

    @Override
    public TryToDoResult tryToDo(Item<?> item, Action action, Object... attributes) {
        return this.tryToDo(item, action, true, attributes);
    }

    @Override
    public TryToDoResult tryToDo(Item<?> item, Action action, boolean award, Object... attributes) {
        return this.doTryToDo(item.getPlayerId(), item, action, award, false, attributes);
    }

    @Override
    public TryToDoResult tryToDoAll(Item<?> item, Action action, Object... attributes) {
        return this.tryToDoAll(item, action, true, attributes);
    }

    @Override
    public TryToDoResult tryToDoAll(Item<?> item, Action action, boolean award, Object... attributes) {
        return this.doTryToDo(item.getPlayerId(), item, action, award, true, attributes);
    }

    @Override
    public Set<Object> tags() {
        return this.tags;
    }

    @Override
    public BehaviorResult getBehaviorResult(Item<?> item, Behavior behavior, Object... attributes) {
        return this.doCountBehaviorResult(item.getPlayerId(), item, behavior, attributes);
    }

    @Override
    public ActionResult getActionResult(long playerId, Action action, Object... attributes) {
        return this.doCountActionResult(playerId, null, action, attributes);
    }

    @Override
    public Trade createCostTrade(Item<?> item, Action action, Object... attributes) {
        return this.doCountCostTrade(item.getPlayerId(), item, action, attributes);
    }

    @Override
    public Trade createAwardTrade(Item<?> item, Action action, Object... attributes) {
        return this.doCountTradeAward(item.getPlayerId(), item, action, attributes);
    }

    public boolean isHasAbility(Ability ability) {
        return this.abilityMap.get(ability) != null;
    }

    private <V> V defaultNumber(V number, V defaultNum) {
        return number == null ? defaultNum : number;
    }

    @Override
    public TryToDoResult tryToDo(long playerId, Action action, Object... attributes) {
        return this.tryToDo(playerId, action, true, attributes);
    }

    @Override
    public TryToDoResult tryToDo(long playerId, Action action, boolean award, Object... attributes) {
        return this.doTryToDo(playerId, null, action, award, false, attributes);
    }

    @Override
    public TryToDoResult tryToDoAll(long playerId, Action action, Object... attributes) {
        return this.tryToDoAll(playerId, action, true, attributes);
    }

    @Override
    public TryToDoResult tryToDoAll(long playerId, Action action, boolean award, Object... attributes) {
        return this.doTryToDo(playerId, null, action, award, true, attributes);
    }

    @Override
    public ActionTrades createActionTrades(Item<?> item, Action action, Object... attributes) {
        return this.doCreateActionTrades(item.getPlayerId(), item, action, attributes);
    }

    @Override
    public ActionTrades createActionTrades(long playerId, Action action, Object... attributes) {
        return this.doCreateActionTrades(playerId, null, action, attributes);
    }

    @Override
    public BehaviorResult getBehaviorResult(long playerId, Behavior behavior, Object... attributes) {
        return this.doCountBehaviorResult(playerId, null, behavior, attributes);
    }

    @Override
    public ActionResult getActionResult(Item<?> item, Action action, Object... attributes) {
        return this.doCountActionResult(item.getPlayerId(), item, action, attributes);
    }

    @Override
    public AwardList getAwardList(long playerId, Action action, Object... attributes) {
        return this.doGetAwardList(playerId, null, action, attributes);
    }

    @Override
    public AwardList getAwardList(Item<?> item, Action action, Object... attributes) {
        return this.doGetAwardList(item.getPlayerId(), item, action, attributes);
    }

    @Override
    public CostList getCostList(Item<?> item, Action action, Object... attributes) {
        return this.doGetCostList(item.getPlayerId(), item, action, attributes);
    }

    @Override
    public CostList getCostList(long playerId, Action action, Object... attributes) {
        return this.doGetCostList(playerId, null, action, attributes);
    }

    @Override
    public Trade createCostTrade(long playerId, Action action, Object... attributes) {
        return this.doCountCostTrade(playerId, null, action, attributes);
    }

    @Override
    public Trade createAwardTrade(long playerId, Action action, Object... attributes) {
        return this.doCountTradeAward(playerId, null, action, attributes);
    }

    @Override
    public <A> Map<Ability, A> getAbilities(Item<?> item, Collection<Ability> abilityCollection, Class<A> clazz, Object... attributes) {
        Map<Ability, A> valueMap = new HashMap<>();
        for (Ability ability : abilityCollection) {
            A object = null;
            if (!this.hasAbility(ability)) {
                valueMap.put(ability, null);
            } else {
                object = this.doCountAbility(item.getPlayerId(), item, ability, clazz, attributes);
            }
            valueMap.put(ability, object);
        }
        return valueMap;
    }

    @Override
    public <A> Map<Ability, A> getAbilities(long playerId, Collection<Ability> abilityCollection, Class<A> clazz, Object... attributes) {
        Map<Ability, A> valueMap = new HashMap<>();
        for (Ability ability : abilityCollection) {
            A object = null;
            if (!this.hasAbility(ability)) {
                valueMap.put(ability, null);
            } else {
                object = this.doCountAbility(playerId, null, ability, clazz, attributes);
            }
            valueMap.put(ability, object);
        }
        return valueMap;
    }

    @Override
    public <A extends Ability> Set<A> getAbilityTypes(Class<A> typeClass) {
        Set<A> types = new HashSet<>();
        for (Ability ability : this.abilityMap.keySet()) {
            if (typeClass.isInstance(ability)) {
                types.add(ObjectAide.as(ability, typeClass));
            }
        }
        return types;
    }

    @Override
    public Set<Ability> getAbilityTypes() {
        return Collections.unmodifiableSet(this.abilityMap.keySet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Ability, V> Map<A, V> getAbilitiesByType(Item<?> item, Class<A> abilityClass, Class<V> clazz, Object... attributes) {
        Map<A, V> valueMap = new HashMap<>();
        for (Ability ability : this.abilityMap.keySet()) {
            V object = null;
            if (abilityClass.isInstance(ability)) {
                if (this.hasAbility(ability)) {
                    object = this.doCountAbility(item.getPlayerId(), item, ability, clazz, attributes);
                }
                valueMap.put((A)ability, object);
            }
        }
        return valueMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Ability, V> Map<A, V> getAbilitiesByType(long playerId, Class<A> abilityClass, Class<V> clazz, Object... attributes) {
        Map<A, V> valueMap = new HashMap<>();
        for (Ability ability : this.abilityMap.keySet()) {
            V object = null;
            if (abilityClass.isInstance(ability)) {
                if (this.hasAbility(ability)) {
                    object = this.doCountAbility(playerId, null, ability, clazz, attributes);
                }
                valueMap.put((A)ability, object);
            }
        }
        return valueMap;
    }

    @Override
    public <O> O getActionOption(Item<?> item, Action action, Option option, Object... attributes) {
        return this.doCountActionOption(item.getPlayerId(), item, action, option, attributes);
    }

    @Override
    public <O> O getActionOption(Item<?> item, O defaultNum, Action action, Option option, Object... attributes) {
        O value = this.doCountActionOption(item.getPlayerId(), item, action, option, attributes);
        return this.defaultNumber(value, defaultNum);
    }

    @Override
    public <O> O getActionOption(long playerId, Action action, Option option, Object... attributes) {
        return this.doCountActionOption(playerId, null, action, option, attributes);
    }

    @Override
    public <O> O getActionOption(long playerId, O defaultNum, Action action, Option option, Object... attributes) {
        O value = this.doCountActionOption(playerId, null, action, option, attributes);
        return this.defaultNumber(value, defaultNum);
    }

    @Override
    public <A> A getAbility(Item<?> item, Ability ability, Class<A> clazz, Object... attributes) {
        return this.doCountAbility(item.getPlayerId(), item, ability, clazz, attributes);
    }

    @Override
    public <A> A getAbility(long playerId, Ability ability, Class<A> clazz, Object... attributes) {
        return this.doCountAbility(playerId, null, ability, clazz, attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> A getAbility(Item<?> item, A defaultObject, Ability ability, Object... attributes) {
        A value = this.doCountAbility(item.getPlayerId(), item, ability,
                (Class<A>)(defaultObject == null ? Object.class : (Class<A>)defaultObject.getClass()), attributes);
        return this.defaultNumber(value, defaultObject);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> A getAbility(long playerId, A defaultObject, Ability ability, Object... attributes) {
        A value = this
                .doCountAbility(playerId, null, ability, (Class<A>)(defaultObject == null ? Object.class : defaultObject.getClass()), attributes);
        return this.defaultNumber(value, defaultObject);
    }

    protected void setAttrMap(long playerId, Map<String, Object> attributeMap, Item<?> item, Object... attributes) {
        String key = null;
        if (item == null) {
            ItemExplorer itemExplorer = this.context.getItemExplorer();
            if (itemExplorer.hasItemManager(this.getItemType())) {
                item = itemExplorer.getItem(playerId, this.getId());
            }
        }
        attributeMap.put(ACTION_ITEM_NAME, item);
        attributeMap.put(ACTION_ITEM_MODEL_NAME, this);
        attributeMap.putIfAbsent($PLAYER_ID, playerId);
        attributeMap.computeIfAbsent($CONTEXT, k -> new HashMap<>());
        for (Object object : attributes) {
            if (key == null) {
                key = object.toString();
            } else {
                attributeMap.put(key, object);
                key = null;
            }
        }
    }

    protected BehaviorPlan getBehaviorPlanByBehavior(Behavior behavior) {
        BehaviorPlan plan = this.behaviorPlanMap.get(behavior);
        if (plan == null) {
            throw new GameRuningException(behavior, ItemResultCode.BEHAVIOR_NO_EXIST);
        }
        return plan;
    }

    protected BehaviorPlan getBehaviorPlanByAction(Action action) {
        BehaviorPlan plan = this.actionBehaviorPlanMap.get(action);
        if (plan == null) {
            throw new GameRuningException(action, ItemResultCode.BEHAVIOR_NO_EXIST);
        }
        return plan;
    }

    protected ActionPlan getActionPlan(Action action) {
        BehaviorPlan plan = this.actionBehaviorPlanMap.get(action);
        if (plan == null) {
            throw new GameRuningException(action, ItemResultCode.BEHAVIOR_NO_EXIST);
        }
        return plan.getActionPlan(action);
    }

    @Override
    public Behavior getBehaviorByAction(Action action) {
        BehaviorPlan plan = this.actionBehaviorPlanMap.get(action);
        if (plan == null) {
            return null;
        }
        return plan.getBehavior();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Ability> getAbilityTypes(Class<? extends Ability>... abilityClasses) {
        Set<Ability> abilitySet = new HashSet<>();
        for (Ability ability : this.abilityMap.keySet()) {
            for (Class<? extends Ability> clazz : abilityClasses) {
                if (clazz.isInstance(ability)) {
                    abilitySet.add(ability);
                }
            }
        }
        return abilitySet;
    }

    @Override
    public Expr currentFormula() {
        if (this.currentFormulaHolder == null) {
            String formula = this.getCurrentFormula();
            this.currentFormulaHolder = this.context.getExprHolderFactory().create(formula);
        }
        return this.currentFormulaHolder.createExpr();
    }

    @Override
    public Expr demandFormula() {
        if (this.demandFormulaHolder == null) {
            String formula = this.getDemandFormula();
            this.demandFormulaHolder = this.context.getExprHolderFactory().create(formula);
        }
        return this.demandFormulaHolder.createExpr();
    }

    protected String getCurrentFormula() {
        return CURRENT_FORMULA;
    }

    protected String getDemandFormula() {
        return DEMAND_FORMULA;
    }

    @Override
    protected void doInit(ItemModelContext context) {
        this.context = context;
        if (this.attrAliasSet == null) {
            this.attrAliasSet = ImmutableSet.of();
        }

        if (this.abilityMap == null) {
            this.abilityMap = ImmutableMap.of();
        }

        this.attrAliasSet = ImmutableSet.copyOf(this.attrAliasSet);
        this.abilityMap = ImmutableMap.copyOf(this.abilityMap);
        this.tags = ImmutableSet.copyOf(this.tags);
        for (String alias : this.attrAliasSet) {
            AliasCollectUtils.addAlias(alias);
        }
        ExprHolderFactory exprHolderFactory = context.getExprHolderFactory();
        this.currentFormulaHolder = as(exprHolderFactory.create(this.getCurrentFormula()));
        this.demandFormulaHolder = as(exprHolderFactory.create(this.getDemandFormula()));

        if (this.actionBehaviorPlanMap == null) {
            this.actionBehaviorPlanMap = new EmptyImmutableMap<>();
        }

        if (this.behaviorPlanMap == null) {
            this.behaviorPlanMap = new EmptyImmutableMap<>();
        }
        onItemInit(context);
        this.behaviorPlanMap = ImmutableMap.copyOf(this.behaviorPlanMap);
        this.actionBehaviorPlanMap = ImmutableMap.copyOf(this.actionBehaviorPlanMap);
    }

    protected void onItemInit(ItemModelContext context) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AbstractItemModel)) {
            return false;
        }

        AbstractItemModel that = (AbstractItemModel)o;

        return new EqualsBuilder().append(getId(), that.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).toHashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AbstractItemModel [id=" + this.id + ", alias=" + this.alias + ", desc=" + this.desc + "]";
    }

}
