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

import com.tny.game.cache.*;
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
public class RedisCacheObjectTest {

    @Autowired
    @Qualifier("cached")
    private DirectCache cache;

    @Autowired
    @Qualifier("client")
    private RedisCacheClient client;

    private CacheTestTask task;

    @BeforeEach
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
