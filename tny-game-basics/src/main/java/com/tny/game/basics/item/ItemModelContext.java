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
