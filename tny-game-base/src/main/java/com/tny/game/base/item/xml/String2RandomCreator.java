package com.tny.game.base.item.xml;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.common.utils.Logs;
import com.tny.game.base.item.probability.RandomCreator;
import com.tny.game.base.item.probability.RandomCreatorFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * string赚随机器
 *
 * @author KGTny
 */
public class String2RandomCreator extends AbstractSingleValueConverter {

    private Map<String, RandomCreatorFactory> randomCreatorFactory;

    public String2RandomCreator(Map<String, RandomCreatorFactory> randomCreatorFactory) {
        if (randomCreatorFactory == null) {
            this.randomCreatorFactory = Collections.emptyMap();
        } else {
            this.randomCreatorFactory = new HashMap<>(randomCreatorFactory);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class clazz) {
        return RandomCreator.class.isAssignableFrom(clazz);
    }

    @Override
    public Object fromString(String name) {
        RandomCreatorFactory factory = randomCreatorFactory.get(name);
        if (factory == null)
            throw new NullPointerException(Logs.format("找不到名字为 {} 的 RandomCreatorFactory", name));
        return factory.getRandomCreator();
    }

}
