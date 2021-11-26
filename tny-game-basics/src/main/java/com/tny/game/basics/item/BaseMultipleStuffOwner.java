package com.tny.game.basics.item;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class BaseMultipleStuffOwner<IM extends ItemModel, SM extends MultipleStuffModel, S extends MultipleStuff<? extends SM, ?>>
		extends BaseStuffOwner<IM, SM, S> implements MultipleStuffOwner<IM, SM, S> {

}
