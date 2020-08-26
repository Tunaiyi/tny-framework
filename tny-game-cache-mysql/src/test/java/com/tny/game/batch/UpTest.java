package com.tny.game.batch;

import com.tny.game.common.runtime.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class UpTest {

    @Resource
    private UpObjectDAO dao;

    private static List<UpObject> objects = new ArrayList<>();
    private static List<Integer> ids = new ArrayList<>();

    private static final int size = 10;

    //	private static final int size = 10;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        for (int index = 0; index < size; index++) {
            ids.add(index);
            objects.add(new UpObject(index));
        }
        objects.add(new UpObject(0));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        this.dao.flushAll();
        //		RunningChecker.start("insert");
        this.dao.set(objects);
        //		RunningChecker.endPrint("insert");
        //		System.out.println(StringUtils.join(Arrays.asList(result)));
    }

    @Test
    public void test() {
    }

    @Test
    public void get() {
        RunningChecker.start("update");
        this.dao.get(ids);
        RunningChecker.endPrint("update");
        //		for (UpObject object : objects) {
        //			System.out.println(JSONUtils.toJson(object));
        //		}
    }

    @Test
    public void insertIgnore() {
        System.out.println(this.dao.insertIgnore(objects.get(0)));
    }

    @Test
    public void update() {
        for (UpObject o : objects)
            o.reset();
        objects.add(new UpObject(20000));
        //		RunningChecker.start("update");
        this.dao.update(objects);
        //		RunningChecker.endPrint("update");
    }
}
