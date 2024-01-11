package com.tny.game.cache;

import java.util.*;

public class CacheItemHelper {

    static final int OPERATION_ADD = 1;

    private static final int OPERATION_UPDATE = 2;

    private static final int OPERATION_SAVE = 3;

    static <T> List<T> cacheItem2Object(
            List<RawCacheItem<T, ?>> failedObjects) {
        if (failedObjects.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> items = new ArrayList<>();
        for (RawCacheItem<T, ?> item : failedObjects)
            items.add(item.getRawValue());
        return items;
    }

    @SuppressWarnings("unchecked")
    static <T> List<RawCacheItem<T, ?>> objects2CacheItems(
            ToCacheClassHolderFactory factory,
            RawCacheItemFactory cacheItemFactory,
            Collection<T> objectCollection, int operation, long millisecond) {
        List<RawCacheItem<T, ?>> items = new ArrayList<>();
        for (T rawValue : objectCollection) {
            ToCacheClassHolder holder = factory.getCacheClassHolder(rawValue
                    .getClass());
            String key = holder.getKey(rawValue);
            Object alterValue;
            switch (operation) {
                case OPERATION_ADD:
                    alterValue = holder.triggerInsert(key, rawValue);
                    break;
                case OPERATION_SAVE:
                    alterValue = holder.triggerSave(key, rawValue);
                    break;
                case OPERATION_UPDATE:
                    alterValue = holder.triggerUpdate(key, rawValue);
                    break;
                default:
                    alterValue = rawValue;
                    break;
            }
            RawCacheItem<T, ?> item = cacheItemFactory.create(key, alterValue, 0L, millisecond);
            item.setRawValue(rawValue);
            items.add(item);
        }
        return items;
    }

    public static byte[][] strings2Bytes(Collection<String> strings) {
        byte[][] list = new byte[strings.size()][];
        int index = 0;
        for (String string : strings) {
            list[index++] = string.getBytes();
        }
        return list;
    }

    public static <T> List<T> checkEmpty(List<T> fails) {
        if (fails == null || fails.isEmpty()) {
            fails = Collections.emptyList();
        }
        return fails;
    }

    public static <T> List<T> getAndCreate(List<T> fails) {
        return fails == null ? new ArrayList<>() : fails;
    }

    public static <T> Map<String, T> getAndCreate(Map<String, T> fails) {
        return fails == null ? new HashMap<>() : fails;
    }

}
