/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
