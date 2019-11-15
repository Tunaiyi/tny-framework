package com.tny.game.suite.base;

import com.tny.game.base.item.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameItemType extends ItemType, RegisterSelf {

    @Override
    default void registerSelf() {
        ItemTypes.register(this);
    }

}
