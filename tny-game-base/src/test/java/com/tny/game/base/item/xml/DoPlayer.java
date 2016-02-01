package com.tny.game.base.item.xml;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
