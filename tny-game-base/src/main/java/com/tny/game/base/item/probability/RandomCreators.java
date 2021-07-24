package com.tny.game.base.item.probability;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.utils.*;

import java.util.*;

/**
 * Created by Kun Yang on 2017/5/8.
 */
public class RandomCreators {

    private final static Map<Class<?>, RandomCreatorFactory<?, ?>> DEFAULT_FACTORIES = new CopyOnWriteMap<>();
    private final static Map<String, RandomCreatorFactory<?, ?>> FACTORIES = new CopyOnWriteMap<>();

    static {
        registerDefaultFactory(SequenceRandomCreatorFactory.getInstance());
        registerDefaultFactory(AllRandomCreatorFactory.getInstance());
        registerDefaultFactory(WeightRandomCreatorFactory.getInstance());
        registerDefaultFactory(WeightOnRepeatRandomCreatorFactory.getInstance());
        registerDefaultFactory(NormalRandomCreatorFactory.getInstance());
        DEFAULT_FACTORIES.forEach((c, f) -> register(f));
    }

    private static void registerDefaultFactory(RandomCreatorFactory<?, ?> factory) {
        DEFAULT_FACTORIES.putIfAbsent(factory.getClass(), factory);
    }

    static void register(RandomCreatorFactory<?, ?> factory) {
        RandomCreatorFactory<?, ?> old = FACTORIES.putIfAbsent(factory.getName(), factory);
        Asserts.checkArgument(old == null, "{} 与 {} name都为 {}", old, factory, factory.getName());
    }

    @SuppressWarnings("unchecked")
    public static <G extends ProbabilityGroup<P>, P extends Probability> RandomCreator<G, P> createRandomCreator(String name) {
        RandomCreatorFactory<?, ?> factory = FACTORIES.get(name);
        if (factory == null) {
            return null;
        }
        return (RandomCreator<G, P>)factory.getRandomCreator();
    }

    public static boolean isDefault(Class<?> clazz) {
        return DEFAULT_FACTORIES.containsKey(clazz);
    }

    public static Collection<RandomCreatorFactory<?, ?>> getFactories() {
        return FACTORIES.values();
    }

    public static RandomCreatorFactory<?, ?> getFactory(String name) {
        return FACTORIES.get(name);
    }

    public static Map<String, RandomCreatorFactory<?, ?>> getFactoriesMap() {
        return Collections.unmodifiableMap(FACTORIES);
    }

}
