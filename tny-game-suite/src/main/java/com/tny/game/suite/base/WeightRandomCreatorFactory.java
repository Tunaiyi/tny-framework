package com.tny.game.suite.base;

import com.tny.game.base.item.Probability;
import com.tny.game.base.item.RandomCreator;
import com.tny.game.base.item.RandomCreatorFactory;
import com.tny.game.base.item.behavior.AwardGroup;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({ITEM, GAME})
public class WeightRandomCreatorFactory implements RandomCreatorFactory {

    private static final String NAME = "weight";

    private static class WeightRandom<P extends Probability> implements RandomCreator<P> {

        @Override
        public List<P> random(int range, int number, Collection<? extends P> probabilityList, Map<String, Object> attributeMap) {
            List<P> awardList = new ArrayList<>();
            if (probabilityList.isEmpty())
                return awardList;
            TreeMap<Integer, P> proMap = new TreeMap<>();
            int total = 0;
            for (P p : probabilityList) {
                if (p instanceof AwardGroup && !((AwardGroup) p).isEffect(attributeMap))
                    continue;
                int probability = p.getProbability(attributeMap);
                if (probability > 0) {
                    total += probability;
                    proMap.put(total, p);
                }
            }
            if (proMap.isEmpty())
                return awardList;
            Random random = ThreadLocalRandom.current();
            while (awardList.size() < number) {
                int rand = random.nextInt(total);
                Entry<Integer, P> entry = proMap.higherEntry(rand);
                if (entry == null)
                    continue;
                awardList.add(entry.getValue());
            }
            return awardList;
        }
    }

    @Override
    public <P extends Probability> RandomCreator<P> getRandomCreator() {
        return new WeightRandom<>();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
