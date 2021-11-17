package com.tny.game.basics.item;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class BaseCountableStuffOwner<IM extends ItemModel, SM extends ItemModel, S extends CountableStuff<SM, ?>>
		extends BaseStuffOwner<IM, SM, S> implements CountableStuffOwner<IM, SM, S> {

}
