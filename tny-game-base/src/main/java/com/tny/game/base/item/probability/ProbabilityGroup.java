package com.tny.game.base.item.probability;

import java.util.*;

/**
 * 概率接口
 *
 * @author KGTny
 */
public interface ProbabilityGroup<P extends Probability> {

    List<P> probabilities();

    int getRange(Map<String, Object> attributeMap);

    int getNumber(Map<String, Object> attributeMap);

}
