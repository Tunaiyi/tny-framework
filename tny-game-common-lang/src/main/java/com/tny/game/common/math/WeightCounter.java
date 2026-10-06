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
        if (allWeight <= 0) {
            // 原实现 perNum=allPro/0=Infinity → (int)(0*Infinity) 全挤 key=0 互相覆盖
            throw new IllegalArgumentException("权重总和必须为正: " + allWeight);
        }
        WeightNum<V> lastOne = weightNums.get(weightNums.size() - 1);
        float perNum = (float) allPro / allWeight;
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
