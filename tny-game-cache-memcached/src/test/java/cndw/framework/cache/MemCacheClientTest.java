package cndw.framework.cache;

import com.tny.game.cache.memcached.*;
import com.tny.game.cache.testclass.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class MemCacheClientTest {

    @Autowired
    @Qualifier("client")
    private MemcachedClient client;

    private static ClientTestTask task;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
        task = new ClientTestTask(this.client) {

            @Override
            protected void doFlushAll() {
                MemCacheClientTest.this.client.flushAll();
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
        task.testGets();
    }

    @Test
    public void testCas() {
        task.testCas();
    }

}
