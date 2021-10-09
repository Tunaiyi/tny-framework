package com.tny.game.suite.base;

import com.tny.game.basics.item.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class GameCountableStuffOwner<IM extends ItemModel, SM extends ItemModel, S extends CountableStuff<SM, ?>>
		extends GameItemStuffOwner<IM, SM, S> implements CountableStuffOwner<IM, SM, S> {

}
