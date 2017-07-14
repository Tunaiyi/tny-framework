package com.tny.game.common.lifecycle;


public interface ServerPostStart extends LifecycleHandler {

    default PostStarter getPostStarter() {
        return PostStarter.value(this.getClass());
    }

    void postStart() throws Throwable;

}
