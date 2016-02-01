package com.tny.game.base.item.xml;

import com.tny.game.base.item.Ability;

public enum TestAbility implements Ability {

    ATTACK,

    DEFEND;

    @Override
    public Integer getID() {
        return 0;
    }

    //	@Override
    //	public int getID() {
    //		return 0;
    //	}

}
