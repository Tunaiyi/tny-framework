package com.tny.game.base.item.probability;

public interface RandomCreatorFactory<G extends ProbabilityGroup<P>, P extends Probability> {

    String getName();

    RandomCreator<G, P> getRandomCreator();

}
