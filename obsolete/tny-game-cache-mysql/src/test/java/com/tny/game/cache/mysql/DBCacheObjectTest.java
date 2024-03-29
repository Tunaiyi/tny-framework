package com.tny.game.cache.mysql;

import com.tny.game.cache.*;
import com.tny.game.cache.mysql.dao.*;
import com.tny.game.cache.testclass.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class DBCacheObjectTest {

    @Autowired
    private DirectCache cache;

    @Autowired
    private CacheDAO cacheDAO;

    private CacheTestTask task;

    @BeforeEach
    public void setUp() throws Exception {
        this.task = new CacheTestTask(this.cache) {

            @Override
            protected void doFlushAll() {
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer0");
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer1");
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer2");
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer3");
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer4");
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer5");
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer6");
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer7");
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer8");
                DBCacheObjectTest.this.cacheDAO.flushAll("CPlayer9");
            }
        };
        this.task.flushAll();
    }

    @Test
    public void testSetAndGetObject() {
        this.task.testSetAndGetObject();
    }

    @Test
    public void testObjectAddGetUpdateSetDel() {
        this.task.testObjectAddGetUpdateSetDel();
    }

    @Test
    public void testGetObjectByClass() {
        this.task.testGetObjectByClass();
    }

    @Test
    public void testGetObjectCollection() {
        this.task.testGetObjectCollection();
    }

    @Test
    public void testSetObjectWithTime() {
        this.task.testSetObjectWithTime();
    }

    @Test
    public void testSetObjectCollection() {
        this.task.testSetObjectCollection();
    }

    @Test
    public void testAddObject() {
        this.task.testAddObject();
    }

    @Test
    public void testAddObjectWithTime() {
        this.task.testAddObjectWithTime();
    }

    @Test
    public void testAddObjectCollection() {
        this.task.testAddObjectCollection();
    }

    @Test
    public void testUpdateObject() {
        this.task.testUpdateObject();
    }

    @Test
    public void testUpdateObjectWithTime() {
        this.task.testUpdateObjectWithTime();
    }

    @Test
    public void testUpdateObjectCollection() {
        this.task.testUpdateObjectCollection();
    }

    @Test
    public void testDeleteObject() {
        this.task.testDeleteObject();
    }

    @Test
    public void testDeleteObjectCollection() {
        this.task.testDeleteObjectCollection();
    }

    @Test
    public void testDeleteObjectArray() {
        this.task.testDeleteObjectArray();
    }

    @Test
    public void testCas() {
        this.task.testCas();
    }

    @Test
    public void testCasTime() {
        this.task.testCasTime();
    }

}
