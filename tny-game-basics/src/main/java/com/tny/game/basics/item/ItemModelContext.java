/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
