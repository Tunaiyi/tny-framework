package com.tny.game.base.item.probability;

import java.util.List;
import java.util.Map;

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
