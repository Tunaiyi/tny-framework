package com.tny.game.base.item.probability;

import com.tny.game.common.collection.CopyOnWriteMap;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Kun Yang on 2017/5/8.
 */
public class RandomCreators {

    private final static Map<String, RandomCreatorFactory> FACTORIES = new CopyOnWriteMap<>();

    static void register(RandomCreatorFactory factory) {
        FACTORIES.putIfAbsent(factory.getName(), factory);
    }

    @SuppressWarnings("unchecked")
    public static <G extends ProbabilityGroup<P>, P extends Probability> RandomCreator<G, P> createRandomCreator(String name) {
        RandomCreatorFactory factory = FACTORIES.get(name);
        if (factory == null)
            return null;
        return (RandomCreator<G, P>) factory.getRandomCreator();
    }

    public static Collection<RandomCreatorFactory> getFactories() {
        return FACTORIES.values();
    }

}
