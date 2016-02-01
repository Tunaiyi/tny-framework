package com.tny.game.asyndb;

import java.util.Collection;
import java.util.Map;

/**
 * @author KGTny
 * @ClassName: Persistor
 * @Description: 持久化执行器
 * @date 2011-10-8 下午4:49:23
 * <p>
 * 持久化执行器
 * <p>
 * 负责执行持久化任务<br>
 */
public interface Synchronizer<O> {

    public boolean insert(O object);

    public Collection<O> insert(Collection<O> objects);

    public boolean update(O object);

    public Collection<O> update(Collection<O> objects);

    public boolean delete(O object);

    public Collection<O> delete(Collection<O> objects);

    public boolean save(O object);

    public Collection<O> save(Collection<O> objects);

    public O get(Class<? extends O> clazz, String key);

    public Map<String, ? extends O> get(Class<? extends O> clazz, Collection<String> keyValues);

}
