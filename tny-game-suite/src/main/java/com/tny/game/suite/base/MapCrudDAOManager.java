package com.tny.game.suite.base;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.collection.*;
import net.paoding.rose.jade.annotation.SQLParam;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;

public abstract class MapCrudDAOManager<T, VO, ID extends Serializable> extends CrudDAOManager<T, VO, ID> {

    private Map<ID, T> taskMap;

    protected MapCrudDAOManager(Map<ID, T> taskMap) {
        this.taskMap = taskMap;
    }

    protected MapCrudDAOManager() {
        this.taskMap = new CopyOnWriteMap<>();
    }

    public void load() {
        Collection<T> objects = super.findAll();
        objects.forEach(o -> taskMap.putIfAbsent(getId(o), o));
        this.findAll();
    }

    protected abstract ID getId(T object);

    private Collection<T> collection2DB(Collection<? extends T> entities,
            Function<Collection<? extends T>, Collection<T>> dbOp,
            Consumer<T> cached) {
        Collection<T> failed = dbOp.apply(entities);
        entities.forEach(o -> {
            if (failed.contains(o))
                return;
            cached.accept(o);
        });
        return failed;
    }

    private T putObject(T object) {
        return this.taskMap.put(getId(object), object);
    }

    private T putIfAbsent(T object) {
        return this.taskMap.putIfAbsent(getId(object), object);
    }

    private Collection<T> putIfAbsent(Collection<T> objects) {
        Collection<T> os = new ArrayList<>();
        for (T o : objects) {
            T old = this.taskMap.putIfAbsent(getId(o), o);
            os.add(old != null ? old : old);
        }
        return os;
    }

    @Override
    public boolean add(T entity) {
        if (super.add(entity)) {
            this.taskMap.put(getId(entity), entity);
            return true;
        }
        return false;
    }

    @Override
    public Collection<T> add(Collection<? extends T> entities) {
        return collection2DB(entities, super::add, this::putIfAbsent);
    }

    @Override
    public boolean save(T entity) {
        if (super.save(entity)) {
            this.taskMap.put(getId(entity), entity);
            return true;
        }
        return false;
    }

    @Override
    public Collection<T> save(Collection<? extends T> entities) {
        return collection2DB(entities, super::save, this::putObject);
    }

    @Override
    public void remove(ID id) {
        dao().delete(id);
        this.taskMap.remove(id);
    }

    @Override
    public void remove(T entity) {
        dao().delete(object2VO(entity));
        this.taskMap.remove(getId(entity));
    }

    @Override
    public void remove(Collection<? extends T> entities) {
        dao().delete(objects2VOs(entities));
        entities.forEach((o) -> this.taskMap.remove(getId(o)));
    }

    @Override
    public void removeAll(Collection<? extends ID> ids) {
        dao().deleteAll(ids);
        this.taskMap.clear();
    }

    @Override
    public boolean update(@SQLParam("e") T entity) {
        if (super.update(entity)) {
            this.taskMap.put(getId(entity), entity);
            return true;
        }
        return false;
    }

    @Override
    public Collection<T> update(Collection<? extends T> entities) {
        return collection2DB(entities, super::update, this::putObject);
    }

    @Override
    public T find(ID id) {
        T object = this.taskMap.get(id);
        if (object != null)
            return object;
        VO vo = this.dao().find(id);
        if (vo == null)
            return null;
        object = vo2Object(vo);
        if (object != null) {
            T old = this.taskMap.putIfAbsent(getId(object), object);
            if (old != null)
                object = old;
        }
        return object;
    }

    public Map<ID, T> getAll() {
        return Collections.unmodifiableMap(this.taskMap);
    }

    @Override
    public Collection<T> findAll() {
        if (!this.taskMap.isEmpty())
            return new ArrayList<>(this.taskMap.values());
        Collection<VO> vos = this.dao().findAll();
        Collection<T> objects = vos2Objects(vos);
        if (objects != null)
            return putIfAbsent(objects);
        return ImmutableList.of();
    }

    @Override
    public Collection<T> findAll(Collection<ID> ids) {
        List<T> os = new ArrayList<>();
        List<ID> noExist = new ArrayList<>();
        for (ID id : ids) {
            T object = this.taskMap.get(id);
            if (object != null)
                os.add(object);
            else
                noExist.add(id);
        }
        Collection<VO> vos = this.dao().findAll(noExist);
        Collection<T> objects = vos2Objects(vos);
        if (objects != null)
            os.addAll(putIfAbsent(objects));
        return os;
    }

}
