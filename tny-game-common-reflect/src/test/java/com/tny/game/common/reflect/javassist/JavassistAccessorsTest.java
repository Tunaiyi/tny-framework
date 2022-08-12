/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.reflect.javassist;

import com.tny.game.common.reflect.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 */
public class JavassistAccessorsTest {

    static final int LEVEL = 1;

    static final String NAME = "name";

    static final int AGE = 23;

    @Test
    public void testProperty() throws InvocationTargetException {
        ClassAccessor accessor = JavassistAccessors.getGClass(Player.class);
        Player player = new Player();
        assertEquals(accessor.getProperty("level").getPropertyValue(player), LEVEL);
        assertEquals(accessor.getProperty("name").getPropertyValue(player), NAME);
        assertEquals(accessor.getProperty("age").getPropertyValue(player), AGE);
    }

    public static class Player {

        private int level = LEVEL;

        private String name = NAME;

        private int age = AGE;

        public int getLevel() {
            return this.level;
        }

        public Player setLevel(int level) {
            this.level = level;
            return this;
        }

        protected String getName() {
            return this.name;
        }

        protected Player setName(String name) {
            this.name = name;
            return this;
        }

        int getAge() {
            return this.age;
        }

        Player setAge(int age) {
            this.age = age;
            return this;
        }

    }

}