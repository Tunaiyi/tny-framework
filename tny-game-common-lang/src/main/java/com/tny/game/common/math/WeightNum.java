package com.tny.game.common.math;

@Deprecated
public class WeightNum<V> {

    private V value;

    private Integer weight;

    protected WeightNum(V value, Integer weight) {
        this.value = value;
        this.weight = weight;
    }

    public V getValue() {
        return this.value;
    }

    public int getWeight() {
        return this.weight;
    }

    public int countProbability(float perNum) {
        return (int) (this.weight * perNum);
    }

}
