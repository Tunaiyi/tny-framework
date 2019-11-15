package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import com.tny.game.suite.login.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameIdentifier extends Identifier {

    default int getZoneId() {
        return IDAide.userID2Zone(getPlayerId());
    }

}
