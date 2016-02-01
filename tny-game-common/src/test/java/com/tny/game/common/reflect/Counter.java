package com.tny.game.common.reflect;

import com.tny.game.event.annotation.Listener;

@Listener
public class Counter {

    private double number;

    private double num1;

    private double num2;

    public Counter(double number, double num1, double num2) {
        super();
        this.number = number;
        this.num1 = num1;
        this.num2 = num2;
    }

    public double count(int time, long size) {
        return (number - num1 + num2) * time / size;
    }

}
