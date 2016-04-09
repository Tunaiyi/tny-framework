package com.tny.game.base.item;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 随机概率事物生产器接口
 *
 * @author KGTny
 */
public interface RandomCreator<P extends Probability> {

    public List<P> random(int range, int number, Collection<? extends P> probabilityList, Map<String, Object> attributeMap);

}
