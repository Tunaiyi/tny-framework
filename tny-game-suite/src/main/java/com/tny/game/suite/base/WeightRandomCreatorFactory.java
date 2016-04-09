package com.tny.game.suite.base;

import com.tny.game.base.item.Probability;
import com.tny.game.base.item.RandomCreator;
import com.tny.game.base.item.RandomCreatorFactory;
import com.tny.game.base.item.behavior.AwardGroup;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Profile({"suite.auto", "suite.all"})
public class WeightRandomCreatorFactory implements RandomCreatorFactory {

    private static final String NAME = "weight";

    private static class WeightRandom<P extends Probability> implements RandomCreator<P> {

        @Override
        public List<P> random(int range, int number, Collection<? extends P> probabilityList, Map<String, Object> attributeMap) {
            List<P> awardList = new ArrayList<>();
            if (probabilityList.isEmpty()) {
                return awardList;
            }
            int total = 0;
            for (P p : probabilityList) {
                if (p.getProbability() > 0) {
                    total += p.getProbability();
                }
            }
            HashSet<P> ignoreSet = new HashSet<>();
            for (int index = 0; index < number; index++) {
                int priorityRange = 0;
                int rand = ThreadLocalRandom.current().nextInt(total);
                for (P awardGroup : probabilityList) {
                    if (awardGroup instanceof AwardGroup && !((AwardGroup) awardGroup).isEffect(attributeMap))
                        continue;
                    if (!ignoreSet.contains(awardGroup) && awardGroup.getProbability() < 0) {
                        awardList.add(awardGroup);
                        ignoreSet.add(awardGroup);
                        break;
                    }
                    priorityRange += awardGroup.getProbability();
                    if (priorityRange > rand) {
                        awardList.add(awardGroup);
                        break;
                    }
                }
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
