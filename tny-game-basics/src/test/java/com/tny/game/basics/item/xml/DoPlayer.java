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

package com.tny.game.basics.item.xml;

import java.lang.reflect.*;

public class DoPlayer implements Do {

    private String name;

    private DoPlayer(String name) {
        this.name = name;
    }

    public void say() {
        System.out.println(name + " saying");
    }

    @Override
    public void tryToDo() {
        System.out.println(name + " doing");
    }

    public static class PlayerHandler implements InvocationHandler {

        private DoPlayer player;

        private PlayerHandler(DoPlayer player) {
            super();
            this.player = player;
        }

        public void set(DoPlayer player) {
            this.player = player;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(player, args);
        }

    }

    public static void main(String[] args) {
        DoPlayer tom = new DoPlayer("Tom");
        DoPlayer kelly = new DoPlayer("Kelly");
        PlayerHandler handler = new PlayerHandler(tom);
        Do proxy = (Do) Proxy.newProxyInstance(DoPlayer.class.getClassLoader(), DoPlayer.class.getInterfaces(), handler);
        proxy.say();
        proxy.tryToDo();
        handler.set(kelly);
        proxy.say();
        proxy.tryToDo();
    }

}
