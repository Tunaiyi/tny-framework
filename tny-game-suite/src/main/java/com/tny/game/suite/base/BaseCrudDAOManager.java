package com.tny.game.suite.base;

import java.io.Serializable;

public abstract class BaseCrudDAOManager<T, ID extends Serializable> extends CrudDAOManager<T, T, ID> {

    @Override
    protected T object2VO(T object) {
        return object;
    }

    @Override
    protected T vo2Object(T vo) {
        return vo;
    }

}
