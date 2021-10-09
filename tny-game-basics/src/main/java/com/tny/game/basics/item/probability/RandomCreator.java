package com.tny.game.basics.item.probability;

import java.util.*;

/**
 * 随机概率事物生产器接口
 *
 * @author KGTny
 */
public interface RandomCreator<G extends ProbabilityGroup<P>, P extends Probability> {

    List<P> random(G group, Map<String, Object> attributeMap);

}
