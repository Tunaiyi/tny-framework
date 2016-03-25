package com.tny.game.base.item;

import com.tny.game.base.exception.GameRuningException;
import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.behavior.*;
import com.tny.game.base.item.behavior.simple.SimpleBehaviorResult;
import com.tny.game.base.item.behavior.simple.SimpleTrade;
import com.tny.game.base.item.behavior.simple.SimpleTryToDoResult;
import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import com.tny.game.common.reflect.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 抽象事物模型
 *
 * @author KGTny
 */
public abstract class AbstractItemModel implements ItemModel, ItemsImportKey {

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

//    /**
//     * 名字
//     */
//    protected String itemName;

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
    protected ItemExplorer itemExplorer;

    /**
     * 事物模型总管理器
     */
    protected ItemModelExplorer itemModelExplorer;

    /**
     * 对象计算附加参数列表
     */
    protected Set<String> attrAliasSet;

    /**
     * 对象能力 － 能力公式map
     */
    protected Map<Ability, FormulaHolder> abilityMap;

    protected volatile FormulaHolder currentFormulaHolder;

    @Override
    public int getID() {
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

    protected void setAttrMap(long playerID, Collection<String> aliasList, Map<String, Object> attributeMap) {
        for (String alias : aliasList)
            this.setAttrMap(playerID, alias, attributeMap);
    }

    protected Item<?> setAttrMap(long playerID, String alias, Map<String, Object> attributeMap) {
        ItemModel model = this.itemModelExplorer.getItemModelByAlias(alias);
        if (model == null)
            throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
        if (model.getItemType().hasEntity()) {
            Item<?> item = this.itemExplorer.getItem(playerID, model.getID());
            attributeMap.put(alias, item);
            return item;
        }
        return null;
    }

    protected TryToDoResult doTryToDo(long playerID, Item<?> item, Action action, boolean award, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        DemandResult demandResult = behaviorPlan.tryToDo(playerID, action, attributeMap);
        if (demandResult != null)
            return new SimpleTryToDoResult(action, demandResult);
        return new SimpleTryToDoResult(action, award ?
                behaviorPlan.countAwardResult(playerID, action, attributeMap) :
                new SimpleTrade(action, TradeType.AWARD),
                behaviorPlan.countCostResult(playerID, action, attributeMap));
    }

    protected List<DemandResult> doCountDemandResult(long playerID, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        List<DemandResult> resultList = new ArrayList<>();
        resultList.addAll(behaviorPlan.countDemandResult(playerID, attributeMap));
        resultList.addAll(behaviorPlan.getActionPlan(action).countDemandResult(playerID, attributeMap));
        return resultList;
    }

    protected BehaviorResult doCountBehaviorResult(long playerID, Item<?> item, Behavior behavior, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByBehavior(behavior);
        List<DemandResult> resultList = behaviorPlan.countDemandResult(playerID, attributeMap);
        Map<Action, ActionResult> actionResultMap = new HashMap<>();
        for (Entry<Action, ActionPlan> entry : behaviorPlan.getActionPlanMap().entrySet()) {
            ActionPlan actionPlan = entry.getValue();
            actionResultMap.put(entry.getKey(), actionPlan.getActionResult(playerID, attributeMap));
        }
        return new SimpleBehaviorResult(resultList, actionResultMap);
    }

    protected Trade doCountCostTrade(long playerID, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        return behaviorPlan.countCostResult(playerID, action, attributeMap);
    }

    protected AwardList doGetAwardList(long playerID, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        return behaviorPlan.getAwardList(playerID, action, attributeMap);
    }

    protected CostList doGetCostList(long playerID, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        return behaviorPlan.getCostList(playerID, action, attributeMap);
    }

    protected ActionResult doCountActionResult(long playerID, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        return behaviorPlan.getActionResult(playerID, action, attributeMap);
    }

    protected Trade doCountTradeAward(long playerID, Item<?> item, Action action, Object... attributes) {
        Map<String, Object> attributeMap = new HashMap<>();
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        return behaviorPlan.countAwardResult(playerID, action, attributeMap);
    }

    protected <A> A doCountAbility(long playerID, Item<?> item, Ability ability, Class<A> clazz, Object... attributes) {
        FormulaHolder formula = this.abilityMap.get(ability);
        if (formula == null) {
            return null;
        }
        Map<String, Object> attributeMap = new HashMap<>();
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        return formula.createFormula().putAll(attributeMap).execute(clazz);
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

    private <O> O doCountActionOption(long playerID, Item<?> item, Action action, Option option, Object... attributes) {
        BehaviorPlan behaviorPlan = this.getBehaviorPlanByAction(action);
        if (!behaviorPlan.isHasOption(action, option)) {
            return null;
            //			throw new GameRuningException(option, ItemResultCode.OPTION_NO_EXIST, action, option);
        }
        Map<String, Object> attributeMap = new HashMap<>();
        this.setAttrMap(playerID, attributeMap, item, attributes);
        this.setAttrMap(playerID, this.attrAliasSet, attributeMap);
        return behaviorPlan.countOption(playerID, action, option, attributeMap);
    }

    @Override
    public TryToDoResult tryToDo(Item<?> item, Action action, Object... attributes) {
        return this.tryToDo(item, action, true, attributes);
    }

    @Override
    public TryToDoResult tryToDo(Item<?> item, Action action, boolean award, Object... attributes) {
        return this.doTryToDo(item.getPlayerID(), item, action, award, attributes);
    }

    //	@Override
    //	public List<DemandResult> countDemandResult(Item<?> item, Action action, Object... attributes) {
    //		return doCountDemandResult(item.getPlayerID(), item, action, attributes);
    //	}

    @Override
    public BehaviorResult getBehaviorResult(Item<?> item, Behavior behavior, Object... attributes) {
        return this.doCountBehaviorResult(item.getPlayerID(), item, behavior, attributes);
    }

    @Override
    public ActionResult getActionResult(long playerID, Action action, Object... attributes) {
        return this.doCountActionResult(playerID, null, action, attributes);
    }

    @Override
    public Trade createCostTrade(Item<?> item, Action action, Object... attributes) {
        return this.doCountCostTrade(item.getPlayerID(), item, action, attributes);
    }

    @Override
    public Trade createAwardTrade(Item<?> item, Action action, Object... attributes) {
        return this.doCountTradeAward(item.getPlayerID(), item, action, attributes);
    }

    public boolean isHasAbility(Ability ability) {
        return this.abilityMap.get(ability) != null;
    }

    private <V> V defaultNumber(V number, V defaultNum) {
        return number == null ? defaultNum : number;
    }

    @Override
    public TryToDoResult tryToDo(long playerID, Action action, Object... attributes) {
        return this.tryToDo(playerID, action, true, attributes);
    }

    @Override
    public TryToDoResult tryToDo(long playerID, Action action, boolean award, Object... attributes) {
        return this.doTryToDo(playerID, null, action, award, attributes);
    }

    //	@Override
    //	public List<DemandResult> countDemandResult(long playerID, Action action, Object... attributes) {
    //		return doCountDemandResult(playerID, null, action, attributes);
    //	}

    @Override
    public BehaviorResult getBehaviorResult(long playerID, Behavior behavior, Object... attributes) {
        return this.doCountBehaviorResult(playerID, null, behavior, attributes);
    }

    @Override
    public ActionResult getActionResult(Item<?> item, Action action, Object... attributes) {
        return this.doCountActionResult(item.getPlayerID(), item, action, attributes);
    }

    @Override
    public AwardList getAwardList(long playerID, Action action, Object... attributes) {
        return this.doGetAwardList(playerID, null, action, attributes);
    }

    @Override
    public AwardList getAwardList(Item<?> item, Action action, Object... attributes) {
        return this.doGetAwardList(item.getPlayerID(), item, action, attributes);
    }

    @Override
    public CostList getCostList(Item<?> item, Action action, Object... attributes) {
        return this.doGetCostList(item.getPlayerID(), item, action, attributes);
    }

    @Override
    public CostList getCostList(long playerID, Action action, Object... attributes) {
        return this.doGetCostList(playerID, null, action, attributes);
    }

    @Override
    public Trade createCostTrade(long playerID, Action action, Object... attributes) {
        return this.doCountCostTrade(playerID, null, action, attributes);
    }

    @Override
    public Trade createAwardTrade(long playerID, Action action, Object... attributes) {
        return this.doCountTradeAward(playerID, null, action, attributes);
    }

    @Override
    public <A> Map<Ability, A> getAbilities(Item<?> item, Collection<Ability> abilityCollection, Class<A> clazz, Object... attributes) {
        Map<Ability, A> valueMap = new HashMap<>();
        for (Ability ability : abilityCollection) {
            A object = null;
            if (!this.hasAbility(ability)) {
                valueMap.put(ability, null);
            } else {
                object = this.doCountAbility(item.getPlayerID(), item, ability, clazz, attributes);
            }
            valueMap.put(ability, object);
        }
        return valueMap;
    }

    @Override
    public <A> Map<Ability, A> getAbilities(long playerID, Collection<Ability> abilityCollection, Class<A> clazz, Object... attributes) {
        Map<Ability, A> valueMap = new HashMap<>();
        for (Ability ability : abilityCollection) {
            A object = null;
            if (!this.hasAbility(ability)) {
                valueMap.put(ability, null);
            } else {
                object = this.doCountAbility(playerID, null, ability, clazz, attributes);
            }
            valueMap.put(ability, object);
        }
        return valueMap;
    }

    @Override
    public <A extends Ability> Set<A> getAbilityTypes(Class<A> typeClass) {
        Set<A> types = new HashSet<>();
        for (Ability ability : this.abilityMap.keySet()) {
            if (typeClass.isInstance(ability))
                types.add(ObjectUtils.as(ability, typeClass));
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
                if (this.hasAbility(ability))
                    object = this.doCountAbility(item.getPlayerID(), item, ability, clazz, attributes);
                valueMap.put((A) ability, object);
            }
        }
        return valueMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Ability, V> Map<A, V> getAbilitiesByType(long playerID, Class<A> abilityClass, Class<V> clazz, Object... attributes) {
        Map<A, V> valueMap = new HashMap<>();
        for (Ability ability : this.abilityMap.keySet()) {
            V object = null;
            if (abilityClass.isInstance(ability)) {
                if (this.hasAbility(ability))
                    object = this.doCountAbility(playerID, null, ability, clazz, attributes);
                valueMap.put((A) ability, object);
            }
        }
        return valueMap;
    }

    @Override
    public <O> O getActionOption(Item<?> item, Action action, Option option, Object... attributes) {
        return this.doCountActionOption(item.getPlayerID(), item, action, option, attributes);
    }

    @Override
    public <O> O getActionOption(Item<?> item, O defaultNum, Action action, Option option, Object... attributes) {
        O value = this.doCountActionOption(item.getPlayerID(), item, action, option, attributes);
        return this.defaultNumber(value, defaultNum);
    }

    @Override
    public <O> O getActionOption(long playerID, Action action, Option option, Object... attributes) {
        return this.doCountActionOption(playerID, null, action, option, attributes);
    }

    @Override
    public <O> O getActionOption(long playerID, O defaultNum, Action action, Option option, Object... attributes) {
        O value = this.doCountActionOption(playerID, null, action, option, attributes);
        return this.defaultNumber(value, defaultNum);
    }

    @Override
    public <A> A getAbility(Item<?> item, Ability ability, Class<A> clazz, Object... attributes) {
        return this.doCountAbility(item.getPlayerID(), item, ability, clazz, attributes);
    }

    @Override
    public <A> A getAbility(long playerID, Ability ability, Class<A> clazz, Object... attributes) {
        return this.doCountAbility(playerID, null, ability, clazz, attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> A getAbility(Item<?> item, A defaultObject, Ability ability, Object... attributes) {
        A value = this.doCountAbility(item.getPlayerID(), item, ability, (Class<A>) defaultObject.getClass(), attributes);
        return this.defaultNumber(value, defaultObject);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> A getAbility(long playerID, A defaultObject, Ability ability, Object... attributes) {
        A value = this.doCountAbility(playerID, null, ability, (Class<A>) defaultObject.getClass(), attributes);
        return this.defaultNumber(value, defaultObject);
    }

    protected void setAttrMap(long playerID, Map<String, Object> attributeMap, Item<?> item, Object... attributes) {
        String key = null;
        //		if (item == null) {
        //			if (this.getItemType().hasEntity())
        //				item = itemExplorer.getItem(playerID, this.getID());
        //		}
        attributeMap.put(ACTION_ITEM_NAME, item);
        attributeMap.put(ACTION_ITEM_MODEL_NAME, this);
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
        if (plan == null)
            throw new GameRuningException(behavior, ItemResultCode.BEHAVIOR_NO_EXIST);
        return plan;
    }

    protected BehaviorPlan getBehaviorPlanByAction(Action action) {
        BehaviorPlan plan = this.actionBehaviorPlanMap.get(action);
        if (plan == null)
            throw new GameRuningException(action, ItemResultCode.BEHAVIOR_NO_EXIST);
        return plan;
    }

    @Override
    public Behavior getBehaviorByAction(Action action) {
        BehaviorPlan plan = this.actionBehaviorPlanMap.get(action);
        if (plan == null)
            return null;
        return plan.getBehavior();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Ability> getOwnAbilityBy(Class<? extends Ability>... abilityClasses) {
        Set<Ability> abilitySet = new HashSet<>();
        for (Ability ability : this.abilityMap.keySet()) {
            for (Class<? extends Ability> clazz : abilityClasses) {
                if (clazz.isInstance(ability))
                    abilitySet.add(ability);
            }
        }
        return abilitySet;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.id;
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        ItemModel other = (ItemModel) obj;
        if (this.id != other.getID())
            return false;
        return true;
    }

    @Override
    public FormulaHolder currentFormula() {
        if (this.currentFormulaHolder != null) {
            return this.currentFormulaHolder;
        } else {
            String formula = this.getCurrentFormula();
            this.currentFormulaHolder = MvelFormulaFactory.create(formula, FormulaType.EXPRESSION);
            return this.currentFormulaHolder;
        }
    }

    protected String getCurrentFormula() {
        return DEMAND_ITEM + " == null ? 0 : " + DEMAND_ITEM + ".number";
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
