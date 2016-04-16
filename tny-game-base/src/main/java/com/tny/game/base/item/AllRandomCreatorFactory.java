package com.tny.game.base.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AllRandomCreatorFactory implements RandomCreatorFactory {

    private static final String name = "all";

    private static final RandomCreator<Probability> ALL_CREATOR = new RandomCreator<Probability>() {

        @Override
        public List<Probability> random(int range, int number, Collection<? extends Probability> probabilityList, Map<String, Object> attributeMap) {
            return new ArrayList<>(probabilityList);
        }

    };

    @SuppressWarnings("unchecked")
    public <P extends Probability> RandomCreator<P> getRandomCreator() {
        return (RandomCreator<P>) ALL_CREATOR;
    }

    @SuppressWarnings("unchecked")
    public static <P extends Probability> RandomCreator<P> getInstance() {
        return (RandomCreator<P>) ALL_CREATOR;
    }

    @Override
    public String getName() {
        return name;
    }

}
