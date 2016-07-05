package com.tny.game.common.aop;

import com.tny.game.common.reflect.aop.annotation.AOP;

public class Player extends Person {

    public Player() {

    }

    public void callProtected() {
        this.protectedMethod();
    }

    @AOP
    protected void protectedMethod() {
        System.out.println("protectedMethod");
    }

    @AOP
    public void tryException() throws Exception {
        throw new NullPointerException();
    }

    @AOP
    public void callName() {
        System.out.println("void callName()");
    }

    @AOP
    public int getName() {
        System.out.println("int getName()");
        return 10;
    }

    int testPackage() {
        System.out.println("int testPackage()");
        return 10;
    }

    protected int testProtected(int value) {
        System.out.println("int testProtected()" + value);
        return 10;
    }

    @AOP
    public Player friend(int data, Player player, long value) {
        System.out.println("Player friend(int data, Player player) ");
        return null;
    }

}
