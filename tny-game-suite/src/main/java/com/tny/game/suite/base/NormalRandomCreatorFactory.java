package com.tny.game.suite.base;

import com.tny.game.base.item.Probability;
import com.tny.game.base.item.RandomCreator;
import com.tny.game.base.item.RandomCreatorFactory;
import com.tny.game.base.item.behavior.AwardGroup;
import com.tny.game.common.utils.collection.ThreadLocalRandom;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Profile({"suite.auto", "suite.all"})
public class NormalRandomCreatorFactory implements RandomCreatorFactory {

    private static final String NAME = "NORMAL";

    private static final RandomCreator<Probability> NORMAL_CREATOR = (range, number, probabilitySet, attributeMap) -> {
        ArrayList<Probability> awardList = new ArrayList<>();
        if (range == 0)
            range = 10000;
        for (int index = 0; index < number; index++) {
            int rand = ThreadLocalRandom.current().nextInt(range);
            for (Probability awardGroup : probabilitySet) {
                if (awardGroup instanceof AwardGroup && !((AwardGroup) awardGroup).isEffect(attributeMap))
                    continue;
                if (awardGroup.getProbability() > rand)
                    awardList.add(awardGroup);
            }
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
