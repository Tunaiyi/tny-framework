package com.tny.game.common.lifecycle;

public interface ServerPrepareStart extends LifecycleHandler {

    default PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass());
    }

    void prepareStart() throws Exception;

}
