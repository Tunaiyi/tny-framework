package cndw.framework.cache;

import com.tny.game.asyndb.impl.*;
import com.tny.game.cache.async.*;
import com.tny.game.cache.testclass.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class ZAsyncCacheObjectTest {

    @Autowired
    private AsyncCache cache;

    @Autowired
    private AverageRateSyncDBExecutor syncDBExecutor;

    private CacheTestTask task;

    public void initPlayerEquip() {
        // cache.set(PLAYER_MAP);
        // cache.set(EQUIP_MAP);
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.task = new CacheTestTask(this.cache) {

            @Override
            protected void doFlushAll() {
                try {
                    ZAsyncCacheObjectTest.this.syncDBExecutor.shutdown();
                    ZAsyncCacheObjectTest.this.cache.flushAll();
                    ZAsyncCacheObjectTest.this.syncDBExecutor.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    //	@Test
    //	public void testCas() {
    //		this.task.testCas();
    //	}

    //	@Test
    //	public void testCasTime() {
    //		this.task.testCasTime();
    //	}

}
