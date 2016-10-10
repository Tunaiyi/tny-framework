package com.tny.game.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class CacheFormatter<O, D> implements CacheTrigger<O, D, Object> {

    public abstract Object format2Save(String key, O object);

    public abstract Object format2Load(String key, D data);

    @Override
    public O triggerLoad(String key, D rawDate, Object newValue) {
        return (O) this.fromLoad(key, rawDate);
    }

    @Override
    public D triggerReplace(String key, O rawObject, Object newValue) {
        return (D) this.toSave(key, rawObject);
    }

    @Override
    public D triggerAdd(String key, O rawObject, Object newValue) {
        return (D) this.toSave(key, rawObject);
    }

    @Override
    public D triggerSet(String key, O rawObject, Object newValue) {
        return (D) this.toSave(key, rawObject);
    }

    @Override
    public void triggerDelete(String key, O rawObject) {
    }

    public Object fromLoad(String key, D object) {
        if (object instanceof Collection) {
            Collection<D> objects = (Collection<D>) object;
            List<Object> list = new ArrayList<>();
            for (D value : objects) {
                list.add(this.format2Load(key, value));
            }
            return objects;
        } else {
            return this.format2Load(key, object);
        }
    }

    public Object toSave(String key, O object) {
        if (object instanceof Collection) {
            Collection<O> objects = (Collection<O>) object;
            List<Object> list = new ArrayList<>();
            for (O value : objects) {
                list.add(this.format2Save(key, value));
            }
            return objects;
        } else {
            return this.format2Save(key, object);
        }
    }

}
