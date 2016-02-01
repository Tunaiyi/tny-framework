package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;

/**
 * 抽象物品管理器
 *
 * @param <SM>
 * @param <S>
 * @author KGTny
 */
public abstract class AbstractCountableStuffOwner<SM extends CountableStuffModel, S extends AbstractCountableStuff<SM>> extends
        StuffOwner<SM, S> {

    //	@Override
    //	public TryToTradeResult tryReceive(SM model, int reciveNum, AlertType alertType) {
    //		S stuff = this.itemMap.get(model.getID());
    //		if (model.isNumberLimit() && stuff == null)
    //			stuff = createStuff(model);
    //		return alertType.isExcess(stuff, reciveNum) ?
    //				new TryToTradeResult(this.getPlayerID(), model, stuff != null ? stuff.getNumber() : 0, reciveNum, ItemResultCode.FULL_NUMBER) :
    //				new TryToTradeResult(this.getPlayerID());
    //	}
    //
    //	@Override
    //	public TryToTradeResult tryConsume(SM model, int costNum, AlertType alertType) {
    //		S stuff = this.itemMap.get(model.getID());
    //		return (stuff == null && costNum > 0) || alertType.isLack(stuff, costNum) ?
    //				new TryToTradeResult(this.getPlayerID(), model, stuff != null ? stuff.getNumber() : 0, costNum, ItemResultCode.FULL_NUMBER) :
    //				new TryToTradeResult(this.getPlayerID());
    //	}

    protected void doConsume(S stuff, Action action, TradeItem<? extends SM> tradeItem) {
        stuff.consume(action, tradeItem);
    }

    protected void doReceive(S stuff, Action action, TradeItem<? extends SM> tradeItem) {
        stuff.receive(action, tradeItem);
    }

    /**
     * 构建item <br>
     *
     * @param model
     * @return
     */
    protected abstract <OIM extends ItemModel> S createStuff(OIM oim);

}
