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

package com.tny.game.basics.item;

import com.tny.game.expr.*;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public interface ItemModelContext {

    // Map<String, RandomCreatorFactory> DEFAULT_RANDOM_CREATOR_FACTORIES = ImmutableMap.builder()
    //         .putAll(RandomCreators.);

    // static {
    //     Map<String, RandomCreatorFactory> factoryMap = new HashMap<>();
    //     for (RandomCreatorFactory factory : RandomCreators.getFactories()) {
    //         factoryMap.put(factory.getName(), factory);
    //     }
    //     RandomCreatorFactory factory = new SequenceRandomCreatorFactory();
    //     factoryMap.put(factory.getName(), factory);
    //     factory = new AllRandomCreatorFactory();
    //     factoryMap.put(factory.getName(), factory);
    //     DEFAULT_RANDOM_CREATOR_FACTORIES = factoryMap;
    // }

    /**
     * @return 获取Item浏览器
     */
    ItemExplorer getItemExplorer();

    /**
     * @return 获取ItemModel浏览器
     */
    ModelExplorer getItemModelExplorer();

    /**
     * @return 获取表达式工厂
     */
    ExprHolderFactory getExprHolderFactory();

}
