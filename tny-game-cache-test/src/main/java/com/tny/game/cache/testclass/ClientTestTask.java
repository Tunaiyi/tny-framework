package com.tny.game.cache.testclass;

import com.tny.game.cache.*;
import com.tny.game.cache.simple.*;

import java.util.*;

import static org.junit.Assert.*;

public abstract class ClientTestTask {

    private CacheClient cacheClient;

    private static int size = 100;
    private static Person p1 = new Person(1, "2", 2, 1);
    private static Person p2 = new Person(1, "2", 2, 2);
    private static List<CacheItem<Person>> personItemList = new ArrayList<>();
    private static List<CacheItem<Person>> changePersonItemList = new ArrayList<>();
    private static Map<String, Person> personMap = new HashMap<>();
    private static Map<String, Person> changePersonMap = new HashMap<>();
    private static List<Person> personList = new ArrayList<>();
    private static List<Person> changePersonList = new ArrayList<>();
    private static List<String> keyList = new ArrayList<>();

    static {
        ClientTestTask.init();
    }

    public ClientTestTask(CacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    public void setCacheClient(CacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    protected static void init() {
        p1 = new Person(1, "2", 2, 1);
        p2 = new Person(1, "2", 2, 2);
        personItemList = new ArrayList<>();
        changePersonItemList = new ArrayList<>();
        personMap = new HashMap<>();
        changePersonMap = new HashMap<>();
        personList = new ArrayList<>();
        changePersonList = new ArrayList<>();
        keyList = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            keyList.add("CPlayer" + index);
            Person person = new Person(index, "CPlayer" + index, index, index);
            Person change = new Person(index, "CPlayer" + index, 200 + index, 200 + index);
            personList.add(person);
            personItemList.add(new SimpleCacheItem<>(person.getName(), person));
            personMap.put(person.getName(), person);
            changePersonList.add(change);
            changePersonMap.put(change.getName(), change);
            changePersonItemList.add(new SimpleCacheItem<>(change.getName(), change));
        }
    }

    public void flushAll() {
        init();
        this.doFlushAll();
    }

    protected abstract void doFlushAll();

    public void setUp() throws Exception {
        this.flushAll();
    }

    public void testGet() {
        System.out.println(this.cacheClient.set("Test", p1, 0L));
        assertEquals(this.cacheClient.get("Test"), p1);
        assertEquals(this.cacheClient.add("Test", p2, 0L), false);
        assertEquals(this.cacheClient.get("Test"), p1);
        assertEquals(this.cacheClient.add("Test2", p2, 0L), true);
        assertEquals(this.cacheClient.get("Test2"), p2);
    }

    public void testGetMultis() {
        this.cacheClient.setMultis(personItemList);
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        for (Person object : personList) {
            assertEquals(true, collection.contains(object));
        }
    }

    public void testGetMultiMap() {
        this.cacheClient.setMultis(personItemList);
        Map<String, Object> map = this.cacheClient.getMultiMap(keyList);
        for (Person object : personList) {
            assertEquals(object, map.get(object.getName()));
        }
    }

    public void testGAdd() {
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
        for (Person person : personList)
            assertEquals(true, this.cacheClient.add(person.getName(), person, 0L));
        collection = this.cacheClient.getMultis(keyList);
        for (Person object : personList) {
            assertEquals(collection.contains(object), true);
        }
    }

    public void testAddMulti() {
        this.cacheClient.addMultis(personMap, 0L);
        this.doFlushAll();
        this.cacheClient.addMultis(personMap, 0L);
    }

    public void testGAddMultiMap() {
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
        Collection<CacheItem<Person>> results = this.cacheClient.addMultis(personMap, 0L);
        assertEquals(true, results.isEmpty());

        collection = this.cacheClient.getMultis(keyList);
        for (Person object : personList) {
            assertEquals(collection.contains(object), true);
        }
    }

    public void testGAddMultiArray() {
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
        Collection<CacheItem<Person>> results = this.cacheClient.addMultis(personItemList);
        assertEquals(true, results.isEmpty());
        collection = this.cacheClient.getMultis(keyList);
        for (Person object : personList) {
            assertEquals(collection.contains(object), true);
        }
    }

    public void testGSet() {
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
        for (Person person : personList)
            assertEquals(true, this.cacheClient.set(person.getName(), person, 0L));
        collection = this.cacheClient.getMultis(keyList);
        for (Person object : personList) {
            assertEquals(collection.contains(object), true);
        }
        for (Person person : changePersonList)
            assertEquals(true, this.cacheClient.set(person.getName(), person, 0L));
        collection = this.cacheClient.getMultis(keyList);
        for (Person object : changePersonList) {
            assertEquals(collection.contains(object), true);
        }
    }

    public void testGSetMultiMap() {
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
        Collection<CacheItem<Person>> results = this.cacheClient.setMultis(personMap, 0L);
        assertEquals(true, results.isEmpty());
        collection = this.cacheClient.getMultis(keyList);
        for (Person object : personList) {
            assertEquals(collection.contains(object), true);
        }
        results = this.cacheClient.setMultis(changePersonMap, 0L);
        assertEquals(true, results.isEmpty());
        collection = this.cacheClient.getMultis(keyList);
        for (Person object : changePersonList) {
            assertEquals(collection.contains(object), true);
        }
    }

    public void testGSetMultiArray() {
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
        Collection<CacheItem<Person>> results = this.cacheClient.setMultis(personItemList);
        assertEquals(true, results.isEmpty());
        collection = this.cacheClient.getMultis(keyList);
        for (Person object : personList) {
            assertEquals(collection.contains(object), true);
        }
        results = this.cacheClient.setMultis(changePersonItemList);
        assertEquals(true, results.isEmpty());
        collection = this.cacheClient.getMultis(keyList);
        for (Person object : changePersonList) {
            assertEquals(collection.contains(object), true);
        }
    }

    public void testGUpdate() {
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
        for (Person person : personList)
            assertEquals(false, this.cacheClient.update(person.getName(), person, 0L));
        collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());

        this.cacheClient.setMultis(personMap, 0L);

        for (Person person : changePersonList)
            assertEquals(true, this.cacheClient.update(person.getName(), person, 0L));

        collection = this.cacheClient.getMultis(keyList);
        for (Person object : changePersonList) {
            assertEquals(collection.contains(object), true);
        }
    }

