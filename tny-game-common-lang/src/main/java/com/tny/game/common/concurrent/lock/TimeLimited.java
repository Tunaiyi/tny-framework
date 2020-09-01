package com.tny.game.common.concurrent.lock;

public interface TimeLimited {

    public boolean isTimeOut();

    public boolean update();

}
