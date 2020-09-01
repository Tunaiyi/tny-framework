package com.tny.game.common.lifecycle;


public interface AppPostStart extends LifecycleHandler {

    default PostStarter getPostStarter() {
        return PostStarter.value(this.getClass());
    }

    void postStart() throws Exception;

}