    public void testGUpdateMultiMap() {
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
        Collection<CacheItem<Person>> results = this.cacheClient.updateMultis(personMap, 0L);
        assertEquals(personMap.size(), results.size());

        collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());

        results = this.cacheClient.setMultis(personMap, 0L);

        results = this.cacheClient.updateMultis(changePersonMap, 0L);
        assertEquals(true, results.isEmpty());

        collection = this.cacheClient.getMultis(keyList);
        for (Person object : changePersonList) {
            assertEquals(collection.contains(object), true);
        }
    }

    public void testGUpdateMultiArray() {

        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
        Collection<CacheItem<Person>> results = this.cacheClient.updateMultis(personItemList);
        assertEquals(personMap.size(), results.size());

        collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());

        results = this.cacheClient.setMultis(personMap, 0L);

        results = this.cacheClient.updateMultis(changePersonItemList);
        assertEquals(true, results.isEmpty());

        collection = this.cacheClient.getMultis(keyList);
        for (Person object : changePersonList) {
            assertEquals(collection.contains(object), true);
        }
    }

    public void testDelete() {
        assertEquals(false, this.cacheClient.delete("Test"));
        this.cacheClient.set("Test", p1, 0L);
        assertEquals(this.cacheClient.get("Test"), p1);
        assertEquals(true, this.cacheClient.delete("Test"));
        assertNull(this.cacheClient.get("Test"));
    }

    public void testGDelMultiArray() {
        Collection<Object> collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());

        Collection<String> result = this.cacheClient.deleteMultis(keyList);
        assertEquals(keyList.size(), result.size());

        System.out.println(result);

        this.cacheClient.addMultis(personItemList);
        this.cacheClient.deleteMultis(keyList);
        collection = this.cacheClient.getMultis(keyList);
        assertEquals(true, collection.isEmpty());
    }

    public void testGets() {
        this.cacheClient.set("Test", p1, 0L);
        CasItem<?> item = this.cacheClient.gets("Test");
        assertEquals(item.getData(), p1);
    }

    @SuppressWarnings("unchecked")
    public void testCas() {
        this.cacheClient.set("Test", p1, 0L);
        CasItem<Person> item = (CasItem<Person>) this.cacheClient.gets("Test");
        long currentVersion = item.getVersion();
        assertEquals(item.getData(), p1);
        item = new SimpleCasItem<>(item, p2);
        assertEquals(this.cacheClient.get("Test"), p1);
        assertEquals(this.cacheClient.cas(item, 0L), true);
        item = (CasItem<Person>) this.cacheClient.gets("Test");
        assertEquals(item.getData(), p2);
        assertEquals(item.getVersion(), currentVersion + 1);

    }

}
