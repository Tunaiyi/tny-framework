package com.tny.game.suite.oplog;

import com.tny.game.oplog.*;

/**
 * Created by Kun Yang on 16/5/25.
 */
public interface UserOpLogFactory {

    UserOpLog create(long playerID);

}
