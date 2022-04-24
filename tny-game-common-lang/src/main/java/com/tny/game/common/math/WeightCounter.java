package com.tny.game.common.math;

import java.util.*;

@Deprecated
public class WeightCounter<V> {

    private Integer totalPro;

    private List<Weight<V>> weights = new ArrayList<>();

    public WeightCounter() {
    }

    public WeightCounter(int totalPro, List<Weight<V>> weights) {
        this.weights.addAll(weights);
        this.totalPro = totalPro;
    }

    public WeightCounter(List<Weight<V>> weights) {
        this.weights = weights;
    }

    public SortedMap<Integer, V> parseProMap(Object... params) {
        SortedMap<Integer, V> proMap = new TreeMap<>();
        if (this.weights.isEmpty()) {
            return proMap;
        }
        List<WeightNum<V>> weightNums = new ArrayList<>();
        int allWeight = 0;
        for (Weight<V> weight : this.weights) {
            WeightNum<V> num = weight.getWeight(params);
            allWeight += num.getWeight();
            weightNums.add(num);
        }
        int allPro = this.totalPro == null ? allWeight : this.totalPro;
        if (weightNums.isEmpty()) {
            return proMap;
        }
        WeightNum<V> lastOne = weightNums.get(weightNums.size() - 1);
        float perNum = (float)allPro / allWeight;
        int stepPro = 0;
        for (WeightNum<V> num : weightNums) {
            if (num == lastOne) {
                proMap.put(allPro, num.getValue());
            } else {
                stepPro += num.countProbability(perNum);
                proMap.put(stepPro, num.getValue());
            }
        }
        return proMap;
    }

    public static void main(String[] args) {
        List<Weight<String>> weights = new ArrayList<>();
        weights.add(new Weight<>("A", "3000"));
        weights.add(new Weight<>("B", "3000"));
        weights.add(new Weight<>("C", "3000"));
        WeightCounter<String> counter = new WeightCounter<>(10000, weights);
        System.out.println(counter.parseProMap());
    }

}
