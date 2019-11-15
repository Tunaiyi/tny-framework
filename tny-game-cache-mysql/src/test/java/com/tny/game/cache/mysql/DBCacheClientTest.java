package com.tny.game.cache.mysql;

import com.tny.game.cache.*;
import com.tny.game.cache.mysql.dao.*;
import com.tny.game.cache.testclass.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class DBCacheClientTest {

    @Resource
    @Qualifier("dbclient")
    private CacheClient cacheClient;

    @Resource
    private CacheDAO cacheDAO;

    private static ClientTestTask task;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        task = new ClientTestTask(this.cacheClient) {

            @Override
            protected void doFlushAll() {
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer0");
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer1");
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer2");
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer3");
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer4");
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer5");
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer6");
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer7");
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer8");
                DBCacheClientTest.this.cacheDAO.flushAll("CPlayer9");
            }

        };
        task.flushAll();
    }

    @Test
    public void testGet() {
        task.testGet();
    }

    @Test
    public void testGetMultis() {
        task.testGetMultis();
    }

    @Test
    public void testGetMultiMap() {
        task.testGAddMultiMap();
    }

    @Test
    public void testGAdd() {
        task.testGAdd();
    }

    @Test
    public void testGAddMultiMap() {
        task.testGAddMultiMap();
    }

    @Test
    public void testAddMulti() {
        task.testAddMulti();
    }

    @Test
    public void testGAddMultiArray() {
        task.testGAddMultiArray();
    }

    @Test
    public void testGSet() {
        task.testGSet();
    }

    @Test
    public void testGSetMultiMap() {
        task.testGSetMultiMap();
    }

    @Test
    public void testGSetMultiArray() {
        task.testGSetMultiArray();
    }

    @Test
    public void testGUpdate() {
        task.testGUpdate();
    }

    @Test
    public void testGUpdateMultiMap() {
        task.testGUpdateMultiMap();
    }

    @Test
    public void testGUpdateMultiArray() {
        task.testGUpdateMultiArray();
    }

    @Test
    public void testDelete() {
        task.testDelete();
    }

    @Test
    public void testGDelMultiArray() {
        task.testGDelMultiArray();
    }

    @Test
    public void testGets() {
        task.testGets();
    }

    @Test
    public void testCas() {
        task.testCas();
    }

}
