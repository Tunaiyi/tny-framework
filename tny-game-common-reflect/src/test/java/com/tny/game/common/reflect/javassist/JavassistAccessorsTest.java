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