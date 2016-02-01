package com.tny.game.suite.base;

import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.dao.CrudDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class BaseDAOManager<T, ID extends Serializable> {

    protected abstract CrudDAO<T, ID> dao();

    public boolean add(T entity) {
        return dao().insert(entity);
    }

    public Collection<T> add(Collection<? extends T> entities) {
        boolean[] results = dao().insert(entities);
        return checkFails(results, entities);
    }

    private Collection<T> checkFails(boolean[] results, Collection<? extends T> entities) {
        Collection<T> fails = null;
        int index = 0;
        for (T e : entities) {
            if (!results[index++]) {
                if (fails == null)
                    fails = new ArrayList<>();
                fails.add(e);
            }
        }
        if (fails == null)
            fails = Collections.emptyList();
        return fails;
    }

    public boolean save(T entity) {
        return dao().save(entity);
    }

    public Collection<T> save(Collection<? extends T> entities) {
        boolean[] results = dao().save(entities);
        return checkFails(results, entities);
    }

    public void remove(ID id) {
        dao().delete(id);
    }

    public void remove(T entity) {
        dao().delete(entity);
    }

    public void remove(Collection<? extends T> entities) {
        dao().delete(entities);
    }

    public void removeAll(Collection<? extends ID> ids) {
        dao().deleteAll(ids);
    }

    public boolean update(@SQLParam("e") T entity) {
        return dao().update(entity);
    }

    public Collection<T> update(Collection<? extends T> entities) {
        boolean[] results = dao().update(entities);
        return checkFails(results, entities);
    }

    public T find(ID id) {
        return this.dao().find(id);
    }

    public Collection<T> findAll(Collection<ID> ids) {
        return this.dao().findAll(ids);
    }

}
