package com.tny.game.common.reflect.javassist;

import com.tny.game.common.reflect.*;
import org.junit.*;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;


/**
 * <p>
 */
public class JavassistAccessorsTest {

    private static final int LEVEL = 1;
    private static final String NAME = "name";
    private static final int AGE = 23;

    @Test
    public void testProperty() throws InvocationTargetException {
        ClassAccessor accessor = JavassistAccessors.getGClass(Player.class);
        assertEquals(accessor.getProperty("age").getPropertyValue(accessor), LEVEL);
        assertEquals(accessor.getProperty("name").getPropertyValue(accessor), NAME);
        assertEquals(accessor.getProperty("age").getPropertyValue(accessor), AGE);
    }

    public static class Player {

        private int level = LEVEL;
        private String name = NAME;
        private int age = AGE;

        public int getLevel() {
            return level;
        }

        public Player setLevel(int level) {
            this.level = level;
            return this;
        }

        protected String getName() {
            return name;
        }

        protected Player setName(String name) {
            this.name = name;
            return this;
        }

        int getAge() {
            return age;
        }

        Player setAge(int age) {
            this.age = age;
            return this;
        }

    }
}