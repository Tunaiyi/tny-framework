package com.tny.game.net;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import org.junit.*;
import test.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AttributesTest {

    private Attributes attributes = ContextAttributes.create();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private static AttrKey<Person> PERSION_KEY = AttrKeys.key("TEST");

    @Test
    public void testAttribute() {
        assertNull(attributes.getAttribute(PERSION_KEY));
        Person person = new Person("TEST PERSON", 155, "HOME");
        attributes.setAttribute(PERSION_KEY, person);
        assertEquals(attributes.getAttribute(PERSION_KEY), person);
        assertEquals(attributes.removeAttribute(PERSION_KEY), person);
        assertNull(attributes.getAttribute(PERSION_KEY));
    }

}
