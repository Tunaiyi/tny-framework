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

package com.tny.game.net;

import com.tny.game.common.context.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AttributesTest {

    private Attributes attributes = ContextAttributes.create();

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    private static AttrKey<Person> PERSION_KEY = AttrKeys.key("TEST");

    @Test
    public void testAttribute() {
        assertNull(this.attributes.getAttribute(PERSION_KEY));
        Person person = new Person("TEST PERSON", 155, "HOME");
        this.attributes.setAttribute(PERSION_KEY, person);
        assertEquals(this.attributes.getAttribute(PERSION_KEY), person);
        assertEquals(this.attributes.removeAttribute(PERSION_KEY), person);
        assertNull(this.attributes.getAttribute(PERSION_KEY));
    }

}
