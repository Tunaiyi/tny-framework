package cndw.framework.cache;

import com.tny.game.cache.redis.RedisClient;
import com.tny.game.cache.testclass.ClientTestTask;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class RedisCacheClientTest {

    @Autowired
    @Qualifier("client")
    private RedisClient client;

    private static ClientTestTask task;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        task = new ClientTestTask(this.client) {
            @Override
            protected void doFlushAll() {
                RedisCacheClientTest.this.client.flushAll();
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
//		task.testGets();
    }

    @Test
    public void testCas() {
//		task.testCas();
    }
}
