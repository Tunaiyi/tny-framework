/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.xml;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.basics.item.probability.*;

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
        RandomCreatorFactory<?, ?> factory = RandomCreators.getFactory(name);
        if (factory == null) {
            throw new NullPointerException(format("找不到名字为 {} 的 RandomCreatorFactory", name));
        }
        return factory.getRandomCreator();
    }

}
