package com.tny.game.base.item.xml;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.base.item.probability.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * string赚随机器
 *
 * @author KGTny
 */
public class String2RandomCreator extends AbstractSingleValueConverter {


    public String2RandomCreator() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class clazz) {
        return RandomCreator.class.isAssignableFrom(clazz);
    }

    @Override
    public Object fromString(String name) {
        RandomCreatorFactory factory = RandomCreators.getFactory(name);
        if (factory == null)
            throw new NullPointerException(format("找不到名字为 {} 的 RandomCreatorFactory", name));
        return factory.getRandomCreator();
    }

}
