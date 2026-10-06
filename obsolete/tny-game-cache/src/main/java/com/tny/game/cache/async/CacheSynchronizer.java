/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.cache.async;

import com.tny.game.asyndb.*;
import com.tny.game.cache.*;

import java.util.*;

public class CacheSynchronizer<T> implements Synchronizer<T> {

    //	private final static Logger logger = LoggerFactory.getLogger(LogName.CACHE_SYNCHRONIZER);

    private DirectCache cache;

    private boolean toDB = true;

    public CacheSynchronizer(DirectCache cache) {
        super();
        this.cache = cache;
    }

    @Override
    public T get(Class<? extends T> clazz, String key) {
        return this.cache.getObjectByKey(clazz, key);
    }

    @Override
    public Map<String, ? extends T> get(Class<? extends T> clazz, Collection<String> keyValues) {
        return this.cache.getObjectMapByKeys(clazz, keyValues);
    }

    //	@Override
    //	public T get_(Class<? extends T> clazz, String key) {
    //		try {
    //			return this.cache.getObject(clazz, key);
    //		} catch (Exception e) {
    //			logger.error("", e);
    //			return null;
    //		}
    //	}

    @Override
    public boolean insert(T object) {
        if (!this.toDB) {
            return true;
        }
        return this.cache.addObject(object);
    }

    @Override
    public boolean update(T object) {
        if (!this.toDB) {
            return true;
        }
        return this.cache.updateObject(object);
    }

    @Override
    public boolean delete(T object) {
        if (!this.toDB) {
            return true;
        }
        return this.cache.deleteObject(object);
    }

    @Override
    public boolean save(T object) {
        if (!this.toDB) {
            return true;
        }
        return this.cache.setObject(object);
    }

    public void setToDB(boolean toDB) {
        this.toDB = toDB;
    }

    @Override
    public Collection<T> insert(Collection<T> objects) {
        if (!this.toDB) {
            return Collections.emptyList();
        }
        return this.cache.addObject(objects);
    }

    @Override
    public Collection<T> update(Collection<T> objects) {
        if (!this.toDB) {
            return Collections.emptyList();
        }
        return this.cache.updateObject(objects);
    }

    @Override
    public Collection<T> delete(Collection<T> objects) {
        if (!this.toDB) {
            return Collections.emptyList();
        }
        return this.cache.deleteObject(objects);
    }

    @Override
    public Collection<T> save(Collection<T> objects) {
        if (!this.toDB) {
            return Collections.emptyList();
        }
        return this.cache.setObject(objects);
    }

}
