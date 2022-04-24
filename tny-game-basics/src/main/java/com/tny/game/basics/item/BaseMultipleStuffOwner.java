package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class BaseMultipleStuffOwner<IM extends ItemModel, SM extends MultipleStuffModel, S extends BaseMultipleStuff<SM, ?>>
        extends BaseStuffOwner<IM, SM, S> implements MultipleStuffOwner<IM, SM, S> {

    protected BaseMultipleStuffOwner() {
    }

    protected BaseMultipleStuffOwner(long playerId, IM model) {
        super(playerId, model);
    }

    protected void deductStuff(S stuff, TradeItem<SM> tradeItem, Action action, Attributes attributes) {
        if (stuff != null) {
            stuff.deduct(action, tradeItem, attributes);
        }
    }

    protected void rewardStuff(S stuff, TradeItem<SM> tradeItem, Action action, Attributes attributes) {
        if (stuff != null) {
            stuff.reward(action, tradeItem, attributes);
        }
    }

    public Number getNumber(SM model) {
        S stuff = this.getItemById(model.getId());
        if (stuff == null) {
            return 0L;
        }
        return stuff.getNumber();
    }

    @Override
    public boolean isOverage(SM model, AlterType type, Number number) {
        S stuff = this.getItemById(model.getId());
        if (stuff == null) {
            if (!model.isNumberLimit()) {
                return false;
            }
            Number limit = model.countNumberLimit(null);
            if (limit.longValue() < 0) {
                return false;
            }
            return limit.longValue() < number.longValue();
        }
        return false;
    }

    @Override
    public boolean isLack(SM model, AlterType type, Number number) {
        S stuff = this.getItemById(model.getId());
        if (stuff == null) {
            return 0 < number.longValue();
        }
        return stuff.getNumber().longValue() < number.longValue();
    }

}
