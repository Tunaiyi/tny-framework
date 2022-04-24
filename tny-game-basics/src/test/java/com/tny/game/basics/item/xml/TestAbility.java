package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;

public enum TestAbility implements Ability {

    ATTACK,

    DEFEND;

    @Override
    public int id() {
        return 0;
    }

    //	@Override
    //	public int getID() {
    //		return 0;
    //	}

}
