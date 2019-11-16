package com.tny.game.suite.base;

import com.tny.game.base.item.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class GameCountableStuffStorage<IM extends ItemModel, SM extends ItemModel, S extends CountableStuff<SM, ?>>
        extends GameItemStorage<IM, SM, S> implements CountableStuffStorage<IM, SM, S> {

}
