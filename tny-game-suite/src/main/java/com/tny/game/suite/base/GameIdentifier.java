package com.tny.game.suite.base;

import com.tny.game.basics.item.*;
import com.tny.game.suite.login.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameIdentifier extends Owned {

    default int getZoneId() {
        return IDAide.userID2Zone(getOwnerId());
    }

}
