package cndw.framework.cache;

import com.tny.game.cache.DirectCache;
import com.tny.game.cache.redis.RedisClient;
import com.tny.game.cache.testclass.CacheTestTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class RedisCacheObjectTest {

    @Autowired()
    @Qualifier("cached")
    private DirectCache cache;

    @Autowired
    @Qualifier("client")
    private RedisClient client;

    private CacheTestTask task;

    @Before
    public void setUp() throws Exception {
        this.task = new CacheTestTask(this.cache) {
            @Override
            protected void doFlushAll() {
                RedisCacheObjectTest.this.client.flushAll();
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
//		this.task.testCas();
    }

    @Test
    public void testCasTime() {
//		this.task.testCasTime();
    }

}
