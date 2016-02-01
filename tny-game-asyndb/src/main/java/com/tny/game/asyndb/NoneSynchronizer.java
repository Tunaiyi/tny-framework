package com.tny.game.asyndb;

import java.util.Collection;
import java.util.Collections;

public abstract class NoneSynchronizer<O> implements Synchronizer<O> {

    @Override
    public boolean insert(O object) {
        return true;
    }

    @Override
    public Collection<O> insert(Collection<O> objects) {
        return Collections.emptyList();
    }

    @Override
    public boolean update(O object) {
        return true;
    }

    @Override
    public Collection<O> update(Collection<O> objects) {
        return Collections.emptyList();
    }

    @Override
    public boolean delete(O object) {
        return true;
    }

    @Override
    public Collection<O> delete(Collection<O> objects) {
        return Collections.emptyList();
    }

    @Override
    public boolean save(O object) {
        return true;
    }

    @Override
    public Collection<O> save(Collection<O> objects) {
        return Collections.emptyList();
    }

}
