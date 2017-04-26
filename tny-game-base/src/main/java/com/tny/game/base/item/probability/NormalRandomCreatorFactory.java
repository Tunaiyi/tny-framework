package com.tny.game.base.item.probability;

import org.springframework.stereotype.Component;

@Component
public class NormalRandomCreatorFactory<G extends ProbabilityGroup<P>, P extends Probability> implements RandomCreatorFactory<G, P> {

    private static final String NAME = "normal";

    private static final RandomCreator<ProbabilityGroup<Probability>, Probability> CREATOR =
            ProbabilityUtils::drawProbabilities;

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
