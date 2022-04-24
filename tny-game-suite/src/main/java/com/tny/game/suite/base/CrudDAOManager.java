package com.tny.game.suite.base;

import net.paoding.rose.jade.annotation.SQLParam;
import net.paoding.rose.jade.dao.CrudDAO;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CrudDAOManager<T, VO, ID extends Serializable> {

    protected abstract CrudDAO<VO, ID> dao();

    protected abstract VO object2VO(T object);

    protected abstract T vo2Object(VO vo);

    protected Collection<VO> objects2VOs(Collection<? extends T> objects) {
        return objects.stream().map(this::object2VO).collect(Collectors.toSet());
    }

    protected Collection<T> vos2Objects(Collection<VO> vos) {
        return vos.stream().map(this::vo2Object).collect(Collectors.toSet());
    }

    public boolean add(T entity) {
        return dao().insert(object2VO(entity));
    }

    public Collection<T> add(Collection<? extends T> entities) {
        Collection<VO> vos = objects2VOs(entities);
        boolean[] results = dao().insert(vos);
        return checkFails(results, entities);
    }

    public boolean save(T entity) {
        return dao().save(object2VO(entity));
    }

    public Collection<T> save(Collection<? extends T> entities) {
        Collection<VO> vos = objects2VOs(entities);
        boolean[] results = dao().save(vos);
        return checkFails(results, entities);
    }

    public void remove(ID id) {
        dao().delete(id);
    }

    public void remove(T entity) {
        dao().delete(object2VO(entity));
    }

    public void remove(Collection<? extends T> entities) {
        dao().delete(objects2VOs(entities));
    }

    public void removeAll(Collection<? extends ID> ids) {
        dao().deleteAll(ids);
    }

    public boolean update(@SQLParam("e") T entity) {
        return dao().update(object2VO(entity));
    }

    public Collection<T> update(Collection<? extends T> entities) {
        Collection<VO> vos = objects2VOs(entities);
        boolean[] results = dao().update(vos);
        return checkFails(results, entities);
    }

    public T find(ID id) {
        VO vo = this.dao().find(id);
        if (vo == null) {
            return null;
        }
        return vo2Object(vo);
    }

    public Collection<T> findAll(Collection<ID> ids) {
        Collection<VO> vos = this.dao().findAll(ids);
        return vos2Objects(vos);
    }

    public Collection<T> findAll() {
        Collection<VO> vos = this.dao().findAll();
        return vos2Objects(vos);
    }

    protected Collection<T> checkFails(boolean[] results, Collection<? extends T> entities) {
        Collection<T> fails = null;
        int index = 0;
        for (T e : entities) {
            if (!results[index++]) {
                if (fails == null) {
                    fails = new ArrayList<>();
                }
                fails.add(e);
            }
        }
        if (fails == null) {
            fails = Collections.emptyList();
        }
        return fails;
    }

}
