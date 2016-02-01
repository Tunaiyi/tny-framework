package com.tny.game.base.item;

public class Test<A> {

    private A a;

    public Test(A a) {
        this.a = a;
    }

    public A getA() {
        return this.a;
    }

    public void say(A object) {
        System.out.println(a.getClass() + " " + object);
    }

}
