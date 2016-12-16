package com.tny.game.lifecycle;


public interface ServerPostClose extends LifecycleHandler {

    default PostCloser getPostCloser() {
        return PostCloser.value(this.getClass());
    }

    void postClose() throws Exception;

}
