/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
        Do proxy = (Do)Proxy.newProxyInstance(DoPlayer.class.getClassLoader(), DoPlayer.class.getInterfaces(), handler);
        proxy.say();
        proxy.tryToDo();
        handler.set(kelly);
        proxy.say();
        proxy.tryToDo();
    }

}
