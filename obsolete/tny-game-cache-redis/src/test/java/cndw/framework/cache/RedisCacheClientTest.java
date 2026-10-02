/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cndw.framework.cache;

import com.tny.game.cache.redis.*;
import com.tny.game.cache.testclass.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/application.xml"})
public class RedisCacheClientTest {

    @Autowired
    @Qualifier("client")
    private RedisCacheClient client;

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
