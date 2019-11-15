package com.tny.game.suite.oplog;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.oplog.*;
import com.tny.game.oplog.simple.*;

/**
 * Created by Kun Yang on 16/5/25.
 */
public interface OpLogFactory {

    default OpLog createLog() {
        ControllerContext cmd = ControllerContext.getCurrent();
        return new SimpleOpLog(cmd.getProtocol());
    }

}
