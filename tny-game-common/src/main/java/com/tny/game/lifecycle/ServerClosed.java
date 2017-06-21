package com.tny.game.lifecycle;


public interface ServerClosed extends LifecycleHandler {

    default PostCloser getPostCloser() {
        return PostCloser.value(this.getClass());
    }

    void onClosed() throws Throwable;

}
