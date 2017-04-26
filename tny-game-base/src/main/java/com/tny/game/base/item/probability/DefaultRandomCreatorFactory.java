package com.tny.game.base.item.probability;

public class DefaultRandomCreatorFactory<G extends ProbabilityGroup<P>, P extends Probability> implements RandomCreatorFactory<G, P> {

    private static final String name = "default";

    private static final RandomCreator<ProbabilityGroup<Probability>, Probability> CREATOR =
            ProbabilityUtils::sequence;

    @SuppressWarnings("unchecked")
    public static <G extends ProbabilityGroup<P>, P extends Probability> RandomCreator<G, P> getInstance() {
        return (RandomCreator<G, P>) CREATOR;
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
