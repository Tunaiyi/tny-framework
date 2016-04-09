package com.tny.game.base.item;

import com.tny.game.base.item.behavior.AwardGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DefaultRandomCreatorFactory implements RandomCreatorFactory {

    private static final String name = "default";

    private static final RandomCreator<Probability> DEFAULT = new RandomCreator<Probability>() {

        @Override
        public List<Probability> random(int range, int number, Collection<? extends Probability> probabilityList, Map<String, Object> attributeMap) {
            ArrayList<Probability> proList = new ArrayList<Probability>(number);
            for (Probability p : probabilityList) {
                if (p instanceof AwardGroup && !((AwardGroup) p).isEffect(attributeMap))
                    continue;
                proList.add(p);
                if (proList.size() == number)
                    return proList;
            }
            return proList;
        }

    };

    @SuppressWarnings("unchecked")
    public static <P extends Probability> RandomCreator<P> getInstance() {
        return (RandomCreator<P>) DEFAULT;
    }

    @SuppressWarnings("unchecked")
    public <P extends Probability> RandomCreator<P> getRandomCreator() {
        return (RandomCreator<P>) DEFAULT;
    }

    @Override
    public String getName() {
        return name;
    }

}
