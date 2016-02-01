package com.tny.game.suite.base;

import com.tny.game.base.item.CountableStuff;
import com.tny.game.base.item.CountableStuffOwner;
import com.tny.game.base.item.ItemModel;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class GameCountableStuffOwner<IM extends ItemModel, S extends CountableStuff<IM>>
        extends GameItemOwner<IM, S> implements CountableStuffOwner<IM, S> {

}
