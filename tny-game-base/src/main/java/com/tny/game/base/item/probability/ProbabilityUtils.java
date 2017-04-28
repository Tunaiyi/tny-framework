package com.tny.game.base.item.probability;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.utils.collection.ThreadLocalRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

public interface ProbabilityUtils {

    static <G extends ProbabilityGroup<P>, P extends Probability> List<P> all(G group, Map<String, Object> attributes) {
        return group.probabilities();
    }

    static <G extends ProbabilityGroup<P>, P extends Probability> List<P> sequence(G group, Map<String, Object> attributes) {
        return group.probabilities().stream()
                .filter(p -> p.isEffect(attributes))
                .limit(group.getNumber(attributes))
                .collect(Collectors.toList());
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
        if (probabilities.isEmpty())
            return ImmutableList.of();
        int total = 0;
        List<ProbabilityEntry<P>> entries = new ArrayList<>();
        for (P p : probabilities) {
            if (p.isEffect(attributes))
                continue;
            int probability = p.getProbability(attributes);
            if (probability > 0) {
                total += probability;
                entries.add(new ProbabilityEntry<>(p, probability));
            }
        }
        if (total == 0)
            return ImmutableList.of();
        int number = group.getNumber(attributes);
        List<P> values = new ArrayList<>();
        if (number <= 0)
            return ImmutableList.of();
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
            if (gain != null)
                entries.remove(gain);
        }
        return values;
    }


    static <G extends ProbabilityGroup<P>, P extends Probability> List<P> drawWeights(G group, Map<String, Object> attributes) {
        List<P> probabilities = group.probabilities();
        if (probabilities.isEmpty())
            return ImmutableList.of();
        TreeMap<Integer, P> proMap = new TreeMap<>();
        int total = 0;
        for (P p : probabilities) {
            if (p.isEffect(attributes))
                continue;
            int probability = p.getProbability(attributes);
            if (probability > 0) {
                total += probability;
                proMap.put(total, p);
            }
        }
        if (proMap.isEmpty())
            return ImmutableList.of();
        int number = group.getNumber(attributes);
        if (number <= 0)
            return ImmutableList.of();
        return draw(number, total, proMap);
    }

    static <G extends ProbabilityGroup<P>, P extends Probability> List<P> drawProbabilities(G group, Map<String, Object> attributes) {
        int range = group.getRange(attributes);
        List<P> probabilities = group.probabilities();
        if (range == 0)
            range = 10000;
        TreeMap<Integer, P> proMap = new TreeMap<>();
        for (P p : probabilities) {
            if (!p.isEffect(attributes))
                continue;
            int probability = p.getProbability(attributes);
            if (probability > 0)
                proMap.put(probability, p);
        }
        if (proMap.isEmpty())
            return ImmutableList.of();
        int number = group.getNumber(attributes);
        if (number <= 0)
            return ImmutableList.of();
        return draw(number, range, proMap);
    }

    static <O> List<O> draw(int number, int range, TreeMap<Integer, O> proMap) {
        if (proMap.isEmpty() || number <= 0)
            return ImmutableList.of();
        Random random = ThreadLocalRandom.current();
        List<O> values = new ArrayList<>();
        while (values.size() < number) {
            int rand = random.nextInt(range);
            Entry<Integer, O> entry = proMap.higherEntry(rand);
            if (entry == null)
                continue;
            values.add(entry.getValue());
        }
        return values;
    }


}
