package com.tny.game.suite.initer;

import com.tny.game.common.lifecycle.*;
import com.tny.game.oplog.*;

public class OpLogSnapshotIniter implements AppPrepareStart {

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() {
        OpLogSnapshotLoader.waitLoad();
    }

}
