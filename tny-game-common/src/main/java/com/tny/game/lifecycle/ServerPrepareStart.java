package com.tny.game.lifecycle;

public interface ServerPrepareStart extends LifecycleHandler {

    default PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass());
    }

    void prepareStart() throws Throwable;

}
