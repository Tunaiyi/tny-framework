package com.tny.game.cache.mysql;

import com.mysql.jdbc.Statement;
import com.tny.game.cache.CacheHelper;
import com.tny.game.cache.CacheItem;

import java.util.*;
import java.util.Map.Entry;

public class ClientHelper extends CacheHelper {

    @SuppressWarnings("unchecked")
    public static List<AlterDBItem<Object>> cacheItem2Item(Collection<? extends CacheItem<?>> cacheItems) {
        if (cacheItems.isEmpty())
            return Collections.emptyList();
        List<AlterDBItem<Object>> items = new ArrayList<>();
        for (CacheItem<?> item : cacheItems) {
            if (item instanceof AlterDBItem) {
                items.add((AlterDBItem<Object>) item);
            } else {
                items.add(new AlterDBItem<Object>(item));
            }
        }
        return items;
    }

    public static <T> List<AlterDBItem<T>> map2Item(Map<String, T> valueMap, long millisecond) {
        if (valueMap.isEmpty())
            return Collections.emptyList();
        List<AlterDBItem<T>> items = new ArrayList<>();
        for (Entry<String, T> item : valueMap.entrySet()) {
            items.add(new AlterDBItem<T>(item.getKey(), item.getValue(), 0L, millisecond));
        }
        return items;
    }

    public static <T, C extends CacheItem<?>> List<C> checkResult(List<AlterDBItem<T>> items, int[] results) {
        List<C> fails = null;
        for (int index = 0; index < results.length; index++) {
            if (results[index] <= 0 && results[index] != Statement.SUCCESS_NO_INFO) {
                AlterDBItem<?> item = items.get(index);
                fails = ClientHelper.getAndCreate(fails);
                C failOne = item.getItem();
                fails.add(failOne);
            }
        }
        return ClientHelper.checkEmpty(fails);
    }

}
