package com.tny.game.cache.mysql;

import com.tny.game.cache.CacheClient;
import com.tny.game.cache.CacheHelper;
import com.tny.game.cache.CacheItem;
import com.tny.game.cache.CasItem;
import com.tny.game.cache.mysql.dao.CacheDAO;
import com.tny.game.cache.simple.SimpleCasItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;

import java.util.*;

// @SuppressWarnings("restriction")
public class DBCacheClient implements CacheClient {

    private final static Logger logger = LoggerFactory.getLogger(DBCacheClient.class);

    private String name = "default";

    @Autowired
    private CacheDAO dao;

    private DBItemFactory dbItemFactory;

    public DBCacheClient() {
        this.dbItemFactory = new DBAlterCacheItemFactory();
    }

    public DBCacheClient(DBItemFactory dbItemFactory) {
        this(null, dbItemFactory);
    }

    public DBCacheClient(String name, DBItemFactory dbItemFactory) {
        if (name == null)
            this.name = "default";
        this.name = name;
        this.dbItemFactory = dbItemFactory;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object get(String key) {
        DBItem item = this.dao.get(key);
        if (item != null) {
            if (item.isItemExpire() || item.getData() == null) {
                this.dao.delete(key);
                return null;
            }
            return item.getRealData();
        }
        return null;
    }

    @Override
    public Collection<Object> getMultis(Collection<String> keys) {
        if (keys == null || keys.isEmpty())
            return Collections.emptyList();
        Collection<DBItem> items = this.dao.get(keys);
        List<Object> objects = new ArrayList<Object>();
        for (DBItem item : items)
            objects.add(item.getRealData());
        return objects;
    }

    @Override
    public Map<String, Object> getMultiMap(Collection<String> keys) {
        if (keys == null || keys.isEmpty())
            return Collections.emptyMap();
        Collection<DBItem> items = this.dao.get(keys);
        Map<String, Object> data = new HashMap<String, Object>();
        for (DBItem item : items)
            data.put(item.getKey(), item.getRealData());
        return data;
    }

    @Override
    public CasItem<?> gets(String key) {
        DBItem item = this.dao.get(key);
        if (item == null || item.isItemExpire() || item.getData() == null) {
            this.dao.delete(key);
            return null;
        }
        Object object = item.getRealData();
        return object == null ? null : new SimpleCasItem<Object>(key, object, item.getVersion());
    }

    @Override
    public boolean add(String key, Object value, long millisecond) {
        try {
            Object object = this.get(key);
            if (object != null)
                return false;
            return this.dao.add(this.dbItemFactory.create(key, value, 0L, millisecond)) > 0;
        } catch (DataIntegrityViolationException e) {
            logger.error("add", e);
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <C extends CacheItem<?>> List<C> addMultis(Collection<C> cacheItems) {
        List<AlterDBItem<Object>> items = ClientHelper.cacheItem2Item(cacheItems);
        if (items.isEmpty())
            return Collections.emptyList();
        int[] results = this.dao.add(items);
        return ClientHelper.checkResult(items, results);
    }

    @Override
    public <T> List<CacheItem<T>> addMultis(Map<String, T> valueMap, long millisecond) {
        List<AlterDBItem<T>> items = ClientHelper.map2Item(valueMap, millisecond);
        if (items.isEmpty())
            return Collections.emptyList();
        int[] results = this.dao.add(items);
        return ClientHelper.checkResult(items, results);
    }

    @Override
    public boolean set(String key, Object value, long millisecond) {
        DBItem item = this.dbItemFactory.create(key, value, 0L, millisecond);
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
        List<AlterDBItem<Object>> items = ClientHelper.cacheItem2Item(cacheItems);
        if (items.isEmpty())
            return Collections.emptyList();
        int[] results = this.dao.set(items);
        return ClientHelper.checkResult(items, results);
    }

    @Override
    public <T> List<CacheItem<T>> setMultis(Map<String, T> valueMap, long millisecond) {
        List<AlterDBItem<T>> items = ClientHelper.map2Item(valueMap, millisecond);
        if (items.isEmpty())
            return Collections.emptyList();
        int[] results = this.dao.set(items);
        return ClientHelper.checkResult(items, results);
    }

    @Override
    public boolean update(String key, Object value, long millisecond) {
        return this.dao.update(this.dbItemFactory.create(key, value, 0L, millisecond)) > 0;
    }

    @Override
    public <C extends CacheItem<?>> List<C> updateMultis(Collection<C> cacheItems) {
        List<AlterDBItem<Object>> items = ClientHelper.cacheItem2Item(cacheItems);
        if (items.isEmpty())
            return Collections.emptyList();
        int[] results = this.dao.update(items);
        return ClientHelper.checkResult(items, results);
    }

    @Override
    public <T> List<CacheItem<T>> updateMultis(Map<String, T> valueMap, long millisecond) {
        List<AlterDBItem<T>> items = ClientHelper.map2Item(valueMap, millisecond);
        if (items.isEmpty())
            return Collections.emptyList();
        int[] results = this.dao.update(items);
        return ClientHelper.checkResult(items, results);
    }

    @Override
    public List<String> deleteMultis(Collection<String> keys) {
        int[] results = this.dao.delete(keys);
        List<String> fails = null;
        int index = 0;
        for (String key : keys) {
            if (results[index] <= 0 && results[index] != java.sql.Statement.SUCCESS_NO_INFO) {
                fails = CacheHelper.getAndCreate(fails);
                fails.add(key);
            }
        }
        return CacheHelper.checkEmpty(fails);
    }

    @Override
    public boolean delete(String key) {
        return this.dao.delete(key) > 0;
    }

    @Override
    public boolean cas(CasItem<?> item, long millisecond) {
        return this.dao.cas(this.dbItemFactory.create(item.getKey(), item.getData(), item.getVersion(), millisecond)) > 0;
    }

    @Override
    public void shutdown() {
    }


}
