package com.tny.game.suite.oplog;

import com.tny.game.net.dispatcher.CurrentCMD;
import com.tny.game.oplog.OpLog;
import com.tny.game.oplog.simple.SimpleOpLog;

/**
 * Created by Kun Yang on 16/5/25.
 */
public interface OpLogFactory {

    default OpLog createLog() {
        CurrentCMD cmd = CurrentCMD.getCurrent();
        return new SimpleOpLog(cmd.getProtocol());
    }

}
