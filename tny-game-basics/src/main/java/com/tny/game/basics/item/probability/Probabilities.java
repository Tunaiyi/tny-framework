/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.probability;

import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public interface Probabilities {

    static <G extends ProbabilityGroup<P>, P extends Probability> List<P> all(G group, Map<String, Object> attributes) {
        return group.probabilities();
    }

    static <G extends ProbabilityGroup<P>, P extends Probability> List<P> sequence(G group, Map<String, Object> attributes) {
        int number = group.getNumber(attributes);
        int size = group.probabilities().size();
        List<P> values = new ArrayList<>();
        while (number > 0) {
            if (number < size) {
                number = 0;
                group.probabilities().stream()
                        .filter(p -> p.isEffect(attributes))
                        .limit(group.getNumber(attributes))
                        .collect(Collectors.toCollection(() -> values));
            } else {
                number -= size;
                values.addAll(group.probabilities());
            }
        }
        return values;
    }

    class ProbabilityEntry<P extends Probability> {

        private int probability;

        private P value;

        private ProbabilityEntry(P value, int probability) {
            this.probability = probability;
            this.value = value;
        }

        public int getProbability() {
            return probability;
        }

        public P getValue() {
            return value;
        }

    }

    static <G extends ProbabilityGroup<P>, P extends Probability> List<P> drawWeightsNoRepeat(G group, Map<String, Object> attributes) {
        List<P> probabilities = group.probabilities();
        if (probabilities.isEmpty()) {
            return ImmutableList.of();
        }
        int number = group.getNumber(attributes);
        if (number <= 0) {
            return ImmutableList.of();
        }
        int total = 0;
        List<P> values = new ArrayList<>();
        List<ProbabilityEntry<P>> entries = new ArrayList<>();
        for (P p : probabilities) {
            if (!p.isEffect(attributes)) {
                continue;
            }
            int probability = p.getProbability(attributes);
            if (probability > 0) {
                total += probability;
                entries.add(new ProbabilityEntry<>(p, probability));
            } else if (probability < 0) {
                values.add(p);
                if (values.size() >= number) {
                    return values;
                }
            }
        }
        if (total == 0) {
            return values;
        }
        Random random = ThreadLocalRandom.current();
        while (values.size() != number && !entries.isEmpty()) {
            int index = 0;
            ProbabilityEntry<P> gain = null;
            int rand = random.nextInt(total);
            for (ProbabilityEntry<P> entry : entries) {
                if (index <= rand && rand < index + entry.getProbability()) {
                    gain = entry;
                    values.add(gain.getValue());
                }
                index += entry.getProbability();
            }
            if (gain != null) {
                entries.remove(gain);
            }
        }
        return values;
    }

    static <G extends ProbabilityGroup<P>, P extends Probability> List<P> drawWeights(G group, Map<String, Object> attributes) {
        List<P> probabilities = group.probabilities();
        if (probabilities.isEmpty()) {
            return ImmutableList.of();
        }
        TreeMap<Integer, P> proMap = new TreeMap<>();
        int number = group.getNumber(attributes);
        if (number <= 0) {
            return ImmutableList.of();
        }
        int total = 0;
        List<P> values = new ArrayList<>();
        for (P p : probabilities) {
            if (!p.isEffect(attributes)) {
                continue;
            }
            int probability = p.getProbability(attributes);
            if (probability > 0) {
                total += probability;
                proMap.put(total, p);
            } else if (probability < 0) {
                values.add(p);
                if (values.size() >= number) {
                    return values;
                }
            }
        }
        if (proMap.isEmpty()) {
            return values;
        }
        return draw(number, total, proMap, values);
    }

    static <G extends ProbabilityGroup<P>, P extends Probability> List<P> drawProbabilities(G group, Map<String, Object> attributes) {
        int range = group.getRange(attributes);
        List<P> probabilities = group.probabilities();
        if (range == 0) {
            range = 10000;
        }
        int number = group.getNumber(attributes);
        if (number <= 0) {
            return ImmutableList.of();
        }
        TreeMap<Integer, P> proMap = new TreeMap<>();
        List<P> values = new ArrayList<>();
        for (P p : probabilities) {
            if (!p.isEffect(attributes)) {
                continue;
            }
            int probability = p.getProbability(attributes);
            if (probability > 0) {
                proMap.put(probability, p);
            } else if (probability < 0) {
                values.add(p);
                if (values.size() >= number) {
                    return values;
                }
            }
        }
        if (proMap.isEmpty()) {
            return values;
        }
        return draw(number, range, proMap, values);
    }

    static <O> List<O> draw(int number, int range, TreeMap<Integer, O> proMap, List<O> values) {
        if (proMap.isEmpty() || number <= 0) {
            return ImmutableList.of();
        }
        Random random = ThreadLocalRandom.current();
        while (values.size() < number) {
            int rand = random.nextInt(range);
            Entry<Integer, O> entry = proMap.higherEntry(rand);
            if (entry == null) {
                continue;
            }
            values.add(entry.getValue());
        }
        return values;
    }

    public static void main(String[] args) {
        List<Integer> values = new ArrayList<>();
        Arrays.asList(1, 2, 3, 4, 5, 6)
                .stream()
                .collect(Collectors.toCollection(() -> values));
        System.out.println(values);
    }

}
