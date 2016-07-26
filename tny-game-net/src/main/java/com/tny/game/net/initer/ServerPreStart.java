package com.tny.game.net.initer;

public interface ServerPreStart extends ServerInitialize {

    PerIniter getIniter();

    @Override
    default StartIniter getStartIniter() {
        return getIniter();
    }

}
