package com.tny.game.base.item.probability;

public class AllRandomCreatorFactory<G extends ProbabilityGroup<P>, P extends Probability> implements RandomCreatorFactory<G, P> {

    private static final String name = "all";

    private static final RandomCreator<ProbabilityGroup<Probability>, Probability> CREATOR = Probabilities::all;

    private static AllRandomCreatorFactory factory = new AllRandomCreatorFactory<>();

    @SuppressWarnings("unchecked")
    public static <G extends ProbabilityGroup<P>, P extends Probability> RandomCreatorFactory<G, P> getInstance() {
        return factory;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RandomCreator<G, P> getRandomCreator() {
        return (RandomCreator<G, P>) CREATOR;
    }

}
