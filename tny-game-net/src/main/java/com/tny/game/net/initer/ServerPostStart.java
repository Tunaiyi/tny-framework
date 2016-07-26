package com.tny.game.net.initer;


public interface ServerPostStart extends ServerInitialize {

    PostIniter getIniter();

    @Override
    default StartIniter getStartIniter() {
        return getIniter();
    }

}
