package com.tny.game.common.lock;

public interface TimeLimited {

    public boolean isTimeOut();

    public boolean update();

}
