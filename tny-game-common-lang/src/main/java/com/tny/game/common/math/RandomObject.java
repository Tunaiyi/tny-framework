package com.tny.game.common.math;

/**
 * Created by Kun Yang on 2017/6/26.
 */
public class RandomObject<V> implements Comparable<RandomObject<V>> {

    private final V object;

    private final int value;

    public RandomObject(V object, int value) {
        this.object = object;
        this.value = value;
    }

    public V getObject() {
        return this.object;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int compareTo(RandomObject<V> o) {
        return (this.value - o.getValue()) * -1;
    }

    @Override
    public String toString() {
        return this.object + ":" + this.value;
    }

}