package com.tny.game.base.item.probability;

public class WeightRandomCreatorFactory<G extends ProbabilityGroup<P>, P extends Probability> implements RandomCreatorFactory<G, P> {

    private static final String NAME = "weight";

    private static final RandomCreator<ProbabilityGroup<Probability>, Probability> CREATOR =
            ProbabilityUtils::drawWeights;

    @Override
    @SuppressWarnings("unchecked")
    public RandomCreator<G, P> getRandomCreator() {
        return (RandomCreator<G, P>) CREATOR;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
