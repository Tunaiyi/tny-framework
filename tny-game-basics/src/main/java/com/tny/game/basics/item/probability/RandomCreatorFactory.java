package com.tny.game.basics.item.probability;

public interface RandomCreatorFactory<G extends ProbabilityGroup<P>, P extends Probability> {

    String getName();

    RandomCreator<G, P> getRandomCreator();

    default void registerSelf() {
        RandomCreators.register(this);
    }

}
