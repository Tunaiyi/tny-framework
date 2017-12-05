package com.tny.game.suite.base;

import com.tny.game.base.item.Identifiable;
import com.tny.game.suite.login.IDAide;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameIdentifiable extends Identifiable {

    default int getServerID() {
        return IDAide.userID2SID(getPlayerID());
    }

}
