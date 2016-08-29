package com.tny.game.suite.base;

import com.tny.game.base.item.Probability;
import com.tny.game.base.item.RandomCreator;
import com.tny.game.base.item.RandomCreatorFactory;
import com.tny.game.base.item.behavior.AwardGroup;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({ITEM, GAME})
public class NormalRandomCreatorFactory implements RandomCreatorFactory {

    private static final String NAME = "NORMAL";

    private static final RandomCreator<Probability> NORMAL_CREATOR = (range, number, probabilitySet, attributeMap) -> {
        ArrayList<Probability> awardList = new ArrayList<>();
        if (range == 0)
            range = 10000;
        TreeMap<Integer, Probability> proMap = new TreeMap<>();
        for (Probability p : probabilitySet) {
            if (p instanceof AwardGroup && !((AwardGroup) p).isEffect(attributeMap))
                continue;
            int probability = p.getProbability(attributeMap);
            if (probability > 0)
                proMap.put(probability, p);
        }
        if (proMap.isEmpty())
            return awardList;
        Random random = java.util.concurrent.ThreadLocalRandom.current();
        while (awardList.size() < number) {
            int rand = random.nextInt(range);
            Entry<Integer, Probability> entry = proMap.higherEntry(rand);
            if (entry == null)
                continue;
            awardList.add(entry.getValue());
        }
        return awardList;
    };


    @Override
    @SuppressWarnings("unchecked")
    public <P extends Probability> RandomCreator<P> getRandomCreator() {
        return (RandomCreator<P>) NORMAL_CREATOR;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
