package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.ActionResult;
import com.tny.game.base.item.behavior.AwardList;
import com.tny.game.base.item.behavior.Behavior;
import com.tny.game.base.item.behavior.BehaviorResult;
import com.tny.game.base.item.behavior.CostList;
import com.tny.game.base.item.behavior.Option;
import com.tny.game.base.item.behavior.TryToDoResult;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 抽象事物接口
 *
 * @param <IM>
 * @author KGTny
 */
public abstract class AbstractItem<IM extends ItemModel> implements Item<IM> {

    /**
     * 事物所属玩家id
     */
    protected long playerID;

    /**
     * 事物模型
     */
    protected IM model;

    @Override
    public long getPlayerID() {
        return this.playerID;
    }

    @Override
    public int getItemID() {
        return this.getModel().getID();
    }

    @Override
    public <IT extends ItemType> IT getItemType() {
        return this.getModel().getItemType();
    }

    @Override
    public IM getModel() {
        return this.model;
    }

    @Override
    public String getAlias() {
        return this.getModel().getAlias();
    }

    @Override
    public boolean hasAbility(Ability ability) {
        return this.getModel().hasAbility(ability);
    }

    @Override
    public boolean hasBehavior(Behavior behavior) {
        return this.getModel().hasBehavior(behavior);
    }

    @Override
    public boolean hasAction(Action action) {
        return this.getModel().hasAction(action);
    }

    @Override
    public boolean hasOption(Action action, Option option) {
        return this.getModel().hasOption(action, option);
    }

    @Override
    public TryToDoResult tryToDo(boolean award, Action action, Object... attributes) {
        return this.getModel().tryToDo(this, action, award, attributes);
    }

    @Override
    public TryToDoResult tryToDo(Action action, Object... attributes) {
        return this.getModel().tryToDo(this, action, attributes);
    }

    @Override
    public Trade createCost(Action action, Object... attributes) {
        return this.getModel().createCostTrade(this, action, attributes);
    }

    @Override
    public Trade createAward(Action action, Object... attributes) {
        return this.getModel().createAwardTrade(this, action, attributes);
    }

    @Override
    public BehaviorResult getBehaviorResult(Behavior behavior, Object... attributes) {
        return this.getModel().getBehaviorResult(this, behavior, attributes);
    }

    @Override
    public <A> Map<Ability, A> getAbilities(Collection<Ability> abilityCollection, Class<A> clazz, Object... attributes) {
        return this.getModel().getAbilities(this, abilityCollection, clazz, attributes);
    }

    @Override
    public <A extends Ability, V> Map<A, V> getAbilitiesByType(Class<A> abilityClass, Class<V> clazz, Object... attributes) {
        return this.getModel().getAbilitiesByType(this, abilityClass, clazz, attributes);
    }

    @Override
    public <A> A getAbility(Ability ability, Class<A> clazz, Object... attributes) {
        return this.getModel().getAbility(this, ability, clazz, attributes);
    }

    @Override
    public <A> A getAbility(A defaultObject, Ability ability, Object... attributes) {
        return this.getModel().getAbility(this, defaultObject, ability, attributes);
    }

    @Override
    public <O> O getActionOption(Action action, Option option, Object... attributes) {
        return this.getModel().getActionOption(this, action, option, attributes);
    }

    @Override
    public <O> O getActionOption(Action action, O defaultNum, Option option, Object... attributes) {
        return this.getModel().getActionOption(this, defaultNum, action, option, attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Ability> getOwnAbilityBy(Class<? extends Ability>... abilityClass) {
        return this.getModel().getOwnAbilityBy(abilityClass);
    }

    @Override
    public AwardList getAwardList(Action action, Object... attributes) {
        return this.getModel().getAwardList(this, action, attributes);
    }

    @Override
    public CostList getCostList(Action action, Object... attributes) {
        return this.getModel().getCostList(this, action, attributes);
    }

    @Override
    public ActionResult getActionResult(Action action, Object... attributes) {
        return this.getModel().getActionResult(this, action, attributes);
    }

    @Override
    public ActionTrades createActionTrades(Action action, Object... attributes) {
        return this.getModel().createActionTrades(this, action, attributes);
    }

    protected void setPlayerID(long playerID) {
        this.playerID = playerID;
    }

    protected void setModel(IM model) {
        this.model = model;
    }

    @Override
    public Behavior getBehaviorByAction(Action action) {
        return this.model.getBehaviorByAction(action);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.model == null) ? 0 : this.getModel().hashCode());
        result = prime * result + (int) (this.playerID ^ (this.playerID >>> 32));
        result = prime * result + (int) this.getID();
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        AbstractItem other = (AbstractItem) obj;
        if (this.model == null) {
            if (other.model != null)
                return false;
        } else if (!this.getModel().equals(other.model))
            return false;
        if (this.playerID != other.playerID)
            return false;
        if (this.getID() != other.getID())
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AbstractItem [playerID=" + this.playerID + ", model=" + this.model + "]";
    }

}
