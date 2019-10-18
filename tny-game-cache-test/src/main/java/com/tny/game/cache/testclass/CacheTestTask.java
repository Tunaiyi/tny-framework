package com.tny.game.cache.testclass;

import com.tny.game.cache.Cache;
import com.tny.game.cache.CacheUtils;
import com.tny.game.cache.CasItem;
import org.junit.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CacheTestTask {

    private Cache cache;

    private static final long LAST_TIME = 1000L;

    private static final long SLEEP_TIME = 1900L;

    private static String PLAYER_KEY_HEAD = "player" + CacheUtils.SEPARATOR;
    private static long[] PLAYER_INDEX = {1, 2, 3, 4, 5};

    private static int ID = 100;
    private static TestPlayer player = new TestPlayer(ID);
    private static Map<String, TestPlayer> PLAYER_MAP = new HashMap<String, TestPlayer>();

    static {
        init();
    }

    public static void init() {
        ID = 100;
        player = new TestPlayer(ID);
        PLAYER_MAP = new HashMap<String, TestPlayer>();
        for (long pIndex : PLAYER_INDEX) {
            String playerKey = PLAYER_KEY_HEAD + pIndex;
            TestPlayer player = new TestPlayer(pIndex);
            PLAYER_MAP.put(playerKey, player);
        }
    }

    public CacheTestTask(Cache cache) {
        this.cache = cache;
    }

    public void flushAll() {
        init();
        this.doFlushAll();
    }

    protected abstract void doFlushAll();

    public void testSetAndGetObject() {
        this.cache.setObject(player);
        TestPlayer cachePlayer = this.cache.getObjectByKey(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId());
        Assert.assertEquals(player, cachePlayer);
        cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        Assert.assertEquals(player, cachePlayer);
    }

    public void testObjectAddGetUpdateSetDel() {
        TestPlayer player = new TestPlayer(101, "abc");
        this.cache.addObject(player);

        TestPlayer cachePlayer = this.cache.getObjectByKey(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId());
        this.compare(player, cachePlayer);

        String test = "testUpdate";
        this.resetPlayer(player, test);
        this.resetFullPlayer(cachePlayer, test);
        Assert.assertTrue(this.cache.updateObject(cachePlayer));
        cachePlayer = this.cache.getObjectByKey(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId());
        this.compareFull(player, cachePlayer);

        test = "testSet";
        this.resetPlayer(player, test);
        this.resetFullPlayer(cachePlayer, test);
        Assert.assertTrue(this.cache.setObject(cachePlayer));
        cachePlayer = this.cache.getObjectByKey(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId());
        this.compareFull(player, cachePlayer);

        test = "testDelect";
        this.resetPlayer(player, test);
        this.resetFullPlayer(cachePlayer, test);
        Assert.assertTrue(this.cache.deleteObject(cachePlayer));
        Assert.assertNull(this.cache.getObjectByKey(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId()));
        Assert.assertNull(this.cache.getObject(TestPlayer.class, player.getPlayerId()));
    }

    public void testGetObjectByClass() {
        this.cache.setObject(player);
        TestPlayer cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        this.compare(player, cachePlayer);
    }

    public void testGetObjectCollection() {
        this.cache.setObject(PLAYER_MAP.values());
        Collection<? extends TestPlayer> playerList = this.cache.getObjectsByKeys(TestPlayer.class, PLAYER_MAP.keySet());
        Assert.assertEquals(playerList.size(), PLAYER_MAP.size());
        for (TestPlayer player : PLAYER_MAP.values()) {
            playerList.contains(player);
        }
    }

    public void testSetObjectWithTime() {
        Assert.assertTrue(this.cache.setObject(player, LAST_TIME));
        TestPlayer cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        this.compare(player, cachePlayer);
        this.sleep();
        this.checkNull(player);
    }

    public void testSetObjectCollection() {
        List<TestPlayer> results = this.cache.setObject(PLAYER_MAP.values());
        Assert.assertEquals(true, results.isEmpty());
        for (String key : PLAYER_MAP.keySet()) {
            TestPlayer cachePlayer = this.cache.getObjectByKey(TestPlayer.class, key);
            this.compare(PLAYER_MAP.get(key), cachePlayer);
        }
    }

    public void testAddObject() {
        Assert.assertTrue(this.cache.addObject(player));
        TestPlayer cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        this.compare(player, cachePlayer);
        Assert.assertFalse(this.cache.addObject(player));
    }

    public void testAddObjectWithTime() {
        Assert.assertTrue(this.cache.addObject(player, LAST_TIME));
        TestPlayer cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        this.compare(player, cachePlayer);
        Assert.assertFalse(this.cache.addObject(player));
        this.sleep();
        this.checkNull(player);
    }

    public void testAddObjectCollection() {
        List<TestPlayer> results = this.cache.addObject(PLAYER_MAP.values());
        Assert.assertEquals(true, results.isEmpty());
        for (String key : PLAYER_MAP.keySet()) {
            TestPlayer cachePlayer = this.cache.getObjectByKey(TestPlayer.class, key);
            this.compare(PLAYER_MAP.get(key), cachePlayer);
        }
    }

    public void testUpdateObject() {
        TestPlayer player = new TestPlayer(101, "abc");
        Assert.assertFalse(this.cache.updateObject(player));
        Assert.assertTrue(this.cache.addObject(player));
        TestPlayer cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        this.compare(player, cachePlayer);
        player.setName("test");
        Assert.assertTrue(this.cache.updateObject(player));
        cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        this.compare(player, cachePlayer);
    }

    public void testUpdateObjectWithTime() {
        TestPlayer player = new TestPlayer(101, "abc");
        Assert.assertFalse(this.cache.updateObject(player, LAST_TIME));
        Assert.assertTrue(this.cache.addObject(player));
        TestPlayer cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        this.compare(player, cachePlayer);
        player.setName("test");
        Assert.assertTrue(this.cache.updateObject(player, LAST_TIME));
        this.sleep();
        this.checkNull(player);

    }

    public void testUpdateObjectCollection() {
        List<TestPlayer> results = this.cache.updateObject(PLAYER_MAP.values());
        Assert.assertEquals(PLAYER_MAP.size(), results.size());
        this.cache.setObject(PLAYER_MAP.values());
        for (String key : PLAYER_MAP.keySet()) {
            TestPlayer cachePlayer = this.cache.getObjectByKey(TestPlayer.class, key);
            this.compare(PLAYER_MAP.get(key), cachePlayer);
        }
        this.change(PLAYER_MAP.values());
        results = this.cache.updateObject(PLAYER_MAP.values());
        Assert.assertEquals(true, results.isEmpty());
        for (String key : PLAYER_MAP.keySet()) {
            TestPlayer cachePlayer = this.cache.getObjectByKey(TestPlayer.class, key);
            this.compare(PLAYER_MAP.get(key), cachePlayer);
        }
    }

    private void change(Collection<TestPlayer> players) {
        for (TestPlayer player : players) {
            player.setName("change_" + player.getPlayerId());
        }
    }

    public void testDeleteObject() {
        TestPlayer player = new TestPlayer(100);
        this.cache.setObject(player);
        this.cache.deleteObject(player);
        this.checkNull(player);
    }

    public void testDeleteObjectCollection() {
        List<TestPlayer> results = this.cache.deleteObject(PLAYER_MAP.values());
        Assert.assertEquals(PLAYER_MAP.size(), results.size());
        this.cache.setObject(PLAYER_MAP.values());

        for (String key : PLAYER_MAP.keySet()) {
            TestPlayer cachePlayer = this.cache.getObjectByKey(TestPlayer.class, key);
            this.compare(PLAYER_MAP.get(key), cachePlayer);
        }
        results = this.cache.deleteObject(PLAYER_MAP.values());
        Assert.assertEquals(true, results.isEmpty());
        this.checkNull(PLAYER_MAP.values());
    }

    public void testDeleteObjectArray() {
        this.cache.setObject(PLAYER_MAP.values());
        for (String key : PLAYER_MAP.keySet()) {
            TestPlayer cachePlayer = this.cache.getObjectByKey(TestPlayer.class, key);
            this.compare(PLAYER_MAP.get(key), cachePlayer);
        }
        this.cache.deleteObject(PLAYER_MAP.values().toArray(new TestPlayer[0]));
        this.checkNull(PLAYER_MAP.values());
    }

    public void testCas() {
        TestPlayer player = new TestPlayer(101, "abc");
        this.cache.setObject(player);
        CasItem<TestPlayer> item = this.cache.getsObject(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId());
        TestPlayer cachePlayer = item.getData();
        this.compare(player, cachePlayer);

        this.resetPlayer(player, "test");
        this.resetPlayer(cachePlayer, "test");

        Assert.assertTrue(this.cache.casObject(item));
        cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        this.compare(player, cachePlayer);

        CasItem<TestPlayer> item1 = this.cache.getsObject(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId());

        CasItem<TestPlayer> item2 = this.cache.getsObject(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId());

        this.resetPlayer(player, "test1");
        this.resetPlayer(item1.getData(), "test1");
        Assert.assertTrue(this.cache.casObject(item1));

        this.resetPlayer(item2.getData(), "test2");
        Assert.assertFalse(this.cache.casObject(item2));

        cachePlayer = this.cache.getObject(TestPlayer.class, player.getPlayerId());
        this.compare(player, cachePlayer);
    }

    public void testCasTime() {
        TestPlayer player = new TestPlayer(101, "abc");
        this.cache.setObject(player);
        CasItem<TestPlayer> item = this.cache.getsObject(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId());
        TestPlayer cachePlayer = item.getData();
        this.compare(player, cachePlayer);

        this.resetPlayer(player, "test");
        this.resetPlayer(cachePlayer, "test");

        Assert.assertTrue(this.cache.casObject(item, LAST_TIME));
        this.sleep();
        this.checkNull(player);
    }

    private void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resetPlayer(TestPlayer player, String head) {
        player.setName(head);
    }

    private void resetFullPlayer(TestPlayer player, String head) {
        player.setName(head);
    }

    private void compare(TestPlayer player, TestPlayer cachePlayer) {
        Assert.assertEquals(player, cachePlayer);
    }

    private void compareFull(TestPlayer player, TestPlayer cachePlayer) {
        Assert.assertEquals(player, cachePlayer);
    }

    private void checkNull(TestPlayer player) {
        Assert.assertNull(this.cache.getObjectByKey(TestPlayer.class, PLAYER_KEY_HEAD + player.getPlayerId()));
        Assert.assertNull(this.cache.getObject(TestPlayer.class, player.getPlayerId()));
    }

    private void checkNull(Collection<TestPlayer> playerMap) {
        for (TestPlayer entry : playerMap) {
            this.checkNull(entry);
        }
    }

}
