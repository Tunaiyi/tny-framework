package com.tny.game.basics.item.probability;

public class WeightRandomCreatorFactory<G extends ProbabilityGroup<P>, P extends Probability> implements RandomCreatorFactory<G, P> {

    private static final String NAME = "weight";

    private static final RandomCreator<ProbabilityGroup<Probability>, Probability> CREATOR =
            Probabilities::drawWeights;

    private static WeightRandomCreatorFactory factory = new WeightRandomCreatorFactory<>();

    @SuppressWarnings("unchecked")
    public static <G extends ProbabilityGroup<P>, P extends Probability> RandomCreatorFactory<G, P> getInstance() {
        return factory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RandomCreator<G, P> getRandomCreator() {
        return (RandomCreator<G, P>)CREATOR;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
