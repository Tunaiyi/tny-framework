package com.tny.game.suite.oplog;

import com.tny.game.net.command.dispatcher.ControllerContext;
import com.tny.game.oplog.OpLog;
import com.tny.game.oplog.simple.SimpleOpLog;

/**
 * Created by Kun Yang on 16/5/25.
 */
public interface OpLogFactory {

    default OpLog createLog() {
        ControllerContext cmd = ControllerContext.getCurrent();
        return new SimpleOpLog(cmd.getProtocol());
    }

}
