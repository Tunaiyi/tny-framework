package com.tny.game.cache.mysql;

import com.tny.game.cache.*;
import com.tny.game.cache.mysql.dao.*;
import com.tny.game.cache.simple.*;
import org.slf4j.*;
import org.springframework.dao.*;
import org.springframework.jdbc.BadSqlGrammarException;

import java.util.*;

// @SuppressWarnings("restriction")
public class DBCacheClient implements CacheClient {

    private final static Logger logger = LoggerFactory.getLogger(DBCacheClient.class);

    private String name = "default";

    private CacheDAO dao;

    private RawCacheItemFactory<Object, ? extends DBCacheItem<?>> dbCacheItemFactory;

    public DBCacheClient(CacheDAO dao, RawCacheItemFactory<?, ? extends DBCacheItem<?>> dbCacheItemFactory) {
        this(null, dao, dbCacheItemFactory);
    }

    @SuppressWarnings("unchecked")
    public DBCacheClient(String name, CacheDAO dao, RawCacheItemFactory<?, ? extends DBCacheItem<?>> dbCacheItemFactory) {
        if (name == null) {
            this.name = "default";
        }
        this.name = name;
        this.dao = dao;
        this.dbCacheItemFactory = (RawCacheItemFactory<Object, ? extends DBCacheItem<?>>)dbCacheItemFactory;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object get(String key) {
        DBCacheItem item = this.dao.get(key);
        if (item != null) {
            if (item.isItemExpire() || item.getData() == null) {
                this.dao.delete(key);
                return null;
            }
            return item.getObject();
        }
        return null;
    }

    @Override
    public Collection<Object> getMultis(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<DBCacheItem> items = this.dao.get(keys);
        List<Object> objects = new ArrayList<>();
        for (DBCacheItem item : items)
            objects.add(item.getObject());
        return objects;
    }

    @Override
    public Map<String, Object> getMultiMap(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        Collection<DBCacheItem> items = this.dao.get(keys);
        Map<String, Object> data = new HashMap<>();
        for (DBCacheItem item : items)
            data.put(item.getKey(), item.getObject());
        return data;
    }

    @Override
    public CasItem<?> gets(String key) {
        DBCacheItem item = this.dao.get(key);
        if (item == null || item.isItemExpire() || item.getData() == null) {
            this.dao.delete(key);
            return null;
        }
        Object object = item.getObject();
        return object == null ? null : new SimpleCasItem<>(key, object, item.getVersion());
    }

    @Override
    public boolean add(String key, Object value, long millisecond) {
        try {
            Object object = this.get(key);
            if (object != null) {
                return false;
            }
            return this.dao.add(this.dbCacheItemFactory.create(key, value, 0L, millisecond)) > 0;
        } catch (DataIntegrityViolationException e) {
            logger.error("add", e);
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <C extends CacheItem<?>> List<C> addMultis(Collection<C> cacheItems) {
        List<DBCacheItem<?>> items = DBCacheItemHelper.cacheItem2Item(cacheItems);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        int[] results = this.dao.add(items);
        return DBCacheItemHelper.checkResult(items, results);
    }

    @Override
    public <T> List<CacheItem<T>> addMultis(Map<String, T> valueMap, long millisecond) {
        List<DBCacheItem<T>> items = DBCacheItemHelper.map2Item(valueMap, millisecond);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        int[] results = this.dao.add(items);
        return DBCacheItemHelper.checkResult(items, results);
    }

    @Override
    public boolean set(String key, Object value, long millisecond) {
        DBCacheItem item = this.dbCacheItemFactory.create(key, value, 0L, millisecond);
        try {
            return this.dao.set(item) > 0;
        } catch (BadSqlGrammarException e) {
            if (this.dao.update(item) <= 0) {
                try {
                    return this.dao.add(item) > 0;
                } catch (DuplicateKeyException e2) {
                    return this.set(key, value, millisecond);
                }
            }
            return true;
        }
    }

    @Override
    public <C extends CacheItem<?>> List<C> setMultis(Collection<C> cacheItems) {
        List<DBCacheItem<?>> items = DBCacheItemHelper.cacheItem2Item(cacheItems);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        int[] results = this.dao.set(items);
        return DBCacheItemHelper.checkResult(items, results);
    }

    @Override
    public <T> List<CacheItem<T>> setMultis(Map<String, T> valueMap, long millisecond) {
        List<DBCacheItem<T>> items = DBCacheItemHelper.map2Item(valueMap, millisecond);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        int[] results = this.dao.set(items);
        return DBCacheItemHelper.checkResult(items, results);
    }

    @Override
    public boolean update(String key, Object value, long millisecond) {
        return this.dao.update(this.dbCacheItemFactory.create(key, value, 0L, millisecond)) > 0;
    }

    @Override
    public <C extends CacheItem<?>> List<C> updateMultis(Collection<C> cacheItems) {
        List<DBCacheItem<?>> items = DBCacheItemHelper.cacheItem2Item(cacheItems);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        int[] results = this.dao.update(items);
        return DBCacheItemHelper.checkResult(items, results);
    }

    @Override
    public <T> List<CacheItem<T>> updateMultis(Map<String, T> valueMap, long millisecond) {
        List<DBCacheItem<T>> items = DBCacheItemHelper.map2Item(valueMap, millisecond);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        int[] results = this.dao.update(items);
        return DBCacheItemHelper.checkResult(items, results);
    }

    @Override
    public List<String> deleteMultis(Collection<String> keys) {
        int[] results = this.dao.delete(keys);
        List<String> fails = null;
        int index = 0;
        for (String key : keys) {
            if (results[index] <= 0 && results[index] != java.sql.Statement.SUCCESS_NO_INFO) {
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add(key);
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

    @Override
    public boolean delete(String key) {
        return this.dao.delete(key) > 0;
    }

    @Override
    public boolean cas(CasItem<?> item, long millisecond) {
        return this.dao.cas(this.dbCacheItemFactory.create(item.getKey(), item.getData(), item.getVersion(), millisecond)) > 0;
    }

    @Override
    public void shutdown() {
    }

}
