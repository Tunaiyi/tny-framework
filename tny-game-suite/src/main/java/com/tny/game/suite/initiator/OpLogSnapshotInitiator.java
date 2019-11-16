package com.tny.game.suite.initiator;

import com.tny.game.common.lifecycle.*;
import com.tny.game.oplog.*;

public class OpLogSnapshotInitiator implements AppPrepareStart {

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() {
        OpLogSnapshotLoader.waitLoad();
    }

}
