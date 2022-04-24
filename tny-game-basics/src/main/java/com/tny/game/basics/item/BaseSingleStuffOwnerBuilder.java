package com.tny.game.basics.item;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/26 3:07 上午
 */
@SuppressWarnings("unchecked")
public abstract class BaseSingleStuffOwnerBuilder<
        IM extends ItemModel,
        SM extends StuffModel,
        S extends Stuff<? extends SM>,
        O extends BaseSingleStuffOwner<IM, ? extends SM, S>,
        B extends StuffOwnerBuilder<IM, S, O, B>>
        extends StuffOwnerBuilder<IM, S, O, B> {

    private int idIndexCounter = 0;

    public B setIdIndexCounter(int idIndexCounter) {
        this.idIndexCounter = idIndexCounter;
        return (B)this;
    }

    @Override
    public O createItem() {
        O item = super.createItem();
        item.setIdIndexCounter(idIndexCounter);
        return item;
    }

}
