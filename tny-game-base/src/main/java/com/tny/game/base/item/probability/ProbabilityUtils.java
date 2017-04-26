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
