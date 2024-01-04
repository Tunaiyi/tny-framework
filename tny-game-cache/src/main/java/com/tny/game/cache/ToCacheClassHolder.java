package com.tny.game.cache;

import com.tny.game.cache.annotation.*;
import com.tny.game.common.reflect.*;
import com.tny.game.common.reflect.javassist.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import static com.tny.game.common.utils.StringAide.*;

@SuppressWarnings("deprecation")
public class ToCacheClassHolder extends TriggerHolder {

    private ToCache toCache;

    private Class<?> cacheClass;

    /**
     * 次级标识获取方法
     */
    private List<PropertyAccessor> keyIDMethods;

    public ToCacheClassHolder(Class<?> cacheClass, CacheTriggerFactory factory) {
        if (cacheClass == null) {
            throw new NullPointerException("init ToCacheClassHolder class is null");
        }
        this.cacheClass = cacheClass;
        ClassAccessor gClass = JavassistAccessors.getGClass(cacheClass);
        this.toCache = this.cacheClass.getAnnotation(ToCache.class);
        if (this.toCache == null) {
            throw new NullPointerException("init ToCacheClassHolder " + cacheClass + " don't have Item annotation");
        }
        if (this.toCache.cacheKeys() == null) {
            List<Field> idFieldList = ReflectAide.getDeepFieldsByAnnotation(cacheClass, CacheID.class);
            this.initIDMethods(gClass, idFieldList);
        } else {
            this.initIDMethods(gClass, this.toCache.cacheKeys());
        }
        this.triggers = this.getCacheTrigger0(factory, this.toCache.triggers());
    }

    @SuppressWarnings("rawtypes")
    private List<CacheTrigger> getCacheTrigger0(CacheTriggerFactory factory, Class<? extends CacheTrigger<?, ?, ?>>[] triggerClass) {
        if (triggerClass == null || triggerClass.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<CacheTrigger> triggerList = new ArrayList<CacheTrigger>();
        for (Class<? extends CacheTrigger<?, ?, ?>> clazz : triggerClass) {
            CacheTrigger<?, ?, ?> trigger = factory.getCacheTrigger(clazz);
            if (trigger != null) {
                triggerList.add(trigger);
            } else {
                throw new NullPointerException(format("没有找到 {} 的 {} 触发器!", this.cacheClass, clazz));
            }
        }
        if (triggerList.isEmpty()) {
            return Collections.emptyList();
        }
        return triggerList;
    }

    public void initIDMethods(ClassAccessor gClass, List<Field> idFieldList) {
        this.keyIDMethods = new ArrayList<PropertyAccessor>(idFieldList.size());
        SortedMap<Integer, PropertyAccessor> idMethodMap = new TreeMap<Integer, PropertyAccessor>();
        for (Field field : idFieldList) {
            CacheID cacheID = field.getAnnotation(CacheID.class);
            if (cacheID == null) {
                continue;
            }
            String propertyName = cacheID.name().equals("") ? field.getName() : cacheID.name();
            PropertyAccessor accessor = gClass.getProperty(propertyName);
            if (accessor == null) {
                throw new IllegalArgumentException("[" + this.cacheClass + "]Index " + cacheID.index() + " - " + propertyName + " is not exist");
            }
            if (!accessor.isReadable()) {
                throw new IllegalArgumentException(
                        "[" + this.cacheClass + "]Index " + cacheID.index() + " - " + accessor.getName() + " is unreadable");
            }
            PropertyAccessor old = idMethodMap.put(cacheID.index(), accessor);
            if (old != null) {
                throw new IllegalArgumentException("[" + this.cacheClass + "]Index " + cacheID.index() + " exist " + old.getName()
                                                   + ",  so can't put " + accessor.getName());
            }
        }
        if (idMethodMap.isEmpty()) {
            throw new IllegalArgumentException("[" + this.cacheClass + "] is not exist @CacheID");
        }
        for (Entry<Integer, PropertyAccessor> entry : idMethodMap.entrySet())
            this.keyIDMethods.add(entry.getValue());
    }

    public void initIDMethods(ClassAccessor gClass, String[] idKeys) {
        this.keyIDMethods = new ArrayList<PropertyAccessor>(idKeys.length);
        for (String idKey : idKeys) {
            PropertyAccessor accessor = gClass.getProperty(idKey);
            if (accessor == null) {
                throw new IllegalArgumentException("[" + this.cacheClass + "]Index " + idKey + " is not exist");
            }
            if (!accessor.isReadable()) {
                throw new IllegalArgumentException("[" + this.cacheClass + "]Index " + accessor.getName() + " is unreadable");
            }
            this.keyIDMethods.add(accessor);
        }
    }

    public Object[] getKeyValues(Object object) {
        Object[] values = new Object[this.keyIDMethods.size()];
        for (int index = 0; index < this.keyIDMethods.size(); index++) {
            Object value;
            PropertyAccessor keyMethod = this.keyIDMethods.get(index);
            try {
                value = keyMethod.getPropertyValue(object);
            } catch (Exception e) {
                throw new RuntimeException("invoke keyMethod as " + keyMethod + " exception", e);
            }
            if (value == null) {
                throw new NullPointerException("invoke keyMethod as " + keyMethod + " return null");
            }
            values[index] = value;
        }
        return values;
    }

    public String getKey(Object object) {
        String key = CacheUtils.getKeyHead(this.toCache.prefix());
        String separator = CacheUtils.getSeparator();
        for (PropertyAccessor keyMethod : this.keyIDMethods) {
            Object value;
            try {
                value = keyMethod.getPropertyValue(object);
            } catch (Exception e) {
                throw new RuntimeException("invoke keyMethod as " + keyMethod + " exception", e);
            }
            if (value == null) {
                throw new NullPointerException("invoke keyMethod as " + keyMethod + " return null");
            }
            key += separator + value;
        }
        return key;
    }

    public String getKey(Object... keyValues) {
        return CacheUtils.getKey(this.toCache.prefix(), keyValues);
    }

    public String getSource() {
        return this.toCache.source();
    }

    public Class<?> getCacheClass() {
        return this.cacheClass;
    }

    @Override
    public String toString() {
        String value = this.cacheClass + " ==> ";
        for (Object o : this.triggers)
            value += o.getClass() + " | ";
        return value;
    }

}
