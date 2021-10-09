package com.tny.game.basics.item.probability;

public class WeightOnRepeatRandomCreatorFactory<G extends ProbabilityGroup<P>, P extends Probability> implements RandomCreatorFactory<G, P> {

    private static final String NAME = "weightNoRepeat";

    private static final RandomCreator<ProbabilityGroup<Probability>, Probability> CREATOR =
            Probabilities::drawWeightsNoRepeat;

    private static WeightOnRepeatRandomCreatorFactory factory = new WeightOnRepeatRandomCreatorFactory<>();

    @SuppressWarnings("unchecked")
    public static <G extends ProbabilityGroup<P>, P extends Probability> RandomCreatorFactory<G, P> getInstance() {
        return factory;
    }

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
