package com.tny.game.common.lifecycle;


public interface AppClosed extends LifecycleHandler {

    default PostCloser getPostCloser() {
        return PostCloser.value(this.getClass());
    }

    void onClosed() throws Exception;

}
