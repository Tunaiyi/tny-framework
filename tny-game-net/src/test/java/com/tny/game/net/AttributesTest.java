/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
