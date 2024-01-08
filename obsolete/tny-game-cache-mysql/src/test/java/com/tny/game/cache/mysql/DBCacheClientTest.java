package com.tny.game.cache.mysql;

import com.tny.game.cache.*;
import com.tny.game.cache.mysql.dao.*;
import com.tny.game.cache.testclass.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class DBCacheClientTest {

    @Autowired
    @Qualifier("dbclient")
    private CacheClient cacheClient;

    @Autowired
    private CacheDAO cacheDAO;

    private static ClientTestTask task;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
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
